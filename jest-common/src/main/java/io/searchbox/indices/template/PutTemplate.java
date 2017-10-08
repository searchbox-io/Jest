package io.searchbox.indices.template;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author asierdelpozo
 * @author cihat keser
 */
public class PutTemplate extends TemplateAction {

    protected PutTemplate(Builder builder) {
        super(builder);

        this.payload = builder.source;
        setURI(buildURI());
    }

    @Override
    public String getRestMethodName() {
        return "PUT";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
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

        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .isEquals();
    }

    public static class Builder extends TemplateAction.Builder<PutTemplate, Builder> {

        private Object source;

        public Builder(String template, Object source) {
            super(template);
            this.source = source;
        }

        @Override
        public PutTemplate build() {
            return new PutTemplate(this);
        }
    }

}

