package io.searchbox.core.search.facet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ferhat
 */
public class TermsStatsFacet extends Facet {

    public static final String TYPE = "terms_stats";

    private Long missing;
    private List<TermsStats> termsStatsList;

    public TermsStatsFacet(String name, Map termsStatsFacet) {
        this.name = name;
        this.missing = ((Double) termsStatsFacet.get("missing")).longValue();
        termsStatsList = new ArrayList<TermsStats>();
        for (Map term : (List<Map>) termsStatsFacet.get("terms")) {
            TermsStats termsStats = new TermsStats(term.get("term").toString(), ((Double) term.get("count")).longValue(),
                    ((Double) term.get("total_count")).longValue(), (Double) term.get("total"),
                    (Double) term.get("mean"), (Double) term.get("min"), (Double) term.get("max"));
            termsStatsList.add(termsStats);
        }
    }

    public Long getMissing() {
        return missing;
    }

    public List<TermsStats> getTermsStatsList() {
        return termsStatsList;
    }

    class TermsStats {

        private String term;
        private Long count;
        private Long totalCount;
        private Double total;
        private Double mean;
        private Double min;
        private Double max;

        public TermsStats(String term, Long count, Long totalCount,
                          Double total, Double mean, Double min, Double max) {
            this.term = term;
            this.count = count;
            this.totalCount = totalCount;
            this.total = total;
            this.mean = mean;
            this.min = min;

            this.max = max;
        }

        public String getTerm() {
            return term;
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

        public Double getMean() {
            return mean;
        }

        public Double getMin() {
            return min;
        }

        public Double getMax() {
            return max;
        }
    }
}
