# spring-jersey-jaxrs-example

## How to run with Maven

    $ mvn exec:java -Dexec.mainClass="io.katharsis.example.spring.jersey.SpringJerseyApplication"

## How to test with curl

    $ curl -v http://localhost:8080/movie/
	$ curl -v -X POST http://localhost:8080/movie/bond/vote?stars=5
    ...
