package io.searchbox.core.search.facet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ferhat
 */

/*
"facets":{
    "tag":{
        "_type":"terms",
                "missing":0,
                "total":2,
                "other":0,
                "terms":[
        {
            "term":"value",
                "count":2
        }
        ]
    }
}
*/

public class TermsFacet {
    private String name;
    private Double missing;
    private Double total;
    private Double other;
    private List<Entry> terms;

    public TermsFacet(String name, Map termFacet) {
        this.name = name;
        missing = (Double) termFacet.get("missing");
        total = (Double) termFacet.get("total");
        other = (Double) termFacet.get("other");

        terms = new ArrayList<Entry>();
        for (Map term : (List<Map>) termFacet.get("terms")) {
            Entry entry = new Entry(term.get("term").toString(), ((Double) term.get("count")).intValue());
            terms.add(entry);
        }
    }

    public String getName() {
        return name;
    }

    public Double getMissing() {
        return missing;
    }

    public Double getTotal() {
        return total;
    }

    public Double getOther() {
        return other;
    }

    class Entry {
        private String termValue;
        private Integer termCount;

        public Entry(String termValue, Integer termCount) {
            this.termCount = termCount;
            this.termValue = termValue;
        }

        public String getTermValue() {
            return termValue;
        }

        public Integer getTermCount() {
            return termCount;
        }
    }
}
