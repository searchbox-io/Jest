package io.searchbox.core.search.aggregation;

import com.google.gson.JsonObject;

import java.util.Objects;

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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        Aggregation rhs = (Aggregation) obj;
        return Objects.equals(name, rhs.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

}
