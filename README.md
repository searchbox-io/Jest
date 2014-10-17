#Jest

[![build status](https://secure.travis-ci.org/searchbox-io/Jest.png)](http://travis-ci.org/searchbox-io/Jest)
[![Coverage Status](https://coveralls.io/repos/searchbox-io/Jest/badge.png?branch=master)](https://coveralls.io/r/searchbox-io/Jest?branch=master)

Jest is a Java HTTP Rest client for [ElasticSearch](http://www.elasticsearch.org).

ElasticSearch is an Open Source (Apache 2), Distributed, RESTful, Search Engine built on top of Apache Lucene.

ElasticSearch already has a Java API which is also used by ElasticSearch internally, [but Jest fills a gap, it is the missing client for ElasticSearch Http Rest interface](#comparison-to-native-api).

>Read great [introduction](http://www.ibm.com/developerworks/java/library/j-javadev2-24/index.html?ca=drs-) to ElasticSearch and Jest from IBM Developer works.


Documentation
---------------------
For the usual Jest Java library, that you can use as a maven dependency, please refer to [the README at jest module](https://github.com/searchbox-io/Jest/tree/master/jest).

For the Android port please refer to [the README at jest-droid module](https://github.com/searchbox-io/Jest/tree/master/jest-droid).


Compatibility
------------
Jest Version | Elasticsearch Version
--- | ---
>= 0.1.0 | 1.0
<= 0.0.6 | < 1.0

Also see [changelog](https://github.com/searchbox-io/Jest/wiki/Changelog) for detailed version history.


Support
------------
All questions, bug reports and feature requests are handled via the [GitHub issue tracker](https://github.com/searchbox-io/Jest/issues) which also acts as the knowledge base. Therefore please search the [previous issues](https://github.com/searchbox-io/Jest/issues?q=is%3Aissue) to see if your case was answered already in the past and if not please create a [New Issue](https://github.com/searchbox-io/Jest/issues/new), *rather than contacting any of the project contributors directly*, as this way more people will be able to see & comment on your case and when resolved it will stay as a future reference in the knowledge base.

If you are a [Searchly (formerly known as Searchbox)](http://www.searchly.com) user and you think your issue is server related then please contact [Searchly Support Team](http://support.searchly.com).


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

We also would like to thank the following people for their significant contributions.
* [Andrea Turli](https://github.com/andreaturli)
* [asierdelpozo](https://github.com/asierdelpozo)
* [Dominic Tootell](https://github.com/tootedom)
* [Erik Schuchmann](https://github.com/eschuchmann)
* [Filippo Rossoni](https://github.com/filippor)
* [happyprg](https://github.com/happyprg)
* [Igor Kupczyński](https://github.com/puszczyk)
* [Kristoffer Renholm](https://github.com/renholm)
* [Mark Woon](https://github.com/markwoon)
* [Matthew Bogner](https://github.com/matthewbogner)
* [Min Cha](https://github.com/MinCha)
* [Neil Gentleman](https://github.com/nigelzor)
* [phospodka](https://github.com/phospodka)
* [Steven Rapp](https://github.com/srapp)
* [Stig Brautaset](https://github.com/stig)


Copyright and License
---------------------

Copyright 2013 www.searchly.com

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in
compliance with the License. You may obtain a copy of the License in the LICENSE file, or at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
