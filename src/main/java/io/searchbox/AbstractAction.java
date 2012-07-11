package io.searchbox;

import io.searchbox.core.Doc;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Dogukan Sonmez
 */


public class AbstractAction implements Action {

    private static Logger log = Logger.getLogger(AbstractAction.class.getName());

    private Object data;

    private String URI;

    private String restMethodName;

    private boolean isDefaultIndexEnabled = false;

    private boolean isDefaultTypeEnabled = false;

    private boolean isBulkOperation =false;

    protected String indexName;

    protected String typeName;

    protected String id;

    private final ConcurrentMap<String,Object> parameterMap = new ConcurrentHashMap<String,Object>();

    public void setRestMethodName(String restMethodName) {
        this.restMethodName = restMethodName;
    }

    public void addParameter(String parameter,Object value){
         parameterMap.put(parameter,value);
    }

    public void removeParameter(String parameter){
        parameterMap.remove(parameter);
    }

    public boolean isParameterExist(String parameter){
        return parameterMap.containsKey(parameter);
    }

    public Object getParameter(String parameter){
        return parameterMap.get(parameter);
    }

    public void setURI(String URI) {
        this.URI = URI;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getURI() {
        return URI;
    }

    public String getRestMethodName() {
        return restMethodName;
    }

    public Object getData() {
        return data;
    }

    public String getName() {
        return null;
    }

    protected String buildURI(Doc doc) {
        return buildURI(doc.getIndex(), doc.getType(), doc.getId());
    }

    protected String buildURI(String index,String type,String id) {
        if(StringUtils.isNotBlank(index) && index.equalsIgnoreCase("_bulk")) return "_bulk";
        StringBuilder sb = new StringBuilder();
        if(!isDefaultIndexEnabled() && StringUtils.isNotBlank(index)) sb.append(index);
        if(!isDefaultTypeEnabled() && StringUtils.isNotBlank(type))sb.append("/").append(type);
        if (StringUtils.isNotBlank(id)) sb.append("/").append(id);
        if(parameterMap.size() > 0) sb.append(buildQueryString());
        log.debug("Created uri: " + sb.toString());
        return sb.toString();
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

    protected boolean isValid(String index,String type,String id) {
        return StringUtils.isNotBlank(index) && StringUtils.isNotBlank(type) && StringUtils.isNotBlank(id);
    }

    protected boolean isValid(Doc doc) {
        return isValid(doc.getIndex(),doc.getType(),doc.getId());
    }

    public boolean isDefaultIndexEnabled() {
        return isDefaultIndexEnabled;
    }

    public void setDefaultIndexEnabled(boolean defaultIndexEnabled) {
        isDefaultIndexEnabled = defaultIndexEnabled;
    }

    public boolean isDefaultTypeEnabled() {
        return isDefaultTypeEnabled;
    }

    public void setDefaultTypeEnabled(boolean defaultTypeEnabled) {
        isDefaultTypeEnabled = defaultTypeEnabled;
    }

    public boolean isBulkOperation() {
        return isBulkOperation;
    }

    public void setBulkOperation(boolean bulkOperation) {
        isBulkOperation = bulkOperation;
    }
}
