package io.searchbox.indices.template;

/**
 * @author asierdelpozo
 * @author cihat keser
 */

public class GetTemplate extends TemplateAction {

    public GetTemplate(String name) {
        this.templateName = name;
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

}
