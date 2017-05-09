package io.searchbox.params;

import java.util.Arrays;
import java.util.List;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Parameters {

    private Parameters() {
    }

    // All REST APIs accept a callback parameter resulting in a JSONP result.
    public static final String CALLBACK = "callback";

    // 'camelCase'
    public static final String RESULT_CASING = "case";

    // 'true' | 'false'
    public static final String IGNORE_UNAVAILABLE = "ignore_unavailable";

    // 'true' | 'false'
    public static final String ALLOW_NO_INDICES = "allow_no_indices";

    // 'true' | 'false'
    public static final String EXPAND_WILDCARDS = "expand_wildcards";

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
    
    // results to skip
    public static final String FROM = "from";

    public static final String SCROLL = "scroll";

    // indices, cluster (default)
    public static final String LEVEL = "level";

    public static final String SCROLL_ID = "scroll_id";

    public static final String SEARCH_TYPE = "search_type";

    public static final String PERCOLATOR = "percolate";

    public static final String RETRY_ON_CONFLICT = "retry_on_conflict";

    public static final String TRACK_SCORES = "track_scores";

    public static final String PIPELINE = "pipeline";

    public static final List<String> ACCEPTED_IN_BULK = Arrays.asList(
            ROUTING,
            PERCOLATOR,
            PARENT,
            TIMESTAMP,
            TTL,
            RETRY_ON_CONFLICT,
            VERSION,
            VERSION_TYPE,
            PIPELINE
    );
}
