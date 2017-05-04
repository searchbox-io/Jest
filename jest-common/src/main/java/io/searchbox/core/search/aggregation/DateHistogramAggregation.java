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
public class DateHistogramAggregation extends BucketAggregation {

    public static final String TYPE = "date_histogram";

    private List<DateHistogram> dateHistograms = new LinkedList<DateHistogram>();

    public DateHistogramAggregation(String name, JsonObject dateHistogramAggregation) {
        super(name, dateHistogramAggregation);
        if (dateHistogramAggregation.has(String.valueOf(BUCKETS)) && dateHistogramAggregation.get(String.valueOf(BUCKETS)).isJsonArray()) {
            parseBuckets(dateHistogramAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray());
        }
    }

    private void parseBuckets(JsonArray bucketsSource) {
        for (JsonElement bucket : bucketsSource) {
            Long time = bucket.getAsJsonObject().get(String.valueOf(KEY)).getAsLong();
            String timeAsString = bucket.getAsJsonObject().get(String.valueOf(KEY_AS_STRING)).getAsString();
            Long count = bucket.getAsJsonObject().get(String.valueOf(DOC_COUNT)).getAsLong();

            dateHistograms.add(new DateHistogram(bucket.getAsJsonObject(), time, timeAsString, count));
        }
    }

    /**
     * @return List of DateHistogram objects if found, or empty list otherwise
     */
    public List<DateHistogram> getBuckets() {
        return dateHistograms;
    }

    public class DateHistogram extends HistogramAggregation.Histogram {

        private String timeAsString;

        DateHistogram(JsonObject bucket, Long time, String timeAsString, Long count) {
            super(bucket, time, count);
            this.timeAsString = timeAsString;
        }

        public Long getTime() {
            return getKey();
        }

        public String getTimeAsString() {
            return timeAsString;
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

            DateHistogram rhs = (DateHistogram) obj;
            return super.equals(obj) && Objects.equals(timeAsString, rhs.timeAsString);
        }

        @Override
        public int hashCode() {
            return Objects.hash(super.hashCode(), timeAsString);
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

        DateHistogramAggregation rhs = (DateHistogramAggregation) obj;
        return super.equals(obj) && Objects.equals(dateHistograms, rhs.dateHistograms);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dateHistograms);
    }
}
