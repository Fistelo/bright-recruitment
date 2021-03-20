# Bright inventions assessment

## Prerequisites

* Java 11
* PostgreSQL database

## How to run the project

Already configured PostgreSQL database can be found in `docker-compose.yml`. Running the container can be done with `docker-compose up` command.

The spring boot application can be run by starting main class `BrightRecruitmentApplication.java` from the favourite IDE or using command `mvn spring-boot:run`. 

## Testing

### Running tests from command line 

In order to run unit tests only use `mvn clean install`

The project contains also e2e test for all APIs. Those start postgres database in docker and perform necessary migrations automatically.
In order to run e2e only from the command line use `mvn -P itest verify`.  

## Testing manually

For manual testing purposes project contains a postman collection of available requests.

 
