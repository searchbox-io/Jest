package io.searchbox.Indices;

import io.searchbox.AbstractAction;
import io.searchbox.Action;

import java.io.IOException;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class DeleteIndex extends AbstractAction implements Action {

    public DeleteIndex(String indexName) {
        super.indexName = indexName;
        setRestMethodName("DELETE");
    }

    public String getURI() {
        return buildURI(indexName,typeName,id);
    }

    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

}
