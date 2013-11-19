package io.searchbox.core.search.facet;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author ferhat
 */
public class TermsStatsFacet extends Facet {

    public static final String TYPE = "terms_stats";

    private Long missing;
    private List<TermsStats> termsStatsList;

    public TermsStatsFacet(String name, JsonObject termsStatsFacet) {
        this.name = name;
        this.missing = ( termsStatsFacet.get("missing")).getAsLong();
        termsStatsList = new ArrayList<TermsStats>();
        for (JsonElement termv : termsStatsFacet.get("terms").getAsJsonArray()) {
          JsonObject term = (JsonObject) termv;
          TermsStats termsStats = new TermsStats(term.get("term").getAsString(), ( term.get("count")).getAsLong(),
                    term.get("total_count").getAsLong(),  term.get("total").getAsDouble(),
                     term.get("mean").getAsDouble(),  term.get("min").getAsDouble(),  term.get("max").getAsDouble());
            termsStatsList.add(termsStats);
        }
    }

    public Long getMissing() {
        return missing;
    }

    public List<TermsStats> getTermsStatsList() {
        return termsStatsList;
    }

    public class TermsStats {

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
