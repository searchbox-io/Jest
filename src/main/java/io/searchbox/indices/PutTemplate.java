package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.Action;

/**
 * @author asierdelpozo
 */

public class PutTemplate extends AbstractAction implements Action {

    public PutTemplate(String name, Object source) {
        setURI(buildPutURI(name));
        setData(source);
        setRestMethodName("PUT");
    }

    private String buildPutURI(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("_template").append("/").append(name);
        return sb.toString();
    }
}

