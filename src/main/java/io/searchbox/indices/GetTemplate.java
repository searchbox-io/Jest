package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.Action;

/**
 * @author asierdelpozo
 */

public class GetTemplate extends AbstractAction implements Action {

    public GetTemplate(String name) {
        setURI(buildGetURI(name));
        setRestMethodName("GET");
    }

    private String buildGetURI(String name) {
        StringBuilder sb = new StringBuilder();
        sb.append("_template/").append(name);
        return sb.toString();
    }

    @Override
    public String getName() {
        return "GET_TEMPLATE";
    }
}
