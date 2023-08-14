#Load Balancer 


## Description
This is a spring boot application that route request to registered instances on a ‘round robin’ basis.

## FAQ
1. How would the load balancer handle it if one of the application APIs goes down?
Health check is done on an interval of 10 second. Inactive instance is removed from the queue.
Before a request is fired to an active instance, a check is done to make sure the instance is active. 

2. How would the load balancer handle it if one of the application APIs starts to go slowly?
The application remove the instance from the queue and stop sending request to the host until the application is healthy again

## Instruction to run: 
1. docker build . -t loadbalancer
2. docker run -d -p 8888:8888 -t loadbalancer --rm -it loadbalancer


## Things to improve: 
1. Add db to keep track of active pod before crash / restart
2. config to deploy on kubernetes cluster 


## Path
http://localhost:<port>/swagger-ui/index.html
http://localhost:<port>/actuator/health

