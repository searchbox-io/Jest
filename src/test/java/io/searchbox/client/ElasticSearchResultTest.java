package io.searchbox.client;

import org.junit.Test;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchResultTest {

    ElasticSearchResult result = new ElasticSearchResult();

    @Test
    public void getSuccessIndexResult(){
        String response = "{\n" +
                "    \"ok\" : true,\n" +
                "    \"_index\" : \"twitter\",\n" +
                "    \"_type\" : \"tweet\",\n" +
                "    \"_id\" : \"1\"\n" +
                "}\n";
        result.setJsonString(response);

    }
}
