package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author cfstout
 */

public abstract class Aggregation {

    protected String name;
    protected JsonObject jsonRoot;

    public Aggregation(String name, JsonObject jsonRoot) {
        this.name = name;
        this.jsonRoot = jsonRoot;
    }

    public String getName() {
        return name;
    }
}
