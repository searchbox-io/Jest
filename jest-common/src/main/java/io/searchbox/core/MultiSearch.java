package io.searchbox.core;

import io.searchbox.action.GenericResultAbstractAction;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.gson.Gson;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class MultiSearch extends GenericResultAbstractAction {

	private Collection<Search> searches;
	private String query;

	public MultiSearch(Builder builder) {
		super(builder);
		this.query = builder.query;
		this.searches = builder.searchList;
		setURI(buildURI());
	}

	@Override
	public String getRestMethodName() {
		return "POST";
	}

	@Override
	public Object getData(Gson gson) {
		/*
		    {"index" : "test"}
		    {"query" : {"match_all" : {}}, "from" : 0, "size" : 10}
		    {"index" : "test", "search_type" : "count"}
		    {"query" : {"match_all" : {}}}
		    {}
		    {"query" : {"match_all" : {}}}
		 */
		String data;

		if (searches.isEmpty()) {
			data = query;
		} else {

			StringBuilder sb = new StringBuilder();
			for (Search search : searches) {
				sb.append("{\"index\" : \"").append(search.getIndex());
				if (StringUtils.isNotBlank(search.getType())) {
					sb.append("\", \"type\" : \"").append(search.getType());
				}
				sb.append("\"}\n{\"query\" : ")
					.append(search.getData(gson))
					.append("}\n");
			}
			data = sb.toString();
		}
		return data;
	}

	@Override
	protected String buildURI() {
		StringBuilder sb = new StringBuilder();
		sb.append(super.buildURI()).append("/_msearch");
		return sb.toString();
	}

	public static class Builder extends GenericResultAbstractAction.Builder<MultiSearch, Builder> {

		private String query;
		private List<Search> searchList = new LinkedList<Search>();

		public Builder(String query) {
			this.query = query;
		}

		public Builder(Search search) {
			searchList.add(search);
		}

		public Builder(Collection<? extends Search> searches) {
			searchList.addAll(searches);
		}

		public Builder addSearch(Search search) {
			searchList.add(search);
			return this;
		}

		public Builder addSearch(Collection<? extends Search> searches) {
			searchList.addAll(searches);
			return this;
		}

		@Override
		public MultiSearch build() {
			return new MultiSearch(this);
		}
	}
}
