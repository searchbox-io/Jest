package io.searchbox.snapshot;

import com.google.gson.Gson;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class CreateSnapshotTest {

    private String repository = "leeseohoo";
    private String snapshot = "leeseola";

    @Test
    public void testSnapshot() {
        CreateSnapshot createSnapshot = new CreateSnapshot.Builder(repository, snapshot).waitForCompletion(true).build();
        assertEquals("PUT", createSnapshot.getRestMethodName());
        assertEquals("/_snapshot/leeseohoo/leeseola?wait_for_completion=true", createSnapshot.getURI());
    }

    @Test
    public void testSnapshotWithSettings() {

        final Settings.Builder registerRepositorySettings = ImmutableSettings.settingsBuilder()
                .put("ignore_unavailable", "true")
                .put("include_global_state", "false")
                .put("indices", "index_1,index_2");

        CreateSnapshot createSnapshot = new CreateSnapshot.Builder(repository, snapshot)
                .settings(registerRepositorySettings.build().getAsMap())
                .waitForCompletion(true)
                .build();

        assertEquals("PUT", createSnapshot.getRestMethodName());
        assertEquals("/_snapshot/leeseohoo/leeseola?wait_for_completion=true", createSnapshot.getURI());
        String settings = new Gson().toJson(createSnapshot.getData(new Gson()));
        assertEquals("\"{\\\"ignore_unavailable\\\":\\\"true\\\",\\\"include_global_state\\\":\\\"false\\\",\\\"indices\\\":\\\"index_1,index_2\\\"}\"", settings);
    }
}
