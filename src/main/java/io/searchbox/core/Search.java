package io.searchbox.core;


import io.searchbox.AbstractAction;
import io.searchbox.AbstractMultiTypeActionBuilder;
import io.searchbox.core.search.sort.Sort;
import io.searchbox.params.Parameters;
import io.searchbox.params.SearchType;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Search extends AbstractAction {

    final static Logger log = LoggerFactory.getLogger(Search.class);

    private Search() {
    }

    private Search(Builder builder) {
        super(builder);
        this.indexName = builder.getJoinedIndices();
        this.typeName = builder.getJoinedTypes();

        String data;
        if (builder.sortList.size() > 0) {
            StringBuilder sorting = new StringBuilder("\"sort\": [");
            sorting.append(StringUtils.join(builder.sortList, ","));
            sorting.append("],");

            data = builder.query.replaceFirst("\\{", "\\{" + sorting.toString());
        } else {
            data = builder.query;
        }
        this.setData(data);

        setURI(buildURI());
    }

    public String getIndex() {
        return this.indexName;
    }

    public String getType() {
        return this.typeName;
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder();
        sb.append(super.buildURI()).append("/_search");
        return sb.toString();
    }

    @Override
    public String getPathToResult() {
        return "hits/hits/_source";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    public static class Builder extends AbstractMultiTypeActionBuilder<Search, Builder> {
        private String query;
        private List<Sort> sortList = new LinkedList<Sort>();

        public Builder(String query) {
            this.query = query;
        }

        public Builder setSearchType(SearchType searchType) {
            return setParameter(Parameters.SEARCH_TYPE, searchType);
        }

        public Builder addSort(Sort sort) {
            sortList.add(sort);
            return this;
        }

        public Builder addSort(Collection<Sort> sorts) {
            sortList.addAll(sorts);
            return this;
        }

        @Override
        public Search build() {
            return new Search(this);
        }
    }
}
