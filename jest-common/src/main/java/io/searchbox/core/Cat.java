package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.AbstractMultiTypeActionBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * @author Bartosz Polnik
 */
public class Cat extends AbstractAction<CatResult> {
    private final static String PATH_TO_RESULT = "result";
    public Cat(AbstractAction.Builder<Cat, ? extends Builder> builder) {
        super(builder);
    }

    Cat() {
    }

    @Override
    public String getRestMethodName() {
        return "GET";
    }

    @Override
    public String getPathToResult() {
        return PATH_TO_RESULT;
    }

    @Override
    public CatResult createNewElasticSearchResult(String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        return createNewElasticSearchResult(new CatResult(gson), responseBody, statusCode, reasonPhrase, gson);
    }

    @Override
    protected JsonObject parseResponseBody(String responseBody) {
        JsonObject result = new JsonObject();
        if (responseBody != null && !responseBody.trim().isEmpty()) {
            result.add(PATH_TO_RESULT, new JsonParser().parse(responseBody).getAsJsonArray());
        }

        return result;
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

    public static class IndicesBuilder extends AbstractMultiTypeActionBuilder<Cat, IndicesBuilder> {
        public IndicesBuilder() {
            setHeader("content-type", "application/json");
        }

        @Override
        public Cat build() {
            Cat cat = new Cat(this);
            String uriSuffix = cat.buildURI();
            cat.setURI("_cat/indices" + (uriSuffix.isEmpty() ? "" : "/") + uriSuffix);
            return cat;
        }
    }

    public static class AliasesBuilder extends AbstractMultiIndexActionBuilder<Cat, AliasesBuilder> {
        public AliasesBuilder() {
            setHeader("content-type", "application/json");
        }

        @Override
        public Cat build() {
            Cat cat = new Cat(this);
            String uriSuffix = cat.buildURI();
            cat.setURI("_cat/aliases" + (uriSuffix.isEmpty() ? "" : "/") + uriSuffix);
            return cat;
        }
    }
}
