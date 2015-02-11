package io.searchbox.core.search.aggregation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static io.searchbox.core.search.aggregation.AggregationField.BUCKETS;
import static io.searchbox.core.search.aggregation.AggregationField.DOC_COUNT;
import static io.searchbox.core.search.aggregation.AggregationField.KEY;
import static io.searchbox.core.search.aggregation.AggregationField.KEY_AS_STRING;

/**
 * @author cfstout
 */
public class DateHistogramAggregation extends Aggregation<DateHistogramAggregation> {

    public static final String TYPE = "date_histogram";

    private List<DateHistogram> dateHistograms;

    public DateHistogramAggregation(String name, JsonObject dateHistogramAggregation) {
        super(name, dateHistogramAggregation);
        dateHistograms = new ArrayList<DateHistogram>();
        if (!dateHistogramAggregation.has(String.valueOf(BUCKETS)) || dateHistogramAggregation.get(String.valueOf(BUCKETS)).isJsonNull()) {
            return;
        }
        for (JsonElement bucket : dateHistogramAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray()) {
            JsonElement time = bucket.getAsJsonObject().get(String.valueOf(KEY));
            JsonElement timeAsString = bucket.getAsJsonObject().get(String.valueOf(KEY_AS_STRING));
            JsonElement count = bucket.getAsJsonObject().get(String.valueOf(DOC_COUNT));
            DateHistogram histogram = new DateHistogram(time.getAsLong(), timeAsString.getAsString(), count.getAsLong());
            dateHistograms.add(histogram);
        }
    }

    /**
     * @return List of DateHistogram objects if found, or empty list otherwise
     */
    public List<DateHistogram> getDateHistograms() {
        return dateHistograms;
    }

    public class DateHistogram extends HistogramAggregation.Histogram {

        private String timeAsString;

        DateHistogram(Long time, String timeAsString, Long count) {
            super(time, count);
            this.timeAsString = timeAsString;
        }

        public Long getTime() {
            return getKey();
        }

        public String getTimeAsString() {
            return timeAsString;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof DateHistogram)) {
                return false;
            }

            DateHistogram that = (DateHistogram) o;

            if (!timeAsString.equals(that.timeAsString)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            return timeAsString.hashCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof DateHistogramAggregation)) {
            return false;
        }

        DateHistogramAggregation that = (DateHistogramAggregation) o;

        if (!dateHistograms.equals(that.dateHistograms)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return dateHistograms.hashCode();
    }
}
