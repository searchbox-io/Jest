package io.searchbox;

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
import java.util.HashMap;
import java.util.LinkedHashSet;
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
    private final ConcurrentMap<String, Object> parameterMap = new ConcurrentHashMap<String, Object>();
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

    public Object getParameter(String parameter) {
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

    public String createCommaSeparatedItemList(LinkedHashSet<String> set) {
        StringBuilder sb = new StringBuilder();
        String tmp = "";
        for (String index : set) {
            sb.append(tmp);
            sb.append(index);
            tmp = ",";
        }
        return sb.toString();
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
        for (Map.Entry<?, ?> entry : parameterMap.entrySet()) {
            if (queryString.length() == 0) {
                queryString.append("?");
            } else {
                queryString.append("&");
            }
            queryString.append(entry.getKey().toString())
                    .append("=")
                    .append(entry.getValue().toString());
        }
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
        protected Map<String, Object> parameters = new HashMap<String, Object>();
        protected Map<String, Object> headers = new HashMap<String, Object>();

        public K setParameter(String key, Object value) {
            parameters.put(key, value);
            return (K) this;
        }

        public K setParameter(Map<String, Object> parameters) {
            this.parameters.putAll(parameters);
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
