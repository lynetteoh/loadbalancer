#Load Balancer 


## Description
This is a spring boot application that route request to registered instances on a ‘round robin’ basis.


## Instruction to run: 
1. docker build . -t loadbalancer
2. docker run -d -p 8888:8888 -t loadbalancer --rm -it loadbalancer


## Things to improve: 
1. Add db to keep track of active pod before crash / restart
2. config to deploy on kubernetes cluster 


## Path
http://localhost:<port>/swagger-ui/index.html
http://localhost:<port>/actuator/health

