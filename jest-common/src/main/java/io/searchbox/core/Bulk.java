package io.searchbox.core;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.BulkableAction;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;
import io.searchbox.params.Parameters;
import io.searchbox.strings.StringUtils;
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
public class Bulk extends AbstractAction<BulkResult> {

    final static Logger log = LoggerFactory.getLogger(Bulk.class);
    protected Collection<BulkableAction> bulkableActions;

    protected Bulk(Builder builder) {
        super(builder);
        indexName = builder.defaultIndex;
        typeName = builder.defaultType;
        bulkableActions = builder.actions;
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
    public String getData(Gson gson) {
        /*
        { "index" : { "_index" : "test", "_type" : "type1", "_id" : "1" } }
        { "field1" : "value1" }
        { "delete" : { "_index" : "test", "_type" : "type1", "_id" : "2" } }
         */
        StringBuilder sb = new StringBuilder();
        for (BulkableAction action : bulkableActions) {
            // write out the action-meta-data line
            // e.g.: { "index" : { "_index" : "test", "_type" : "type1", "_id" : "1" } }
            Map<String, Map<String, String>> opMap = new LinkedHashMap<String, Map<String, String>>(1);

            Map<String, String> opDetails = new LinkedHashMap<String, String>(3);
            if (StringUtils.isNotBlank(action.getId())) {
                opDetails.put("_id", action.getId());
            }
            if (StringUtils.isNotBlank(action.getIndex())) {
                opDetails.put("_index", action.getIndex());
            }
            if (StringUtils.isNotBlank(action.getType())) {
                opDetails.put("_type", action.getType());
            }

            for (String parameter : Parameters.ACCEPTED_IN_BULK) {
                try {
                    Collection<Object> values = action.getParameter(parameter);
                    if (values != null) {
                        if (values.size() == 1) {
                            opDetails.put(parameter, values.iterator().next().toString());
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
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_bulk";
    }

    @Override
    public BulkResult createNewElasticSearchResult(String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        return createNewElasticSearchResult(new BulkResult(gson), responseBody, statusCode, reasonPhrase, gson);
    }

    @Override
    protected BulkResult createNewElasticSearchResult(BulkResult result, String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        JsonObject jsonMap = parseResponseBody(responseBody);
        result.setResponseCode(statusCode);
        result.setJsonString(responseBody);
        result.setJsonObject(jsonMap);
        result.setPathToResult(getPathToResult());

        if (isHttpSuccessful(statusCode)) {
            if(jsonMap.has("errors") && jsonMap.get("errors").getAsBoolean())
            {
                result.setSucceeded(false);
                result.setErrorMessage("One or more of the items in the Bulk request failed, check BulkResult.getItems() for more information.");
                log.debug("Bulk operation failed due to one or more failed actions within the Bulk request");
            } else {
                result.setSucceeded(true);
                log.debug("Bulk operation was successfull");
            }
        } else {
            result.setSucceeded(false);
            // provide the generic HTTP status code error, if one hasn't already come in via the JSON response...
            // eg.
            //  IndicesExist will return 404 (with no content at all) for a missing index, but:
            //  Update will return 404 (with an error message for DocumentMissingException)
            if (result.getErrorMessage() == null) {
                result.setErrorMessage(statusCode + " " + (reasonPhrase == null ? "null" : reasonPhrase));
            }
            log.debug("Bulk operation failed with an HTTP error");
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj) && Objects.equals(bulkableActions, ((Bulk) obj).bulkableActions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), this.bulkableActions);
    }

    public static class Builder extends GenericResultAbstractAction.Builder<Bulk, Builder> {
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
