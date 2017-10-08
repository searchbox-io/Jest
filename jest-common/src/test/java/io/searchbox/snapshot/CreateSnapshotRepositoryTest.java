package io.searchbox.snapshot;

import com.google.gson.Gson;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class CreateSnapshotRepositoryTest {

    private String repository = "seohoo";

    @Test
    public void testBasicUriGeneration() {
        final Settings.Builder registerRepositorySettings = ImmutableSettings.settingsBuilder()
                .put("settings.chunk_size", "10m")
                .put("settings.compress", "true")
                .put("settings.location", "/mount/backups/my_backup")
                .put("settings.max_restore_bytes_per_sec", "40mb")
                .put("settings.max_snapshot_bytes_per_sec", "40mb")
                .put("settings.readonly", "false")
                .put("type", "fs");

        CreateSnapshotRepository createSnapshotRepository = new CreateSnapshotRepository.Builder(repository).settings(registerRepositorySettings.build().getAsMap()).build();

        assertEquals("PUT", createSnapshotRepository.getRestMethodName());
        assertEquals("/_snapshot/" + repository, createSnapshotRepository.getURI());
        String settings = new Gson().toJson(createSnapshotRepository.getData(new Gson()));
        assertEquals("\"{\\\"settings.chunk_size\\\":\\\"10m\\\",\\\"settings.compress\\\":\\\"true\\\",\\\"settings.location\\\":\\\"/mount/backups/my_backup\\\",\\\"settings.max_restore_bytes_per_sec\\\":\\\"40mb\\\",\\\"settings.max_snapshot_bytes_per_sec\\\":\\\"40mb\\\",\\\"settings.readonly\\\":\\\"false\\\",\\\"type\\\":\\\"fs\\\"}\"", settings);
    }

    @Test
    public void testVerifyParam() {
        CreateSnapshotRepository createSnapshotRepository = new CreateSnapshotRepository.Builder(repository).verify(false).build();
        assertEquals("/_snapshot/seohoo?verify=false", createSnapshotRepository.getURI());
    }
}
