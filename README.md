Jest
====

ElasticSearch Java Rest Client.


Get Elasticsearch Http Client
------------------------------

```java
ElasticSearchHttpClient client = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject()
```

Register default index and type
------------------------------
```java
client.registerDefaultIndex("twitter")
client.registerDefaultType("tweet")
```

Create a Source
-----------------
Some example of Source creation:

From elasticsearch jsonBuilder

```java
new Source(jsonBuilder()
.startObject()
.field("user", "kimchy")
.field("postDate", "date")
.field("message", "trying out Elastic Search")
.endObject().string())
```

From Map

```java
Map map = new LinkedHashMap()
map.put("name", null)
map.put("client", null)
source = new Source(map)
```

Or from any java bean:

```java
MyBean obj = new MyBean()
obj.setValidUser(true)
obj.setMessage("JEST java client api")
obj.setUser("JEST")
obj.setUserId(111111)
source = new Source(obj)
```

Index
---------------
```java
ElasticSearchResult result = client.execute(new Index("indexName","typeName","id",Object Source))

ElasticSearchResult result = client.execute(new Index("indexName","typeName",Object Source))

ElasticSearchResult result = client.execute(new Index(Object Source))

List<Object> sources = new ArrayList<Object>()

ElasticSearchResult result = client.execute(new Index(sources))
```

Delete
--------------

```java
ElasticSearchResult result = client.execute(new Delete("indexName","typeName","id"))

ElasticSearchResult result = client.execute(new Delete("indexName","typeName"))

ElasticSearchResult result = client.execute(new Delete("indexName"))

String[] ids = new String[3]
ElasticSearchResult result = client.execute(new Delete(ids))
```

Get
--------------
```java
ElasticSearchResult result = client.execute(new Get("indexName","typeName","id"))

Doc doc = new Doc("indexName","typeName","id")
ElasticSearchResult result = client.execute(new Get(doc))

List<Doc> docs = new ArrayList<Doc>()
ElasticSearchResult result = client.execute(new Get(docs))

String[] ids = new String[3]
ElasticSearchResult result = client.execute(new Get(ids))
```

Search
-----------

```java
QueryBuilder query = boolQuery()
                .must(termQuery("content", "JEST"))
                .must(termQuery("content", "JAVA"))
                .mustNot(termQuery("content", "Elasticsearch"))
                .should(termQuery("content", "search"))
```


```java
ElasticSearchResult result = client.execute(new Search(query))
```

Also you can add multiple index name or type to the search

```java
search.addIndex("twitter")
search.addType("tweet")
```

Create Index
--------------
```java
ElasticSearchResult result =  client.execute(new CreateIndex("newindex"))

ElasticSearchResult result =  client.execute(new CreateIndex("newindex"),Settings settings)

ElasticSearchResult result =  client.execute(new CreateIndex("newindex"),String jsonSettingsFile)

```


**To Be Continued**
