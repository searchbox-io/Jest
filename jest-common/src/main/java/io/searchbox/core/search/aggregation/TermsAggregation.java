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

public class TermsAggregation extends BucketAggregation {

    public static final String TYPE = "terms";

    private Long docCountErrorUpperBound;
    private Long sumOtherDocCount;
    private List<Entry> buckets;

    public TermsAggregation(String name, JsonObject termAggregation) {
        super(name, termAggregation);
        docCountErrorUpperBound = termAggregation.get(String.valueOf(DOC_COUNT_ERROR_UPPER_BOUND)).getAsLong();
        sumOtherDocCount = termAggregation.get(String.valueOf(SUM_OTHER_DOC_COUNT)).getAsLong();
        buckets = new ArrayList<Entry>();
        for(JsonElement bucketv : termAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray()) {
            JsonObject bucket = (JsonObject) bucketv;
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
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Entry rhs = (Entry) o;
            return new EqualsBuilder()
                    .append(getKey(), rhs.getKey())
                    .append(getCount(), rhs.getCount())
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
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TermsAggregation rhs = (TermsAggregation) o;
        return new EqualsBuilder()
                .append(getBuckets(), rhs.getBuckets())
                .append(getDocCountErrorUpperBound(), rhs.getDocCountErrorUpperBound())
                .append(getSumOtherDocCount(), rhs.getSumOtherDocCount())
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getDocCountErrorUpperBound())
                .append(getSumOtherDocCount())
                .append(getBuckets())
                .toHashCode();
    }
}
