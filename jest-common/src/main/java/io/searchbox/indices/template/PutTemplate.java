package io.searchbox.indices.template;

import com.google.gson.Gson;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author asierdelpozo
 * @author cihat keser
 */
public class PutTemplate extends TemplateAction {

    private Object source;

    public PutTemplate(Builder builder) {
        super(builder);

        this.source = builder.source;
        setURI(buildURI());
    }

    @Override
    public Object getData(Gson gson) {
        return source;
    }

    @Override
    public String getRestMethodName() {
        return "PUT";
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .appendSuper(super.hashCode())
                .append(source)
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

        PutTemplate rhs = (PutTemplate) obj;
        return new EqualsBuilder()
                .appendSuper(super.equals(obj))
                .append(source, rhs.source)
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

