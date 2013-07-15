package io.searchbox;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multiset;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.searchbox.annotations.JestId;
import io.searchbox.core.Doc;
import io.searchbox.params.Parameters;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public abstract class AbstractAction implements Action {

    final static Logger log = LoggerFactory.getLogger(AbstractAction.class);
    private final ConcurrentMap<String, Object> headerMap = new ConcurrentHashMap<String, Object>();
    private final Multimap<String, Object> parameterMap = HashMultimap.create();
    protected String indexName;
    protected String typeName;
    private Object data;
    private String URI;
    private boolean isBulkOperation;
    private String pathToResult;

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

    public Map<String, Object> getHeaders() {
        return headerMap;
    }

    public String getURI() {
        String finalUri = URI;
        if (parameterMap.size() > 0) {
            finalUri += buildQueryString();
        }
        return finalUri;
    }

    protected void setURI(String URI) {
        this.URI = URI;
    }

    public Object getData() {
        return data;
    }

    protected void setData(Object data) {
        this.data = data;
    }

    public String getPathToResult() {
        return pathToResult;
    }

    protected void setPathToResult(String pathToResult) {
        this.pathToResult = pathToResult;
    }

    protected String buildURI() {
        StringBuilder sb = new StringBuilder();

        if (StringUtils.isNotBlank(indexName)) {
            sb.append(indexName);

            if (StringUtils.isNotBlank(typeName)) {
                sb.append("/").append(typeName);
            }
        }

        String uri = sb.toString();
        return uri;
    }

    protected String buildQueryString() {
        StringBuilder queryString = new StringBuilder();
        Multiset<String> paramKeys = parameterMap.keys();

        queryString.append("?");
        for (String key : paramKeys) {
            Collection<Object> values = parameterMap.get(key);
            for (Object value : values) {
                queryString.append(key)
                        .append("=")
                        .append(value.toString())
                        .append("&");
            }
        }

        // if there are any params  ->  deletes the final ampersand
        // if no params             ->  deletes the question mark
        queryString.deleteCharAt(queryString.length() - 1);

        return queryString.toString();
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

    @Deprecated
    @Override
    public final Boolean isOperationSucceed(@SuppressWarnings("rawtypes") Map result) {
        return isOperationSucceed(new JsonParser().parse(new Gson().toJson(result, Map.class)).getAsJsonObject());
    }

    @Override
    public Boolean isOperationSucceed(JsonObject result) {
        return true;
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

        abstract public T build();
    }
}
