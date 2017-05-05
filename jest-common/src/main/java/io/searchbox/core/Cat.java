package io.searchbox.core;

import com.google.common.base.Joiner;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import io.searchbox.action.AbstractAction;
import io.searchbox.action.AbstractMultiIndexActionBuilder;
import io.searchbox.action.AbstractMultiTypeActionBuilder;

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
        if (responseBody == null || responseBody.trim().isEmpty()) {
            return new JsonObject();
        }

        JsonElement parsed = new JsonParser().parse(responseBody);
        if (parsed.isJsonArray()) {
            JsonObject result = new JsonObject();
            result.add(PATH_TO_RESULT, parsed.getAsJsonArray());
            return result;
        } else {
            throw new JsonSyntaxException("Cat response did not contain a JSON Array");
        }
    }

    public static class IndicesBuilder extends AbstractMultiTypeActionBuilder<Cat, IndicesBuilder> implements CatBuilder {
        private static final String operationPath = "indices";

        public IndicesBuilder() {
            setHeader("accept", "application/json");
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
            setHeader("accept", "application/json");
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

    public static class ShardsBuilder extends AbstractMultiIndexActionBuilder<Cat, ShardsBuilder> implements CatBuilder {
        private static final String operationPath = "shards";
        public ShardsBuilder() {
            setHeader("accept", "application/json");
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

        @Override
        public String getJoinedIndices() {
            if (indexNames.size() > 0) {
                return Joiner.on(',').join(indexNames);
            } else {
                return null;
            }
        }
    }

    public static class SegmentsBuilder extends AbstractMultiIndexActionBuilder<Cat, SegmentsBuilder> implements CatBuilder {
        private static final String operationPath = "segments";
        public SegmentsBuilder() {
            setHeader("accept", "application/json");
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

        @Override
        public String getJoinedIndices() {
            return indexNames.size() > 0 ? Joiner.on(',').join(indexNames) : null;
        }
    }

    public static class NodesBuilder extends AbstractAction.Builder<Cat, NodesBuilder> implements CatBuilder {
        private static final String operationPath = "nodes";
        public NodesBuilder() {
            setHeader("accept", "application/json");
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
