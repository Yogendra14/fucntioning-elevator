# Getting Started

### Reference Documentation
This Program mimics working Elevator in 2 modes:
1. Single Passenger: Elevator can carry only 1 Passenger at a time.
2. Multiple Passenger: Elevator can carry multiple Passenger at a time.

###### There are sub-modes for optimization:
1. Sequential: Processing request in sequence
2. Optimal: Processing request in `optimal` mode to minimize number of floors visited.

##### Assumptions:
1. Floors numbering is Numeric.
2. Floor numbering starts from 1.
3. In multiple passenger mode, elevator can pickup Passengers moving in same direction only.

##### How to Run:
1. Run program using below command:
spring-boot:run

Once Application is up and running, use any rest client to execute API using below template for various cases:
curl -X http://localhost:8182/mode/{mode}/{optimization}/executeRequests?input={inputRequest}

###### Valid Values:
**_mode_**: (single passenger Vs Multiple Passenger)
1. single
2. multiple

**_optimization_**: (Request processing optimization)
1. sequential
2. optimal

**_inputRequest_**:
{ElevatorStartPosition}:{PickupFloor1-DropOffFloor1};{PickupFloor2-DropOffFloor2};{PickupFloor3-DropOffFloor3} and so on

###### For example: 
Single Passenger Mode, Sequential Processing
Request:
```shell script
curl -X POST "http://localhost:8182/mode/single/sequential/executeRequests?input=6:1-8;4-10;8-5;6-2"
```

Response:
```json
{
    "steps": [
        "Start at Floor: 10",
        "Move to Floor 8 (Pick up Passenger 1) (2)",
        "Move to Floor 3 (Drop off Passenger 1) (7)",
        "Move to Floor 4 (Pick up Passenger 2) (8)",
        "Move to Floor 6 (Drop off Passenger 2) (10)",
        "Move to Floor 5 (Pick up Passenger 3) (11)",
        "Move to Floor 9 (Drop off Passenger 3) (15)",
        "Move to Floor 5 (Pick up Passenger 4) (19)",
        "Move to Floor 8 (Drop off Passenger 4) (22)"
    ],
    "floorsVisited": [9,8,7,6,5,4,3,4,5,6,5,6,7,8,9,8,7,6,5,6,7,8],
    "totalFloorsVisited": 22
}
```

Curls for rest of scenarios:
1. Request: 10:8-3;4-6;5-9;5-8
    1. single/sequential: 22
        ```shell script
            curl -X POST "http://localhost:8182/mode/single/sequential/executeRequests?input=10:8-3;4-6;5-9;5-8"
        ```
    2. single/optimal: 21
        ```shell script
            curl -X POST "http://localhost:8182/mode/single/optimal/executeRequests?input=10:8-3;4-6;5-9;5-8"
        ```
    3. multiple/sequential: 13
        ```shell script
            curl -X POST "http://localhost:8182/mode/multiple/sequential/executeRequests?input=10:8-3;4-6;5-9;5-8"
        ```
    4. multiple/optimal: 13
        ```shell script
            curl -X POST "http://localhost:8182/mode/multiple/optimal/executeRequests?input=10:8-3;4-6;5-9;5-8"
        ```
2. Request: 8:4-7;5-9;3-9;4-6
    1. single/sequential: 22
        ```shell script
            curl -X POST "http://localhost:8182/mode/single/sequential/executeRequests?input=8:4-7;5-9;3-9;4-6"
        ```
    2. single/optimal: 29
        ```shell script
            curl -X POST "http://localhost:8182/mode/single/sequential/executeRequests?input=8:4-7;5-9;3-9;4-6"
        ```
    3. multiple/sequential: 18
        ```shell script
            curl -X POST "http://localhost:8182/mode/multiple/sequential/executeRequests?input=8:4-7;5-9;3-9;4-6"
        ```
    4. multiple/optimal: 11
        ```shell script
            curl -X POST "http://localhost:8182/mode/multiple/optimal/executeRequests?input=8:4-7;5-9;3-9;4-6"
        ```
3. Request: 6:1-8;4-10;8-5;6-2
    1. single/sequential: 32
        ```shell script
            curl -X POST "http://localhost:8182/mode/single/sequential/executeRequests?input=6:1-8;4-10;8-5;6-2"
        ```
    2. single/optimal: 32
        ```shell script
            curl -X POST "http://localhost:8182/mode/single/optimal/executeRequests?input=6:1-8;4-10;8-5;6-2"
        ```
    3. multiple/sequential: 22
        ```shell script
            curl -X POST "http://localhost:8182/mode/multiple/sequential/executeRequests?input=6:1-8;4-10;8-5;6-2"
        ```
    4. multiple/optimal: 9
        ```shell script
            curl -X POST "http://localhost:8182/mode/multiple/optimal/executeRequests?input=6:1-8;4-10;8-5;6-2"
        ```
