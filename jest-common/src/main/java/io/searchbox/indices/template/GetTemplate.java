package io.searchbox.indices.template;

/**
 * @author asierdelpozo
 * @author cihat keser
 */
public class GetTemplate extends TemplateAction {

    protected GetTemplate(Builder builder) {
        super(builder);
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    public static class Builder extends TemplateAction.Builder<GetTemplate, Builder> {

        public Builder(String template) {
            super(template);
        }

        @Override
        public GetTemplate build() {
            return new GetTemplate(this);
        }
    }

}
