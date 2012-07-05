package io.searchbox.core;


import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.log4j.Logger;

/**
 * @author Dogukan Sonmez
 */


public class Update extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Update.class.getName());

    public Update(Doc doc, Object script) {
        setURI(buildURI(doc));
        setRestMethodName("POST");
        setData(script);
    }

    public Update(String indexName,String typeName,String id, Object script) {
        setURI(buildURI(new Doc(indexName,typeName,id)));
        setRestMethodName("POST");
        setData(script);
    }

    protected String buildURI(Doc doc) {
        StringBuilder sb = new StringBuilder();
        sb.append(buildURI(doc.getIndex(),doc.getType(),doc.getId()))
                .append("/")
                .append("_update");
        log.debug("Created URI for update action is :" + sb.toString());
        return sb.toString();
    }

    @Override
    public String getName() {
        return "UPDATE";
    }
}
