package io.searchbox.core;

import io.searchbox.Index;
import io.searchbox.core.settings.Settings;
import org.apache.commons.lang.StringUtils;

/**
 * @author Dogukan Sonmez
 */


public class IndexDocument extends AbstractClientRequest implements ClientRequest {

    public IndexDocument(Index index, Object data) {
        setURI(buildURI(index));
        setData(data);
        if (!StringUtils.isNotBlank(index.getId()) || getSettings().containsKey(Settings.ROUTING.toString())) {
            setRestMethodName("POST");
        } else {
            setRestMethodName("PUT");
        }
    }

    public IndexDocument(String indexName) {
        setURI(buildURI(indexName));
        setRestMethodName("PUT");

    }

    protected String buildURI(Index index) {
        StringBuilder sb = new StringBuilder();
        sb.append(index.getName())
                .append("/")
                .append(index.getType())
                .append("/");
        if (StringUtils.isNotBlank(index.getId())) sb.append(index.getId());
        return sb.toString();

    }

    private String buildURI(String indexName) {
        StringBuilder sb = new StringBuilder();
        sb.append(indexName)
                .append("/");
        return sb.toString();
    }


}
