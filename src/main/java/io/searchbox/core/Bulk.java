package io.searchbox.core;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.searchbox.AbstractAction;
import io.searchbox.BulkableAction;
import io.searchbox.params.Parameters;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Bulk extends AbstractAction {

    final static Logger log = LoggerFactory.getLogger(AbstractAction.class);
    private Gson gson = new Gson();

    public Bulk(Builder builder) {
        super(builder);
        indexName = builder.defaultIndex;
        typeName = builder.defaultType;

        setData(generateBulkPayload(builder.actions));
        setURI(buildURI());
    }

    public Gson getGson() {
        return gson;
    }

    /**
     * Make sure to always provide a non-pretty-printing Gson instance!
     * This restriction is due to the way Elasticsearch's Bulk REST API uses line separators.
     *
     * @param gson
     */
    public void setGson(Gson gson) {
        this.gson = gson;
    }

    protected Object generateBulkPayload(List<BulkableAction> actions) {
        /*
        { "index" : { "_index" : "test", "_type" : "type1", "_id" : "1" } }
        { "field1" : "value1" }
        { "delete" : { "_index" : "test", "_type" : "type1", "_id" : "2" } }
         */
        StringBuilder sb = new StringBuilder();
        for (BulkableAction action : actions) {
            // write out the action-meta-data line
            // e.g.: { "index" : { "_index" : "test", "_type" : "type1", "_id" : "1" } }
            Map<String, Map<String, String>> opMap = new HashMap<String, Map<String, String>>(1);

            Map<String, String> opDetails = new HashMap<String, String>(3);
            if (StringUtils.isNotBlank(action.getIndex())) {
                opDetails.put("_index", action.getIndex());
            }
            if (StringUtils.isNotBlank(action.getType())) {
                opDetails.put("_type", action.getType());
            }
            if (StringUtils.isNotBlank(action.getId())) {
                opDetails.put("_id", action.getId());
            }

            for (String parameter : Parameters.ACCEPTED_IN_BULK) {
                try {
                    Collection<Object> values = action.getParameter(parameter);
                    if (values != null) {
                        if (values.size() == 1) {
                            opDetails.put("_" + parameter, values.iterator().next().toString());
                        } else if (values.size() > 1) {
                            throw new IllegalArgumentException("Expecting a single value for '" + parameter + "' parameter, you provided: " + values.size());
                        }
                    }
                } catch (NullPointerException e) {
                    log.debug("Could not retrieve '" + parameter + "' parameter from action.", e);
                }
            }

            opMap.put(action.getBulkMethodName(), opDetails);
            sb.append(gson.toJson(opMap, new TypeToken<Map<String, Map<String, String>>>() {
            }.getType()));
            sb.append("\n");

            // write out the action source/document line
            // e.g.: { "field1" : "value1" }
            Object source = action.getData();
            if (source != null) {
                sb.append(getJson(source));
                sb.append("\n");
            }

        }
        return sb.toString();
    }

    private Object getJson(Object source) {
        if (source instanceof String) {
            return StringUtils.deleteWhitespace((String) source);
        } else {
            return gson.toJson(source);
        }
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder(super.buildURI());
        sb.append("/_bulk");
        return sb.toString();
    }

    public static class Builder extends AbstractAction.Builder<Bulk, Builder> {
        private List<BulkableAction> actions = new LinkedList<BulkableAction>();
        private String defaultIndex;
        private String defaultType;

        public Builder defaultIndex(String defaultIndex) {
            this.defaultIndex = defaultIndex;
            return this;
        }

        public Builder defaultType(String defaultType) {
            this.defaultType = defaultType;
            return this;
        }

        public Builder addAction(BulkableAction action) {
            this.actions.add(action);
            return this;
        }

        public Builder addAction(Collection<? extends BulkableAction> actions) {
            this.actions.addAll(actions);
            return this;
        }

        public Bulk build() {
            return new Bulk(this);
        }
    }

}
