[![Build Status](https://travis-ci.org/searchbox-io/Jest.svg?branch=master)](https://travis-ci.org/searchbox-io/Jest)
[![Coverage Status](https://coveralls.io/repos/searchbox-io/Jest/badge.svg?branch=master)](https://coveralls.io/r/searchbox-io/Jest?branch=master)


Overview
---------------------
Jest is a Java HTTP Rest client for [Elasticsearch][es].

Elasticsearch is an Open Source (Apache 2), Distributed, RESTful, Search Engine built on top of Apache Lucene.

Elasticsearch already has a Java API which is also used by Elasticsearch internally, [but Jest fills a gap, it is the missing client for Elasticsearch Http Rest interface](#Comparison_to_native_API).

Read (a bit outdated but nevertheless) great [introduction][ibm] to Elasticsearch and Jest from IBM Developer works.


Comparison to native API
---------------------
"There are several alternative clients available when working with Elasticsearch from Java, like Jest that provides a POJO marshalling mechanism on indexing and for the search results. In this example we are using the Client that is included in Elasticsearch. By default the client doesn't use the REST API but connects to the cluster as a normal node that just doesn't store any data. It knows about the state of the cluster and can route requests to the correct node but supposedly consumes more memory. For our application this doesn't make a huge difference but for production systems that's something to think about."
<cite>-- [Florian Hopf](http://blog.florian-hopf.de/2013/05/getting-started-with-elasticsearch-part.html)</cite>

<!-- -->
"So if you have several ES clusters running different versions, then using the native (or transport) client will be a problem, and you will need to go HTTP (and Jest is the main option I think). If versioning is not an issue, the native client will be your best option as it is cluster aware (thus knows how to route your queries and does not need another hop), and also moves some computation away from your ES cluster (like merging search results that will be done locally instead of on the data node)."
<cite>-- [Rotem Hermon](http://www.quora.com/Elasticsearch/What-is-the-best-client-library-for-elasticsearch)</cite>

<!-- -->
"Elasticsearch does not have Java rest client. It has only native client comes built in. That is the gap. You can add security layer to HTTP but native API. That is why none of SAAS offerings can be used with native api."
<cite>-- [Searchly](https://twitter.com/searchboxio)</cite>


Support and Contribution
------------
All questions, bug reports and feature requests are handled via the [GitHub issue tracker][issuetracker] which also acts as the knowledge base. Please see [Contribution Guidelines][contributing] for more information.


Thanks
---------------------
Thanks to [JetBrains][jetbrains] for providing a license for [IntelliJ IDEA][idea] to develop this project.

We also would like to thank [all contributors](team-list.html) for their significant contributions.


Copyright and License
---------------------

Copyright 2013 www.searchly.com

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this work except in
compliance with the License. You may obtain a copy of the License in the LICENSE file, or at:

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is
distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.



[es]: http://www.elasticsearch.org
[ibm]: http://www.ibm.com/developerworks/java/library/j-javadev2-24/index.html?ca=drs-
[readme]: https://github.com/searchbox-io/Jest/tree/master/jest
[droidreadme]: https://github.com/searchbox-io/Jest/tree/master/jest-droid
[changelog]: https://github.com/searchbox-io/Jest/wiki/Changelog
[issuetracker]: https://github.com/searchbox-io/Jest/issues
[contributing]: contributing.html
[jetbrains]: http://www.jetbrains.com/
[idea]: http://www.jetbrains.com/idea/
