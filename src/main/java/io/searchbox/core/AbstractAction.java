package io.searchbox.core;

import io.searchbox.Document;
import org.apache.commons.lang.StringUtils;

/**
 * @author Dogukan Sonmez
 */


public class AbstractAction implements Action {

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
        return sb.toString();

    }
}
