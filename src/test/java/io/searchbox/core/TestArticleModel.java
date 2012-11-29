package io.searchbox.core;

import io.searchbox.annotations.JestId;

/**
 * @author ferhat sobay
 */
public class TestArticleModel {
    @JestId
    private String id;
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}