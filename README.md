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

Each artipie adapter has to implement `Slice` interface with single method `response`.
This method should process the request and return reactive response object:
```java
class Maven implements Slice {
  @Override
  public Response response(String line, Iterable<Map.Entry<String, String>> headers,
      Flow.Publisher<Byte> body) {
      this.upload(body);
      return new RsWithStatus(200);
  }
}
```

Response is reactive object whith single method `send`. This method is called by
server implementation, server provides connection implementation as `send` parameter
which can accept response data: the server asks response to send itself to connection.

```java
class MavenResponse implements Response {

    @Override
    void send(final Connection con) {
        con.accept(200, headers, empty);
    }
}
```

HTTP server implements `Connection` interface which can accept response data:
server asks response to send itself to connection, response asks connection
to accept the data. Artipie adapter are not supposed to implement this interface,
it should be done by HTTP server implementation, e.g. vertex-server module.

## Some useful examples for different objects

### Routing

You can do routing in the following style:

```java
class Repo extends Slice.Wrap {
  Repo(Storage storage) {
    super(
      new SliceRoute.Path(new RtRule.ByMethod(RqMethod.PUT.value()), new SliceUpload(storage)),
      new SliceRoute.Path(new RtRule.ByMethod(RqMethod.GET.value()), new SliceDownload(storage)),
      SliceRoute.FALLBACK, new SliceSimple(RsStatus.METHOD_NOT_ALLOWED)
    );
}
```

### Authentication

Authentication protocol is specified by `Identities` interface
which parses user identity from request head (line and headers).

Possible implementations are:
 - Basic - from HTTP basic uthentication
 - ... TBD

### Authorization

Authorization is specified by `Permissions` interface which checks user permissions
for action. It can be encapsulated by `SliceAuth` wrapper to perform authorization checks:
```java
final Slice slice = new SliceAuth(
  new SliceUpload(storage),
  new Permission.ByName("upload", permissions),
  new BasicAuth(passwords)
);
```
This slice reads user identity by authentication decoder (`Identities` implementation),
if the identity was not found, then 401 error will be returned. Then the identity will be passed
to authorizaton check (`Permissions` class) with specified permission (`upload` in the example).
If user is not authorized to perform this action 403 error will be returned. If all check passed
successfully, then request will be redirected to underlying `Slice` implementation (`SliceUpload`
in the example).
...TBD

### Main components of request

```java
final RequestLineFrom request = new RequestLineFrom(line);
final Uri uri = request.uri();
final RqMethod = request.method();
```

### Specific header

```java
new RqHeaders.Single(headers, "x-header-name");
```

### Returning of async responses

```java
return new AsyncResponse(
    CompletableFuture.supplyAsync(
        /**
         * Business logic here
        **/
    ).thenApply(
        rsp -> new RsWithBody(
            new RsWithStatus(RsStatus.OK), body
        )
    )
)
```

## How to contribute

Fork repository, make changes, send us a pull request. We will review
your changes and apply them to the `master` branch shortly, provided
they don't violate our quality standards. To avoid frustration, before
sending us your pull request please run full Maven build:

```
$ mvn clean install -Pqulice
```

To avoid build errors use Maven 3.3+.
