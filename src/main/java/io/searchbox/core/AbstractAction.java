package io.searchbox.core;

import io.searchbox.Document;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.HashMap;
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
}
