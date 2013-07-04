package io.searchbox.indices;

import com.google.gson.JsonObject;
import io.searchbox.AbstractAction;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class CreateIndex extends AbstractAction {

    private boolean isCreateOp = false;

    public CreateIndex(Builder builder) {
        super(builder);
        indexName = builder.index;
        setURI(buildURI());

        if (builder.settings.size() > 0) {
            setData(builder.settings);
        } else {
            setData(new JsonObject());
            isCreateOp = true;
        }
    }

    @Override
    public String getRestMethodName() {
        return isCreateOp ? "PUT" : "POST";
    }

    public static class Builder extends AbstractAction.Builder<CreateIndex, Builder> {
        private Map<String, String> settings = new HashMap<String, String>();
        private String index;

        public Builder(String index) {
            this.index = index;
        }

        public Builder settings(Map<String, String> settings) {
            this.settings = settings;
            return this;
        }

        @Override
        public CreateIndex build() {
            return new CreateIndex(this);
        }
    }

}
