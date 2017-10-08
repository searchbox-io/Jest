package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.AbstractMultiTypeActionBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * @author Bartosz Polnik
 */
public class Cat extends AbstractAction<CatResult> {
    private final static String PATH_TO_RESULT = "result";
    private final String operationPath;

    protected <T extends AbstractAction.Builder<Cat, ? extends Builder> & CatBuilder> Cat(T builder) {
        super(builder);
        this.operationPath = builder.operationPath();
        setURI(buildURI());
    }

    @Override
    protected String buildURI() {
        String uriSuffix = super.buildURI();
        return "_cat/" + this.operationPath + (uriSuffix.isEmpty() ? "" : "/") + uriSuffix;
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
    public int hashCode() {
        return new HashCodeBuilder()
                .append(super.hashCode())
                .toHashCode();
    }

    public static class IndicesBuilder extends AbstractMultiTypeActionBuilder<Cat, IndicesBuilder> implements CatBuilder {
        private static final String operationPath = "indices";

        public IndicesBuilder() {
            setHeader("content-type", "application/json");
        }

        @Override
        public Cat build() {
            return new Cat(this);
        }

        @Override
        public String operationPath() {
            return operationPath;
        }
    }

    public static class AliasesBuilder extends AbstractMultiIndexActionBuilder<Cat, AliasesBuilder> implements CatBuilder {
        private static final String operationPath = "aliases";
        public AliasesBuilder() {
            setHeader("content-type", "application/json");
        }

        @Override
        public Cat build() {
            return new Cat(this);
        }

        @Override
        public String operationPath() {
            return operationPath;
        }
    }

    protected interface CatBuilder {
        String operationPath();
    }
}
