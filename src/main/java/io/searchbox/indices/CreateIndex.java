package io.searchbox.indices;

import com.google.gson.Gson;
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
    private Object settings;

    public CreateIndex(Builder builder) {
        super(builder);

        this.indexName = builder.index;
        if (builder.settings.size() > 0) {
            this.settings = builder.settings;
        } else {
            this.settings = new JsonObject();
            this.isCreateOp = true;
        }
        setURI(buildURI());
    }

    @Override
    public Object getData(Gson gson) {
        return settings;
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
