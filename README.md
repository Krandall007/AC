# Air-traffic control system

Another simple demo project

## Requirements

A software subsystem of an air-traffic control system is defined to manage a queue of aircraft (AC) in an airport.  
The aircraft queue is managed by a process that responds to three types of requests: 
 1. System boot used to start the system.
 2. Enqueue aircraft used to insert a new AC into the system. 
 3. Dequeue aircraft used to remove an AC from the system.

AC’s have at least (but are not limited to having) the following properties: 
 1. AC type:  Emergency, VIP, Passenger or Cargo
 2. AC size:  Small or Large

The process that manages the queue of AC’s satisfies the following: 
 1. There is no limit to the number of AC’s it can manage.
 2. Dequeue aircraft requests result in selection of one AC for removal such that:
    a. VIP aircraft has precedence over all other ACs except Emergency. Emergency aircraft has highest priority. 
    b. Passenger AC’s have removal precedence over Cargo AC’s.
    c. Large AC’s of a given type have removal precedence over Small AC’s of the same type.
    d. Earlier enqueued AC’s of a given type and size have precedence over later enqueued AC’s of the same type and size.

System Implementation Requirements
 1. Develop one or more data structures to hold the state of an individual AC. 
 2. Develop one or more data structures to hold the state of the AC queue. 
 3. Define data structures and/or constants needed to represent requests.
 4. Develop the error for three defined requests (queue, dequeue and list) and follows the above guidelines to implement 
    an aircraft queue manager.  
 5. Assume multiple users of your dequeue implementation.  Multiple air traffic controllers will ask for the next plane 
    to be dequeued.  
 6. Expose the implementation as REST endpoints for integration with a 3rd party UI.  
 7. To the greatest extent possible, show all of your error.  Feel free to use standard libraries provided by your chosen 
    implementation language.
 8. Please send your complete project including artifacts.  If the project is built in Java, please use Maven, Gradle or 
    Ivy/ANT for the build.  Please add README if needed to run the project locally.  
 9. Bonus:  Use a non in-memory/persistent datastore.
 10. Additional Bonus:  UI to show queue and add/remove planes
   
## MongoDB and in-memory storage
 
By default the project uses in-memory storage. 
To switch to MongoDB 

1. Install MongoDB. Docker command is below:
```bash
 docker pull mongo:3.4
 docker run -d -p 27017:27017 --name acmongo mongo:3.4
```
2. Change active application profile. By default is it memory, add parameter **--spring.profiles.active=mongo** to the 
application command line or use export command 
```bash
export spring_profiles_active="mongo"
``` 
3. Provide mongo connection URL. The property name is **spring.data.mongodb.uri**, default value is 
**mongodb://localhost:27017/aircontrol**. If you are using Docker from example above you do not have to change it. 
Example:
```bash
spring_data_mongodb_uri="mongodb://localhost:27017/aircontrol"
```  

## Start the application

 To start the application execute. Add profile and mongo URL parameters if necessary
```bash
java -jar air-traffic-control-1.0-SNAPSHOT.jar
```  

## API
 
The application runs on localhost:8080. There are 3 API methods
 
1. GET /  
List the queue, response contains:
 * items - list of planes (items property). Each plane has id, type, size, timestamp when 
    it was added to the queue and human-readable label
 * hasMore - indicator if there are more items in the queue
 * filter - filter that was used to generate this response 

Parameters:
* lastId - Pagination parameter, ID of the last item on the last page client loaded
* limit -  Page size, valid values are from 1 to 50 
* search - Search string, filters planes by label
* size   - Filter by size. Accepted values - LARGE, SMALL
* type   - Filter by type. Accepted values - EMERGENCY, VIP, PASSENGER, CARGO

Examples
```text
http://localhost:8080/
http://localhost:8080/?limit=2&lastId=5b5bb3c34202ac800247d78b
http://localhost:8080/?type=VIP
http://localhost:8080/?search=1
``` 

2. PUT /
Enqueue a plane to the queue. Request content type must be JSON and request body must be JSON object with 3 properties
* type - EMERGENCY, VIP, PASSENGER, CARGO
* size - LARGE, SMALL
* label - Human-readable plane name, optional. Server will generate if empty. Not required to be unique 

Response - create record. JSON object with ID, timestamp, and all other properties

Examples:
```bash
curl -H 'Content-Type: application/json' -X PUT -d '{"type":"CARGO","size":"SMALL", "label" : "My little cargo plane"}' http://localhost:8080
curl -H 'Content-Type: application/json' -X PUT -d '{"type":"VIP","size":"LARGE"}' http://localhost:8080
```

3. DELETE /
Dequeue a plane from the queue. No request parameters. Server return error code 3 if the queue is empty.

Example:
```bash
curl -X DELETE http://localhost:8080
```

## API error

Error object consists from error code and error message. Error message is for debug purpose.
Error codes:
 * 1 - Bad request. Client error
 * 2 - Optimistic lock exception. Client can try again 
 * 3 - Queue is empty 
 * 4 - Internal Server Error  
 
Example:
```json
{
    "error": 1,
    "message": "org.springframework.http.converter.HttpMessageNotReadableException: JSON parse error: Unrecognized .."
}
```
 
## Build
 
 The project uses gradle.
 To build the project execute
 ```bash
 gradle build
```
 
 To start embedded Tomcat execute. It will pick profile and url from environment variables 
```bash
 gradle run
```  
  
