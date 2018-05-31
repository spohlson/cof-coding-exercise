# cof-coding-exercise

## Running the App:

1. Install [Gradle 4](https://gradle.org/install/)
2. Clone repository
3. Update `src/main/resources/application.yml` file:
    * Insert Quandl's api key
    * Change the server port if you wish to run on a different port than 8080
    
    ```
    spring:
        profiles: local
      
    server:
        port: 8080
      
    quandl:
        apiKey: <api_key>
    ```
4. From inside the cloned repo via the command line run the following to start the application:
    
        gradle bootRun


## Swagger API Documentation:

* Run the application then go to the [Swagger UI](http://localhost:8080/swagger-ui.html) in a browser. This will show you how to access the APIs via command line and/or REST client like [Postman](https://www.getpostman.com/).


