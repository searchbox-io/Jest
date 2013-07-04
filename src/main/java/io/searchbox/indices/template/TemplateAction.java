package io.searchbox.indices.template;

import io.searchbox.AbstractAction;

/**
 * @author cihat keser
 */
public abstract class TemplateAction extends AbstractAction {

    protected String templateName;

    public TemplateAction(Builder builder) {
        super(builder);
        templateName = builder.template;
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder("_template/");
        sb.append(templateName);
        return sb.toString();
    }

    protected abstract static class Builder<T extends TemplateAction, K> extends AbstractAction.Builder<T, K> {
        protected String template;

        public Builder(String template) {
            this.template = template;
        }
    }

}
