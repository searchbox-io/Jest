package io.searchbox.core;

import io.searchbox.Document;
import io.searchbox.core.settings.Settings;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

/**
 * @author Dogukan Sonmez
 */


public class Index extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Index.class.getName());

    public Index(Document document) {
        setURI(buildURI(document));
        setData(document.getSource());
        setName("INDEX");
        if (!StringUtils.isNotBlank(document.getId()) || document.getSettings().containsKey(Settings.ROUTING.toString())) {
            setRestMethodName("POST");
            log.debug("POST method set for index request");
        } else {
            setRestMethodName("PUT");
            log.debug("PUT method set for index request");
        }
    }
}
