package io.searchbox.client;

import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 */


public class ElasticSearchResult {

    private static Logger log = Logger.getLogger(ElasticSearchResult.class.getName());

    private Map jsonMap;

    private String jsonString;

    private String pathToResult;

    private boolean isSucceeded;

    public String getPathToResult() {
        return pathToResult;
    }

    public void setPathToResult(String pathToResult) {
        this.pathToResult = pathToResult;
    }

    public Object getValue(String key) {
        return jsonMap.get(key);
    }

    public boolean isSucceeded() {
        return isSucceeded;
    }

    public void setSucceeded(boolean succeeded) {
        isSucceeded = succeeded;
    }

    public String getJsonString() {
        return jsonString;
    }

    public void setJsonString(String jsonString) {
        this.jsonString = jsonString;
    }

    public String getErrorMessage() {
        return (String) jsonMap.get("error");
    }

    public Map getJsonMap() {
        return jsonMap;
    }

    public void setJsonMap(Map jsonMap) {
        this.jsonMap = jsonMap;
    }

    public Object getSourceAsObject(Class<?> clazz) {
        List<Object> sourceList = (List<Object>) extractSource();
        Object source = sourceList.get(0);
        Object obj = null;
        if (source instanceof Map) {
            Constructor[] ctors = clazz.getDeclaredConstructors();
            Constructor ctor = null;
            for (Constructor ctor1 : ctors) {
                ctor = ctor1;
                if (ctor.getGenericParameterTypes().length == 0)
                    break;
            }
            try {
                assert ctor != null;
                ctor.setAccessible(true);
                Class[] parameterTypes = ctor.getParameterTypes();
                if (parameterTypes.length > 0) {
                    Object[] objects = new Object[parameterTypes.length];
                    int i = 0;
                    for (Class type : parameterTypes) {
                        objects[i++] = type.newInstance();
                    }
                    obj = ctor.newInstance(objects);
                } else {
                    obj = ctor.newInstance();
                }
                for (Object key : ((Map) source).keySet()) {
                    Field field = obj.getClass().getDeclaredField((String) key);
                    if (field != null) {
                        field.setAccessible(true);
                        field.set(obj, ((Map) source).get(key));
                    }
                }
            } catch (InstantiationException e) {
                log.error("Instantiation error while creating object from source. Exception:", e);
            } catch (IllegalAccessException e) {
                log.error("Illegal access exception while creating object from source. Exception", e);
            } catch (InvocationTargetException e) {
                log.error("Invocation target exception while creating object from source. Exception ", e);
            } catch (NoSuchFieldException e) {
                log.error("NoSuch field exception while creation object from source. Exception", e);
            } catch (AssertionError error) {
                log.error("There is no implicit constructor in class: " + clazz.getCanonicalName(), error);
            }
        } else {
            return clazz.cast(source);
        }
        return obj;
    }

    public <T> T getSourceAsObjectList(Class<?> type) {
        return null;
    }

    protected Object extractSource() {
        List<Object> sourceList = new ArrayList<Object>();
        String[] keys = getKeys();
        if (keys == null) {
            sourceList.add(jsonMap);
            return sourceList;
        }
        String sourceKey = keys[keys.length - 1];
        Object obj = jsonMap.get(keys[0]);
        if (keys.length > 1) {
            for (int i = 1; i < keys.length - 1; i++) {
                obj = ((Map) obj).get(keys[i]);
            }
            if (obj instanceof Map) {
                Map<String, Object> source = (Map<String, Object>) ((Map) obj).get(sourceKey);
                if (source != null) sourceList.add(source);
            } else if (obj instanceof List) {
                for (Object newObj : (List) obj) {
                    if (newObj instanceof Map) {
                        Map<String, Object> source = (Map<String, Object>) ((Map) newObj).get(sourceKey);
                        if (source != null) sourceList.add(source);
                    }
                }
            }
        } else {
            if (obj != null) {
                sourceList.add(obj);
            }
        }
        return sourceList;
    }

    protected String[] getKeys() {
        return pathToResult == null ? null : (pathToResult + "").split("/");
    }

}
