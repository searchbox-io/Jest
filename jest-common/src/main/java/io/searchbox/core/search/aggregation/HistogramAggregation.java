package io.searchbox.core.search.aggregation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static io.searchbox.core.search.aggregation.AggregationField.*;

/**
 * @author cfstout
 */
public class HistogramAggregation extends BucketAggregation {

    public static final String TYPE = "histogram";

    private List<Histogram> histograms = new LinkedList<Histogram>();

    public HistogramAggregation(String name, JsonObject histogramAggregation) {
        super(name, histogramAggregation);
        if(histogramAggregation.has(String.valueOf(BUCKETS)) && histogramAggregation.get(String.valueOf(BUCKETS)).isJsonArray()) {
            parseBuckets(histogramAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray());
        }
    }

    private void parseBuckets(JsonArray bucketsSource) {
        for (JsonElement bucketv : bucketsSource) {
            JsonObject bucket = bucketv.getAsJsonObject();
            Histogram histogram = new Histogram(
                    bucket,
                    bucket.get(String.valueOf(KEY)).getAsLong(),
                    bucket.get(String.valueOf(DOC_COUNT)).getAsLong());
            histograms.add(histogram);
        }
    }

    public List<Histogram> getBuckets() {
        return histograms;
    }

    public static class Histogram extends Bucket {

        private Long key;

        Histogram(JsonObject bucket, Long key, Long count) {
            super(bucket, count);
            this.key = key;
        }

        public Long getKey() {
            return key;
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

            Histogram rhs = (Histogram) obj;
            return super.equals(obj) && Objects.equals(key, rhs.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), key);
        }
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

        HistogramAggregation rhs = (HistogramAggregation) obj;
        return super.equals(obj) && Objects.equals(histograms, rhs.histograms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), histograms);
    }
}
