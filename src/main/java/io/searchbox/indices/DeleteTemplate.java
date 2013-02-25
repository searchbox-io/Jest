package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.Action;

/**
 * @author asierdelpozo
 */

public class DeleteTemplate extends AbstractAction implements Action {

    public DeleteTemplate(String name) {
    	setURI(buildPutURI(name));
    	setRestMethodName("DELETE");
    }

    private String buildPutURI(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("_template").append("/").append(name);
        return sb.toString();
    }
    
    @Override
    public String getName() {
        return "DELETE_TEMPLATE";
    }
}
