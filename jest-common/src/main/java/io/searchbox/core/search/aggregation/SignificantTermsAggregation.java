package io.searchbox.core.search.aggregation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static io.searchbox.core.search.aggregation.AggregationField.BG_COUNT;
import static io.searchbox.core.search.aggregation.AggregationField.BUCKETS;
import static io.searchbox.core.search.aggregation.AggregationField.DOC_COUNT;
import static io.searchbox.core.search.aggregation.AggregationField.KEY;
import static io.searchbox.core.search.aggregation.AggregationField.SCORE;

/**
 * @author cfstout
 */
public class SignificantTermsAggregation extends Aggregation<SignificantTermsAggregation> {

    public static final String TYPE = "significant_terms";

    private Long totalCount;
    private List<SignificantTerm> significantTerms;

    public SignificantTermsAggregation(String name, JsonObject significantTermsAggregation) {
        super(name, significantTermsAggregation);
        significantTerms = new ArrayList<SignificantTerm>();
        for (JsonElement bucketv : significantTermsAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray()) {
            JsonObject bucket = bucketv.getAsJsonObject();
            SignificantTerm term = new SignificantTerm(
                    bucket.get(String.valueOf(KEY)).getAsString(),
                    bucket.get(String.valueOf(DOC_COUNT)).getAsLong(),
                    bucket.get(String.valueOf(SCORE)).getAsDouble(),
                    bucket.get(String.valueOf(BG_COUNT)).getAsLong()
            );
            significantTerms.add(term);
        }
        totalCount = significantTermsAggregation.has(String.valueOf(DOC_COUNT)) ? significantTermsAggregation.get(String.valueOf(DOC_COUNT)).getAsLong() : null;
    }

    /**
     * @return total count of documents matching foreground aggregation if found, null otherwise
     */
    public Long getTotalCount() {
        return totalCount;
    }

    public List<SignificantTerm> getSignificantTerms() {
        return significantTerms;
    }

    public class SignificantTerm {
        private String key;
        private Long count;
        private Double score;
        private Long backgroundCount;

        public SignificantTerm(String key, Long count, Double score, Long backgroundCount) {
            this.key = key;
            this.count = count;
            this.score = score;
            this.backgroundCount = backgroundCount;
        }

        public String getKey() {
            return key;
        }

        public Long getCount() {
            return count;
        }

        public Double getScore() {
            return score;
        }

        public Long getBackgroundCount() {
            return backgroundCount;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            SignificantTerm that = (SignificantTerm) o;

            if (!backgroundCount.equals(that.backgroundCount)) {
                return false;
            }
            if (!count.equals(that.count)) {
                return false;
            }
            if (!key.equals(that.key)) {
                return false;
            }
            if (!score.equals(that.score)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = key.hashCode();
            result = 31 * result + count.hashCode();
            result = 31 * result + score.hashCode();
            result = 31 * result + backgroundCount.hashCode();
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

        SignificantTermsAggregation that = (SignificantTermsAggregation) o;

        if (!significantTerms.equals(that.significantTerms)) {
            return false;
        }
        if (totalCount != null ? !totalCount.equals(that.totalCount) : that.totalCount != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = totalCount != null ? totalCount.hashCode() : 0;
        result = 31 * result + significantTerms.hashCode();
        return result;
    }
}
