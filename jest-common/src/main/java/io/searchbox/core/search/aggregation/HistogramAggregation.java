package io.searchbox.core.search.aggregation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.LinkedList;
import java.util.List;

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
            return new EqualsBuilder()
                    .appendSuper(super.equals(obj))
                    .append(key, rhs.key)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .appendSuper(super.hashCode())
                    .append(key)
                    .toHashCode();
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
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(histograms, rhs.histograms)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(histograms)
                .toHashCode();
    }
}
