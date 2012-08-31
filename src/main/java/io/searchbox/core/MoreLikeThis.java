package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;

import java.io.IOException;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class MoreLikeThis extends AbstractAction implements Action {

    private Object query;

    public static class Builder {
        private final String id;
        private String index;
        private String type;
        private Object query;

        public Builder(String id) {
            this.id = id;
        }

        public Builder index(String val) {
            index = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder query(Object val) {
            query = val;
            return this;
        }

        public MoreLikeThis build() {
            return new MoreLikeThis(this);
        }

    }

    private MoreLikeThis(Builder builder) {
        indexName = builder.index;
        typeName = builder.type;
        id = builder.id;
        setData(builder.query);
    }

    @Override
    public String getName() {
        return "MORELIKETHIS";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
