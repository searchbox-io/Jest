package io.searchbox.core.search.facet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ferhat
 */
public class GeoDistanceFacet extends Facet {
    public static final String TYPE = "geo_distance";

    private List<Range> ranges;

    public GeoDistanceFacet(String name, Map geoDistanceFacet) {
        this.name = name;
        ranges = new ArrayList<Range>();
        for (Map term : (List<Map>) geoDistanceFacet.get("ranges")) {
            Range range = new Range((Double) term.get("from"), (Double) term.get("to"), ((Double) term.get("total_count")).longValue(), (Double) term.get("total"),
                    Double.parseDouble(term.get("min").toString()), Double.parseDouble(term.get("max").toString()), (Double) term.get("mean"));
            ranges.add(range);
        }
    }

    public List<Range> getRanges() {
        return ranges;
    }

    class Range {
        private Double from;
        private Double to;
        private Long totalCount;
        private Double total;
        private Double min;
        private Double max;
        private Double mean;

        Range(Double from, Double to, Long totalCount, Double total,
              Double min, Double max, Double mean) {
            this.from = from;
            this.to = to;
            this.totalCount = totalCount;
            this.total = total;
            this.min = min;
            this.max = max;
            this.mean = mean;
        }

        public Double getFrom() {
            return from;
        }

        public Double getTo() {
            return to;
        }

        public Long getTotalCount() {
            return totalCount;
        }

        public Double getTotal() {
            return total;
        }

        public Double getMin() {
            return min;
        }

        public Double getMax() {
            return max;
        }

        public Double getMean() {
            return mean;
        }
    }

}
