package io.searchbox.action;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.google.gson.Gson;

import io.searchbox.client.JestResult;

public class GenericResultAbstractDocumentTargetedActionTest {

    private static class DummyDocumentAction extends GenericResultAbstractDocumentTargetedAction {

        public DummyDocumentAction(Builder builder) {
            super(builder);
        }
        
        @Override
        public String getRestMethodName() {
            return "GET";
        }
        
        static class Builder extends AbstractDocumentTargetedAction.Builder<DummyDocumentAction, Builder> {

            @Override
            public DummyDocumentAction build() {
                return new DummyDocumentAction(this);
            }
            
        }
    }
    
    @Test
    public void getSuccessDummyGenericResult() {
        String responseBody = "{" +
                        "    \"_index\" : \"twitter\"," +
                        "    \"_type\" : \"tweet\"," +
                        "    \"_id\" : \"1\"," +
                        "    \"exists\" : true" +
                        "}";
        DummyDocumentAction dummyDocumentAction = new DummyDocumentAction.Builder().build();
        JestResult jestResult = dummyDocumentAction.createNewElasticSearchResult(responseBody, 200, null, new Gson());
        assertTrue(jestResult.getErrorMessage(), jestResult.isSucceeded());
    }
}
