package io.searchbox.core.search.facet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ferhat
 */
public class RangeFacet {
    private String name;
    private List<Range> ranges;

    public RangeFacet(String name, Map rangeFacet) {
        this.name = name;
        ranges = new ArrayList<Range>();
        for (Map term : (List<Map>) rangeFacet.get("ranges")) {
            Range range = new Range((Double) term.get("from"), (Double) term.get("to"), ((Double) term.get("count")).longValue(),
                    ((Double) term.get("total_count")).longValue(), (Double) term.get("total"), (Double) term.get("min"),
                    (Double) term.get("max"), (Double) term.get("mean"));
            ranges.add(range);
        }
    }

    public String getName() {
        return name;
    }

    public List<Range> getRanges() {
        return ranges;
    }

    class Range {
        private Double from = Double.NEGATIVE_INFINITY;
        private Double to = Double.POSITIVE_INFINITY;
        private Long count;
        private Long totalCount;
        private Double total;
        private Double min = Double.POSITIVE_INFINITY;
        private Double max = Double.NEGATIVE_INFINITY;
        private Double mean;

        public Range(Double from, Double to, Long count, Long totalCount,
                     Double total, Double min, Double max, Double mean) {
            this.count = count;
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

        public Long getCount() {
            return count;
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
