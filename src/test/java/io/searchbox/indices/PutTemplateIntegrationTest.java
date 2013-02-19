package io.searchbox.indices;


import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;
import io.searchbox.client.JestResult;
import io.searchbox.core.AbstractIntegrationTest;

import java.io.IOException;

import org.junit.Test;

/**
 * @author asierdelpozo
 */

public class PutTemplateIntegrationTest extends AbstractIntegrationTest {

    @Test
    public void testPutTemplate() {
    	PutTemplate putTemplate = new PutTemplate("new_template_1", 
    		"{	" +
    			"\"template\" : \"*\","+
    			"\"order\" : 0,"+
    			"\"settings\" : {"+
    			"	\"number_of_shards\" : 1"+
    			"},"+
    			"\"mappings\" : {"+
    			"	\"type1\" : {"+
    			"		\"_source\" : { \"enabled\" : false }"+
    			"	}"+
    			"}"+
    		"}");
        try {
            JestResult result = client.execute(putTemplate);
            assertNotNull(result);
            assertTrue(result.isSucceeded());
            
            GetTemplate getTemplate = new GetTemplate("new_template_1");
            result = client.execute(getTemplate);
            assertNotNull(result);
            assertTrue(result.isSucceeded());  
        } catch (IOException e) {
            fail("Test failed while executing creating index with default settings");
        }
    }
  
}
