#Changelog

## v0.1.6 (TBD)
* Improvements:
    * **[BREAKING CHANGE]** Refactored internal server pool iteration logic ([4d6cd25](https://github.com/searchbox-io/Jest/commit/4d6cd258610f05491785b41d3ee479d3228b93d8)).
        * `RoundRobinServerList` class and `ServerList` interface is removed.
        * In class `AbstractJestClient`, method `public LinkedHashSet<String> getServers()` is replaced by `protected int getServerPoolSize()` .
        * In class `AbstractJestClient`, method `public void setServers(ServerList list)` is removed.
        * In class `AbstractJestClient`, method `protected String getElasticSearchServer()` is renamed to `protected String getNextServer()`.
* New features:
    * Aggregations in search responses is now supported. ([#146](https://github.com/searchbox-io/Jest/issues/146)) - [cfstout](https://github.com/cfstout)
* Bug fixes:
    * N/A
* Dependency version upgrades:

  Component | Old Version | New Version
  --- | --- | ---


## v0.1.5 (08/02/2015)
* Improvements:
    * Minor structural refactor on `JestHttpClient`
    * Structural and assertion refactor on test classes
* New features:
    * None :disappointed:
* Bug fixes:
    * Date format for the default Gson instance did not match the date format (ISO date) used in default Elasticsearch date field. ([#165](https://github.com/searchbox-io/Jest/issues/165))
    * When using `JestResult.getSourceAsObject()` method, `@JestId` annotated field on the returned object was not set if no field corresponding to `_id` was present in `source` data (e.g.: indexing was done without using class serialization flow in Jest). ([#142](https://github.com/searchbox-io/Jest/issues/142)) - [tomsen-san](https://github.com/tomsen-san)
* Dependency version upgrades:

  Component | Old Version | New Version
  --- | --- | ---
  Elasticsearch | 1.3.4 | 1.4.2
  Lucene | 4.9.1 | 4.10.2
  Randomizedtesting | 2.1.6 | 2.1.11
  HttpCore | 4.3.3 | 4.4
  HttpClient | 4.3.6 | 4.4
  Slf4j | 1.7.7 | 1.7.10
  Mockito | 1.10.8 | 1.10.19


## v0.1.4 (14/12/2014)
* Improvements:
    * Added contribution guidelines & code of conduct.
    * Converted JSON serialization process of `Sort` inside `Search` from string building to Gson. ([dcf2807](https://github.com/searchbox-io/Jest/commit/dcf2807d258852c91c4ecba5b7ebaf9a199cd922))
    * **[BREAKING CHANGE]** Moved getFacets method from JestResult to SearchResult ([#160](https://github.com/searchbox-io/Jest/issues/160))
    * **[BREAKING CHANGE]** Removed some of the setters from `Doc` class:
        * `void setIndex(String index)`
        * `void setType(String type)`
        * `void setId(String id)`
        * `void removeAllFields()`
        * `boolean removeField(String field)`
    * **[BREAKING CHANGE]** Return type of `Doc.getFields()` has changed from `HashSet<String>` to `Collection<String>`
* New features:
    * `MultiGet` action now supports `routing` and `source` fields per document through the helper class `Doc` ([#161](https://github.com/searchbox-io/Jest/issues/161))
* Bug fixes:
    * Multiple index affecting operations do not accept the `ignore_indices` parameter since ES 1.x and was replaced by `ignore_unavailable` and `allow_no_indices` parameters; this change was not reflected in Jest previously. ([#162](https://github.com/searchbox-io/Jest/pull/162)) - [whiter4bbit](https://github.com/whiter4bbit)
* Dependency version upgrades:

  Component | Old Version | New Version
  --- | --- | ---
  HttpCore | 4.3.2 | 4.3.3
  HttpClient | 4.3.5 | 4.3.6
  Log4J | 1.2.17 | 2.1
  Gson | 2.3 | 2.3.1
  Mockito | 1.9.5 | 1.10.8
  JUnit | 4.11 | 4.12
  Maven Surefire Plugin | 2.17 | 2.18
  Maven Compiler Plugin | 3.1 | 3.2
  Maven Source Plugin | 2.3 | 2.4
  Maven JavaDoc Plugin | 2.10 | 2.10.1
  Maven Coveralls Plugin | 2.2.0 | 3.0.1


## v0.1.3 (21/10/2014)
* Improvements:
    * Readme - [puszczyk](https://github.com/puszczyk)
    * Stability and predictability fixes for some tests
    * `SearchResult` now provides getters for `total` & `max_score` fields ([#140](https://github.com/searchbox-io/Jest/issues/140))
    * Less chatty log configuration for test phase
* New features:
    * Idle connection reaper - opt-in ability to clean idle connections in the pool periodically ([#149](https://github.com/searchbox-io/Jest/pull/149)) - [matthewbogner](https://github.com/matthewbogner)
    * `GetSettings` and `UpdateSettings` actions are implemented ([#155](https://github.com/searchbox-io/Jest/pull/155))
* Bug fixes:
    * `NullPointerException` was thrown when discovery is enabled and nodes do not have `http_address` element as part of their result  (i.e. logstash nodes) ([#139](https://github.com/searchbox-io/Jest/pull/139)) - [phospodka](https://github.com/phospodka)
    * When an empty string was provided as the body (or source) for an `Action` it was re-interpreted by GSON which lead to unexpected behavior in some edge-cases. Such arguments are sent to Elastichsearch as-is now.
* Dependency version upgrades:

  Component | Old Version | New Version
  --- | --- | ---
  Elasticsearch | 1.2.2 | 1.3.4
  Lucene | 4.8.1 | 4.9.1
  Randomized-testing | 2.1.2 | 2.1.6
  Apache HttpClient | 4.3.4 | 4.3.5
  Apache HttpAsyncClient | 4.0.1 | 4.0.2
  Guava | 17.0 | 18.0
  GSON | 2.2.4 | 2.3
  Maven JavaDoc Plugin | 2.9.1 | 2.10
  Exec Maven Plugin | 1.3.1 | 1.3.2

## v0.1.2 (20/07/2014)
* Improvements:
    * Readme
    * Added debug level logging for each executed action ([#124](https://github.com/searchbox-io/Jest/pull/124)) - [happyprg](https://github.com/happyprg)
    * Date format for the default GSON instance now matches the ES' format ([#130](https://github.com/searchbox-io/Jest/issues/130) & [#134](https://github.com/searchbox-io/Jest/pull/134)) - [puszczyk](https://github.com/puszczyk)
    * `CreateIndex` action now accepts any object as settings source ([#118](https://github.com/searchbox-io/Jest/issues/118) & [#40](https://github.com/searchbox-io/Jest/issues/40))
    * Added private default constructor for `JestResult` so that it plays nicely with mocking mechanisms when testing ([#137](https://github.com/searchbox-io/Jest/issues/137))
    * Refactored several integration tests so that they work with ES 1.2.x
* Implemented new actions:
    * `TypeExist` ([#122](https://github.com/searchbox-io/Jest/pull/122)) - [happyprg](https://github.com/happyprg)
* Bug fixes:
    * `SearchScroll` action always used HTTP GET method which caused problems with `scroll_id`s larger than 1900 characters ([#126](https://github.com/searchbox-io/Jest/pull/126)) - [srapp](https://github.com/srapp)
    * In alias mappings, provided JSON sources were unnecessarily escaped ([#127](https://github.com/searchbox-io/Jest/issues/127) & [#128](https://github.com/searchbox-io/Jest/pull/128)) - [puszczyk](https://github.com/puszczyk)
    * `ignore_indices` parameter was ignored even when it is explicitly set ([#133](https://github.com/searchbox-io/Jest/pull/133)) - [eschuchmann](https://github.com/eschuchmann)
* Dependency version upgrades:

  Component | Old Version | New Version
  --- | --- | ---
  Sonatype Parent POM | 7 | 9
  Elasticsearch | 1.0.2 | 1.2.2
  Lucene | 4.6.1 | 4.8.1
  Randomized-testing | 2.1.1 | 2.1.2
  Apache HttpClient | 4.3.3 | 4.3.4
  Apache HttpAsyncClient | 4.0 | 4.0.1
  Slf4j | 1.7.6 | 1.7.7
  Guava | 16.0.1 | 17.0
  Apache CommonsLang | 3.3.1 | 3.3.2
  Maven Source Plugin | 2.2.1 | 2.3
  Maven Exec Plugin | 1.2.1 | 1.3.1
  LittleProxy | 1.0.0-beta7 | 1.0.0-beta8
  Maven Surefire Plugin | 2.7.2 | 2.17
  Maven Release Plugin | 2.3.2 | 2.5


## v0.1.1 (20/04/2014)
* Improvements:
    * Better isolation for some tests
    * Readme
    * Result creation is now done by the respective `Action` implementation instead of `AbstractJestClient` [(#106)](https://github.com/searchbox-io/Jest/issues/106)
    * Added `CountResult` and `SearchResult` classes which are returned when the respective actions are executed (i.e.: `SearchResult result = client.execute(search);`)
    * "Explain" data is now available through `SearchResult` [(#106)](https://github.com/searchbox-io/Jest/issues/106)
    * System default proxy is now used by `JestClientFactory` [(#56)](https://github.com/searchbox-io/Jest/issues/56)
* Dependency version upgrades:
    * Elasticsearch to `1.0.2` from `1.0.0`
    * RandomizedTesting to `2.1.1` from `2.0.15`
    * HttpCore to `4.3.2` from `4.3.1`
    * HttpClient to `4.3.3` from `4.3.2`
    * Slf4j to `1.7.6` from `1.7.5`
    * Log4j to `1.2.17` from `1.2.16`
    * Guava to `16.0.1` from `16.0`
    * CommonsLang to `3.3.1` from `3.1`
* Maven plugin version upgrades:
    * Surefire to `2.17` from `2.7.2`
    * Compiler to `3.1` from `2.0.2`
    * Javadoc to `2.9.1` from `2.9`
    * Cobertura to `2.6` from `2.5.2`
    * Coveralls to `2.2.0` from `2.0.1`
* Bug fixes:
    * `JestHttpClient.getAsyncClient` return type was `HttpAsyncClient` instead of `CloseableHttpAsyncClient` [(#111)](https://github.com/searchbox-io/Jest/issues/111)
* **Breaking changes:**
    * `Action` interface and its implementation are moved from `io.searchbox` to `io.searchbox.action` package
    * `Action.isOperationSucceed` method is removed as it is obsolete with ES version > 1.0


## v0.1.0 (08/03/2014)
* First release with Elasticsearch v1.X compatibility.
* Improvements:
    * Refactored HttpClient creation and added ability to provide custom configuration to HttpClient - [renholm](https://github.com/renholm)
    * Fixed dependency converge issues [(#102)](https://github.com/searchbox-io/Jest/issues/102)
    * Now using eleasticsearch test-jar instead of [tlrx's elasticsearch-test package](https://github.com/tlrx/elasticsearch-test)
    * Added license file to root and informational tags to sub-module poms.
* Bug fixes:
    * NodeChecker elevated any exception caught while performing NodesInfo action which caused scheduled calls (and the discovery service) to stop.

## v0.0.6 (22/02/2014)
* The last release with support for Elasticsearch version 0.90.x
* Moved to a sub-module based structure to support jest-droid project
* Readme changes:
    * Added coveralls & bitdeli badges to readme
    * Clarified use of JestId annotation in readme and added a sample test case for JestId type conversion
* Version upgrades:
    * Upgrade gson to 2.2.4 due to hashing bug in 2.2.3 - [joningle](https://github.com/joningle)
    * Upgrade to HttpClient 4.3.x. - [markwoon](https://github.com/markwoon)
* Bug fixes:
    * Bulk operation ignored the user set defaultType [(#78)](https://github.com/searchbox-io/Jest/issues/78)
* Improvements:
    * Add timeout options when building ClientConfig - [MinCha](https://github.com/MinCha)
    * Index, type & id parameters are now kept mandatory by builder constructor for Explain and MoreLikeThis actions.
* **Breaking changes:**
    * Instead of `ClientConfig` you should now use `HttpClientConfig`or `DroidClientConfig` according to the actual client implementation you use.
    * `JestClientFactory.setClientConfig(ClientConfig)` is now `JestClientFactory.setHttpClientConfig(HttpClientConfig)`

## v0.0.5 (16/10/2013)

* Implemented new actions:
    * Clear Cache
    * Analyze
    * Nodes Stats
    * Nodes Hot Threads
    * Cluster State
* Bug fixes:
    * No httpclient value fails the NodeChecker task - [(#53)](https://github.com/searchbox-io/Jest/issues/53)
    * Bulk action removes all whitespaces from content of inner index actions - [(#60)](https://github.com/searchbox-io/Jest/issues/60)
    * Jest should url encode docids - [(#59)](https://github.com/searchbox-io/Jest/issues/59)
* Centralized GSON instance configuration
* Updated elasticsearch version to 0.90.5 - [markwoon](https://github.com/markwoon)
* Updated commons-lang version to 3.1 - [markwoon](https://github.com/markwoon)

## v0.0.4 (09/07/2013)

* Indices Status action implemented
* JestResult refactored [FilippoR](https://github.com/filippor)
* Discovery API refactored [tootedom](https://github.com/tootedom)
* Mapping actions refactored
* Flush action implemented
* Open/Close actions implemented
* Aliases implemented
* Indices Stats action implemented
* Fixed a bug JVM hangs - David Connard
* Responses with no entities handled properly - David Connard
* Cluster health action implemented [nigelzor](https://github.com/nigelzor)
* Update ES to v0.90.2 [markwoon](https://github.com/markwoon)
* Complete rewrite of Bulk actions

Special thanks to [kramer](https://github.com/kramer) for his great contribution.

## v0.0.3 (23/02/2013)

* Fixed a thread safe issue while working in multi threaded mode.
* Added support for http request headers to actions. Thanks to [peterhawkins](https://github.com/peterhawkins)
* Fixed "Search ignores addParameter() method".
* New client configuration parameters added which can be use if client constructed in multi threaded mode.
```java
ClientConstants.MAX_TOTAL_CONNECTION // Max total http connection
ClientConstants.DEFAULT_MAX_TOTAL_CONNECTION_PER_ROUTE // Max total connection for each route (url)
ClientConstants.MAX_TOTAL_CONNECTION_PER_ROUTE // Configuration for each individual route
```
* Fixed an issue causes request fail for Sort if an empty list is provided.
* Added support for all types of facet query results.
For instance;
```java
Search search = new Search(query);
search.addIndex("terms_facet");
search.addType("document");
JestResult result = client.execute(search);
List<TermsFacet> termsFacets = result.getFacets(TermsFacet.class);
```
Please see facet integration tests.
* GetTemplate and PutTemplate action implemented. Thanks to [asierdelpozo](https://github.com/asierdelpozo)
* PutMapping and GetMapping implemented as draft.
* Client configuration simplified.Map of serverProperties and clientProperties merged as properties.
```java
clientConfig.getProperties().put(ClientConstants.SERVER_LIST, servers);
```
## v0.0.2 (08/12/2012)

* ElasticSearch dependency is now optional, please ensure to add it to use QueryBuilder and Settings.
* QueryBuilder usage is changed as
```java
Search search = new Search(Search.createQueryWithBuilder(queryBuilder.toString()));
```
due to make ElasticSearch dependency optional.
* Jest http client supports execution of action with non blocking IO asynchronously.
* If it is enabled, Jest now discovers nodes with Nodes API [issue/2](https://github.com/searchbox-io/Jest/issues/2).
```java
clientConfig.getClientFeatures().put(ClientConstants.DISCOVERY_ENABLED, true);      //boolean
clientConfig.getClientFeatures().put(ClientConstants.DISCOVERY_FREQUENCY, 1l);      //long
clientConfig.getClientFeatures().put(ClientConstants.DISCOVERY_FREQUENCY_TIMEUNIT, TimeUnit.MINUTES);
```
* Sorting support added to Search API [pull/5](https://github.com/searchbox-io/Jest/pull/5). Thanks to [raymanrt](https://github.com/raymanrt)
* Index Exits API is implemented. Thanks to [hlassiege](https://github.com/hlassiege)
* While fetching search results as POJOS, if result of search request "_source" does not contains @JestId field, this marked field's value will be "_id". [Issue/7](https://github.com/searchbox-io/Jest/issues/7)
* List of POJOS can be indexed via bulk api.
```java
Bulk bulk = new Bulk("twitter", "tweet");
Article article1 = new Article("tweet1");
Article article2 = new Article("tweet1");
bulk.addIndexList(Arrays.asList(article1, article2););
client.execute(bulk);
```