package io.searchbox.indices;

import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

/**
 * Force-merge API: https://www.elastic.co/guide/en/elasticsearch/reference/2.4/indices-forcemerge.html
 *
 * Caution: this API has been introduced in ES 2.1. With earlier versions, use {@link Optimize}.
 *
 * @author Dogukan Sonmez
 * @author cihat keser
 * @author Yoann Rodiere
 */
public class ForceMerge extends GenericResultAbstractAction {

    protected ForceMerge(Builder builder) {
        super(builder);
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_forcemerge";
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<ForceMerge, Builder> {

        /**
         * The number of segments to merge to. To fully merge the index, set it to 1.
         * Defaults to simply checking if a merge needs to execute, and if so, executes it.
         */
        public Builder maxNumSegments(Number maxNumSegments) {
            return setParameter("max_num_segments", maxNumSegments);
        }

        /**
         * Should the merge process only expunge segments with deletes in it. In Lucene,
         * a document is not deleted from a segment, just marked as deleted. During a merge
         * process of segments, a new segment is created that does not have those deletes.
         * This flag allow to only merge segments that have deletes. Defaults to false.
         */
        public Builder onlyExpungeDeletes(boolean onlyExpungeDeletes) {
            return setParameter("only_expunge_deletes", onlyExpungeDeletes);
        }

        /**
         * Should a flush be performed after the forced merge. Defaults to true.
         */
        public Builder flush(boolean flush) {
            return setParameter("flush", flush);
        }

        @Override
        public ForceMerge build() {
            return new ForceMerge(this);
        }
    }
}