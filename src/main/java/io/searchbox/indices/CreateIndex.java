package io.searchbox.indices;

import io.searchbox.AbstractAction;
import io.searchbox.Action;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

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

    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
