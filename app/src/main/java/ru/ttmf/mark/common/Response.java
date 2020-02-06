package ru.ttmf.mark.common;

public class Response {
    private Object object;
    private QueryType type;
    private NetworkStatus status;
    private String error;

    public Response(QueryType type, NetworkStatus status) {
        this.type = type;
        this.status = status;
    }

    public Response(Object object, QueryType type, NetworkStatus status) {
        this.object = object;
        this.type = type;
        this.status = status;
    }

    public Response(QueryType type, NetworkStatus status, String error) {
        this.object = object;
        this.type = type;
        this.status = status;
        this.error = error;
    }

    public Object getObject() {
        return object;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public NetworkStatus getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public QueryType getType() {
        return type;
    }

    public void setType(QueryType type) {
        this.type = type;
    }
}
