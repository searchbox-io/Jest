package io.searchbox.indices.template;

/**
 * @author asierdelpozo
 * @author cihat keser
 */

public class DeleteTemplate extends TemplateAction {

    public DeleteTemplate(String name) {
        this.templateName = name;
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "DELETE";
    }

}
