package io.searchbox.params;

/**
 * @author ferhat
 */
public enum SearchType {

    DFS_QUERY_THEN_FETCH("dfs_query_then_fetch"),
    QUERY_THEN_FETCH("query_then_fetch"),
    COUNT("count"),
    SCAN("scan");

    private String value;

    SearchType(String value) {
        this.value = value;
    }

    public String toString() {
        return this.value;
    }
}
