package io.searchbox.indices.template;

import io.searchbox.action.GenericResultAbstractAction;

/**
 * @author cihat keser
 */
public abstract class TemplateAction extends GenericResultAbstractAction {

    protected String templateName;

    protected TemplateAction(Builder builder) {
        super(builder);
        templateName = builder.template;
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder("_template/");
        sb.append(templateName);
        return sb.toString();
    }

    protected abstract static class Builder<T extends TemplateAction, K> extends GenericResultAbstractAction.Builder<T, K> {
        protected String template;

        public Builder(String template) {
            this.template = template;
        }
    }

}
