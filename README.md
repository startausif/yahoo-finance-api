# yahoo-finance-api integration in vertx

How to use this project

1. The project exposes an API which takes a file (stock file) and coloum header (stocks information as query string) as input. In response a path to a file(output csv file) is given. 
2. Also, the API is capable of taking input from local system (if file is not passed as input to API) and process the request.
3. One need to create path (/opt/yahoo/conf/app.properties, /opt/yahoo/conf/log4j.xml, /var/yahoo/logs/finance.log) in local system to use this project.
