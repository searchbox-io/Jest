package io.searchbox.core.search.facet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ferhat
 */
public class HistogramFacet {

    private String name;
    private List<Histogram> histograms;

    public HistogramFacet(String name, Map histogramFacet) {
        this.name = name;
        histograms = new ArrayList<Histogram>();

        for (Map term : (List<Map>) histogramFacet.get("entries")) {
            Histogram histogram = new Histogram(((Double) term.get("key")).longValue(), ((Double) term.get("count")).longValue());
            histograms.add(histogram);
        }
    }

    public String getName() {
        return name;
    }

    public List<Histogram> getHistograms() {
        return histograms;
    }

    class Histogram {
        private Long key;
        private Long count;

        Histogram(Long key, Long count) {
            this.key = key;
            this.count = count;
        }

        public Long getKey() {
            return key;
        }

        public Long getCount() {
            return count;
        }
    }

}
