package io.searchbox.client;

import io.searchbox.indices.Index;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * @author Dogukan Sonmez
 */


public interface ElasticSearchClient {

    void index(Index index) throws Exception;

    void indexAsync(Index index);

    void delete(Index index) throws IOException;

    void deleteAsync(Index index);

    Object get(String name, String type, String id) throws IOException;

    void update(Index index) throws IOException;

    void updateAsync(Index index);

    Object search();

    void percolate();

    void bulk();

    void count();

    void shutdownClient();

}
