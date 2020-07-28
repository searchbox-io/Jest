package io.searchbox.indices;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.searchbox.action.GenericResultAbstractAction;

import java.util.Map;
import java.util.Objects;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class CreateIndex extends GenericResultAbstractAction {

    private Object aliases;
    private Object settings;
    private Object mappings;

    protected CreateIndex(Builder builder) {
        super(builder);

        this.indexName = builder.index;
        this.payload = builder.payload;
        this.aliases = builder.aliases;
        this.settings = builder.settings;
        this.mappings = builder.mappings;
    }

    @Override
    public String getData(Gson gson) {

        if (this.payload != null) {
            if (payload instanceof String) {
                return (String) payload;
            } else if (payload instanceof Map) {
                return gson.toJson(payload);
            }
        } else {

            JsonObject jsonObject = new JsonObject();

            if (aliases != null) {
                if (aliases instanceof String)
                    jsonObject.add("aliases", gson.fromJson(aliases.toString(), JsonElement.class));
                else if (aliases instanceof Map)
                    jsonObject.add("aliases", gson.toJsonTree(aliases));
            }
            if (settings != null) {
                if (settings instanceof String)
                    jsonObject.add("settings", gson.fromJson(settings.toString(), JsonElement.class));
                else if (settings instanceof Map)
                    jsonObject.add("settings", gson.toJsonTree(settings));
            }
            if (mappings != null) {
                if (mappings instanceof String)
                    jsonObject.add("mappings", gson.fromJson(mappings.toString(), JsonElement.class));
                else if (mappings instanceof Map)
                    jsonObject.add("mappings", gson.toJsonTree(mappings));
            }

            if (jsonObject.size() == 0)
                return null;
            return jsonObject.toString();
        }
        return null;
    }

    @Override
    public String getRestMethodName() {
        return "PUT";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), indexName, payload);
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

        CreateIndex rhs = (CreateIndex) obj;
        return super.equals(obj)
                && Objects.equals(indexName, rhs.indexName)
                && Objects.equals(payload, rhs.payload)
                && Objects.equals(settings, rhs.settings)
                && Objects.equals(mappings, rhs.mappings)
                && Objects.equals(aliases, rhs.aliases);
    }

    public static class Builder extends GenericResultAbstractAction.Builder<CreateIndex, Builder> {
        private Object payload = null;
        private Object settings = null;
        private Object mappings = null;
        private Object aliases = null;
        private String index;

        public Builder(String index) {
            this.index = index;
        }

        public Builder payload(String payload) {
            this.payload = payload;
            return this;
        }

        public Builder payload(Map<String, Object> payload) {
            this.payload = payload;
            return this;
        }

        public Builder settings(String settings) {
            this.settings = settings;
            return this;
        }

        public Builder settings(Map<String, Object> settings) {
            this.settings = settings;
            return this;
        }

        public Builder mappings(String mappings) {
            this.mappings = mappings;
            return this;
        }

        public Builder mappings(Map<String, Object> mappings) {
            this.mappings = mappings;
            return this;
        }

        public Builder aliases(String aliases) {
            this.aliases = aliases;
            return this;
        }

        public Builder aliases(Map<String, Object> aliases) {
            this.aliases = aliases;
            return this;
        }

        @Override
        public CreateIndex build() {
            return new CreateIndex(this);
        }
    }

}
