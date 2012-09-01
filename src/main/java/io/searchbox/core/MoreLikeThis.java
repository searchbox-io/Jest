package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class MoreLikeThis extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(MoreLikeThis.class.getName());

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
        setURI(buildURI(builder.index, builder.type, builder.id));
        if (builder.query != null) {
            setData(builder.query);
            setRestMethodName("POST");
        } else {
            setRestMethodName("GET");
        }

    }

    protected String buildURI(String index, String type, String id) {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI(index, type, id))
                .append("/")
                .append("_mlt");
        log.debug("Created URI for update action is :" + sb.toString());
        return sb.toString();
    }


    @Override
    public String getName() {
        return "MORELIKETHIS";
    }


    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
