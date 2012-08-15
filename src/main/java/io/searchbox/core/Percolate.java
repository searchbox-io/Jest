package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class Percolate extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Percolate.class.getName());

    public Percolate(String indexName, String designedQueryName, Object query) {
        setURI(buildURI(indexName, designedQueryName));
        setData(query);
        setRestMethodName("PUT");
    }

    private String buildURI(String indexName, String designedQueryName) {
        StringBuilder sb = new StringBuilder();
        sb.append("_percolator")
                .append("/")
                .append(indexName)
                .append("/")
                .append(designedQueryName);
        log.debug("Created URI for percolate request is : " + sb.toString());
        return sb.toString();
    }

    @Override
    public String getName() {
        return "PERCOLATE";
    }

    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
