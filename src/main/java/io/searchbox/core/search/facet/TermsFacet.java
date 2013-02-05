package io.searchbox.core.search.facet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ferhat
 */

public class TermsFacet {
    private String name;
    private Long missing;
    private Long total;
    private Long other;
    private List<Entry> entries;

    public TermsFacet(String name, Map termFacet) {
        this.name = name;
        missing = ((Double) termFacet.get("missing")).longValue();
        total = ((Double) termFacet.get("total")).longValue();
        other = ((Double) termFacet.get("other")).longValue();

        entries = new ArrayList<Entry>();
        for (Map term : (List<Map>) termFacet.get("terms")) {
            Entry entry = new Entry(term.get("term").toString(), ((Double) term.get("count")).intValue());
            entries.add(entry);
        }
    }

    public String getName() {
        return name;
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

    public List<Entry> entries() {
        return entries;
    }

    class Entry {
        private String term;
        private Integer count;

        public Entry(String term, Integer count) {
            this.term = term;
            this.count = count;
        }

        public String getTerm() {
            return term;
        }

        public Integer getCount() {
            return count;
        }
    }
}
