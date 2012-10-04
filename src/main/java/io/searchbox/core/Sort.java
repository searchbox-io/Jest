package io.searchbox.core;


public class Sort {
	public enum Sorting { ASC, DESC }
	
	private String field = null;
	private Sorting direction = null;
	
	public Sort(String field) {
		this.field = field;
	}
	
	public Sort(String field, Sorting direction) {
		this.field = field;
		this.direction = direction;
	}
	
	public String toString() {
		if(direction == null)
			return "\"" + this.field + "\"";
		
		String dir = "asc";
		if(this.direction == Sorting.DESC)
			dir = "desc";	
		return "{ \"" + this.field + "\" : {\"order\" : \"" + dir +"\"} }";
	}

}