package io.searchbox.indices.template;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class PutTemplateTest {

    @Test
    public void tesstBsicUriGeneration() {
        PutTemplate putTemplate = new PutTemplate.Builder("sponsored_tweet", new Object()).build();

        assertEquals("PUT", putTemplate.getRestMethodName());
        assertEquals("_template/sponsored_tweet", putTemplate.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameTemplateNameAndSource() {
        PutTemplate putTemplate1 = new PutTemplate.Builder("sponsored_tweet", "{}").build();
        PutTemplate putTemplate1Duplicate = new PutTemplate.Builder("sponsored_tweet", "{}").build();

        assertEquals(putTemplate1, putTemplate1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentTemplateSource() {
        PutTemplate putTemplate1 = new PutTemplate.Builder("sponsored_tweet", "{}").build();
        PutTemplate putTemplate2 = new PutTemplate.Builder("sponsored_tweet", "{source}").build();

        assertNotEquals(putTemplate1, putTemplate2);
    }

}