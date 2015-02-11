package io.searchbox.core.search.aggregation;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

import static io.searchbox.core.search.aggregation.AggregationField.BUCKETS;
import static io.searchbox.core.search.aggregation.AggregationField.DOC_COUNT;
import static io.searchbox.core.search.aggregation.AggregationField.DOC_COUNT_ERROR_UPPER_BOUND;
import static io.searchbox.core.search.aggregation.AggregationField.KEY;
import static io.searchbox.core.search.aggregation.AggregationField.SUM_OTHER_DOC_COUNT;

/**
 * @author cfstout
 */

public class TermsAggregation extends Aggregation<TermsAggregation> {

    public static final String TYPE = "terms";

    private Long docCountErrorUpperBound;
    private Long sumOtherDocCount;
    private List<Bucket> buckets;

    public TermsAggregation(String name, JsonObject termAggregation) {
        super(name, termAggregation);
        docCountErrorUpperBound = termAggregation.get(String.valueOf(DOC_COUNT_ERROR_UPPER_BOUND)).getAsLong();
        sumOtherDocCount = termAggregation.get(String.valueOf(SUM_OTHER_DOC_COUNT)).getAsLong();
        buckets = new ArrayList<Bucket>();
        for(JsonElement bucketv : termAggregation.get(String.valueOf(BUCKETS)).getAsJsonArray()) {
            JsonObject bucket = (JsonObject) bucketv;
            Bucket entry = new Bucket(bucket.get(String.valueOf(KEY)).getAsString(), bucket.get(String.valueOf(DOC_COUNT)).getAsInt());
            buckets.add(entry);
        }
    }

    public Long getDocCountErrorUpperBound() {
        return docCountErrorUpperBound;
    }

    public Long getSumOtherDocCount() {
        return sumOtherDocCount;
    }

    public List<Bucket> getBuckets() {
        return buckets;
    }

    public class Bucket {
        private String name;
        private Integer count;

        public Bucket(String name, Integer count) {
            this.name = name;
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public Integer getCount() {
            return count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Bucket bucket = (Bucket) o;

            if (!count.equals(bucket.count)) {
                return false;
            }
            if (!name.equals(bucket.name)) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
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

        TermsAggregation that = (TermsAggregation) o;

        if (!buckets.equals(that.buckets)) {
            return false;
        }
        if (!docCountErrorUpperBound.equals(that.docCountErrorUpperBound)) {
            return false;
        }
        if (!sumOtherDocCount.equals(that.sumOtherDocCount)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = docCountErrorUpperBound.hashCode();
        result = 31 * result + sumOtherDocCount.hashCode();
        result = 31 * result + buckets.hashCode();
        return result;
    }
}
