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
$ cd credit_suisse
$ ./gradlew build
```

To execute unit tests:

```sh
$ cd credit_suisse
$ ./gradlew test
```
