package com.hilohq.hilo.hilo_flink.mongodb;

import org.bson.Document;

public final class Query {
    public enum Action {
        insert,
        update
    }

    private final Action action;
    private final Document document;

    public Query(final Action action,
                 final Document document) {
        this.action = action;
        this.document = document;
    }

    public Action action() {
        return action;
    }

    public Document document() {
        return document;
    }

}
