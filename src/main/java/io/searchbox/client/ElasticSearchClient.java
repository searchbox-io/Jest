package io.searchbox.client;

import io.searchbox.indices.Index;

/**
 * @author Dogukan Sonmez
 */


public interface ElasticSearchClient {

    void index(Index index);

    void indexAsync(Index index);

    void delete(Index index);

    void deleteAsync(Index index);

    Object get(String name, String type, String id);

    void updateAsync(Index index);

    Object search();

    void percolate();

    void bulk();

    void count();

    void shutdownClient();

}
