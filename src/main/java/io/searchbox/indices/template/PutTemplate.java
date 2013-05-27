package io.searchbox.indices.template;

/**
 * @author asierdelpozo
 * @author cihat keser
 */

public class PutTemplate extends TemplateAction {

    public PutTemplate(String name, Object source) {
        this.templateName = name;
        setURI(buildURI());
        setData(source);
    }

    @Override
    public String getRestMethodName() {
        return "PUT";
    }

}

