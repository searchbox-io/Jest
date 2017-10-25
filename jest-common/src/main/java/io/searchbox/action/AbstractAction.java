package io.searchbox.action;

import com.google.common.base.Joiner;
import com.google.common.base.MoreObjects;
import com.google.common.collect.LinkedHashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import io.searchbox.annotations.JestId;
import io.searchbox.client.JestResult;
import io.searchbox.client.config.ElasticsearchVersion;
import io.searchbox.params.Parameters;
import io.searchbox.strings.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public abstract class AbstractAction<T extends JestResult> implements Action<T> {

    public static String CHARSET = "utf-8";

    protected final static Logger log = LoggerFactory.getLogger(AbstractAction.class);
    protected String indexName;
    protected String typeName;
    protected String nodes;
    protected Object payload;

    private final ConcurrentMap<String, Object> headerMap = new ConcurrentHashMap<String, Object>();
    private final Multimap<String, Object> parameterMap = LinkedHashMultimap.create();
    private final Set<String> cleanApiParameters = new LinkedHashSet<String>();

    public AbstractAction() {
    }

    @SuppressWarnings("unchecked")
    public AbstractAction(Builder builder) {
        parameterMap.putAll(builder.parameters);
        headerMap.putAll(builder.headers);
        cleanApiParameters.addAll(builder.cleanApiParameters);

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

    protected T createNewElasticSearchResult(T result, String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        JsonObject jsonMap = parseResponseBody(responseBody);
        result.setResponseCode(statusCode);
        result.setJsonString(responseBody);
        result.setJsonObject(jsonMap);
        result.setPathToResult(getPathToResult());

        if (isHttpSuccessful(statusCode)) {
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
            log.debug("Response is failed. errorMessage is " + result.getErrorMessage());
        }
        return result;
    }

    protected boolean isHttpSuccessful(int httpCode) {
        return (httpCode / 100) == 2;
    }

    protected JsonObject parseResponseBody(String responseBody) {
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return new JsonObject();
        }

        JsonElement parsed = new JsonParser().parse(responseBody);
        if (parsed.isJsonObject()) {
            return parsed.getAsJsonObject();
        } else {
            throw new JsonSyntaxException("Response did not contain a JSON Object");
        }
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
                    log.error("Unhandled exception occurred while getting annotated id from source", e);
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
    public String getURI(ElasticsearchVersion elasticsearchVersion) {
        String finalUri = buildURI(elasticsearchVersion);
        if (!parameterMap.isEmpty() || !cleanApiParameters.isEmpty()) {
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

    @Override
    public String getData(Gson gson) {
        if (payload == null) {
            return null;
        } else if (payload instanceof String) {
            return (String) payload;
        } else {
            return gson.toJson(payload);
        }
    }

    @Override
    public String getPathToResult() {
        return null;
    }

    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        StringBuilder sb = new StringBuilder();

        try {
            if (StringUtils.isNotBlank(indexName)) {
                sb.append(URLEncoder.encode(indexName, CHARSET));

                String commandExtension = getURLCommandExtension(elasticsearchVersion);

                if (StringUtils.isNotBlank(commandExtension)) {
                    sb.append("/").append(URLEncoder.encode(commandExtension, CHARSET));
                }

                if (StringUtils.isNotBlank(typeName)) {
                    sb.append("/").append(URLEncoder.encode(typeName, CHARSET));
                }
            }
        } catch (UnsupportedEncodingException e) {
            // unless CHARSET is overridden with a wrong value in a subclass,
            // this exception won't be thrown.
            log.error("Error occurred while adding index/type to uri", e);
        }

        return sb.toString();
    }

    protected String getURLCommandExtension(ElasticsearchVersion elasticsearchVersion) {
        return null;
    }

    protected String buildQueryString() throws UnsupportedEncodingException {
        StringBuilder queryString = new StringBuilder();

        if (!cleanApiParameters.isEmpty()) {
            queryString.append("/").append(Joiner.on(',').join(cleanApiParameters));
        }

        queryString.append("?");
        for (Map.Entry<String, Object> entry : parameterMap.entries()) {
            queryString.append(URLEncoder.encode(entry.getKey(), CHARSET))
                    .append("=")
                    .append(URLEncoder.encode(entry.getValue().toString(), CHARSET))
                    .append("&");
        }
        // if there are any params  ->  deletes the final ampersand
        // if no params             ->  deletes the question mark
        queryString.deleteCharAt(queryString.length() - 1);

        return queryString.toString();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("uri", getURI(ElasticsearchVersion.UNKNOWN))
                .add("method", getRestMethodName())
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getURI(ElasticsearchVersion.UNKNOWN), getRestMethodName(), getHeaders(), payload);
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
        return Objects.equals(getURI(ElasticsearchVersion.UNKNOWN), rhs.getURI(ElasticsearchVersion.UNKNOWN))
                && Objects.equals(getRestMethodName(), rhs.getRestMethodName())
                && Objects.equals(getHeaders(), rhs.getHeaders())
                && Objects.equals(payload, rhs.payload);
    }

    public abstract String getRestMethodName();

    @SuppressWarnings("unchecked")
    protected static abstract class Builder<T extends Action, K> {
        protected Multimap<String, Object> parameters = LinkedHashMultimap.<String, Object>create();
        protected Map<String, Object> headers = new LinkedHashMap<String, Object>();
        protected Set<String> cleanApiParameters = new LinkedHashSet<String>();

        public K toggleApiParameter(String key, boolean enable) {
            if (enable) {
                addCleanApiParameter(key);
            } else {
                removeCleanApiParameter(key);
            }

            return (K) this;
        }

        public K removeCleanApiParameter(String key) {
            cleanApiParameters.remove(key);
            return (K) this;
        }

        public K addCleanApiParameter(String key) {
            cleanApiParameters.add(key);
            return (K) this;
        }

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
