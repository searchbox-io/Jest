package io.searchbox.core;

import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static junit.framework.Assert.*;

/**
 * @author Dogukan Sonmez
 */


public class SortTest {

    @Test
    public void simpleTest() {
    	Sort s = new Sort("my_field");
    	assertEquals("\"my_field\"", s.toString());
    }
    
    @Test
    public void complexTest() {
    	Sort s = new Sort("my_field", Sort.Sorting.ASC);
    	assertEquals("{ \"my_field\" : {\"order\" : \"asc\"} }", s.toString());
    	
    	s = new Sort("my_field", Sort.Sorting.DESC);
    	assertEquals("{ \"my_field\" : {\"order\" : \"desc\"} }", s.toString());
    }

}
