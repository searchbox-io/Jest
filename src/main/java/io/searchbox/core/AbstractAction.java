package io.searchbox.core;

import io.searchbox.Document;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class AbstractAction implements Action {

    private static Logger log = Logger.getLogger(AbstractAction.class.getName());

    private Object data;

    private String URI;

    private String restMethodName;

    public void setRestMethodName(String restMethodName) {
        this.restMethodName = restMethodName;
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

    protected String buildURI(Document document) {
        StringBuilder sb = new StringBuilder();
        sb.append(document.getIndexName())
                .append("/")
                .append(document.getType())
                .append("/");
        if (StringUtils.isNotBlank(document.getId())) sb.append(document.getId());
        if (document.getSettings().size() > 0) sb.append(buildQueryString(document.getSettings()));
        log.debug("Created uri: " + sb.toString());
        return sb.toString();
    }

    protected String buildURI(Doc doc) {
        StringBuilder sb = new StringBuilder();
        sb.append(doc.getIndex())
                .append("/")
                .append(doc.getType())
                .append("/")
                .append(doc.getId());
        log.debug("Created uri: " + sb.toString());
        return sb.toString();
    }

    protected String buildURI(String index,String type,String id) {
        StringBuilder sb = new StringBuilder();
        sb.append(index)
                .append("/")
                .append(type)
                .append("/");
        if (StringUtils.isNotBlank(id)) sb.append(id);
        log.debug("Created uri: " + sb.toString());
        return sb.toString();
    }

    protected String buildQueryString(HashMap<String, Object> settings) {
        StringBuilder queryString = new StringBuilder("");
        for (Map.Entry<?, ?> entry : settings.entrySet()) {
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

    protected boolean isValid(String index,String type) {
        return isValid(index,type,"1");
    }

    protected boolean isValid(Doc doc) {
        return StringUtils.isNotBlank(doc.getIndex()) &&StringUtils.isNotBlank(doc.getType()) && StringUtils.isNotBlank(doc.getId());
    }

    protected boolean isValid(List<Doc> docs) {
        for(Doc doc:docs){
            boolean isValid = isValid(doc);
            if(!isValid) return false;
        }
        return true;
    }



    protected boolean isValidWithId(Document doc) {
        return StringUtils.isNotBlank(doc.getIndexName()) && StringUtils.isNotBlank(doc.getType()) && StringUtils.isNotBlank(doc.getId());
    }
}
