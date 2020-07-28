package io.searchbox.indices.reindex;

import com.google.gson.Gson;
import io.searchbox.action.GenericResultAbstractAction;
import io.searchbox.client.config.ElasticsearchVersion;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fabien baligand
 */
public class Reindex extends GenericResultAbstractAction {

    private Object source;
    private Object destination;
    private Object script;
    Map<String, Object> body;

    Reindex(Builder builder) {
        super(builder);
        body = new HashMap<>();

        source = builder.source;
        destination = builder.dest;
        script = builder.script;

        if (builder.conflicts != null) {
            body.put("conflicts", builder.conflicts);
        }
        if (builder.size != null) {
            body.put("size", builder.size);
        }
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


        if (this.script != null) {
            if (this.script instanceof String) {
                this.body.put("script", gson.fromJson((String) this.script, Map.class));
            } else if (this.script instanceof Map) {
                Map<String, Object> script = new HashMap<String, Object>((Map) this.script);
                Object params = script.get("params");
                if (params instanceof String) {
                    Map paramMap = gson.fromJson((String) params, Map.class);
                    script.put("params", paramMap);
                    body.put("script", script);
                } else {
                    body.put("script", this.script);
                }
            }
        }

        return gson.toJson(this.body);
    }

    @Override
    protected String buildURI(ElasticsearchVersion elasticsearchVersion) {
        return super.buildURI(elasticsearchVersion) + "/_reindex";
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
