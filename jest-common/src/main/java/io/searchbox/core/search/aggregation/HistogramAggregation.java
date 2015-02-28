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
public class HistogramAggregation extends Aggregation {

    public static final String TYPE = "histogram";

    private List<Histogram> histograms;

    public HistogramAggregation(String name, JsonObject histogramAggregation) {
        super(name, histogramAggregation);
        histograms = new ArrayList<Histogram>();

        for (JsonElement bucketv : histogramAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray()) {
            JsonObject bucket = bucketv.getAsJsonObject();
            Histogram histogram = new Histogram(
                    bucket.get(String.valueOf(KEY)).getAsLong(),
                    bucket.get(String.valueOf(DOC_COUNT)).getAsLong());
            histograms.add(histogram);
        }
    }

    public List<Histogram> getHistograms() {
        return histograms;
    }

    public static class Histogram {

        private Long key;
        private Long count;

        Histogram(Long key, Long count) {
            this.key = key;
            this.count = count;
        }

        public Long getKey() {
            return key;
        }

        public Long getCount() {
            return count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof Histogram)) {
                return false;
            }

            Histogram rhs = (Histogram) o;
            return new EqualsBuilder()
                    .append(getCount(), rhs.getCount())
                    .append(getKey(), rhs.getKey())
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(getKey())
                    .append(getCount())
                    .toHashCode();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        HistogramAggregation rhs = (HistogramAggregation) o;
        return new EqualsBuilder()
                .append(getHistograms(), rhs.getHistograms())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getHistograms())
                .toHashCode();
    }
}
