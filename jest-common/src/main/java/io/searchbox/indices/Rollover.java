package io.searchbox.indices;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.GenericResultAbstractAction;

import java.util.HashMap;
import java.util.Map;

public class Rollover extends GenericResultAbstractAction {

    protected Rollover(Rollover.Builder builder) {
        super(builder);

        this.indexName = builder.index;
        Map<String, Object> rolloverConditions = new HashMap<>();
        if (builder.conditions != null) {
            rolloverConditions.put("conditions", builder.conditions);
        }
        if (builder.settings != null) {
            rolloverConditions.put("settings", builder.settings);
        }
        this.payload = rolloverConditions;

        setURI(buildURI() + (builder.isDryRun ? "?dry_run" : ""));
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_rollover";
    }


    public static class Builder extends AbstractAction.Builder<Rollover, Rollover.Builder> {

        private String index;
        private Object conditions;
        private Object settings;
        private boolean isDryRun;

        public Builder(String index) {
            this.index = index;
        }

        public Rollover.Builder conditions(Object conditions) {
            this.conditions = conditions;
            return this;
        }

        public Rollover.Builder setDryRun(boolean dryRun) {
            this.isDryRun = dryRun;
            return this;
        }

        public Rollover.Builder settings(Object settings) {
            this.settings = settings;
            return this;
        }

        @Override
        public Rollover build() {
            return new Rollover(this);
        }
    }
}
