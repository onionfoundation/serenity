# serenity

FIXME

## Getting Started

1. Start the application: `lein run-dev` \*
2. Go to [localhost:8080](http://localhost:8080/) to see: `Hello World!`
3. Read your app's source code at src/serenity/service.clj. Explore the docs of functions
   that define routes and responses.
4. Run your app's tests with `lein test`. Read the tests at test/serenity/service_test.clj.
5. Learn more! See the [Links section below](#links).

\* `lein run-dev` automatically detects code changes. Alternatively, you can run in production mode
with `lein run`.

## Configuration

To configure logging see config/logback.xml. By default, the app logs to stdout and logs/.
To learn more about configuring Logback, read its [documentation](http://logback.qos.ch/documentation.html).

## Development

 * `lein build-all` to compile the entire client side application
 * `lein cljsbuild once prod` to do a single "prod" compile of the client application
 * `lein cljsbuild auto dev` to do incremental "dev" compilation of the client application
 * `lein repl` and then `(run-dev)` to a have server-dev-repl
   * Browse to the [dev app page](http://127.0.0.1:8080/dev/index.html)
   * Or see the [production app](http://127.0.0.1:8080/)
 * `lein test` to run the service tests
 * `lein test-all` to run the service and client-side tests

## Links
* [Other examples](https://github.com/pedestal/samples)

