package io.searchbox.snapshot;

import com.google.gson.Gson;
import io.searchbox.client.config.ElasticsearchVersion;
import org.json.JSONException;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class RestoreSnapshotTest {

    private String repository = "leeseohoo";
    private String snapshot = "leeseola";

    @Test
    public void testSnapshot() throws JSONException {

        Map<String, String> repositorySettings = new HashMap<>();
        repositorySettings.put("indices", "index_1,index_2");
        repositorySettings.put("ignore_unavailable", "true");
        repositorySettings.put("include_global_state", "false");
        repositorySettings.put("rename_pattern", "index_(.+)");
        repositorySettings.put("rename_replacement", "restored_index_$1");


        RestoreSnapshot restoreSnapshot = new RestoreSnapshot.Builder(repository, snapshot)
                .settings(repositorySettings).build();
        assertEquals("POST", restoreSnapshot.getRestMethodName());
        assertEquals("/_snapshot/leeseohoo/leeseola/_restore", restoreSnapshot.getURI(ElasticsearchVersion.UNKNOWN));
        String settings = restoreSnapshot.getData(new Gson());

        String expectedJSON = "{\n" +
                "\"ignore_unavailable\": \"true\",\n" +
                "\"include_global_state\": \"false\",\n" +
                "\"indices\": \"index_1,index_2\",\n" +
                "\"rename_pattern\": \"index_(.+)\",\n" +
                "\"rename_replacement\": \"restored_index_$1\"\n" +
                "}";

        JSONAssert.assertEquals(expectedJSON, settings, false);
    }
}
