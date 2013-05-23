package io.searchbox.indices.template;

import io.searchbox.AbstractAction;

/**
 * @author cihat keser
 */
public abstract class TemplateAction extends AbstractAction {

    protected String templateName;

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder("_template/");
        sb.append(templateName);
        return sb.toString();
    }

}
