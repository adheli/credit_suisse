# Project

[![Build Status](https://travis-ci.org/adheli/credit_suisse.svg?branch=master)](https://travis-ci.org/adheli/credit_suisse)

This is a project for Credit Suisse coding test.

# What this project does

  - Read a json file and serializes it to an array of Java objects, which represents log entries from a system.
  - Check the timestamp of the log entries (when some event started and finished) and check how long the proccess took.
  - Save the event's result in a HSQL database.

### Tech

* [Gradle](https://gradle.org/) - Build tool
* [JUnit Jupiter](https://junit.org) - for Unit testing

### How to execute

To build the project:

```sh
$ ./gradlew build
```

To execute unit tests:

```sh
$ ./gradlew test
```

To build jar:

```sh
$ ./gradlew fatJar
```

To execute the jar to create table (not necessary):

```sh
$ java -jar path/to/jar/credit_suisse-1.0-SNAPSHOT.jar create_table
```

To execute the jar to process input file:

```sh
$ java -jar path/to/jar/credit_suisse-1.0-SNAPSHOT.jar path/to/input_file.json
```

To execute the jar to show the results:

```sh
$ java -jar path/to/jar/credit_suisse-1.0-SNAPSHOT.jar results
```

To execute the jar clean data from table:

```sh
$ java -jar path/to/jar/credit_suisse-1.0-SNAPSHOT.jar clean_data
```
