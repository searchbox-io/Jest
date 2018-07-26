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
public class CreateSnapshotRepositoryTest {

    private String repository = "seohoo";

    @Test
    public void testBasicUriGeneration() throws JSONException {

        Map<String, String> repositorySettings = new HashMap<>();
        repositorySettings.put("type", "fs");
        repositorySettings.put("settings.compress", "true");
        repositorySettings.put("settings.location", "/mount/backups/my_backup");
        repositorySettings.put("settings.chunk_size", "10m");
        repositorySettings.put("settings.max_restore_bytes_per_sec", "40mb");
        repositorySettings.put("settings.max_snapshot_bytes_per_sec", "40mb");
        repositorySettings.put("settings.readonly", "false");

        CreateSnapshotRepository createSnapshotRepository = new CreateSnapshotRepository.Builder(repository).settings(repositorySettings).build();

        assertEquals("PUT", createSnapshotRepository.getRestMethodName());
        assertEquals("/_snapshot/" + repository, createSnapshotRepository.getURI(ElasticsearchVersion.UNKNOWN));
        String settings = createSnapshotRepository.getData(new Gson());

        String expectedJSON = "{" +
                "   \"settings.chunk_size\":\"10m\"," +
                "   \"settings.compress\":\"true\"," +
                "   \"settings.location\":\"/mount/backups/my_backup\"," +
                "   \"settings.max_restore_bytes_per_sec\":\"40mb\"," +
                "   \"settings.max_snapshot_bytes_per_sec\":\"40mb\"," +
                "   \"settings.readonly\":\"false\"," +
                "   \"type\":\"fs\"" +
                "}";

        JSONAssert.assertEquals(expectedJSON, settings, false);
    }

    @Test
    public void testVerifyParam() {
        CreateSnapshotRepository createSnapshotRepository = new CreateSnapshotRepository.Builder(repository).verify(false).build();
        assertEquals("/_snapshot/seohoo?verify=false", createSnapshotRepository.getURI(ElasticsearchVersion.UNKNOWN));
    }
}
