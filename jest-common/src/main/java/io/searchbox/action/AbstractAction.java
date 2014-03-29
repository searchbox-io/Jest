package io.searchbox.action;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.searchbox.annotations.JestId;
import io.searchbox.client.JestResult;
import io.searchbox.core.Doc;
import io.searchbox.params.Parameters;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public abstract class AbstractAction<T extends JestResult> implements Action<T> {

    final static Logger log = LoggerFactory.getLogger(AbstractAction.class);
    public static String CHARSET = "utf-8";
    private final ConcurrentMap<String, Object> headerMap = new ConcurrentHashMap<String, Object>();
    private final Multimap<String, Object> parameterMap = HashMultimap.create();
    protected String indexName;
    protected String typeName;
    protected String nodes;
    private String URI;
    private boolean isBulkOperation;
    private String pathToResult;
    private boolean cleanApi = false;

    public AbstractAction() {
    }

    @SuppressWarnings("unchecked")
    public AbstractAction(Builder builder) {
        parameterMap.putAll(builder.parameters);
        headerMap.putAll(builder.headers);

        if (builder instanceof AbstractMultiIndexActionBuilder) {
            indexName = ((AbstractMultiIndexActionBuilder) builder).getJoinedIndices();
            if (builder instanceof AbstractMultiTypeActionBuilder) {
                indexName = ((AbstractMultiTypeActionBuilder) builder).getJoinedIndices();
                typeName = ((AbstractMultiTypeActionBuilder) builder).getJoinedTypes();
            }
        } else if (builder instanceof AbstractMultiINodeActionBuilder) {
            nodes = ((AbstractMultiINodeActionBuilder) builder).getJoinedNodes();
        }
    }

    protected T createNewElasticSearchResult(T result, String json, int statusCode, String reasonPhrase, Gson gson) {
        JsonObject jsonMap = convertJsonStringToMapObject(json);
        result.setJsonString(json);
        result.setJsonObject(jsonMap);
        result.setPathToResult(getPathToResult());

        if ((statusCode / 100) == 2) {
            result.setSucceeded(true);
            log.debug("Request and operation succeeded");
        } else {
            result.setSucceeded(false);
            // provide the generic HTTP status code error, if one hasn't already come in via the JSON response...
            // eg.
            //  IndicesExist will return 404 (with no content at all) for a missing index, but:
            //  Update will return 404 (with an error message for DocumentMissingException)
            if (result.getErrorMessage() == null) {
                result.setErrorMessage(statusCode + " " + (reasonPhrase == null ? "null" : reasonPhrase));
            }
            log.debug("Response is failed");
        }
        return result;
    }

    protected static JsonObject convertJsonStringToMapObject(String jsonTxt) {
        if (jsonTxt != null && !jsonTxt.trim().isEmpty()) {
            try {
                return new JsonParser().parse(jsonTxt).getAsJsonObject();
            } catch (Exception e) {
                log.error("An exception occurred while converting json string to map object");
            }
        }
        return new JsonObject();
    }

    public static String getIdFromSource(Object source) {
        if (source == null) return null;
        Field[] fields = source.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (field.isAnnotationPresent(JestId.class)) {
                try {
                    field.setAccessible(true);
                    Object name = field.get(source);
                    return name == null ? null : name.toString();
                } catch (IllegalAccessException e) {
                    log.error("Unhandled exception occurred while getting annotated id from source");
                }
            }
        }
        return null;
    }

    public Collection<Object> getParameter(String parameter) {
        return parameterMap.get(parameter);
    }

    public Object getHeader(String header) {
        return headerMap.get(header);
    }

    @Override
    public Map<String, Object> getHeaders() {
        return headerMap;
    }

    @Override
    public String getURI() {
        String finalUri = URI;
        if (parameterMap.size() > 0) {
            try {
                finalUri += buildQueryString();
            } catch (UnsupportedEncodingException e) {
                // unless CHARSET is overridden with a wrong value in a subclass,
                // this exception won't be thrown.
                log.error("Error occurred while adding parameters to uri.", e);
            }
        }
        return finalUri;
    }

    protected void setURI(String URI) {
        this.URI = URI;
    }

    protected void setCleanApi(Boolean cleanApi) {
        this.cleanApi = true;
    }

    @Override
    public Object getData(Gson gson) {
        return null;
    }

    @Override
    public String getPathToResult() {
        return pathToResult;
    }

    protected void setPathToResult(String pathToResult) {
        this.pathToResult = pathToResult;
    }

    protected String buildURI() {
        StringBuilder sb = new StringBuilder();

        try {
            if (StringUtils.isNotBlank(indexName)) {
                sb.append(URLEncoder.encode(indexName, CHARSET));

                if (StringUtils.isNotBlank(typeName)) {
                    sb.append("/").append(URLEncoder.encode(typeName, CHARSET));
                }
            }
        } catch (UnsupportedEncodingException e) {
            // unless CHARSET is overridden with a wrong value in a subclass,
            // this exception won't be thrown.
            log.error("Error occurred while adding index/type to uri", e);
        }

        String uri = sb.toString();
        return uri;
    }

    protected String buildQueryString() throws UnsupportedEncodingException {
        StringBuilder queryString = new StringBuilder();
        Multiset<String> paramKeys = parameterMap.keys();

        if (cleanApi) {
            return "/" + StringUtils.join(paramKeys, ",");
        } else {
            queryString.append("?");
            for (String key : paramKeys) {
                Collection<Object> values = parameterMap.get(key);
                for (Object value : values) {
                    queryString.append(URLEncoder.encode(key, CHARSET))
                            .append("=")
                            .append(URLEncoder.encode(value.toString(), CHARSET))
                            .append("&");
                }
            }

            // if there are any params  ->  deletes the final ampersand
            // if no params             ->  deletes the question mark
            queryString.deleteCharAt(queryString.length() - 1);

            return queryString.toString();
        }

    }

    protected boolean isValid(String index, String type, String id) {
        return StringUtils.isNotBlank(index) && StringUtils.isNotBlank(type) && StringUtils.isNotBlank(id);
    }

    protected boolean isValid(Doc doc) {
        return isValid(doc.getIndex(), doc.getType(), doc.getId());
    }

    public boolean isBulkOperation() {
        return isBulkOperation;
    }

    protected void setBulkOperation(boolean bulkOperation) {
        isBulkOperation = bulkOperation;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("uri", getURI())
                .append("method", getRestMethodName())
                .toString();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(getURI())
                .append(getRestMethodName())
                .append(getHeaders())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        AbstractAction rhs = (AbstractAction) obj;
        return new EqualsBuilder()
                .append(getURI(), rhs.getURI())
                .append(getRestMethodName(), rhs.getRestMethodName())
                .append(getHeaders(), rhs.getHeaders())
                .isEquals();
    }

    public abstract String getRestMethodName();

    @SuppressWarnings("unchecked")
    protected static abstract class Builder<T extends Action, K> {
        protected Multimap<String, Object> parameters = HashMultimap.<String, Object>create();
        protected Map<String, Object> headers = new HashMap<String, Object>();

        public K setParameter(String key, Object value) {
            parameters.put(key, value);
            return (K) this;
        }

        @Deprecated
        public K setParameter(Map<String, Object> parameters) {
            for (Map.Entry<String, Object> entry : parameters.entrySet()) {
                this.parameters.put(entry.getKey(), entry.getValue());
            }
            return (K) this;
        }

        public K setHeader(String key, Object value) {
            headers.put(key, value);
            return (K) this;
        }

        public K setHeader(Map<String, Object> headers) {
            this.headers.putAll(headers);
            return (K) this;
        }

        public K refresh(boolean refresh) {
            return setParameter(Parameters.REFRESH, refresh);
        }

        /**
         * All REST APIs accept the case parameter.
         * When set to camelCase, all field names in the result will be returned
         * in camel casing, otherwise, underscore casing will be used. Note,
         * this does not apply to the source document indexed.
         */
        public K resultCasing(String caseParam) {
            setParameter(Parameters.RESULT_CASING, caseParam);
            return (K) this;
        }

        abstract public T build();
    }
}
