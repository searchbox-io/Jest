package io.searchbox.core.search.aggregation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.LinkedList;
import java.util.List;

import static io.searchbox.core.search.aggregation.AggregationField.*;

/**
 * @author cfstout
 */
public class SignificantTermsAggregation extends BucketAggregation {

    public static final String TYPE = "significant_terms";

    private Long totalCount;
    private List<SignificantTerm> significantTerms = new LinkedList<SignificantTerm>();

    public SignificantTermsAggregation(String name, JsonObject significantTermsAggregation) {
        super(name, significantTermsAggregation);

        if (significantTermsAggregation.has(String.valueOf(BUCKETS)) && significantTermsAggregation.get(String.valueOf(BUCKETS)).isJsonArray()) {
            parseBuckets(significantTermsAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray());
        }
        if (significantTermsAggregation.has(String.valueOf(DOC_COUNT))) {
            totalCount = significantTermsAggregation.get(String.valueOf(DOC_COUNT)).getAsLong();
        }
    }

    private void parseBuckets(JsonArray bucketsSource) {
        for (JsonElement bucketv : bucketsSource) {
            JsonObject bucket = bucketv.getAsJsonObject();
            SignificantTerm term = new SignificantTerm(
                    bucket,
                    bucket.get(String.valueOf(KEY)).getAsString(),
                    bucket.get(String.valueOf(DOC_COUNT)).getAsLong(),
                    bucket.get(String.valueOf(SCORE)).getAsDouble(),
                    bucket.get(String.valueOf(BG_COUNT)).getAsLong()
            );
            significantTerms.add(term);
        }
    }

    /**
     * @return total count of documents matching foreground aggregation if found, null otherwise
     */
    public Long getTotalCount() {
        return totalCount;
    }

    public List<SignificantTerm> getBuckets() {
        return significantTerms;
    }

    public class SignificantTerm extends Bucket {
        private String key;
        private Double score;
        private Long backgroundCount;

        public SignificantTerm(JsonObject bucket, String key, Long count, Double score, Long backgroundCount) {
            super(bucket, count);
            this.key = key;
            this.score = score;
            this.backgroundCount = backgroundCount;
        }

        public String getKey() {
            return key;
        }

        public Double getScore() {
            return score;
        }

        public Long getBackgroundCount() {
            return backgroundCount;
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

            SignificantTerm rhs = (SignificantTerm) obj;
            return new EqualsBuilder()
                    .appendSuper(super.equals(obj))
                    .append(key, rhs.key)
                    .append(score, rhs.score)
                    .append(backgroundCount, rhs.backgroundCount)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .appendSuper(super.hashCode())
                    .append(backgroundCount)
                    .append(key)
                    .append(score)
                    .toHashCode();
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

        SignificantTermsAggregation rhs = (SignificantTermsAggregation) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(totalCount, rhs.totalCount)
                .append(significantTerms, rhs.significantTerms)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(totalCount)
                .append(significantTerms)
                .toHashCode();
    }
}
