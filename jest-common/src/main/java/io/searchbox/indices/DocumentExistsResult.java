package io.searchbox.indices;

import com.google.gson.Gson;
import io.searchbox.client.JestResult;

public class DocumentExistsResult extends JestResult {

    DocumentExistsResult(Gson gson) {
        super(gson);
    }

    public boolean documentExists() {
        return isSucceeded();
    }
}
