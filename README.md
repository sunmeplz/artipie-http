<img src="https://www.artipie.com/logo.svg" width="64px" height="64px"/>

[![Maven build](https://github.com/artipie/http/workflows/Maven%20Build/badge.svg)](https://github.com/artipie/http/actions?query=workflow%3A%22Maven+Build%22)
[![PDD status](http://www.0pdd.com/svg?name=artipie/http)](http://www.0pdd.com/p?name=artipie/http)
[![License](https://img.shields.io/github/license/artipie/http.svg?style=flat-square)](https://github.com/artipie/http/blob/master/LICENSE)

[![Maven Central](https://img.shields.io/maven-central/v/com.artipie/http.svg)](https://maven-badges.herokuapp.com/maven-central/com.artipie/http)
[![Javadoc](http://www.javadoc.io/badge/com.artipie/http.svg)](http://www.javadoc.io/doc/com.artipie/http)
[![Hits-of-Code](https://hitsofcode.com/github/artipie/http)](https://hitsofcode.com/view/github/artipie/http)

Artipie HTTP base interfaces.

To install add this dependency to `pom.xml` file:
```xml
<dependency>
  <groupId>com.artipie</groupId>
  <artifactId>http</artifactId>
  <version><!-- use latest version --></version>
</dependency>
```

This module tends to be reactive and provides these interfaces:
 - `Slice` - Arti-pie slice, should be implemented by adapter interface
 or Artipie application, it can receive request data and return reactive responses
 - `Response` - returned by `Slice` from adapters, can be sent to `Connection`
 - `Connection` - response asks connection to accept response data, `Connection`
 should be implemented by HTTP web server implementation to accept HTTP responses

## How to contribute

Fork repository, make changes, send us a pull request. We will review
your changes and apply them to the `master` branch shortly, provided
they don't violate our quality standards. To avoid frustration, before
sending us your pull request please run full Maven build:

```
$ mvn clean install -Pqulice
```

To avoid build errors use Maven 3.3+.
