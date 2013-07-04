package io.searchbox.indices.template;

/**
 * @author asierdelpozo
 * @author cihat keser
 */
public class DeleteTemplate extends TemplateAction {

    public DeleteTemplate(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "DELETE";
    }

    public static class Builder extends TemplateAction.Builder<DeleteTemplate, Builder> {

        public Builder(String template) {
            super(template);
        }

        @Override
        public DeleteTemplate build() {
            return new DeleteTemplate(this);
        }
    }

}
