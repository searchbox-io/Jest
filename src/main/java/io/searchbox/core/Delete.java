package io.searchbox.core;

import com.google.gson.JsonObject;
import io.searchbox.AbstractDocumentTargetedAction;
import io.searchbox.BulkableAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class Delete extends AbstractDocumentTargetedAction implements BulkableAction {

    final static Logger log = LoggerFactory.getLogger(Delete.class);

    private Delete(Builder builder) {
        super(builder);
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "DELETE";
    }

    @Override
    public String getPathToResult() {
        return "ok";
    }

    @Override
    public Boolean isOperationSucceed(JsonObject result) {
        return (result.get("ok").getAsBoolean() && result.get("found").getAsBoolean());
    }

    @Override
    public String getBulkMethodName() {
        return "delete";
    }

    public static class Builder extends AbstractDocumentTargetedAction.Builder<Delete, Builder> {

        public Delete build() {
            return new Delete(this);
        }
    }
}
