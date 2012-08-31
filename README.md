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

MultiGet
--------------
```java

Bulk bulk = new Bulk();
bulk.addIndex(index);
bulk.addDelete(delete);

ElasticSearchResult result = client.execute(bulk)

```


Create Index
--------------
```java
ElasticSearchResult result =  client.execute(new CreateIndex("newindex"))

ElasticSearchResult result =  client.execute(new CreateIndex("newindex"),Settings settings)

ElasticSearchResult result =  client.execute(new CreateIndex("newindex"),String jsonSettingsFile)

```


**To Be Continued**
