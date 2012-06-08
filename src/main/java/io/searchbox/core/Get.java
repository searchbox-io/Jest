package io.searchbox.core;

import io.searchbox.Index;

/**
 * @author Dogukan Sonmez
 */


public class Get extends AbstractClientRequest implements ClientRequest {

    public Get(Index index) {
        setURI(buildURI(index));
        setRestMethodName("GET");
    }

    public Get(Index[] indexes) {

    }

    public Get(String[] ids) {

    }
}
