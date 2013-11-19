package io.searchbox.core.search.facet;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author ferhat
 */

public class TermsFacet extends Facet {

    public static final String TYPE = "terms";

    private Long missing;
    private Long total;
    private Long other;
    private List<Term> terms;

    public TermsFacet(String name, JsonObject termFacet) {
        this.name = name;
        missing = ( termFacet.get("missing")).getAsLong();
        total = (termFacet.get("total")).getAsLong();
        other = (termFacet.get("other")).getAsLong();

        terms = new ArrayList<Term>();
        for (JsonElement termv : termFacet.get("terms").getAsJsonArray()) {
          JsonObject term = (JsonObject) termv;
            Term entry = new Term(term.get("term").getAsString(), term.get("count").getAsInt());
            terms.add(entry);
        }
    }

    public Long getMissing() {
        return missing;
    }

    public Long getTotal() {
        return total;
    }

    public Long getOther() {
        return other;
    }

    public List<Term> terms() {
        return terms;
    }

    public class Term {
        private String name;
        private Integer count;

        public Term(String name, Integer count) {
            this.name = name;
            this.count = count;
        }

        public String getName() {
            return name;
        }

        public Integer getCount() {
            return count;
        }
    }
}
