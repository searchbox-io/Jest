package io.searchbox.core;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import io.searchbox.client.JestResult;

/**
 * Created by Lior Knaany on 12/26/16.
 */
public class UpdateByQueryResult extends JestResult {

    public UpdateByQueryResult(Gson gson) {
        super(gson);
    }

    public boolean didTimeOut() {
        return (jsonObject != null && jsonObject.has("timedOut")) ? jsonObject.get("timedOut").getAsBoolean() : false;
    }

    public long getConflictsCount() {
        return (jsonObject != null && jsonObject.has("version_conflicts")) ? jsonObject.get("version_conflicts").getAsLong() : 0L;
    }

    public long getMillisTaken() {
        return (jsonObject != null && jsonObject.has("took")) ? jsonObject.get("took").getAsLong() : 0L;
    }

    public long getUpdatedCount() {
        return (jsonObject != null && jsonObject.has("updated")) ? jsonObject.get("updated").getAsLong() : 0L;
    }

    public int getBatchCount() {
        return (jsonObject != null && jsonObject.has("batches")) ? jsonObject.get("batches").getAsInt() : 0;
    }

    public int getRetryCount() {
        return (jsonObject != null && jsonObject.has("retries")) ? jsonObject.get("retries").getAsInt() : 0;
    }

    public int getNoopCount() {
        return (jsonObject != null && jsonObject.has("noops")) ? jsonObject.get("noops").getAsInt() : 0;
    }

    public JsonArray getFailures() {
        return (jsonObject != null && jsonObject.has("failures")) ? jsonObject.get("failures").getAsJsonArray() : null;
    }

    @Override
    public String toString() {
        return "Updated: "           + getUpdatedCount()
                + ", conflicts: "    + getConflictsCount()
                + ", time taken: "   + getMillisTaken()
                + ", did time out: " + didTimeOut()
                + ", batches: "      + getBatchCount()
                + ", retries: "      + getRetryCount()
                + ", noops: "        + getNoopCount();
    }
}
