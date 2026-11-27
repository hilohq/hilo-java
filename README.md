# HiLO

[![Build](https://github.com/hilohq/hilo-java/actions/workflows/build.yml/badge.svg)](https://github.com/hilohq/hilo-java/actions/workflows/build.yml)

HiLO turns raw, schemaless JSON logs into monitoring without hand-written instrumentation.
You point it at a stream of log lines. It infers the schema (which fields exist and what JSON
type each one usually has), then profiles every field's values to classify it as a counter, a
monotonic series, a gauge, a discrete value, a timestamp and so on. From those classes it
generates the downstream artifacts: Prometheus metric definitions with suitable PromQL
expressions (`irate(x[1m])` for counters, the raw series for gauges), a Grafana dashboard with
one panel per metric, MySQL table definitions, and push jobs that send the values to Prometheus
Pushgateway, InfluxDB or MySQL. Every step runs as an Apache Flink job. The learned models are
stored in MongoDB and served over gRPC.

> **Status:** a personal prototype written in 2020–2021. It is no longer developed. See
> [Status and known limitations](#status-and-known-limitations).

## Design highlights

- **Schema inference from raw logs.** `JsonParser` flattens each JSON line into dotted keys and
  `JsonInfer` records the JSON type of every value. Per-field type histograms are reduced in
  Flink, and `TypeInfoPrefer` picks the majority type, so a field that is sometimes `null`
  still gets a usable type. Later pipelines validate every line against that schema.
- **Statistical feature classification.** Each field is profiled with mergeable statistics:
  value moments, percentile histograms of values and of line-to-line deltas, a continuity score,
  and value histograms for strings and booleans. `FeatureInfer` turns those statistics into a
  class such as `counter`, `monotonic`, `gauge`, `discrete` or `timestamp`. The statistics
  serialize to protobuf, so a later run merges its data into the stored feature model.
- **Declarative pipelines.** Each job is described by a protobuf message (`BatchPipeline` /
  `StreamPipeline` in `pipelines.proto`). `BatchPipelineUtils` / `StreamPipelineUtils` map the
  message to an input format, a chain of `BatchStages` and an output format. The gRPC
  `Pipelines` service and `hilo-tool` submit these to a remote Flink cluster.
- **Pluggable connectors.** Sources and sinks are ordinary Flink `InputFormat` / `OutputFormat`
  / sink implementations: Mongo (writes go through a `MongoCommandGenerator` that turns a result
  into upserts or inserts), Prometheus Pushgateway, InfluxDB, JDBC/MySQL and the Grafana HTTP API.
  A new target needs a stage, an output format and a pipeline message.
- **Automatic dashboard generation.** The learned feature classes drive the Prometheus metric
  model, which picks `irate(...)` for counters and the raw series for gauges. That model is
  turned into a Grafana dashboard JSON with one panel per metric and posted through the Grafana
  API.

## Architecture

```mermaid
flowchart LR
    logs[("Raw JSON logs<br/>(text file, one per line)")]

    subgraph flink["Apache Flink jobs (pipelines + stages)"]
        schema["Schema learning<br/>JsonSchemaStatsGenerate →<br/>JsonSchemaUpdateGenerate"]
        validate["Schema validation<br/>JsonSchemaValidate"]
        features["Feature learning<br/>FeaturesStatsGenerate →<br/>FeaturesUpdateGenerate<br/>(FeatureInfer)"]
        prom["Prometheus metric model<br/>PrometheusMetricsGenerate"]
        jdbcgen["MySQL table model<br/>JdbcMysqlTableGenerate"]
        dash["Dashboard generation<br/>PrometheusGrafanaDashboardGenerate →<br/>GrafanaDashboardGenerate"]
        push["Value push jobs<br/>PrometheusValueGenerate /<br/>InfluxDbValuesGenerate / JdbcRowGenerate"]
    end

    mongo[("MongoDB<br/>schemas, schema_logs,<br/>features, feature_logs,<br/>prometheus, jdbc")]

    subgraph grpc["gRPC services"]
        schemaSvc["Schema"]
        featuresSvc["Features"]
        integrationsSvc["Integrations"]
        pipelinesSvc["Pipelines"]
    end

    grafana["Grafana<br/>(HTTP API)"]
    pushgw["Prometheus<br/>Pushgateway"]
    influx["InfluxDB"]
    mysql["MySQL (JDBC)"]

    logs --> schema --> mongo
    logs --> validate
    mongo -- schema --> validate
    validate --> features --> mongo
    validate --> prom --> mongo
    validate --> jdbcgen --> mongo
    mongo -- prometheus model --> dash --> grafana
    validate --> push
    mongo -- metric / feature models --> push
    push --> pushgw
    push --> influx
    push --> mysql

    mongo --- schemaSvc
    mongo --- featuresSvc
    mongo --- integrationsSvc
    pipelinesSvc -- submits jobs --> flink
```

Each learning step is its own batch pipeline. A pipeline reads its input (a text file, or a
Mongo collection for dashboard generation), runs its stages and writes the result to an output
format, usually Mongo upserts and inserts through `connector-mongo`. Later pipelines load what
earlier ones learned from Mongo in `open()`:

1. **Schema** (`JsonSchemaGeneratePipeline`). `JsonInfer` flattens each JSON line into dotted
   keys (`a.b.c`) and records each field's JSON type. Type counts are aggregated per field.
   `TypeInfoPrefer` picks the majority type, and the pipeline emits the field definition plus a
   `fieldCreate` / `fieldUpdate` schema log.
2. **Features** (`JsonFeaturesGeneratePipeline`). Lines are parsed and validated against the
   stored schema, and fields whose type does not match are dropped. String values are re-parsed
   by `StringParser`, so `"80525500000"` becomes a number. Per-field statistics are collected:
   value stats, percentile histograms of values and deltas, and a continuity score.
   `FeatureInfer` then assigns a feature class: `counter`, `monotonic`, `gauge`, `discrete`,
   `timestamp`, `boolEnum` or `ephemeral`. `FeaturesUpdateGenerate` first loads the features
   already stored for the schema (through a `FeaturesLoader`; `MongoFeaturesLoader` in the
   pipeline) and merges the new statistics into them. It logs a `featureCreate` for each new
   feature and a `featureUpdate` when a feature's class changes.
3. **Integrations.**
   - `PrometheusMetricsGeneratePipeline` creates a gauge metric for each `gauge`, `monotonic`,
     `counter` or `discrete` feature. The Grafana expression is `irate(<key>[1m])` for counters
     and monotonic features and `<key>` otherwise.
   - `PrometheusGrafanaDashboardGeneratePipeline` reads that metric model from Mongo, builds a
     Grafana dashboard JSON with one Prometheus graph panel per metric, and posts it to
     `/api/dashboards/db`.
   - `JdbcMysqlTableGeneratePipeline` derives MySQL columns and an `INSERT` load query.
4. **Push.** `JsonPrometheusPushPipeline` (a Flink streaming job, to Pushgateway),
   `JsonInfluxDbPushPipeline` and `JsonJdbcPushPipeline` send the values of the learned metrics.

The gRPC APIs are defined in `protobufs/src/main/proto/**/api.proto`:

| Service | RPCs |
|---|---|
| `Schema` | `GetSchemaIdsBySource`, `GetSchema`, `GetSchemaLogs` |
| `Features` | `GetFeatures`, `GetFeatureLogs` |
| `Integrations` | `GetPrometheusMetrics`, `GetJdbcMetrics` |
| `Pipelines` | `ExecuteBatchPipeline`, `ExecuteStreamPipeline`, `GetPipelineStatus`, `CancelPipeline` |

## Modules

| Module | Description |
|---|---|
| `config` | `HiloConfig`: connection settings from environment variables, with defaults for the local Docker setup (see [Configuration](#configuration)). |
| `protobufs` | `.proto` definitions for the schema, feature, integration and pipeline models and the gRPC services, compiled by the protobuf Gradle plugin. |
| `parsers` | Log parsing: JSON flattening (`JsonParser`), type inference (`JsonInfer`), schema validation (`JsonValidator`), typed values and `StringParser`. |
| `features` | Per-field statistics (numeric, percentile histograms, continuity, string, boolean) and the `FeatureInfer` classifier. |
| `structure-learning` | Flink functions that learn schemas and features, and serialize the updates into Mongo commands. |
| `connector-mongo` | Reactive Mongo client plus Flink `InputFormat` / `OutputFormat` / sink for protobuf documents. |
| `integration-prometheus` | Prometheus metric-model generation, value generation, Pushgateway client and sink. |
| `integration-grafana` | Grafana dashboard entities (Jackson), dashboard generation and an HTTP client / Flink output format. |
| `integration-influxdb` | Conversion of feature values into InfluxDB points, plus an InfluxDB output format. |
| `integration-jdbc` | MySQL table/column generation, the load query, and row generation for JDBC output. |
| `schema-service` | gRPC `Schema` service, a client, and a Mongo-backed schema store. |
| `schema-server` | `main` that serves the `Schema` service (port 1234 by default). |
| `features-service` | gRPC `Features` service, a client, and a Mongo-backed feature store. |
| `features-server` | `main` that serves the `Features` service (port 1235 by default). |
| `integrations-service` | gRPC `Integrations` service, a client, and a Mongo store for the Prometheus and JDBC models. |
| `pipelines` | Batch and stream pipeline definitions, stage wiring, and executors that submit jobs to a remote Flink cluster. |
| `pipelines-service` | gRPC `Pipelines` service and the `FlinkClient` that submits, polls and cancels jobs. |
| `hilo-tool` | CLI (`pipeline execute --input <file>`) that chains the schema, features, MySQL table and JDBC push pipelines against a local stack. |
| `integration-tests` | End-to-end tests that run the pipelines on a Docker Compose stack (Flink, MongoDB, Kafka, ZooKeeper). |

## Tech stack

- Java 17 (Gradle toolchain), Gradle 8.10 (wrapper), Shadow plugin for fat jars, Spotless with google-java-format
- Apache Flink 1.11 (DataSet and DataStream APIs)
- Protocol Buffers 3.12 and gRPC 1.31
- MongoDB (reactive streams driver) as the model store
- Prometheus simpleclient and Pushgateway, Grafana HTTP API, InfluxDB client, MySQL JDBC
- Jackson, Guava, Apache Commons
- JUnit 4

## Build and test

The build compiles and tests with a Java 17 toolchain. The Gradle 8.10 wrapper itself runs on
any JDK from 17 to 23 (tested with JDK 21). If no JDK 17 is installed, Gradle downloads one
through the foojay toolchain resolver configured in `settings.gradle`.

```sh
./gradlew build            # compiles every module, generates the protobuf/gRPC classes, runs unit tests
./gradlew test             # unit tests only
./gradlew :parsers:test    # a single module
./gradlew spotlessCheck    # google-java-format check (CI runs this before the build)
./gradlew spotlessApply    # reformat the sources
```

The protobuf and gRPC classes are generated from `protobufs/src/main/proto` into
`protobufs/build/generated/source/proto/main` on every build. They are not checked in.

The unit tests need no external services. They cover the parsers, schema and feature
learning, Prometheus metric and value generation, and Grafana dashboard generation. The
schema, feature and Prometheus tests run the real Flink reduce functions in-process on the
`blocks` and `transactions` samples and compare the results to the golden files described below.

CI (`.github/workflows/build.yml`) runs `spotlessCheck` and then `build` on Temurin 17 for
pushes and pull requests to `master`.

After an intentional change to feature learning, regenerate the `features.json` golden files
from the code and review the diff:

```sh
./gradlew :structure-learning:regenerateGoldenFeatures
```

## Configuration

Connection settings are read by `HiloConfig` (module `config`) from environment variables. The
defaults match the Docker Compose stack used by the integration tests, so nothing needs to be
set to run them. `hilo-tool pipeline execute` also accepts the settings marked "CLI" as flags,
for example `--flink-port 8082` or `--grafana-token <token>`, and flags take precedence over the
environment. Prefer the environment variables for secrets, because flags are visible in the
process list.

| Environment variable | Default | CLI flag | Used by |
|---|---|---|---|
| `HILO_MONGO_URL` | `mongodb://root:example@localhost:27017/admin` | `--mongo-url` | gRPC servers, `hilo-tool` reads, integration-test helpers |
| `HILO_MONGO_DATABASE` | `test` | `--mongo-database` | every Mongo store and pipeline output |
| `HILO_PIPELINE_MONGO_URL` | `mongodb://root:example@mongodb:27017/admin` | `--pipeline-mongo-url` | Mongo URL put into the jobs (resolved inside the Flink cluster) |
| `HILO_FLINK_HOST` | `127.0.0.1` | `--flink-host` | `FlinkClient`, `DefaultBatchExecutor`, `DefaultStreamExecutor` |
| `HILO_FLINK_PORT` | `8081` | `--flink-port` | same as above |
| `HILO_SCHEMA_SERVICE_HOST` / `_PORT` | `localhost` / `1234` | | `SchemaServer` (port), `SchemaClient` |
| `HILO_FEATURES_SERVICE_HOST` / `_PORT` | `localhost` / `1235` | | `FeaturesServer` (port), `FeaturesClient` |
| `HILO_INTEGRATIONS_SERVICE_HOST` / `_PORT` | `localhost` / `1235` | | `IntegrationsClient` |
| `HILO_PIPELINES_SERVICE_HOST` / `_PORT` | `localhost` / `1234` | | `PipelinesClient` |
| `HILO_GRAFANA_URL` | `http://172.20.0.1:3000` | `--grafana-url` | dashboard generation in `hilo-tool` |
| `GRAFANA_TOKEN` | none (required for dashboard generation) | `--grafana-token` | dashboard generation in `hilo-tool` |
| `HILO_PUSHGATEWAY_ADDRESS` | `172.20.0.1:9091` | `--pushgateway-address` | Prometheus push in `hilo-tool` |
| `HILO_JDBC_URL` | `jdbc:mysql://172.20.0.1:3306/database` | `--jdbc-url` | JDBC push in `hilo-tool` |
| `HILO_JDBC_USERNAME` / `HILO_JDBC_PASSWORD` | `root` / `password` | `--jdbc-username` / `--jdbc-password` | JDBC push in `hilo-tool` |
| `HILO_JDBC_DRIVER` | `com.mysql.jdbc.Driver` | `--jdbc-driver` | JDBC push in `hilo-tool` |
| `HILO_INFLUXDB_URL` | `127.0.0.1:8086` | | default `InfluxDbClient` address |
| `HILO_INFLUXDB_TOKEN` / `_ORG` / `_BUCKET` | `token` / `org` / `db0` | | `InfluxDbOutputFormat` |

The `172.20.0.1` defaults are the Docker bridge gateway, as seen from inside the Flink
containers. Settings are resolved where a pipeline is built or a client is opened, and the
values a job needs are copied into the pipeline definition.

## Integration tests

The integration tests run the real pipelines on a local Flink cluster and check the results
in MongoDB through the gRPC services. They need Docker and the `docker-compose` CLI.

```sh
./gradlew :integration-tests:integrationTest
```

This task builds the `integration-tests` shadow jar, and `scripts/docker-start.sh` starts
`integration-tests/docker-compose.yml`. That file runs a Flink 1.11.2 JobManager and
TaskManager (with `/tmp` mounted), MongoDB 4.4 (`root` / `example`), Kafka and ZooKeeper. The
tests copy the sample logs to `/tmp/<sample>/logs.txt`, submit jobs to the JobManager at
`127.0.0.1:8081`, and query Mongo at `localhost:27017` (the defaults in
[Configuration](#configuration)). Stop the stack with `integration-tests/scripts/docker-stop.sh`.

In GitHub Actions, the integration tests run only when the `Build` workflow is started by hand
with the `integration-tests` input set.

## Worked example: the `blocks` sample

`integration-tests/src/integration-test/resources/samples/blocks/` holds 100 Ethereum block
headers (`logs.txt`), together with the schema, features and Prometheus model that HiLO
learns from them (`schema.json`, `features.json`, `prometheus.json`). The integration tests and
the unit tests both check the pipelines against these files.

**Input: one JSON object per line** (trimmed here):

```json
{"difficulty":"12549332509227", "gasUsed":50244, "hash":"0x8e38b4db…", "number":1000000, "timestamp":1455404053, "totalDifficulty":"7135202464334937706", "transactions":[…], "uncles":[], …}
{"difficulty":"12555460113247", "gasUsed":21000, "hash":"0xcb5cab72…", "number":1000001, "timestamp":1455404058, "totalDifficulty":"7135215019795050953", …}
```

**Step 1: inferred schema** (`schema.json`, 20 fields). Every field gets its majority JSON type
and a type histogram:

```json
"number": {
  "key": "number", "name": "number", "type": "JSON_NUMBER",
  "stats": { "typeHistogram": { "numberCount": "100" } }
},
"totalDifficulty": {
  "key": "totalDifficulty", "name": "totalDifficulty", "type": "JSON_STRING",
  "stats": { "typeHistogram": { "stringCount": "100" } }
}
```

In the `transactions` sample the same mechanism handles mixed types. `to` is `null` for
contract creations, so its histogram is `{"nullCount": "3", "stringCount": "214"}` and it is
typed `JSON_STRING`.

**Step 2: feature classes** (`features.json`). Values are profiled after validation and string
re-parsing. Some of the resulting classes:

| Field | JSON type | Learned feature class |
|---|---|---|
| `number` | number | `counter` (increases by exactly 1 from one line to the next) |
| `totalDifficulty` | string holding a number | `monotonic` |
| `timestamp` | number | `timestamp` |
| `gasUsed`, `gasLimit`, `size`, `difficulty` | number / numeric string | `discrete` |
| `hash`, `miner`, `parentHash`, … (11 fields) | string | `ephemeral` (identifiers and free text; not turned into metrics) |

The two array fields (`transactions`, `uncles`) are dropped during validation, so 18 features
are learned. String fields keep a value histogram (up to 1,024 distinct values), so the golden
file records, for example, 100 distinct `hash` values and 17 distinct `miner` addresses.

**Step 3: Prometheus metric model** (`prometheus.json`, 6 metrics). Counters and monotonic
features are wrapped in `irate`, and the others are graphed as-is:

```json
"number": {
  "key": "number",
  "gauge": { "name": "number", "help": "number" },
  "dashboard": { "grafana": { "expr": "irate(number[1m])" } }
}
```

| Metric | Grafana expression |
|---|---|
| `number` | `irate(number[1m])` |
| `totalDifficulty` | `irate(totalDifficulty[1m])` |
| `difficulty`, `gasLimit`, `gasUsed`, `size` | the metric name |

**Step 4: Grafana dashboard.** `PrometheusGrafanaDashboardGeneratePipeline` turns the model into
a dashboard titled with the schema id. It has one `graph` panel per metric with the
`Prometheus` datasource and a target whose `expr` is the expression above. The default time
range is `now-30m` to `now`. The dashboard is posted to Grafana's `/api/dashboards/db` with
`overwrite: true`.

## Status and known limitations

This is a 2020–2021 prototype, kept as a portfolio piece. It shows the full flow from logs to
schema, features, metrics and a dashboard, but it is not production-ready:

- Stages that read a stored model when a job starts (for example schema validation, or
  loading the metric model) open their Mongo store with `HILO_MONGO_DATABASE` from the Flink
  task manager's environment. Only the output database comes from the pipeline definition, so
  a non-default database has to be set on the task managers too.
- Only JSON logs are supported. The `text` and `mysql` schema variants in `schema.proto` are
  placeholders with no implementation.
- `JsonSchemaUpdateGenerate` (marked `TODO`) does not load the previously stored schema, so
  every schema-learning run is treated as a fresh schema. Feature learning is incremental (see
  step 2 above).
- `FeatureInfer` classifies every string-typed feature as `ephemeral`, however few distinct
  values it has. The string value histogram is collected but not yet used for classification,
  so for example the two-valued `v` field of the `transactions` sample is not detected as an
  enum.
- `hilo-tool pipeline execute` runs the schema, features, MySQL table and JDBC push pipelines.
  The Prometheus/Grafana chain (`loadPrometheus`) is implemented but not wired to a command.
- There are server `main`s only for the Schema and Features services. The default ports of
  `IntegrationsClient` (1235) and `PipelinesClient` (1234) overlap with those services.
