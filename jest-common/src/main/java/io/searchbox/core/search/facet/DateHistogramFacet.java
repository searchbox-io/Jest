package io.searchbox.core.search.facet;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author ferhat
 */
public class DateHistogramFacet extends Facet {

    public static final String TYPE = "date_histogram";

    private List<DateHistogram> dateHistograms;

    public DateHistogramFacet(String name, JsonObject dateHistogramFacet) {
        this.name = name;
        dateHistograms = new ArrayList<DateHistogram>();
        for (JsonElement term : dateHistogramFacet.get("entries").getAsJsonArray()) {
            JsonElement time = term.getAsJsonObject().get("time");
            JsonElement count = term.getAsJsonObject().get("count");
            DateHistogram histogram = new DateHistogram(time.getAsLong(), count.getAsLong());
            dateHistograms.add(histogram);
        }
    }

    public List<DateHistogram> getDateHistograms() {
        return dateHistograms;
    }

    public class DateHistogram {

        private Long time;
        private Long count;

        DateHistogram(Long time, Long count) {
            this.time = time;
            this.count = count;
        }

        public Long getTime() {
            return time;
        }

        public Long getCount() {
            return count;
        }
    }
}
