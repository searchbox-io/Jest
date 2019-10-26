package io.searchbox.action;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class GenericResultAbstractActionTest {

    private static class DummyGenericAction extends GenericResultAbstractAction {

        public DummyGenericAction() {
            super();
        }
        
        public DummyGenericAction(Builder builder) {
            super(builder);
        }

        @Override
        public String getRestMethodName() {
            return "GET";
        }
        
        static class Builder extends AbstractAction.Builder<DummyGenericAction, Builder> {

            @Override
            public DummyGenericAction build() {
                return new DummyGenericAction(this);
            }
            
        }
    }
    
    @Test
    public void testConstructors() {
        DummyGenericAction dummyGenericAction = new DummyGenericAction();
        assertEquals(dummyGenericAction, dummyGenericAction);
        assertNotNull(new DummyGenericAction.Builder().build());
    }
}
