package io.searchbox.core;

import io.searchbox.Index;
import io.searchbox.core.settings.Settings;
import org.apache.commons.lang.StringUtils;

/**
 * @author Dogukan Sonmez
 */


public class Delete extends AbstractClientRequest implements ClientRequest{

    public Delete(Index index){
        setURI(buildURI(index));
        setRestMethodName("DELETE");
    }

    public Delete(String indexName){

    }
}
