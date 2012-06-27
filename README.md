Jest
====

ElasticSearch Java Rest Client.


Get Elasticsearch Http Client
------------------------------

```java
ElasticSearchHttpClient client = (ElasticSearchHttpClient) new ElasticSearchClientFactory().getObject()
```

Create a Document
-----------------
```java
Document document = new Document("twitter", "tweet","1")
document.setSource(new Source("{user:\"searchboxio\"}"))
```

It is possible to create a document without source and id
and You can create a source from a java bean.
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

Index Document
---------------
```java
ElasticSearchResult result = client.execute(new Index(document))
```

Delete Document
--------------

```java
ElasticSearchResult result = client.execute(new Delete(document))
```

Get Document
--------------
```java
ElasticSearchResult result = client.execute(new Get(document))
```

Update Document
----------------
```java
ElasticSearchResult result = client.execute(new Update(document))
```

You can set update script as source of document

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



**To Be Continued**
