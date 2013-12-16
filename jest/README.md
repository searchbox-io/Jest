#Jest

Jest is a Java HTTP Rest client for [ElasticSearch](http://www.elasticsearch.org).

ElasticSearch is an Open Source (Apache 2), Distributed, RESTful, Search Engine built on top of Apache Lucene.

ElasticSearch already has a Java API which is also used by ElasticSearch internally, [but Jest fills a gap, it is the missing client for ElasticSearch Http Rest interface](#comparison-to-native-api).

>Read great [introduction](http://www.ibm.com/developerworks/java/library/j-javadev2-24/index.html?ca=drs-) to ElasticSearch and Jest from IBM Developer works.

Installation
------------

Jest maven repository is hosted on [Sonatype](http://www.sonatype.org).

Add Sonatype repository definition to your root pom.xml

``` xml
<repositories>
.
.
 <repository>
   <id>sonatype</id>
   <name>Sonatype Groups</name>
   <url>https://oss.sonatype.org/content/groups/public/</url>
 </repository>
.
.
</repositories>

```

Add Jest as a dependency to your project.


``` xml
<dependency>
  <groupId>io.searchbox</groupId>
  <artifactId>jest</artifactId>
  <version>0.0.5</version>
</dependency>
```

>Ensure to check [Changelog](https://github.com/searchbox-io/Jest/wiki/Changelog).

Usage
------------

>Jest has a sample application can be found [here](https://github.com/searchbox-io/java-jest-sample).

To start using Jest first we need a JestClient;

``` java
 // Configuration
 ClientConfig clientConfig = new ClientConfig.Builder("http://localhost:9200").multiThreaded(true).build();

 // Construct a new Jest client according to configuration via factory
 JestClientFactory factory = new JestClientFactory();
 factory.setClientConfig(clientConfig);
 JestClient client = factory.getObject();
```
> JestClient is designed to be singleton, don't construct it for each request!

### Creating an Index

You can create an index via Jest with ease;

``` java
client.execute(new CreateIndex.Builder("articles").build());
```

Index setting can be passed as a JSON file or ElasticSearch Settings;

via JSON;

``` java
String settings = "\"settings\" : {\n" +
                "        \"number_of_shards\" : 5,\n" +
                "        \"number_of_replicas\" : 1\n" +
                "    }\n";

client.execute(new CreateIndex.Builder("articles").settings(ImmutableSettings.builder().loadFromSource(settings).build().getAsMap()).build());
```

via SettingsBuilder;

``` java
import org.elasticsearch.common.settings.ImmutableSettings;
.
.

ImmutableSettings.Builder settingsBuilder = ImmutableSettings.settingsBuilder();
settings.put("number_of_shards",5);
settings.put("number_of_replicas",1);

client.execute(new CreateIndex.Builder("articles").settings(settingsBuilder.build().getAsMap()).build());
```
>Add ElasticSearch dependency to use Settings api

### Creating an Index Mapping

You can create an index mapping via Jest with ease; you just need to pass the mapping source JSON document as string.

``` java
PutMapping putMapping = new PutMapping.Builder(
        "my_index",
        "my_type",
        "{ \"document\" : { \"properties\" : { \"message\" : {\"type\" : \"string\", \"store\" : \"yes\"} } } }"
).build();
client.execute(putMapping);
```

You can also use the DocumentMapper.Builder to create the mapping source.

``` java
import org.elasticsearch.index.mapper.DocumentMapper;
import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
.
.
RootObjectMapper.Builder rootObjectMapperBuilder = new RootObjectMapper.Builder("my_mapping_name").add(
        new StringFieldMapper.Builder("message").store(true)
);
DocumentMapper documentMapper = new DocumentMapper.Builder("my_index", null, rootObjectMapperBuilder).build(null);
String expectedMappingSource = documentMapper.mappingSource().toString();
PutMapping putMapping = new PutMapping.Builder(
        "my_index",
        "my_type",
        expectedMappingSource
).build();
client.execute(putMapping);
```
>Add ElasticSearch dependency to use DocumentMapper.Builder api

### Indexing Documents

ElasticSearch requires index data as JSON. There are several ways to create documents to index via Jest.
From now on, we will refer documents as source. Source objects can be String, Map or POJOs.

as JSON String;

``` java
String source = "{\"user\":\"kimchy\"}";
```

or creating JSON via ElasticSearch JSONBuilder;

``` java
String source = jsonBuilder()
.startObject()
.field("user", "kimchy")
.field("postDate", "date")
.field("message", "trying out Elastic Search")
.endObject().string();
```

as Map;

``` java
Map<String, String> source = new LinkedHashMap<String,String>()
source.put("user", "kimchy");
```

as POJO;

``` java
Article source = new Article();
source.setAuthor("John Ronald Reuel Tolkien");
source.setContent("The Lord of the Rings is an epic high fantasy novel");
```

An example of indexing given source to twitter index with type tweet;

``` java
Index index = new Index.Builder(source).index("twitter").type("tweet").build();
client.execute(index);
```

Index id can be typed explicitly;

``` java
Index index = new Index.Builder(source).index("twitter").type("tweet").id("1").build();
client.execute(index);
```

@JestId annotation can be used to mark a property of a bean as id;

```java
class Article {

@JestId
private String documentId;

}
```

Now whenever an instance of Article is indexed, index id will be value of documentId.

If @JestId value is null, it will be set the value of ElasticSearch generated "_id".

Jest also supports using JestId annotation on fields with type other than String but the
catch is then you will need to manually manage the document IDs.

```java
class NumericArticle {

@JestId
private Long documentId;

}
```

It should be noted that when a non-String type is used for the documentId, conversion
errors may occur unless you manually manage the documentId. For example if a NumericArticle
instance without a documentId is indexed then Elasticsearch will assign an automatically
generated id to that document but Jest will not be able to convert that id to Long since
all id fields are of type String in Elasticsearch and the auto generated id contains non
numeric characters.
So the non-String type support for JestId annotation is purely for ease of use and should
not be used if you plan to use the automatic id generation functionality of Elasticsearch.

### Searching Documents

Search queries can be either JSON String or created by ElasticSearch SourceBuilder
Jest works with default ElasticSearch queries, it simply keeps things as is.

As JSON;

``` java
String query = "{\n" +
            "    \"query\": {\n" +
            "        \"filtered\" : {\n" +
            "            \"query\" : {\n" +
            "                \"query_string\" : {\n" +
            "                    \"query\" : \"test\"\n" +
            "                }\n" +
            "            },\n" +
            "            \"filter\" : {\n" +
            "                \"term\" : { \"user\" : \"kimchy\" }\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}";

Search search = new Search.Builder(query)
                // multiple index or types can be added.
                .addIndex("twitter")
                .addIndex("tweet")
                .build();

JestResult result = client.execute(search);
```

By using SearchSourceBuilder;

```java
SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
searchSourceBuilder.query(QueryBuilders.matchQuery("user", "kimchy"));

Search search = new Search.Builder(searchSourceBuilder.toString())
                                // multiple index or types can be added.
                                .addIndex("twitter")
                                .addIndex("tweet")
                                .build();

JestResult result = client.execute(search);
```

>Add ElasticSearch dependency to use SearchSourceBuilder

Result can be cast to List of domain object;

``` java
JestResult result = client.execute(search);
List<Article> articles = result.getSourceAsObjectList(Article.class);
```

Please refer [ElasticSearch Query DSL](http://www.elasticsearch.org/guide/reference/query-dsl/) documentation to work with complex queries.

### Getting Documents

``` java
Get get = new Get.Builder("twitter", "1").type("tweet").build();

JestResult result = client.execute(get);
```

Result can be cast to domain object;

``` java
Get get = new Get.Builder("twitter", "1").type("tweet").build();

JestResult result = client.execute(get);

Article article = result.getSourceAsObject(Article.class);
```

### Updating Documents

```java
String script = "{\n" +
                "    \"script\" : \"ctx._source.tags += tag\",\n" +
                "    \"params\" : {\n" +
                "        \"tag\" : \"blue\"\n" +
                "    }\n" +
                "}";

client.execute(new Update.Builder(script).index("twitter").type("tweet").id("1").build());
```

### Deleting Documents

```java
client.execute(new Delete.Builder("twitter", "tweet", "1").build());
```

### Bulk Operations

ElasticSearch's bulk API makes it possible to perform many index/delete operations in a single API call. This can greatly increase the indexing speed.

```java
Bulk bulk = new Bulk.Builder()
    .defaultIndex("twitter")
    .defaultType("tweet")
    .addAction(new Index.Builder(article1).build())
    .addAction(new Index.Builder(article2).build())
    .addAction(new Delete.Builder("twitter", "tweet", "1").build())
    .build();

client.execute(bulk);
```

List of objects can be indexed via bulk api

```java
String article1 = "tweet1";
String article2 = "tweet2";

Bulk bulk = new Bulk.Builder()
                .defaultIndex("twitter")
                .defaultType("tweet")
                .addAction(Arrays.asList(
                    new Index.Builder(article1).build(),
                    new Index.Builder(article2).build()))
                .build();

client.execute(bulk);
```

### Action Parameters

ElasticSearch offers request parameters to set properties like routing, versioning, operation type etc.

For instance you can set "refresh" property to "true" while indexing a document as below;

```java
Index index = new Index.Builder("{\"user\":\"kimchy\"}")
    .index("cvbank")
    .type("candidate")
    .id("1")
    .setParameter(Parameters.REFRESH, true),
    .build();
client.execute(index);
```

### Execution Asynchronously

Jest http client support execution of action with non blocking IO asynchronously.

Following example illustrates how to execute action with jest asynchronous call.

```java
client.executeAsync(action,new JestResultHandler<JestResult>() {
    @Override
    public void completed(JestResult result) {
        ... do process result ....
    }
    @Override
    public void failed(Exception ex) {
       ... catch exception ...
    }
});
```

### Enable Host Discovery with Nodes API
------------
You need to configure the discovery options in the client config as follows:

```java
//enable host discovery
ClientConfig clientConfig = new ClientConfig.Builder("http://localhost:9200")
    .discoveryEnabled(true)
    .discoveryFrequency(1l, TimeUnit.MINUTES)
    .build();
```

This will enable new node discovery and update the list of servers in the client periodically.

### Further Reading

[Integration Tests](https://github.com/searchbox-io/Jest/tree/master/src/test/java/io/searchbox/core) are best place to see things in action.

Logging
------------
Jest is using slf4j for logging and expects you to plug in your own implementation, so log4j dependency is in "provided" scope.

For instance to use log4j implementation, add below dependency to your pom.xml

``` xml
<dependency>
 <groupId>org.slf4j</groupId>
	<artifactId>slf4j-log4j12</artifactId>
	<version>1.6.1</version>
</dependency>
```
Please read slf4j manual [here](http://www.slf4j.org/manual.html).

ElasticSearch Optional Dependency
------------
If you want to use ElasticSearch's QueryBuilder or Settings classes, ensure to add ElasticSearch dependency.

``` xml
<dependency>
    <groupId>org.elasticsearch</groupId>
    <artifactId>elasticsearch</artifactId>
    <version>${elasticsearch.version}</version>
</dependency>
```

<a id="comparison"></a>Comparison to native API
---------------------
>There are several alternative clients available when working with ElasticSearch from Java, like Jest that provides a POJO marshalling mechanism on indexing and for the search results. In this example we are using the Client that is included in ElasticSearch. By default the client doesn't use the REST API but connects to the cluster as a normal node that just doesn't store any data. It knows about the state of the cluster and can route requests to the correct node but supposedly consumes more memory. For our application this doesn't make a huge difference but for production systems that's something to think about.
><cite>-- [Florian Hopf](http://blog.florian-hopf.de/2013/05/getting-started-with-elasticsearch-part.html)</cite>

<!-- -->
>So if you have several ES clusters running different versions, then using the native (or transport) client will be a problem, and you will need to go HTTP (and Jest is the main option I think). If versioning is not an issue, the native client will be your best option as it is cluster aware (thus knows how to route your queries and does not need another hop), and also moves some computation away from your ES cluster (like merging search results that will be done locally instead of on the data node).
><cite>-- [Rotem Hermon](http://www.quora.com/ElasticSearch/What-is-the-best-client-library-for-elasticsearch)</cite>

<!-- -->
>ElasticSearch does not have Java rest client. It has only native client comes built in. That is the gap. You can add security layer to HTTP but native API. That is why none of SAAS offerings can be used with native api.
><cite>-- [Searchly](https://twitter.com/searchboxio)</cite>

Thanks
---------------------
Thanks to [JetBrains](http://www.jetbrains.com/) for providing a license for [IntelliJ IDEA](http://www.jetbrains.com/idea/) to develop this project.


Copyright and License
---------------------

Copyright 2013 www.searchly.com

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in
compliance with the License. You may obtain a copy of the License in the LICENSE file, or at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
