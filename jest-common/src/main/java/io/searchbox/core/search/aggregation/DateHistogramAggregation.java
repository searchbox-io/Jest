package io.searchbox.core.search.aggregation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

import static io.searchbox.core.search.aggregation.AggregationField.*;

/**
 * @author cfstout
 */
public class DateHistogramAggregation extends Aggregation {

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
            if (!super.equals(o)) {
                return false;
            }

            DateHistogram rhs = (DateHistogram) o;
            return new EqualsBuilder()
                    .append(getTimeAsString(), rhs.getTimeAsString())
                    .append(getTime(), rhs.getTime())
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .appendSuper(super.hashCode())
                    .append(getTimeAsString())
                    .append(getTime())
                    .toHashCode();
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

        DateHistogramAggregation rhs = (DateHistogramAggregation) o;
        return new EqualsBuilder()
                .append(getDateHistograms(), rhs.getDateHistograms())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getDateHistograms())
                .toHashCode();
    }
}
