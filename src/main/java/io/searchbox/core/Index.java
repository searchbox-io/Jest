package io.searchbox.core;

import io.searchbox.Document;
import io.searchbox.core.settings.Settings;
import org.apache.commons.lang.StringUtils;

/**
 * @author Dogukan Sonmez
 */


public class Index extends AbstractAction implements Action {

    public Index(Document document) {
        setURI(buildURI(document));
        setData(document.getSource());
        if (!StringUtils.isNotBlank(document.getId()) || document.getSettings().containsKey(Settings.ROUTING.toString())) {
            setRestMethodName("POST");
        } else {
            setRestMethodName("PUT");
        }
    }
}
