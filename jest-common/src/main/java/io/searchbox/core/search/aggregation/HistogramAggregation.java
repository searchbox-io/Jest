package io.searchbox.core.search.aggregation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static io.searchbox.core.search.aggregation.AggregationField.BUCKETS;
import static io.searchbox.core.search.aggregation.AggregationField.DOC_COUNT;
import static io.searchbox.core.search.aggregation.AggregationField.KEY;

/**
 * @author cfstout
 */
public class HistogramAggregation extends Aggregation<HistogramAggregation> {

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

            Histogram histogram = (Histogram) o;

            if (!count.equals(histogram.count)) {
                return false;
            }
            if (!key.equals(histogram.key)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = key.hashCode();
            result = 31 * result + count.hashCode();
            return result;
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

        HistogramAggregation that = (HistogramAggregation) o;

        if (!histograms.equals(that.histograms)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return histograms.hashCode();
    }
}
