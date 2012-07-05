package io.searchbox.Indices;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * @author Dogukan Sonmez
 */


public class CreateIndex extends AbstractAction implements Action {

    public CreateIndex(String indexName) {
        setURI(buildURI(indexName, null, null));
        setRestMethodName("PUT");
        setData(ImmutableSettings.Builder.EMPTY_SETTINGS.getAsMap());
    }

    public CreateIndex(String indexName, Settings settings) {
        setURI(buildURI(indexName, null, null));
        setData(settings.getAsMap());
        setRestMethodName("POST");
    }

    public CreateIndex(String indexName, String settingsSource) throws FileNotFoundException {
        setURI(buildURI(indexName, null, null));
        setData(readSettingsFromSource(settingsSource).getAsMap());
        setRestMethodName("POST");
    }

    private Settings readSettingsFromSource(String source) throws FileNotFoundException {
        File file = new File(getClass().getResource(source).getFile());
        return ImmutableSettings.settingsBuilder().loadFromStream(file.getName(), new FileInputStream(file)).build();
    }


    @Override
    public String getName() {
        return "CREATEINDEX";
    }
}
