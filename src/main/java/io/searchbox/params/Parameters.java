package io.searchbox.params;

import java.util.Arrays;
import java.util.List;

/**
 * @author Dogukan Sonmez
 */


public class Parameters {

    private Parameters() {
    }

    //'quorum' | 'one' | 'all'
    public static final String CONSISTENCY = "consistency";

    //$parent
    public static final String PARENT = "parent";

    //$percolate
    public static final String PERCOLATE = "percolate";

    //0 | 1
    public static final String REFRESH = "refresh";

    //sync | async
    public static final String REPLICATION = "replication";

    //$routing
    public static final String ROUTING = "routing";

    //eg 5m,10s
    public static final String TIMEOUT = "timeout";

    //eg 1,2,3
    public static final String VERSION = "version";

    //internal | external
    public static final String VERSION_TYPE = "version_type";

    //eg 86400000,1d
    public static final String TTL = "ttl";

    //eg 2009-11-15T14%3A12%3A12
    public static final String TIMESTAMP = "timestamp";

    //create
    public static final String OP_TYPE = "op_type";

    // true,false
    public static final String EXPLAIN = "explain";

    // result size
    public static final String SIZE = "size";

    public static final String SCROLL = "scroll";

    public static final String SCROLL_ID = "scroll_id";

    public static final String SEARCH_TYPE = "search_type";

    public static final String PERCOLATOR = "percolate";

    public static final String RETRY_ON_CONFLICT = "retry_on_conflict";

    public static final List<String> ACCEPTED_IN_BULK = Arrays.asList(
            ROUTING,
            PERCOLATOR,
            PARENT,
            TIMESTAMP,
            TTL,
            RETRY_ON_CONFLICT,
            VERSION,
            VERSION_TYPE
    );
}
