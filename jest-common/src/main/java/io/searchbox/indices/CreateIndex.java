package io.searchbox.indices;

import io.searchbox.action.GenericResultAbstractAction;

import java.util.Objects;

/**
 * @author Dogukan Sonmez
 * @author cihat keser
 */
public class CreateIndex extends GenericResultAbstractAction {

    protected CreateIndex(Builder builder) {
        super(builder);

        this.indexName = builder.index;
        if (builder.settings != null) {
            this.payload = builder.settings;
        } else {
            this.payload = new Object();
        }
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "PUT";
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), indexName, payload);
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
        return super.equals(obj)
                && Objects.equals(indexName, rhs.indexName)
                && Objects.equals(payload, rhs.payload);
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
