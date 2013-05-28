package io.searchbox;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.searchbox.annotations.JestId;
import io.searchbox.core.Doc;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
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

    private Object data = null;

    private String URI;

    private boolean isBulkOperation = false;

    public String getIndexName() {
        return indexName;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getId() {
        return id;
    }

    protected String indexName = null;

    protected String typeName = null;

    protected String id = null;

    private String pathToResult;

    private final ConcurrentMap<String, Object> parameterMap = new ConcurrentHashMap<String, Object>();

    private final ConcurrentMap<String, Object> headerMap = new ConcurrentHashMap<String, Object>();

    public void addParameter(String parameter, Object value) {
        parameterMap.put(parameter, value);
    }

    public void removeParameter(String parameter) {
        parameterMap.remove(parameter);
    }

    public boolean isParameterExist(String parameter) {
        return parameterMap.containsKey(parameter);
    }

    public Object getParameter(String parameter) {
        return parameterMap.get(parameter);
    }

    public void addHeader(String header, Object value) {
        headerMap.put(header, value);
    }

    public void removeHeader(String header) {
        headerMap.remove(header);
    }

    public boolean isHeaderExist(String header) {
        return headerMap.containsKey(header);
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

    public Object getData() {
        return data;
    }

    public String getName() {
        return null;
    }

    public String getPathToResult() {
        return pathToResult;
    }

    public String getIdFromSource(Object source) {
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
        }

        if (StringUtils.isNotBlank(typeName)) {
            sb.append("/").append(typeName);
        }

        if (StringUtils.isNotBlank(id)) {
            sb.append("/").append(id);
        }

        String uri = sb.toString();
        log.debug("Created uri: {}", uri);
        return uri;
    }

    protected String buildQueryString() {
        StringBuilder queryString = new StringBuilder("");
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

    @Deprecated
    @Override
    public final Boolean isOperationSucceed(@SuppressWarnings("rawtypes") Map result) {
        return isOperationSucceed(new JsonParser().parse(new Gson().toJson(result, Map.class)).getAsJsonObject());
    }

    @Override
    public Boolean isOperationSucceed(JsonObject result) {
        return true;
    }

    protected void setURI(String URI) {
        this.URI = URI;
    }

    protected void setData(Object data) {
        this.data = data;
    }

    protected void setBulkOperation(boolean bulkOperation) {
        isBulkOperation = bulkOperation;
    }

    protected void setPathToResult(String pathToResult) {
        this.pathToResult = pathToResult;
    }

    public abstract String getRestMethodName();
}
