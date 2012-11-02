package io.searchbox.indices;

/**
 * @author Dogukan Sonmez
 */

public class IndicesExists extends AbstractAction implements Action {

    public IndicesExists(String indexName) {
        setURI(buildURI(indexName, null, null));
        setRestMethodName("HEAD");
        setData(ImmutableSettings.Builder.EMPTY_SETTINGS.getAsMap());
    }

    public IndicesExists(String indexName, Settings settings) {
        setURI(buildURI(indexName, null, null));
        setData(settings.getAsMap());
        setRestMethodName("HEAD");
    }

    public IndicesExists(String indexName, String settingsSource) throws FileNotFoundException {
        setURI(buildURI(indexName, null, null));
        setData(readSettingsFromSource(settingsSource).getAsMap());
        setRestMethodName("HEAD");
    }

    private Settings readSettingsFromSource(String source) throws FileNotFoundException {
        File file = new File(getClass().getResource(source).getFile());
        return ImmutableSettings.settingsBuilder().loadFromStream(file.getName(), new FileInputStream(file)).build();
    }

    @Override
    public byte[] createByteResult(Map jsonMap) throws IOException {
        return new byte[0];  //To change body of implemented methods use File | Settings | File Templates.
    }
}