package io.searchbox.indices.template;

/**
 * @author asierdelpozo
 * @author cihat keser
 */
public class PutTemplate extends TemplateAction {

    public PutTemplate(Builder builder) {
        super(builder);
        setURI(buildURI());
        setData(builder.source);
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

