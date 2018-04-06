package io.searchbox.indices;

import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * Optimize API: https://www.elastic.co/guide/en/elasticsearch/reference/2.4/indices-optimize.html
 *
 * Caution: this API has been deprecated in ES 2.1. With later versions, prefer {@link ForceMerge}.
 *
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Optimize extends GenericResultAbstractAction {

    protected Optimize(Builder builder) {
        super(builder);
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_optimize";
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<Optimize, Builder> {

        /**
         * The number of segments to optimize to. To fully optimize the index, set it to 1.
         * Defaults to simply checking if a merge needs to execute, and if so, executes it.
         */
        public Builder maxNumSegments(Number maxNumSegments) {
            return setParameter("max_num_segments", maxNumSegments);
        }

        /**
         * Should the optimize process only expunge segments with deletes in it. In Lucene,
         * a document is not deleted from a segment, just marked as deleted. During a merge
         * process of segments, a new segment is created that does not have those deletes.
         * This flag allow to only merge segments that have deletes. Defaults to false.
         */
        public Builder onlyExpungeDeletes(boolean onlyExpungeDeletes) {
            return setParameter("only_expunge_deletes", onlyExpungeDeletes);
        }

        /**
         * Should a flush be performed after the optimize. Defaults to true.
         */
        public Builder flush(boolean flush) {
            return setParameter("flush", flush);
        }

        @Override
        public Optimize build() {
            return new Optimize(this);
        }
    }
}