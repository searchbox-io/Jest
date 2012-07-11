package io.searchbox;

/**
 * @author Dogukan Sonmez
 */


public class Parameters {

    private Parameters(){}

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

}
