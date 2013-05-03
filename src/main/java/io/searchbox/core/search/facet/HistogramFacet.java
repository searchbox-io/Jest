package io.searchbox.core.search.facet;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author ferhat
 */
public class HistogramFacet extends Facet {

    public static final String TYPE = "histogram";

    private List<Histogram> histograms;

    public HistogramFacet(String name, JsonObject histogramFacet) {
        this.name = name;
        histograms = new ArrayList<Histogram>();

        for (JsonElement termv :  histogramFacet.get("entries").getAsJsonArray()) {
          JsonObject term = (JsonObject) termv;
            Histogram histogram = new Histogram(term.get("key").getAsLong(), term.get("count").getAsLong());
            histograms.add(histogram);
        }
    }

    public List<Histogram> getHistograms() {
        return histograms;
    }

    public class Histogram {
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
