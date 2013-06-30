package io.searchbox;

import org.apache.commons.lang.StringUtils;

/**
 * @author cihat keser
 */
public abstract class AbstractDocumentTargetedAction extends AbstractAction implements DocumentTargetedAction {

    protected String id;

    public AbstractDocumentTargetedAction() {
    }

    public AbstractDocumentTargetedAction(Builder builder) {
        super(builder);
    }

    @Override
    public String getIndex() {
        return indexName;
    }

    @Override
    public String getType() {
        return typeName;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    protected String buildURI() {
        StringBuilder sb = new StringBuilder(super.buildURI());

        if (StringUtils.isNotBlank(id)) {
            sb.append("/").append(id);
        }

        String uri = sb.toString();
        return uri;
    }

}
