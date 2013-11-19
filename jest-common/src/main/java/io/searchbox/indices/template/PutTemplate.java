package io.searchbox.indices.template;

import com.google.gson.Gson;

/**
 * @author asierdelpozo
 * @author cihat keser
 */
public class PutTemplate extends TemplateAction {

    private Object source;

    public PutTemplate(Builder builder) {
        super(builder);

        this.source = builder.source;
        setURI(buildURI());
    }

    @Override
    public Object getData(Gson gson) {
        return source;
    }

    @Override
    public String getRestMethodName() {
        return "PUT";
    }

    public static class Builder extends TemplateAction.Builder<PutTemplate, Builder> {

        private Object source;

        public Builder(String template, Object source) {
            super(template);
            this.source = source;
        }

        @Override
        public PutTemplate build() {
            return new PutTemplate(this);
        }
    }

}

