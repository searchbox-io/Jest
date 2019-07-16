JEST
====

[![Build Status](https://travis-ci.org/searchbox-io/Jest.svg?branch=master)](https://travis-ci.org/searchbox-io/Jest)
[![Coverage Status](https://coveralls.io/repos/searchbox-io/Jest/badge.svg?branch=master)](https://coveralls.io/r/searchbox-io/Jest?branch=master)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.searchbox/jest/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.searchbox/jest)


Jest is a Java HTTP Rest client for [ElasticSearch][es].

ElasticSearch is an Open Source (Apache 2), Distributed, RESTful, Search Engine built on top of Apache Lucene.

ElasticSearch already has a Java API which is also used by ElasticSearch internally, [but Jest fills a gap, it is the missing client for ElasticSearch Http Rest interface](jest/README.md#comparison-to-native-api).

>Read great [introduction][ibm] to ElasticSearch and Jest from IBM Developer works.


Documentation
---------------------
For the usual Jest Java library, that you can use as a maven dependency, please refer to [the README at jest module][readme].

For the Android port please refer to [the README at jest-droid module][droidreadme].


Compatibility
------------
Jest Version | Elasticsearch Version
--- | ---
\>= 6.0.0 | 6
\>= 5.0.0 | 5
\>= 2.0.0 | 2
0.1.0 - 1.0.0 | 1
<= 0.0.6 | < 1

Also see [changelog][changelog] for detailed version history.


Support and Contribution
------------
All questions, bug reports and feature requests are handled via the [GitHub issue tracker][issuetracker] which also acts as the knowledge base. Please see the [Contribution Guidelines][contributing] for more information.


Thanks
---------------------

We would like to thank the following people for their significant contributions.
* [Andrea Turli](https://github.com/andreaturli)
* [Andrej Kazakov](https://github.com/andrejserafim)
* [asierdelpozo](https://github.com/asierdelpozo)
* [Clayton Stout](https://github.com/cfstout)
* [Dominic Tootell](https://github.com/tootedom)      
* [Erik Dreyer](https://github.com/edreyer)
* [Erik Schuchmann](https://github.com/eschuchmann)
* [Fernandez Ludovic](https://github.com/ldez)
* [Filippo Rossoni](https://github.com/filippor)
* [FrancoisThareau](https://github.com/FrancoisThareau)
* [happyprg](https://github.com/happyprg)
* [Igor Kupczyński](https://github.com/igor-kupczynski)
* [Kristoffer Renholm](https://github.com/renholm)
* [Mark Woon](https://github.com/markwoon)
* [Martin W. Kirst](https://github.com/nitram509)
* [Matthew Bogner](https://github.com/matthewbogner)
* [Min Cha](https://github.com/MinCha)
* [Neil Gentleman](https://github.com/nigelzor)
* [Pavel Zalunin](https://github.com/whiter4bbit)
* [phospodka](https://github.com/phospodka)
* [Steven Rapp](https://github.com/srapp)
* [Stig Brautaset](https://github.com/stig)
* [Thomas Mühlfriedel](https://github.com/tomsen-san)


Copyright and License
---------------------

Copyright 2018 www.searchly.com

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
[contributing]: https://github.com/searchbox-io/Jest/blob/master/CONTRIBUTING.md
