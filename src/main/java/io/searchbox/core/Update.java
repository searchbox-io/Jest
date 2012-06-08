package io.searchbox.core;

import io.searchbox.Index;

/**
 * @author Dogukan Sonmez
 */


public class Update extends AbstractClientRequest implements ClientRequest {

    public Update(Index index, Object data) {
        setURI(buildURI(index));
        setRestMethodName("POST");
        setData(data);
    }

    protected String buildURI(Index index) {
        StringBuilder sb = new StringBuilder();
        sb.append(index.getName())
                .append("/")
                .append(index.getType())
                .append("/").
                append(index.getId())
                .append("/")
                .append("_update");
        return sb.toString();
    }
}
