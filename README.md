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
jsonBuilder()
.startObject()
.field("user", "kimchy")
.field("postDate", "date")
.field("message", "trying out Elastic Search")
.endObject().string()
```

From Map

```java
Map map = new LinkedHashMap()
map.put("name", null)
map.put("client", null)
```

Or from any java bean:

```java
MyBean obj = new MyBean()
obj.setValidUser(true)
obj.setMessage("JEST java client api")
obj.setUser("JEST")
obj.setUserId(111111)
```

Index
---------------
```java
ElasticSearchResult result = client.execute(new Index.Builder(source).index("twitter").type("tweet").id("1").build());

ElasticSearchResult result = client.execute(new Index.Builder(source).index("twitter").type("tweet").build())

ElasticSearchResult result = client.execute(new Index.Builder(source).build())

It is possible to annotate a field of Source object by @JESTID annotation
then JEST automatically set annotated field as an id

```

Delete
--------------

```java
ElasticSearchResult result = client.execute(new Delete.Builder("twitter", "tweet").id("1").build())

ElasticSearchResult result = client.execute(new Delete.Builder("twitter", "tweet").build())

```

Get
--------------
```java
ElasticSearchResult result = client.execute( new Get.Builder("1").index("twitter").type("tweet").build())

```

MultiGet
--------------
```java
ElasticSearchResult result = client.execute( new MultiGet(new String[]{"1", "2", "3"}))

```

Update
--------------
```java
ElasticSearchResult result = client.execute(new Update.Builder(script).index("twitter").type("tweet").id("1").build())

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

Also you can add multiple index or type to the search

```java
search.addIndex("twitter")
search.addType("tweet")
```

Multi Search
--------------
```java
MultiSearch multiSearch = new MultiSearch()
multiSearch.addSearch(new Search(query))
multiSearch.addSearch(anOtherSearch)

ElasticSearchResult result = client.execute(multiSearch)

```

Percolate
--------------
```java
ElasticSearchResult result = client.execute(new Percolate("twitter","percolateQuery",query))

```

Bulk
--------------
```java

Bulk bulk = new Bulk();
bulk.addIndex(index);
bulk.addDelete(delete);

ElasticSearchResult result = client.execute(bulk)

```

Count
--------------
```java

Count count = new Count(query)

ElasticSearchResult result = client.execute(count)

```
Also you can add multiple index or type to the count

```java
count.addIndex("twitter")
count.addType("tweet")
```

Delete By Query
--------------
```java
ElasticSearchResult result = client.execute(new DeleteByQuery(query))

```

More Like this
--------------
```java
ElasticSearchResult result = client.execute(new MoreLikeThis.Builder("1").query(query).index("twitter").type("tweet").build())

```

Validate
--------------
```java
ElasticSearchResult result = client.execute(new Validate.Builder(query).index("twitter").type("tweet").build())

```

Explain
--------------
```java
ElasticSearchResult result = client.execute(new Explain.Builder(query).index("twitter").type("tweet").id("1").build())

```


Create Index
--------------
```java
ElasticSearchResult result =  client.execute(new CreateIndex("newindex"))

ElasticSearchResult result =  client.execute(new CreateIndex("newindex"),Settings settings)

ElasticSearchResult result =  client.execute(new CreateIndex("newindex"),String jsonSettingsFile)

```

Delete Index
--------------
```java
ElasticSearchResult result =  client.execute(new DeleteIndex("twitter"))

```


**To Be Continued**
