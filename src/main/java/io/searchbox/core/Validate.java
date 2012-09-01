package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class Validate extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Validate.class.getName());

    public static class Builder {
        private String index;
        private String type;
        private final Object query;

        public Builder(Object query) {
            this.query = query;
        }

        public Builder index(String val) {
            index = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Validate build() {
            return new Validate(this);
        }
    }

    private Validate(Builder builder) {
        setURI(buildURI(builder.index, builder.type, null));
        setData(builder.query);
    }

    protected String buildURI(String index, String type, String id) {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(index)) sb.append(index);

        if (StringUtils.isNotBlank(type)) sb.append("/").append(type);

        if (StringUtils.isNotBlank(id)) sb.append("/").append(id);
        sb.append("/").append("_validate/query");

        log.debug("Created URI for update action is :" + sb.toString());
        return sb.toString();
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public String getName() {
        return "VALIDATE";
    }

    @Override
    public String getPathToResult() {
        return "valid";
    }

    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
