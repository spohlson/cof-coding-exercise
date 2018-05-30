# cof-coding-exercise

## Instructions:
1. Install [Gradle](https://gradle.org/install/)
2. Clone repository
3. Update src/main/resources/application.yml file:
    * Insert Quandl's api key
    * Change the server port if you wish it to not run on 8080
    
    ```
    spring:
        profiles: local
      
    server:
        port: 8080
      
    quandl:
        apiKey: <api_key>
    ```

