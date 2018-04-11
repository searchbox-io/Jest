package io.searchbox.indices.template;

import io.searchbox.client.config.ElasticsearchVersion;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class GetTemplateTest {

    @Test
    public void testBasicUriGeneration() {
        GetTemplate getTemplate = new GetTemplate.Builder("personal_tweet").build();

        assertEquals("GET", getTemplate.getRestMethodName());
        assertEquals("_template/personal_tweet", getTemplate.getURI(ElasticsearchVersion.UNKNOWN));
    }

    @Test
    public void equalsReturnsTrueForSameTemplate() {
        GetTemplate getTemplate1 = new GetTemplate.Builder("personal_tweet").build();
        GetTemplate getTemplate1Duplicate = new GetTemplate.Builder("personal_tweet").build();

        assertEquals(getTemplate1, getTemplate1Duplicate);
    }

    @Test
    public void equalsReturnsFalseForDifferentTemplate() {
        GetTemplate getTemplate1 = new GetTemplate.Builder("personal_tweet").build();
        GetTemplate getTemplate2 = new GetTemplate.Builder("company_tweet").build();

        assertNotEquals(getTemplate1, getTemplate2);
    }

}