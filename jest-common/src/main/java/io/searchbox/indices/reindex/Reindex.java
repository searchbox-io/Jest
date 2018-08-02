package io.searchbox.indices.reindex;

import com.google.gson.Gson;
import io.searchbox.action.GenericResultAbstractAction;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fabien baligand
 */
public class Reindex extends GenericResultAbstractAction {

    private Object source;
    private Object destination;
    Map<String, Object> body;

    Reindex(Builder builder) {
        super(builder);

        body = new HashMap<>();

        source = builder.source;
        destination = builder.dest;

        if (builder.conflicts != null) {
            body.put("conflicts", builder.conflicts);
        }
        if (builder.size != null) {
            body.put("size", builder.size);
        }
        if (builder.script != null) {
            body.put("script", builder.script);
        }

        setURI(buildURI());
    }

    @Override
    public String getData(Gson gson) {

        if (this.source != null) {
            if (this.source instanceof String) {
                this.body.put("source", gson.fromJson((String) this.source, Map.class));
            } else if (this.source instanceof Map) {
                Map<String, Object> source = new HashMap<String, Object>((Map) this.source);
                Object query = source.get("query");
                if (query instanceof String) {
                    Map queryMap = gson.fromJson((String) query, Map.class);
                    source.put("query", queryMap);
                    body.put("source", source);
                } else {
                    body.put("source", this.source);
                }
            }
        }

        if (this.destination != null) {
            if (this.destination instanceof String) {
                this.body.put("dest", gson.fromJson((String) this.destination, Map.class));
            } else {
                body.put("dest", this.destination);
            }
        }

        return gson.toJson(this.body);
    }

    @Override
    protected String buildURI() {
        return super.buildURI() + "/_reindex";
    }

    @Override
    public String getRestMethodName() {
        return "POST";
    }

    public static class Builder extends GenericResultAbstractAction.Builder<Reindex, Builder> {
    	
    	private Object source;
    	private Object dest;
    	private String conflicts;
    	private Long size;
    	private Object script;

        public Builder(Object source, Object dest) {
        	this.source = source;
        	this.dest = dest;
        }

        public Builder conflicts(String conflicts) {
        	this.conflicts = conflicts;
            return this;
        }

        public Builder size(Long size) {
        	this.size = size;
            return this;
        }

        public Builder script(Object script) {
        	this.script = script;
            return this;
        }

        public Builder waitForCompletion(boolean waitForCompletion) {
            return setParameter("wait_for_completion", waitForCompletion);
        }

        public Builder waitForActiveShards(int waitForActiveShards) {
            return setParameter("wait_for_active_shards", waitForActiveShards);
        }

        public Builder timeout(long timeout) {
            return setParameter("timeout", timeout);
        }

        public Builder requestsPerSecond(double requestsPerSecond) {
            return setParameter("requests_per_second", requestsPerSecond);
        }

        public Reindex build() {
            return new Reindex(this);
        }
    }

}
