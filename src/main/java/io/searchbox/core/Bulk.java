package io.searchbox.core;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import io.searchbox.AbstractAction;
import io.searchbox.BulkableAction;
import io.searchbox.params.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * The bulk API makes it possible to perform many index/delete operations in a
 * single API call. This can greatly increase the indexing speed.
 * <br/>
 * <br/>
 * Make sure that your source data (provided in Action instances) <b> does NOT
 * have unescaped line-breaks</b> (e.g.: <code>&quot;\n&quot;</code> or <code>&quot;\r\n&quot;</code>)
 * as doing so will break up the elasticsearch's bulk api format and bulk operation
 * will fail.
 *
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Bulk extends AbstractAction {

    final static Logger log = LoggerFactory.getLogger(AbstractAction.class);
    protected Collection<BulkableAction> bulkableActions;

    public Bulk(Builder builder) {
        super(builder);
        indexName = builder.defaultIndex;
        typeName = builder.defaultType;
        bulkableActions = builder.actions;

        setURI(buildURI());
    }

    private Object getJson(Gson gson, Object source) {
        if (source instanceof String) {
            return source;
        } else {
            return gson.toJson(source);
        }
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    public Object getData(Gson gson) {
        /*
        { "index" : { "_index" : "test", "_type" : "type1", "_id" : "1" } }
        { "field1" : "value1" }
        { "delete" : { "_index" : "test", "_type" : "type1", "_id" : "2" } }
         */
        StringBuilder sb = new StringBuilder();
        for (BulkableAction action : bulkableActions) {
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
            Object source = action.getData(gson);
            if (source != null) {
                sb.append(getJson(gson, source));
                sb.append("\n");
            }

        }
        return sb.toString();
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
