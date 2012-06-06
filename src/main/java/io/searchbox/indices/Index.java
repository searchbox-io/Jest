package io.searchbox.indices;

/**
 * @author Dogukan Sonmez
 */


public class Index {

    private String name;

    private String type;

    private String id;

    private Object data;

    private int version;

    private String op_type;

    private String routing;

    private String parent;

    private String timestamp;

    private String ttl;

    private String percolate;

    private String timeoutInMinute;

    public Index() {
    }

    public Index(String name, String type, String id) {
        this.name = name;
        this.type = type;
        this.id = id;
    }


    public Index(String name, String type, String id, Object data) {
        this.name = name;
        this.type = type;
        this.id = id;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getOp_type() {
        return op_type;
    }

    public void setOp_type(String op_type) {
        this.op_type = op_type;
    }

    public String getRouting() {
        return routing;
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }

    public String getParent() {
        return parent;
    }

    public void setParent(String parent) {
        this.parent = parent;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTtl() {
        return ttl;
    }

    public void setTtl(String ttl) {
        this.ttl = ttl;
    }

    public String getPercolate() {
        return percolate;
    }

    public void setPercolate(String percolate) {
        this.percolate = percolate;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getTimeoutInMinute() {
        return timeoutInMinute;
    }

    public void setTimeoutInMinute(String timeoutInMinute) {
        this.timeoutInMinute = timeoutInMinute;
    }
}