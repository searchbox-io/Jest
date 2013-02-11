package io.searchbox.core.search.facet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ferhat
 */

public class TermsFacet extends Facet {

    public static final String TYPE = "terms";

    private Long missing;
    private Long total;
    private Long other;
    private List<Term> terms;

    public TermsFacet(String name, Map termFacet) {
        this.name = name;
        missing = ((Double) termFacet.get("missing")).longValue();
        total = ((Double) termFacet.get("total")).longValue();
        other = ((Double) termFacet.get("other")).longValue();

        terms = new ArrayList<Term>();
        for (Map term : (List<Map>) termFacet.get("terms")) {
            Term entry = new Term(term.get("term").toString(), ((Double) term.get("count")).intValue());
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
