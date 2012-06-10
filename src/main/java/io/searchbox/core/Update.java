package io.searchbox.core;


import io.searchbox.Document;

/**
 * @author Dogukan Sonmez
 */


public class Update extends AbstractAction implements Action {

    public Update(Document document) {
        setURI(buildURI(document));
        setRestMethodName("POST");
        setData(document.getSource());
    }

    protected String buildURI(Document document) {
        StringBuilder sb = new StringBuilder();
        sb.append(document.getIndexName())
                .append("/")
                .append(document.getType())
                .append("/")
                .append(document.getId())
                .append("/")
                .append("_update");
        return sb.toString();
    }
}
