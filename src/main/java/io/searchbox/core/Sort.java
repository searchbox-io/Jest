package io.searchbox.core;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

/**
 * @author Riccardo Tasso
 */

// TODO: 
// * Geo Distance Sorting (Lat Lon as Properties, Lat Lon as String, Geohash, Lat Lon as Array)
// * Script Based Sorting
// * Track Scores (it should be in the Search object)

public class Sort {
	public enum Sorting { ASC, DESC }
	public enum Missing { LAST, FIRST }
	
	private String field = null;
	private Sorting direction = null;
	private Object missing = null;
	private Boolean unmapped = null;
	
	public Sort(String field) {
		this.field = field;
	}
	
	public Sort(String field, Sorting direction) {
		this.field = field;
		this.direction = direction;
	}
	
	/**
	 * 
	 * @param m should be a Missing object (LAST or FIRST) or a custom value (String, Integer, Double, ...)
	 */
	public void setMissing(Object m) {
		this.missing = m;
	}
	
	public void setIgnoreUnmapped() {
		this.unmapped = true;
	}
	
	public String toString() {
		// simple case
		if(direction == null && missing == null && unmapped == null)
			return "\"" + this.field + "\"";
		
		// build of complex cases

		Gson gson = new Gson(); // Gson will help
		
		Map<String, Object> obj = new HashMap<String, Object>();
		
		if(direction != null) {
			String dir = "asc";
			if(direction == Sorting.DESC)
				dir = "desc";
			obj.put("order", dir);
		}
		
		if(missing != null) {
			Object miss = null;
			if(this.missing instanceof Missing) {
				Missing current = (Missing) this.missing;
				if(current == Missing.LAST) miss = "_last";
				else miss = "_first";
			} else {
				miss = this.missing;
			}
			obj.put("missing", miss);
		}
		
		if(unmapped != null) {
			obj.put("ignore_unmapped", unmapped);
		}
		
		String json = gson.toJson(obj);
		
		return "{ \"" + this.field + "\" : " + json + "}";
	}

}