package io.searchbox.indices.reindex;

import io.searchbox.action.GenericResultAbstractAction;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableMap;

/**
 * @author fabien baligand
 */
public class Reindex extends GenericResultAbstractAction {

    Reindex(Builder builder) {
        super(builder);

        Map<String, Object> payload = new HashMap<>();
        payload.put("source", builder.source);
        payload.put("dest", builder.dest);
        if (builder.conflicts != null) {
            payload.put("conflicts", builder.conflicts);
        }
        if (builder.size != null) {
            payload.put("size", builder.size);
        }
        if (builder.script != null) {
            payload.put("script", builder.script);
        }
        this.payload = ImmutableMap.copyOf(payload);
        
        setURI(buildURI());
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
