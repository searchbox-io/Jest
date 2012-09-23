#Jest

Jest is a Java HTTP Rest client for [ElasticSearch](http://www.elasticsearch.org).

ElasticSearch is an Open Source (Apache 2), Distributed, RESTful, Search Engine built on top of Apache Lucene.

ElasticSearch already has a Java API which is also used by ElasticSearch internally, but Jest fills a gap, it is the missing client for ElasticSearch Http Rest interface.

 
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
  <version>0.0.1</version>
</dependency>
```


Continious Integration
------------

[![build status](https://secure.travis-ci.org/searchbox-io/Jest.png)](http://travis-ci.org/searchbox-io/Jest)

Usage
------------

To start using Jest first we need a JestClient;

``` java 
 // Configuration
 ClientConfig clientConfig = new ClientConfig();
 LinkedHashSet<String> servers = new LinkedHashSet<String>();
 servers.add("http://localhost:9200");
 clientConfig.getServerProperties().put(ClientConstants.SERVER_LIST,servers);
 
 // Construct a new Jest client according to configuration via factory
 JestClientFactory factory = new JestClientFactory();
 factory.setClientConfig(clientConfig());
 JestClient client = factory.getObject();
```

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

client.execute(new CreateIndex("articles"), settingsBuilder.build());
```

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
client.execute();
```

Index id can be typed explicitly;

``` java
Index index = new Index.Builder(source).index("twitter").type("tweet").id("1").build();
client.execute();
```

@JestId annotation can be used to mark a property of a bean as id;

```java
class Article {

@JestId
private Long documentId;

}
```

Now whenever an instance of Article is indexed, index id will be value of documentId.

### Searching Documents

Search queries can be either JSON String or ElasticSearch QueryBuilder object.
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

```java
QueryBuilder queryBuilder = QueryBuilders.queryString("kimchy"); 

Search search = new Search(queryBuilder);
search.addIndex("twitter");
search.addType("tweet");            
            
JestResult result = client.execute(search);
```

Result can be cast to List of domain object;

``` java
JestResult result = client.execute(search);
List<Articles> articles = result.getSourceAsObjectList(Article.class);
```

Please refer [ElasticSearch Query DSL](http://www.elasticsearch.org/guide/reference/query-dsl/) documentation to work with complex queries.

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