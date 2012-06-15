package io.searchbox.core;


import io.searchbox.Document;
import org.apache.log4j.Logger;

/**
 * @author Dogukan Sonmez
 */


public class Update extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Update.class.getName());

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
        if (document.getSettings().size() > 0) sb.append(buildQueryString(document.getSettings()));
        log.debug("Created URI for update action is :" + sb.toString());
        return sb.toString();
    }
}
