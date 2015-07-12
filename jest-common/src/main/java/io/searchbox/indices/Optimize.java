package io.searchbox.indices;

import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Optimize extends GenericResultAbstractAction {

    protected Optimize(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_optimize";
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
         * Should a refresh be performed after the optimize. Defaults to true.
         */
        public Builder refresh(boolean refresh) {
            return setParameter("refresh", refresh);
        }

        /**
         * Should a flush be performed after the optimize. Defaults to true.
         */
        public Builder flush(boolean flush) {
            return setParameter("flush", flush);
        }

        /**
         * Should the request wait for the merge to end. Defaults to true. Note, a merge can
         * potentially be a very heavy operation, so it might make sense to run it set to false.
         */
        public Builder waitForMerge(boolean waitForMerge) {
            return setParameter("wait_for_merge", waitForMerge);
        }

        @Override
        public Optimize build() {
            return new Optimize(this);
        }
    }
}