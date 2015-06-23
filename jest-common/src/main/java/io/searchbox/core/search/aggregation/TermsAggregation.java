package io.searchbox.core.search.aggregation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.LinkedList;
import java.util.List;

import static io.searchbox.core.search.aggregation.AggregationField.BUCKETS;
import static io.searchbox.core.search.aggregation.AggregationField.DOC_COUNT;
import static io.searchbox.core.search.aggregation.AggregationField.DOC_COUNT_ERROR_UPPER_BOUND;
import static io.searchbox.core.search.aggregation.AggregationField.KEY;
import static io.searchbox.core.search.aggregation.AggregationField.SUM_OTHER_DOC_COUNT;

/**
 * @author cfstout
 */

public class TermsAggregation extends BucketAggregation {

    public static final String TYPE = "terms";

    private Long docCountErrorUpperBound;
    private Long sumOtherDocCount;
    private List<Entry> buckets = new LinkedList<Entry>();

    public TermsAggregation(String name, JsonObject termAggregation) {
        super(name, termAggregation);
        if (termAggregation.has(String.valueOf(DOC_COUNT_ERROR_UPPER_BOUND))) {
            docCountErrorUpperBound = termAggregation.get(String.valueOf(DOC_COUNT_ERROR_UPPER_BOUND)).getAsLong();
        }
        if (termAggregation.has(String.valueOf(SUM_OTHER_DOC_COUNT))) {
            sumOtherDocCount = termAggregation.get(String.valueOf(SUM_OTHER_DOC_COUNT)).getAsLong();
        }

        if (termAggregation.has(String.valueOf(BUCKETS)) && termAggregation.get(String.valueOf(BUCKETS)).isJsonArray()) {
            parseBuckets(termAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray());
        }
    }

    private void parseBuckets(JsonArray bucketsSource) {
        for(JsonElement bucketElement : bucketsSource) {
            JsonObject bucket = (JsonObject) bucketElement;
            Entry entry = new Entry(bucket, bucket.get(String.valueOf(KEY)).getAsString(), bucket.get(String.valueOf(DOC_COUNT)).getAsLong());
            buckets.add(entry);
        }
    }

    public Long getDocCountErrorUpperBound() {
        return docCountErrorUpperBound;
    }

    public Long getSumOtherDocCount() {
        return sumOtherDocCount;
    }

    public List<Entry> getBuckets() {
        return buckets;
    }

    public class Entry extends Bucket {
        private String key;

        public Entry(JsonObject bucket, String key, Long count) {
            super(bucket, count);
            this.key = key;
        }

        public String getKey() {
            return key;
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

            Entry rhs = (Entry) obj;
            return new EqualsBuilder()
                    .appendSuper(super.equals(obj))
                    .append(key, rhs.key)
                    .isEquals();
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(getCount())
                    .append(getKey())
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

        TermsAggregation rhs = (TermsAggregation) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(buckets, rhs.buckets)
                .append(docCountErrorUpperBound, rhs.docCountErrorUpperBound)
                .append(sumOtherDocCount, rhs.sumOtherDocCount)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(docCountErrorUpperBound)
                .append(sumOtherDocCount)
                .append(buckets)
                .toHashCode();
    }
}
