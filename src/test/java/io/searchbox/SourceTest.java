package io.searchbox;

import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

/**
 * @author Dogukan Sonmez
 */


public class SourceTest {

    Source source;

    @Test
    public void createJsonWithEmptyMapParameter() {
        Map emptyMap = new HashMap();
        source = new Source(emptyMap);
        String actual = source.toString();
        String expected = "{}";
        executeTest(expected,actual);
    }

    @Test
    public void createSourceWithValidMap() {
        Map map = new LinkedHashMap();
        map.put("name", "dogukan");
        map.put("client", "JEST");
        source = new Source(map);
        String actual = source.toString();
        String expected = "{\"name\":\"dogukan\",\"client\":\"JEST\"}";
        executeTest(expected, actual);
    }

    @Test
    public void createSourceWithMapWithNullValues() {
        Map map = new LinkedHashMap();
        map.put("name", null);
        map.put("client", null);
        source = new Source(map);
        String actual = source.toString();
        String expected = "{}";
        executeTest(expected, actual);
    }

    @Test
    public void createSourceWithEmptyObject() {
        TestBean obj = new TestBean();
        source = new Source(obj);
        String actual = source.toString();
        String expected = "{\"userId\":0,\"isValidUser\":false}";
        executeTest(expected, actual);
    }

    @Test
    public void createSourceWithValidObject() {
        TestBean obj = new TestBean();
        obj.setValidUser(true);
        obj.setMessage("JEST java client api");
        obj.setUser("JEST");
        obj.setUserId(111111);
        source = new Source(obj);
        String actual = source.toString();
        String expected = "{\"user\":\"JEST\",\"message\":\"JEST java client api\",\"userId\":111111,\"isValidUser\":true}";
        executeTest(expected, actual);
    }

    @Test
    public void createSourceWithEmptyString(){
        String data = "";
        source = new Source(data);
        String actual = source.toString();
        String expected = "\"\"";
        executeTest(expected,actual);
    }

    private void executeTest(String expected, String actual) {
        assertNotNull(actual);
        assertEquals(expected, actual);
    }


    class TestBean {
        private String user;
        private String message;
        private int userId;
        private boolean isValidUser;

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public boolean isValidUser() {
            return isValidUser;
        }

        public void setValidUser(boolean validUser) {
            isValidUser = validUser;
        }
    }

}
