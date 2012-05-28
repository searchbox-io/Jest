package io.searchbox.client;

import io.searchbox.indices.Index;

/**
 * @author Dogukan Sonmez
 */


public interface ElasticSearchClient {

    public void index(Index index);

    public void delete(Index index);

    public Object get();

    public void update(Index index);

    public Object search();

    public void percolate();

    public void bulk();

    public void count();

}
