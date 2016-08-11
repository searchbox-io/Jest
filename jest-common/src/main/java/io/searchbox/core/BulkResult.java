package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import io.searchbox.client.JestResult;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author cihat.keser
 */
public class BulkResult extends JestResult {
    public BulkResult(JestResult source) {
        super(source);
    }

    public BulkResult(Gson gson) {
        super(gson);
    }

    /**
     *
     * @return empty list if Bulk action failed on HTTP level, otherwise all individual action items in the response
     */
    public List<BulkResultItem> getItems() {
        List<BulkResultItem> items = new LinkedList<BulkResultItem>();

        if (jsonObject != null && jsonObject.has("items")) {
            for (JsonElement jsonElement : jsonObject.getAsJsonArray("items")) {
                for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
                    items.add(new BulkResultItem(
                            entry.getKey(),
                            entry.getValue().getAsJsonObject()
                    ));
                }
            }
        }

        return items;
    }


    /**
     *
     * @return empty list if Bulk action failed on HTTP level, otherwise individual failed action items in the response
     */
    public List<BulkResultItem> getFailedItems() {
        List<BulkResultItem> items = new LinkedList<BulkResultItem>();

        if (jsonObject != null && jsonObject.has("items")) {
            for (JsonElement jsonElement : jsonObject.getAsJsonArray("items")) {
                for (Map.Entry<String, JsonElement> entry : jsonElement.getAsJsonObject().entrySet()) {
                    JsonObject values = entry.getValue().getAsJsonObject();
                    if (values.has("error")) {
                        items.add(new BulkResultItem(
                                entry.getKey(),
                                values
                        ));
                    }
                }
            }
        }

        return items;
    }

    public class BulkResultItem {
        public final String operation;
        public final String index;
        public final String type;
        public final String id;
        public final int status;
        /**
         * Can be null if the item completed without errors.
         */
        public final String error;
        /**
         * Can be null if the error was not a JSON object or if the item completed without errors
         */
        public final String errorType;
        /**
         * Can be null if the error was not a JSON object or if the item completed without errors
         */
        public final String errorReason;
        /**
         * Can be null when item completed with errors.
         */
        public final Integer version;

    public BulkResultItem(String operation, String index, String type, String id, int status,
        String error, Integer version, String errorType, String errorReason) {
            this.operation = operation;
            this.index = index;
            this.type = type;
            this.id = id;
            this.status = status;
            this.error = error;
            this.errorType = errorType;
            this.errorReason = errorReason;
            this.version = version;
        }

        public BulkResultItem(String operation, JsonObject values) {
            this.operation = operation;
            this.index = values.get("_index").getAsString();
            this.type = values.get("_type").getAsString();
            this.id = values.has("_id") && !values.get("_id").isJsonNull() ? values.get("_id").getAsString() : null;
            this.status = values.get("status").getAsInt();
            this.error = values.has("error") ? values.get("error").toString() : null;

            if (values.has("error") && values.get("error").isJsonObject()) {
                final JsonObject errorObject = values.get("error").getAsJsonObject();
                this.errorType = errorObject.has("type") ? errorObject.get("type").getAsString() : null;
                this.errorReason = errorObject.has("reason") ? errorObject.get("reason").getAsString() : null;
            } else {
                this.errorType = null;
                this.errorReason = null;
            }

            this.version = values.has("_version") ? values.get("_version").getAsInt() : null;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;

            if (o == null || getClass() != o.getClass()) return false;

            BulkResultItem that = (BulkResultItem) o;

            return new EqualsBuilder()
                    .append(status, that.status)
                    .append(operation, that.operation)
                    .append(index, that.index)
                    .append(type, that.type)
                    .append(id, that.id)
                    .append(error, that.error)
                    .append(errorType, that.errorType)
                    .append(errorReason, that.errorReason)
                    .append(version, that.version)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder(17, 37)
                    .append(operation)
                    .append(index)
                    .append(type)
                    .append(id)
                    .append(status)
                    .append(error)
                    .append(errorType)
                    .append(errorReason)
                    .append(version)
                    .toHashCode();
        }
    }

}
