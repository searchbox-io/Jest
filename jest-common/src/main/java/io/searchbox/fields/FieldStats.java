package io.searchbox.fields;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.params.Parameters;

import java.util.HashMap;
import java.util.Map;

public class FieldStats extends GenericResultAbstractAction {

    protected FieldStats(FieldStats.Builder builder) {
        super(builder);

        this.indexName = builder.index;

        Map<String, Object> fieldStatsBody = new HashMap<>();
        fieldStatsBody.put("fields", builder.fields);

        this.payload = fieldStatsBody;

        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    protected String buildURI() {
        String buildURI = super.buildURI();
        if (buildURI.isEmpty())
            return "_field_stats";

        return buildURI + "/_field_stats";
    }


    public static class Builder extends AbstractAction.Builder<FieldStats, FieldStats.Builder> {

        private String index;
        private Object fields;

        public Builder(Object fields) {
            this.fields = fields;
        }

        public FieldStats.Builder setIndex(String index) {
            this.index = index;
            return this;
        }

        public FieldStats.Builder setLevel(String level) {
            parameters.put(Parameters.LEVEL, level);
            return this;
        }

        @Override
        public FieldStats build() {
            return new FieldStats(this);
        }
    }
}
