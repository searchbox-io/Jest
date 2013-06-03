package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiIndexActionBuilder;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Optimize extends AbstractAction {

    private Optimize() {
    }

    private Optimize(Builder builder) {
        this.indexName = builder.getJoinedIndices();
        this.addParameter(builder.parameters);
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_optimize");
        return sb.toString();
    }

    public static class Builder extends AbstractMultiIndexActionBuilder<Optimize, Builder> {
        private Map<String, Object> parameters = new HashMap<String, Object>();

        /**
         * The number of segments to optimize to. To fully optimize the index, set it to 1.
         * Defaults to simply checking if a merge needs to execute, and if so, executes it.
         */
        public Builder maxNumSegments(Number maxNumSegments) {
            parameters.put("max_num_segments", maxNumSegments);
            return this;
        }

        /**
         * Should the optimize process only expunge segments with deletes in it. In Lucene,
         * a document is not deleted from a segment, just marked as deleted. During a merge
         * process of segments, a new segment is created that does not have those deletes.
         * This flag allow to only merge segments that have deletes. Defaults to false.
         */
        public Builder onlyExpungeDeletes(boolean onlyExpungeDeletes) {
            parameters.put("only_expunge_deletes", onlyExpungeDeletes);
            return this;
        }

        /**
         * Should a refresh be performed after the optimize. Defaults to true.
         */
        public Builder refresh(boolean refresh) {
            parameters.put("refresh", refresh);
            return this;
        }

        /**
         * Should a flush be performed after the optimize. Defaults to true.
         */
        public Builder flush(boolean flush) {
            parameters.put("flush", flush);
            return this;
        }

        /**
         * Should the request wait for the merge to end. Defaults to true. Note, a merge can
         * potentially be a very heavy operation, so it might make sense to run it set to false.
         */
        public Builder waitForMerge(boolean waitForMerge) {
            parameters.put("wait_for_merge", waitForMerge);
            return this;
        }

        @Override
        public Optimize build() {
            return new Optimize(this);
        }
    }
}