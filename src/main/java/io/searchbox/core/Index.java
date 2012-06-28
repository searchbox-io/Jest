package io.searchbox.core;

import org.apache.log4j.Logger;

import java.util.List;

/**
 * @author Dogukan Sonmez
 */


public class Index extends AbstractAction implements Action {

    private static Logger log = Logger.getLogger(Index.class.getName());

    protected Index() {
    }

    public Index(String indexName,String typeName,String id,Object source){
        if (!isValid(indexName,typeName,id)) throw new RuntimeException("Invalid index parameters cannot be set for index");
        setRestMethodName("PUT");
        setData(source);
        setURI(buildURI(indexName,typeName,id));
    }

    public Index(String indexName,String typeName,Object source){
        if (!isValid(indexName,typeName)) throw new RuntimeException("Invalid index parameters be set for index");
        setRestMethodName("POST");
        setData(source);
        setURI(buildURI(indexName,typeName,null));
    }

    public Index(String indexName,String typeName,List<Object> sources){
        if (!isValid(indexName,typeName)) throw new RuntimeException("Invalid index parameters be set for index");
        setRestMethodName("POST");
        setData(sources);
        setURI(null);
    }

    public Index(String typeName,Object source,String id){
        if (!isValid(typeName,id)) throw new RuntimeException("Invalid index parameters be set for index");
        setRestMethodName("PUT");
        setData(source);
        setURI(null);
    }

    public Index(String typeName,Object source){
        setRestMethodName("POST");
        setData(source);
        setURI(null);
    }

    public Index(String typeName,List<Object> sources){
        setRestMethodName("POST");
        setData(sources);
        setURI(null);
    }

    public Index(Object source,String id){
        setRestMethodName("PUT");
        setData(source);
        setURI(null);
    }

    public Index(Object source){
        setRestMethodName("POST");
        setData(source);
        setURI(null);
    }

    public Index(List<Object> sources){
        setRestMethodName("POST");
        setData(sources);
        setURI(null);
    }

    @Override
    public String getName() {
        return "INDEX";
    }
}
