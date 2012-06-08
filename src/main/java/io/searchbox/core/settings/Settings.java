package io.searchbox.core.settings;

/**
 * @author Dogukan Sonmez
 */


public enum Settings {

    VERSION("version"),
    OP_TYPE("op_type"),
    ROUTING("routing"),
    PARENT("parent"),
    TIMESTAMP("timestamp"),
    TTL("ttl"),
    PERCOLATE("percolate"),
    TIMEOUT("timeout");

    private String value;

    Settings(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
