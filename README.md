#Jest

Jest is a Java HTTP Rest client for [ElasticSearch](http://www.elasticsearch.org).

ElasticSearch is an Open Source (Apache 2), Distributed, RESTful, Search Engine built on top of Apache Lucene.

ElasticSearch already has a Java API which is also used by ElasticSearch internally, but Jest fills a gap, it is the missing client for ElasticSearch Http Rest interface.

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
  <version>0.0.3</version>
</dependency>
```

>Ensure to check [Changelog](https://github.com/searchbox-io/Jest/wiki/Changelog).

Continuous Integration
------------

[![build status](https://secure.travis-ci.org/searchbox-io/Jest.png)](http://travis-ci.org/searchbox-io/Jest)

Usage
------------

>Jest has a sample application can be found [here](https://github.com/searchbox-io/java-jest-sample).

To start using Jest first we need a JestClient;

``` java 
 // Configuration
 ClientConfig clientConfig = new ClientConfig();
 LinkedHashSet<String> servers = new LinkedHashSet<String>();
 servers.add("http://localhost:9200");
 clientConfig.getProperties().put(ClientConstants.SERVER_LIST, servers);
 clientConfig.getProperties().put(ClientConstants.IS_MULTI_THREADED, true);
 
 // Construct a new Jest client according to configuration via factory
 JestClientFactory factory = new JestClientFactory();
 factory.setClientConfig(clientConfig);
 JestClient client = factory.getObject();
```
> JestClient is designed to be signleton, don't construct it for each request!

### Creating an Index

You can create an index via Jest with ease;

``` java
client.execute(new CreateIndex("articles"));
```

Index setting can be passed as a JSON file or ElasticSearch Settings;

via JSON;

``` java
String settings = "\"settings\" : {\n" +
                "        \"number_of_shards\" : 5,\n" +
                "        \"number_of_replicas\" : 1\n" +
                "    }\n";

client.execute(new CreateIndex("articles"), settings)                        
```

via SetingsBuilder;

``` java
import org.elasticsearch.common.settings.ImmutableSettings;
.
.

ImmutableSettings.Builder settingsBuilder = ImmutableSettings.settingsBuilder();
settings.put("number_of_shards",5); 
settings.put("number_of_replicas",1); 

client.execute(new CreateIndex("articles"), settingsBuilder.build().getAsMap());
```
>Add ElasticSearch dependency to use Settings api

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
private Long documentId;

}
```

Now whenever an instance of Article is indexed, index id will be value of documentId.

If @JestId value is null, it will be set the value of ElasticSearch generated "_id".

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
            
Search search = new Search(query);
// multiple index or types can be added.
search.addIndex("twitter");
search.addType("tweet");            
            
JestResult result = client.execute(search);                       
```

By using SearchSourceBuilder;

```java
SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
searchSourceBuilder.query(QueryBuilders.matchQuery("user", "kimchy"));

Search search = new Search(searchSourceBuilder.toString());
search.addIndex("twitter");
search.addType("tweet");            
            
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
Get get = new Get.Builder("1").index("twitter").type("tweet").build();

JestResult result = client.execute(get);
```

Result can be cast to domain object;

``` java
Get get = new Get.Builder("1").index("twitter").type("tweet").build();

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
client.execute(new Delete.Builder("1").index("twitter").type("tweet").build());
```

### Bulk Operations

ElasticSearch's bulk API makes it possible to perform many index/delete operations in a single API call. This can greatly increase the indexing speed.

```java
Bulk bulk = new Bulk("twitter", "tweet");
bulk.addIndex(new Index.Builder(article1).build());
bulk.addIndex(new Index.Builder(article2).build());

bulk.addDelete(new Delete.Builder("1").build());

client.execute(bulk);
```

List of objects can be indexed via bulk api

```java
Bulk bulk = new Bulk("twitter", "tweet");
Article article1 = new Article("tweet1");
Article article2 = new Article("tweet1");
bulk.addIndexList(Arrays.asList(article1, article2););
client.execute(bulk);
```

### Action Parameters

ElasticSearch offers request parameters to set properties like routing, versioning, operation type etc.

For instance you can set "refresh" property to "true" while indexing a document as below;

```java
Index index = new Index.Builder("{\"user\":\"kimchy\"}").index("cvbank").type("candidate").id("1").build();
index.addParameter(Parameters.REFRESH, true);
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
clientConfig.getProperties().put(ClientConstants.DISCOVERY_ENABLED, true);      //boolean
clientConfig.getProperties().put(ClientConstants.DISCOVERY_FREQUENCY, 1l);      //long
clientConfig.getProperties().put(ClientConstants.DISCOVERY_FREQUENCY_TIMEUNIT, TimeUnit.MINUTES); //timeunit
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

Contributors
------------
Jest is developed by [@dogukansonmez](https://github.com/dogukansonmez) and [SearchBox.io](http://www.searchbox.io) team.

Copyright and License
---------------------

Copyright 2012 SearchBox.io

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in
compliance with the License. You may obtain a copy of the License in the LICENSE file, or at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
