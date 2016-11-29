package io.searchbox.snapshot;

import com.google.gson.Gson;
import org.elasticsearch.common.settings.Settings;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author happyprg(hongsgo@gmail.com)
 */
public class RestoreSnapshotTest {

    private String repository = "leeseohoo";
    private String snapshot = "leeseola";

    @Test
    public void testSnapshot() {
        final Settings.Builder registerRepositorySettings = Settings.settingsBuilder();
        registerRepositorySettings.put("indices", "index_1,index_2");
        registerRepositorySettings.put("ignore_unavailable", "true");
        registerRepositorySettings.put("include_global_state", "false");
        registerRepositorySettings.put("rename_pattern", "index_(.+)");
        registerRepositorySettings.put("rename_replacement", "restored_index_$1");

        RestoreSnapshot restoreSnapshot = new RestoreSnapshot.Builder(repository, snapshot)
                .settings(registerRepositorySettings.build().getAsMap()).build();
        assertEquals("POST", restoreSnapshot.getRestMethodName());
        assertEquals("/_snapshot/leeseohoo/leeseola/_restore", restoreSnapshot.getURI());
        String settings = new Gson().toJson(restoreSnapshot.getData(new Gson()));
        assertEquals("\"{\\\"ignore_unavailable\\\":\\\"true\\\",\\\"include_global_state\\\":\\\"false\\\",\\\"indices\\\":\\\"index_1,index_2\\\",\\\"rename_pattern\\\":\\\"index_(.+)\\\",\\\"rename_replacement\\\":\\\"restored_index_$1\\\"}\"", settings);
    }
}
