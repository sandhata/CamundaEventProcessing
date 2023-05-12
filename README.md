# Camunda Message Buffering

# Problem Statement

When we try to trigger a message event while it's still processing the previous message within the short span of time then we get an error from 
Camunda and this has to be handled explicitly. 
And let's say if we have 20 different micro-service that uses Camunda then the exception handling has to be added manually in all 20 different 
micro-service.

#Solution

1. In order to overcome this, we have created a generic micro-service that accepts business-key, processinstance-id, message name and the payload to 
be sent as input.
2. This also has Request Params (retryCount and retryDelay) that accepts the number of re-try attempts to be made and the delay between successive 
re-try attempts.

#Swagger UI 

http://localhost:9091/swagger-ui/index.html#/message-buffering-controller

#Camunda Cockpit UI 

http://localhost:9091

#Camunda UI Login Credentials

User Name : demo
Password  : demo