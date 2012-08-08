package io.searchbox.client.http;

import com.google.gson.internal.StringMap;
import io.searchbox.client.ElasticSearchResult;
import io.searchbox.core.*;
import org.elasticsearch.action.*;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.count.CountRequest;
import org.elasticsearch.action.count.CountRequestBuilder;
import org.elasticsearch.action.count.CountResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequest;
import org.elasticsearch.action.deletebyquery.DeleteByQueryRequestBuilder;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.get.*;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.mlt.MoreLikeThisRequest;
import org.elasticsearch.action.mlt.MoreLikeThisRequestBuilder;
import org.elasticsearch.action.percolate.PercolateRequest;
import org.elasticsearch.action.percolate.PercolateRequestBuilder;
import org.elasticsearch.action.percolate.PercolateResponse;
import org.elasticsearch.action.search.*;
import org.elasticsearch.action.support.PlainActionFuture;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.AdminClient;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.internal.InternalClient;
import org.elasticsearch.common.BytesHolder;
import org.elasticsearch.common.Nullable;
import org.elasticsearch.common.Unicode;
import org.elasticsearch.common.xcontent.ToXContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.elasticsearch.index.get.GetResult;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.internal.InternalSearchHit;
import org.elasticsearch.search.internal.InternalSearchHits;
import org.elasticsearch.search.internal.InternalSearchResponse;
import org.elasticsearch.threadpool.ThreadPool;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class JestElasticSearchHttpClient implements Client, InternalClient {

    private ElasticSearchHttpClient httpClient;

    public JestElasticSearchHttpClient(ElasticSearchHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public void close() {

    }

    @Override
    public AdminClient admin() {
        return null;
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response>> ActionFuture<Response> execute(Action<Request, Response, RequestBuilder> requestResponseRequestBuilderAction, Request request) {
        return null;
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response>> void execute(Action<Request, Response, RequestBuilder> requestResponseRequestBuilderAction, Request request, ActionListener<Response> responseActionListener) {
    }

    @Override
    public <Request extends ActionRequest, Response extends ActionResponse, RequestBuilder extends ActionRequestBuilder<Request, Response>> RequestBuilder prepareExecute(Action<Request, Response, RequestBuilder> requestResponseRequestBuilderAction) {
        return null;
    }

    @Override
    public ActionFuture<IndexResponse> index(IndexRequest indexRequest) {
        ElasticSearchResult result = null;
        PlainActionFuture<IndexResponse> future = PlainActionFuture.newFuture();

        try {
            result = httpClient.execute(new Index(indexRequest.index(), indexRequest.type(), indexRequest.source()));
        } catch (Exception e) {
            future.onFailure(e);
            e.printStackTrace();
        }

        if (result != null) {
            Map jsonMap = result.getJsonMap();
            future.onResponse(new IndexResponse((String) jsonMap.get("_index"), (String) jsonMap.get("_type"), (String) jsonMap.get("_id"),
                    ((Double) jsonMap.get("_version")).longValue()));
        }

        return future;
    }

    @Override
    public void index(IndexRequest indexRequest, ActionListener<IndexResponse> indexResponseActionListener) {
        ElasticSearchResult result = null;

        try {
            result = httpClient.execute(new Index(indexRequest.index(), indexRequest.type(), indexRequest.source()));
        } catch (Exception e) {
            indexResponseActionListener.onFailure(e);
            e.printStackTrace();
        }

        if (result != null) {
            Map jsonMap = result.getJsonMap();
            indexResponseActionListener.onResponse(new IndexResponse((String) jsonMap.get("_index"), (String) jsonMap.get("_type"),
                    (String) jsonMap.get("_id"), ((Double) jsonMap.get("_version")).longValue()));
        }
    }

    @Override
    public IndexRequestBuilder prepareIndex() {
        IndexRequestBuilder builder = new IndexRequestBuilder(this);
        builder.setIndex(httpClient.getDefaultIndex());
        builder.setType(httpClient.getDefaultType());
        return builder;
    }

    @Override
    public IndexRequestBuilder prepareIndex(String index, String type) {
        IndexRequestBuilder builder = new IndexRequestBuilder(this);
        builder.setIndex(index);
        builder.setType(type);
        return builder;
    }

    @Override
    public IndexRequestBuilder prepareIndex(String index, String type, @Nullable String id) {
        IndexRequestBuilder builder = new IndexRequestBuilder(this);
        builder.setIndex(index);
        builder.setType(type);
        builder.setId(id);
        return builder;
    }

    @Override
    public ActionFuture<UpdateResponse> update(UpdateRequest updateRequest) {

        ElasticSearchResult result = null;
        PlainActionFuture<UpdateResponse> future = PlainActionFuture.newFuture();

        try {
            result = httpClient.execute(new Update(updateRequest.index(), updateRequest.type(), updateRequest.id(), null));
        } catch (Exception e) {
            future.onFailure(e);
            e.printStackTrace();
        }

        if (result != null) {
            Map jsonMap = result.getJsonMap();
            future.onResponse(new UpdateResponse((String) jsonMap.get("_index"), (String) jsonMap.get("_type"), (String) jsonMap.get("_id"),
                    ((Double) jsonMap.get("_version")).longValue()));
        }

        return future;
    }

    @Override
    public void update(UpdateRequest updateRequest, ActionListener<UpdateResponse> updateResponseActionListener) {
        ElasticSearchResult result = null;

        try {
            result = httpClient.execute(new Update(updateRequest.index(), updateRequest.type(), updateRequest.id(), null));
        } catch (Exception e) {
            updateResponseActionListener.onFailure(e);
            e.printStackTrace();
        }

        if (result != null) {
            Map jsonMap = result.getJsonMap();
            updateResponseActionListener.onResponse(new UpdateResponse((String) jsonMap.get("_index"), (String) jsonMap.get("_type"), (String) jsonMap.get("_id"),
                    ((Double) jsonMap.get("_version")).longValue()));
        }
    }

    @Override
    public UpdateRequestBuilder prepareUpdate() {
        UpdateRequestBuilder builder = new UpdateRequestBuilder(this);
        builder.setIndex(httpClient.getDefaultIndex());
        builder.setType(httpClient.getDefaultType());
        return builder;
    }

    @Override
    public UpdateRequestBuilder prepareUpdate(String index, String type, String id) {
        UpdateRequestBuilder builder = new UpdateRequestBuilder(this);
        builder.setIndex(index);
        builder.setType(type);
        builder.setId(id);
        return builder;
    }


    @Override
    public ActionFuture<DeleteResponse> delete(DeleteRequest deleteRequest) {
        ElasticSearchResult result = null;
        PlainActionFuture<DeleteResponse> future = PlainActionFuture.newFuture();

        try {
            result = httpClient.execute(new Delete(deleteRequest.index(), deleteRequest.type(), deleteRequest.id()));
        } catch (Exception e) {
            future.onFailure(e);
            e.printStackTrace();
        }

        if (result != null) {
            Map jsonMap = result.getJsonMap();
            future.onResponse(new DeleteResponse((String) jsonMap.get("_index"), (String) jsonMap.get("_type"), (String) jsonMap.get("_id"),
                    ((Double) jsonMap.get("_version")).longValue(), result.isSucceeded()));
        }

        return future;
    }

    @Override
    public void delete(DeleteRequest deleteRequest, ActionListener<DeleteResponse> deleteResponseActionListener) {
        ElasticSearchResult result = null;

        try {
            result = httpClient.execute(new Delete(deleteRequest.index(), deleteRequest.type(), deleteRequest.id()));
        } catch (Exception e) {
            deleteResponseActionListener.onFailure(e);
            e.printStackTrace();
        }

        if (result != null) {
            Map jsonMap = result.getJsonMap();
            deleteResponseActionListener.onResponse(new DeleteResponse((String) jsonMap.get("_index"), (String) jsonMap.get("_type"), (String) jsonMap.get("_id"),
                    ((Double) jsonMap.get("_version")).longValue(), result.isSucceeded()));
        }
    }

    @Override
    public DeleteRequestBuilder prepareDelete() {
        DeleteRequestBuilder builder = new DeleteRequestBuilder(this);
        builder.setIndex(httpClient.getDefaultIndex());
        builder.setType(httpClient.getDefaultType());
        return builder;
    }

    @Override
    public DeleteRequestBuilder prepareDelete(String index, String type, String id) {
        DeleteRequestBuilder builder = new DeleteRequestBuilder(this);
        builder.setIndex(index);
        builder.setType(type);
        builder.setType(id);
        return builder;
    }

    @Override
    public ActionFuture<BulkResponse> bulk(BulkRequest bulkRequest) {
        return null;
    }

    @Override
    public void bulk(BulkRequest bulkRequest, ActionListener<BulkResponse> bulkResponseActionListener) {

    }

    @Override
    public BulkRequestBuilder prepareBulk() {
        return null;
    }

    @Override
    public ActionFuture<DeleteByQueryResponse> deleteByQuery(DeleteByQueryRequest deleteByQueryRequest) {
        return null;
    }

    @Override
    public void deleteByQuery(DeleteByQueryRequest deleteByQueryRequest, ActionListener<DeleteByQueryResponse> deleteByQueryResponseActionListener) {

    }

    @Override
    public DeleteByQueryRequestBuilder prepareDeleteByQuery(String... strings) {
        return null;
    }

    @Override
    public ActionFuture<GetResponse> get(GetRequest getRequest) {
        ElasticSearchResult result;
        PlainActionFuture<GetResponse> future = PlainActionFuture.newFuture();

        try {
            result = httpClient.execute(new Get(getRequest.index(), getRequest.type(), getRequest.id()));

            if (result != null) {
                future.onResponse(createGetResponse(result.getJsonMap()));
            }

        } catch (Exception e) {
            future.onFailure(e);
            e.printStackTrace();
        }

        return future;
    }

    @Override
    public void get(GetRequest getRequest, ActionListener<GetResponse> getResponseActionListener) {
        ElasticSearchResult result;

        try {
            result = httpClient.execute(new Get(getRequest.index(), getRequest.type(), getRequest.id()));

            if (result != null) {
                getResponseActionListener.onResponse(createGetResponse(result.getJsonMap()));
            }

        } catch (Exception e) {
            getResponseActionListener.onFailure(e);
            e.printStackTrace();
        }
    }

    @Override
    public GetRequestBuilder prepareGet() {
        GetRequestBuilder builder = new GetRequestBuilder(this);
        builder.setIndex(httpClient.getDefaultIndex());
        builder.setType(httpClient.getDefaultType());
        return builder;
    }

    @Override
    public GetRequestBuilder prepareGet(String index, @Nullable String type, String id) {
        GetRequestBuilder builder = new GetRequestBuilder(this);
        builder.setIndex(index);
        builder.setType(type);
        builder.setId(id);
        return builder;
    }

    // Constructors of GetResult and GetResponse are package visible therefore we have to hack them
    private GetResponse createGetResponse(Map jsonMap) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        Constructor resultConstructor = GetResult.class.getConstructor(String.class, String.class,
                String.class, long.class, boolean.class, BytesHolder.class, Map.class);
        resultConstructor.setAccessible(true);

        GetResult getResult = (GetResult) resultConstructor.newInstance(jsonMap.get("_index"), jsonMap.get("_type"),
                ((Double) jsonMap.get("_version")).longValue(), jsonMap.get("exists"), jsonMap.get("_source"), null);

        Constructor responseConstructor = GetResponse.class.getConstructor(GetResult.class);
        responseConstructor.setAccessible(true);

        return (GetResponse) responseConstructor.newInstance(getResult);
    }

    @Override
    public ActionFuture<MultiGetResponse> multiGet(MultiGetRequest multiGetRequest) {
        return null;
    }

    @Override
    public void multiGet(MultiGetRequest multiGetRequest, ActionListener<MultiGetResponse> multiGetResponseActionListener) {

    }

    @Override
    public MultiGetRequestBuilder prepareMultiGet() {
        return null;
    }

    @Override
    public ActionFuture<CountResponse> count(CountRequest countRequest) {
        return null;
    }

    @Override
    public void count(CountRequest countRequest, ActionListener<CountResponse> countResponseActionListener) {

    }

    @Override
    public CountRequestBuilder prepareCount(String... strings) {
        return null;
    }

    @Override
    public ActionFuture<SearchResponse> search(SearchRequest searchRequest) {
        PlainActionFuture<SearchResponse> future = PlainActionFuture.newFuture();
        ElasticSearchResult result = null;

        try {
            //clearing chars which are  created at byte array conversation
            String query = new String(searchRequest.source()).trim().substring(5);
            Search search = new Search(query.substring(0, query.length() - 1));

            search.addIndex(Arrays.asList(searchRequest.indices()));
            search.addType(Arrays.asList(searchRequest.types()));
            result = httpClient.execute(search);
        } catch (Exception e) {
            future.onFailure(e);
            e.printStackTrace();
        }

        if (result != null) {
            Map jsonMap = result.getJsonMap();
            Map hits = (Map) jsonMap.get("hits");

            List<StringMap> resultHits = (List) (hits.get("hits"));

            List<InternalSearchHit> hitList = new ArrayList<InternalSearchHit>();

            for (Map resultHit : resultHits) {
                InternalSearchHit hit = new InternalSearchHit(0, (String) resultHit.get("_id"), (String) resultHit.get("_type"),
                        Unicode.fromStringAsBytes(resultHit.get("_source").toString()), null);
                hitList.add(hit);
            }

            InternalSearchHits internalSearchHits = new InternalSearchHits(hitList.toArray(new InternalSearchHit[hitList.size()]), hitList.size(), ((Double) hits.get("max_score")).longValue());

            InternalSearchResponse internalSearchResponse = new InternalSearchResponse(internalSearchHits, null, (Boolean) jsonMap.get("timed_out"));
            future.onResponse(new SearchResponse(internalSearchResponse, "", ((Double) ((Map) jsonMap.get("_shards")).get("total")).intValue(),
                    ((Double) ((Map) jsonMap.get("_shards")).get("successful")).intValue(), ((Double) jsonMap.get("took")).longValue(), null));
        }

        return future;
    }

    @Override
    public void search(SearchRequest searchRequest, ActionListener<SearchResponse> searchResponseActionListener) {
        ElasticSearchResult result = null;

        try {
            //clearing chars which are  created at byte array conversation
            String query = new String(searchRequest.source()).trim().substring(5);
            Search search = new Search(query.substring(0, query.length() - 1));

            search.addIndex(Arrays.asList(searchRequest.indices()));
            search.addType(Arrays.asList(searchRequest.types()));
            result = httpClient.execute(search);
        } catch (Exception e) {
            searchResponseActionListener.onFailure(e);
            e.printStackTrace();
        }

        if (result != null) {
            Map jsonMap = result.getJsonMap();
            Map hits = (Map) jsonMap.get("hits");

            List<StringMap> resultHits = (List) (hits.get("hits"));

            List<InternalSearchHit> hitList = new ArrayList<InternalSearchHit>();

            for (Map resultHit : resultHits) {
                InternalSearchHit hit = new InternalSearchHit(0, (String) resultHit.get("_id"), (String) resultHit.get("_type"),
                        Unicode.fromStringAsBytes(resultHit.get("_source").toString()), null);
                hitList.add(hit);
            }

            InternalSearchHits internalSearchHits = new InternalSearchHits(hitList.toArray(new InternalSearchHit[hitList.size()]), hitList.size(), ((Double) hits.get("max_score")).longValue());

            InternalSearchResponse internalSearchResponse = new InternalSearchResponse(internalSearchHits, null, (Boolean) jsonMap.get("timed_out"));
            searchResponseActionListener.onResponse(new SearchResponse(internalSearchResponse, "", ((Double) ((Map) jsonMap.get("_shards")).get("total")).intValue(),
                    ((Double) ((Map) jsonMap.get("_shards")).get("successful")).intValue(), ((Double) jsonMap.get("took")).longValue(), null));
        }
    }

    // Another hardcore hack goes here, is there a better way to do this ?
    @Override
    public SearchRequestBuilder prepareSearch(String... strings) {
        final SearchRequestBuilder builder = new SearchRequestBuilder(this);
        builder.internalBuilder(new SearchSourceBuilder() {
            @Override
            public XContentBuilder toXContent(XContentBuilder xContentBuilder, Params params) throws IOException {
                String query = "";
                try {
                    Field queryBuilderField = SearchSourceBuilder.class.getDeclaredField("queryBuilder");
                    queryBuilderField.setAccessible(true);
                    query = queryBuilderField.get(builder.internalBuilder()).toString();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }

                return xContentBuilder.value(query);
            }
        });

        if (strings.length > 0) {
            builder.setIndices(strings);
        } else {
            builder.setIndices(httpClient.getDefaultIndex());
        }
        return builder;
    }

    @Override
    public ActionFuture<SearchResponse> searchScroll(SearchScrollRequest searchScrollRequest) {
        return null;
    }

    @Override
    public void searchScroll(SearchScrollRequest searchScrollRequest, ActionListener<SearchResponse> searchResponseActionListener) {

    }

    @Override
    public SearchScrollRequestBuilder prepareSearchScroll(String s) {
        return null;
    }

    @Override
    public ActionFuture<MultiSearchResponse> multiSearch(MultiSearchRequest multiSearchRequest) {
        return null;
    }

    @Override
    public void multiSearch(MultiSearchRequest multiSearchRequest, ActionListener<MultiSearchResponse> multiSearchResponseActionListener) {

    }

    @Override
    public MultiSearchRequestBuilder prepareMultiSearch() {
        return null;
    }

    @Override
    public ActionFuture<SearchResponse> moreLikeThis(MoreLikeThisRequest moreLikeThisRequest) {
        return null;
    }

    @Override
    public void moreLikeThis(MoreLikeThisRequest moreLikeThisRequest, ActionListener<SearchResponse> searchResponseActionListener) {

    }

    @Override
    public MoreLikeThisRequestBuilder prepareMoreLikeThis(String s, String s1, String s2) {
        return null;
    }

    @Override
    public ActionFuture<PercolateResponse> percolate(PercolateRequest percolateRequest) {
        return null;
    }

    @Override
    public void percolate(PercolateRequest percolateRequest, ActionListener<PercolateResponse> percolateResponseActionListener) {
    }

    @Override
    public PercolateRequestBuilder preparePercolate(String index, String type) {
        PercolateRequestBuilder builder = new PercolateRequestBuilder(this);
        builder.setIndex(index);
        builder.setType(type);
        return builder;
    }

    @Override
    public ThreadPool threadPool() {
        return null;
    }
}
