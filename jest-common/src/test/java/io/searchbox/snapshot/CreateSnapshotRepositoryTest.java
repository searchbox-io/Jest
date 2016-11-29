package io.searchbox.snapshot;

import com.google.gson.Gson;
import org.elasticsearch.common.settings.Settings;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class CreateSnapshotRepositoryTest {

    private String repository = "seohoo";

    @Test
    public void testBasicUriGeneration() {
        final Settings.Builder registerRepositorySettings = Settings.settingsBuilder();
        registerRepositorySettings.put("type", "fs");
        registerRepositorySettings.put("settings.compress", "true");
        registerRepositorySettings.put("settings.location", "/mount/backups/my_backup");
        registerRepositorySettings.put("settings.chunk_size", "10m");
        registerRepositorySettings.put("settings.max_restore_bytes_per_sec", "40mb");
        registerRepositorySettings.put("settings.max_snapshot_bytes_per_sec", "40mb");
        registerRepositorySettings.put("settings.readonly", "false");

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
