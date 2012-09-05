package io.searchbox;

import java.io.IOException;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public interface Action {

    String getURI();

    String getRestMethodName();

    Object getData();

    String getName();

    String getPathToResult();

    boolean isBulkOperation();

    byte[] createByteResult(Map jsonMap) throws IOException;

}
