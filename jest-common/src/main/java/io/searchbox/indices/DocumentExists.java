package io.searchbox.indices;

import com.google.gson.Gson;
import io.searchbox.action.AbstractDocumentTargetedAction;

public class DocumentExists extends AbstractDocumentTargetedAction<DocumentExistsResult> {

    DocumentExists(DocumentExists.Builder builder) {
        super(builder);
    }

    @Override
    public String getRestMethodName() {
        return "HEAD";
    }

    @Override
    public DocumentExistsResult createNewElasticSearchResult(String responseBody, int statusCode, String reasonPhrase, Gson gson) {
        return createNewElasticSearchResult(new DocumentExistsResult(gson), responseBody, statusCode, reasonPhrase, gson);
    }

    public static class Builder extends AbstractDocumentTargetedAction.Builder<DocumentExists, DocumentExists.Builder> {

        public Builder(String index, String type, String id) {
            this.index(index);
            this.type(type);
            this.id(id);
        }

        public DocumentExists build() {
            return new DocumentExists(this);
        }
    }
}