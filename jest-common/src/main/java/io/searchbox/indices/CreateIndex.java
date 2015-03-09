package io.searchbox.indices;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.searchbox.action.GenericResultAbstractAction;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class CreateIndex extends GenericResultAbstractAction {

    private boolean isCreateOp = false;
    private Object settings;

    public CreateIndex(Builder builder) {
        super(builder);

        this.indexName = builder.index;
        if (builder.settings != null) {
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(isCreateOp)
                .append(settings)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }

        CreateIndex rhs = (CreateIndex) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(isCreateOp, rhs.isCreateOp)
                .append(settings, rhs.settings)
                .isEquals();
    }

    public static class Builder extends GenericResultAbstractAction.Builder<CreateIndex, Builder> {
        private Object settings = null;
        private String index;

        public Builder(String index) {
            this.index = index;
        }

        public Builder settings(Object settings) {
            this.settings = settings;
            return this;
        }

        @Override
        public CreateIndex build() {
            return new CreateIndex(this);
        }
    }

}
