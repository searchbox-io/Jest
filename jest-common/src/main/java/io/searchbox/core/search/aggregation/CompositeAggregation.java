package io.searchbox.core.search.aggregation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static io.searchbox.core.search.aggregation.AggregationField.*;

public class CompositeAggregation extends BucketAggregation{

    private List<Entry> buckets;
    private JsonObject afterKey;

    public CompositeAggregation(String name, JsonObject agg) {
        super(name, agg);
        if (agg.has(AFTER_KEY.toString()) && agg.get(AFTER_KEY.toString()).isJsonObject()) {
            afterKey = agg.get(AFTER_KEY.toString()).getAsJsonObject();
        }
        if (agg.has(BUCKETS.toString()) && agg.get(BUCKETS.toString()).isJsonArray()) {
            final JsonArray bucketsArray = agg.get(BUCKETS.toString()).getAsJsonArray();
            buckets = StreamSupport.stream(bucketsArray.spliterator(), false)
                    .map(JsonElement::getAsJsonObject)
                    .map(bucket ->   new Entry(bucket,
                                        bucket.get(KEY.toString()).getAsJsonObject(),
                                        bucket.get(DOC_COUNT.toString()).getAsLong()))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public List<Entry> getBuckets() {
        return buckets;
    }

    public JsonObject getAfterKey() {
        return afterKey;
    }

    public class Entry extends Bucket {
        private JsonObject key;

        public Entry(JsonObject bucketRoot,  JsonObject key, Long count) {
            super(bucketRoot, count);
            this.key = key;
        }

        public JsonObject getKey() {
            return key;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            if (!super.equals(o)) return false;
            Entry entry = (Entry) o;
            return Objects.equals(key, entry.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), key);
        }
    }
}
