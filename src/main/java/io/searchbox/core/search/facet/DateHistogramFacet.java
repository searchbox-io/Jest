package io.searchbox.core.search.facet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ferhat
 */
public class DateHistogramFacet extends Facet {

    public static final String TYPE = "date_histogram";

    private List<DateHistogram> dateHistograms;

    public DateHistogramFacet(String name, Map dateHistogramFacet) {
        this.name = name;
        dateHistograms = new ArrayList<DateHistogram>();
        for (Map term : (List<Map>) dateHistogramFacet.get("entries")) {
            DateHistogram histogram = new DateHistogram(((Double) term.get("time")).longValue(), ((Double) term.get("count")).longValue());
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
