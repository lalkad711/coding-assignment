# Transactions Reporting API

- This is a REST API that which facilitates creation of transaction reports, both, **CREDIT** and **DEBIT**.
- The API takes in a list of transactions and creates a corresponding **xml messages** for them to be processed by the downstream systems.
- The API creates xml file per execution date. The file are created as per specified [Output Schema](./src/main/resources/output.xsd).

# Project setup

> [!CAUTION]
> Although everything works fine on OpenJdk21. There are some compatibility issues that Lombok has with OpenJdk23. Here's a link to the conversaton [#3722](https://github.com/projectlombok/lombok/issues/3722)

## System requirements

- JDK21 or above
- Maven
- IDE of your choice

## Running the API from CLI

`mvn spring-boot:run`

## Compiling the code

`mvn compile`

## Compiling and running the tests (Runs both unit and integrtion test)

`mvn test`

## Installing the code in your local maven repository

`mvn install`

## To indent the code using spotless plugin
` mvn spotless:apply`


| API | Description |
| :-------------------: |:-----------:|
| API Specification | http://localhost:8443/swagger-ui.html |
| Heath and Management | http://localhost:9090/actuator |
