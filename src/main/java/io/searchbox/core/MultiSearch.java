package io.searchbox.core;

import io.searchbox.AbstractAction;
import io.searchbox.Action;

import java.io.IOException;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class MultiSearch extends AbstractAction implements Action {
    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
