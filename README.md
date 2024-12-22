
# Gym Application

application enables gym trainees and trainers to register their profile, giving trainees the option to select one or more trainers.
Users are able to log their activities and view them both as trainers and as trainees.Application provides the functionality to modify profile information and activate or deactivate profiles and etc.

# How to run
* First should be run Second MicroService repository's discovery service 
* Second We must Database then ActiveMQ and then either this microservice or workload microservice
* Access Swagger on url: http://localhost:8080/swagger-ui.html
* Related project or Second MicroService that is connected to this: https://github.com/StarFleet1334/MicroServicesGEureka

# Active MQ
* docker pull rmohr/activemq:latest
* docker run -it --rm -p 62626:61616 -p 8161:8161 rmohr/activemq:latest
* Here in case if port is busy you can modify 6*6*6:61616
* But then remember you have to modify also that number everywhere
* If you want to access url: http://localhost:8161/
* Type as user: admin
* Type as password: admin

# Relation Database
* Database used: MariaDb
* to start up run : 'docker-compose up' from source directory
* to view actual data different GUI's can be used. In my case I use DBeaver 24.3.0

# Non-Relation Database - MongoDb
* Just download from this url: https://www.mongodb.com/try/download/community-kubernetes-operator
* and then run as indicated above and you should see your database created automatically
* you can modify database name in workload-microservice

# Swagger
* In Swagger to start accessing endpoints first you should login as admin and credentials are following:
* username: admin
* password: admin

## Documentation

* http://localhost:8080/swagger-ui.html -> To access swagger
* http://localhost:8080/actuator/health -> For actuator (Health + some resource pings)
* http://localhost:8080/actuator/prometheus -> for metrices related to promotheous


## API Endpoints

#### Login Functionality

```http
  POST /api/login
```

| RequestParam | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `username` | `string` | **Required**. |
| `password` | `string` | **Required**. |

#### Modify Password Functionality

```http
  PUT /login/change-login
```

| RequstBody | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `changeLoginDTO`      | `ChangeLoginRequestDTO` | **Required**.|

* ChangeLoginRequestDTO contains -> username, oldPassword and newPassword fields

#### LogOut Functionality
```http
  POST /api/logout
```

| RequstBody | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `credentials`      | `UserCredentials` | **Required**.|

* UserCredentials conatains -> username and password fields



#### Register Trainee Functionality
```http
  POST /api/trainees
```

| RequestBody | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `traineeRequestDTO`      | `CreateTraineeRequestDTO` | **Required**.|

* CreateTraineeRequestDTO conatains -> firstName,lastName,dateOfBirth and address fields


#### Get All Trainees Functionality
```http
  GET /api/trainees
```
#### Change Trainee Account State Functionality
```http
  PATCH /api/trainees/{username}/{statusAction}
```

| PathVariable | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `username`      | `string` | **Required**.|
| `statusAction`      | `StatusAction` | **Required**.|

* StatusAction is enum and contains -> ACTIVATE and DEACTIVATE states


#### Delete Trainee Functionality
```http
  DELETE /api/trainees/{username}
```

| PathVariable | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `username`      | `string` | **Required**.|

#### Get Trainee Profile Functionality
```http
  GET /api/trainees/{username}/profile
```

| PathVariable | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `username`      | `string` | **Required**.|



#### Update Trainee's Trainers Functionality
```http
  PUT /api/trainees/{username}/trainers/{traineeAction}
```

| PathVariavle | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `username`      | `string` | **Required**.|
| `traineeAction`      | `TraineeAction` | **Required**.|

| RequstBody | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `requestDTO`      | `UpdateTraineeTrainersRequestDTO` | **Required**.|

* TraineeAction is enum and contains -> ADD and REMOVE states
* UpdateTraineeTrainersRequestDTO contains -> list of trainerUsernames



#### Update Trainee Profile Functionality
```http
  PUT /api/trainees/{username}
```

| PathVariavle | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `username`      | `string` | **Required**.|

| RequstBody | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `requestDTO`      | `UpdateTraineeProfileRequestDTO` | **Required**.|

* UpdateTraineeProfileRequestDTO contains -> firstName,lastName,dateOfBirth,address and isActive fields


#### Get Unassigned Trainers Functionality
```http
  GET /api/trainees/{username}/unassigned-trainers
```

| PathVariavle | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `username`      | `string` | **Required**.|

#### Get Trainee Trainings Functionality
```http
  GET /api/trainees/{username}/trainings
```

| RequestParam | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `periodFrom`      | `LocalDate` | **Required**.|
| `periodTo`      | `LocalDate` | **Required**.|
| `trainingName`      | `string` | **Required**.|
| `trainingType`      | `string` | **Required**.|



#### Create Training Functionality
```http
  POST api/trainings
```

| RequestBody | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `trainingRequestDTO`      | `TrainingRequestDTO` | **Required**.|

* TrainingRequestDTO contains -> traineeUserName,trainerUserName,trainingName,trainingDate and duration

#### Get All Trainings Functionality
```http
  GET api/trainings
```
#### Create Training Type Functionality
```http
  POST api/training-type
```

| RequestBody | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `trainingTypeRequestDTO`      | `TrainingTypeRequestDTO` | **Required**.|

* TrainingTypeRequestDTO contains -> trainingTypeName


#### Get All Training Types Functionality
```http
  GET api/training-type
```


#### Get Training Type By ID Functionality
```http
  GET api/training-type/{id}
```

| PathVariable | Type     | Description                       |
| :-------- | :------- | :-------------------------------- |
| `id`      | `Long` | **Required**.|



#### Register Trainer Functionality

```http
  POST api/trainers
```

| RequestBody | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `trainerRequestDTO` | `TrainerRequestDTO` | **Required**. |

* TrainerRequestDTO contains -> firstName,lastName,trainingTypeId fields



#### Get All Trainers Functionality

```http
  GET api/trainers
```



#### Change Trainer Account State Functionality

```http
  PATCH api/trainers/{username}/{statusAction}
```

| PathVariable | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `username` | `string` | **Required**. |
| `statusAction` | `StatusAction` | **Required**. |

* StatusAction is enum and contains -> ACTIVATE AND DEACTIVATE fields



#### Get Trainer Profile Functionality

```http
  GET api/trainers/{username}
```

| PathVariable | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `username` | `string` | **Required**. |





#### Update Trainer Profile Functionality

```http
  PUT api/trainers/{username}
```

| PathVariable | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `username` | `string` | **Required**. |


| RequestBody | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `requestDTO` | `UpdateTrainerProfileRequestDTO` | **Required**. |

* UpdateTrainerProfileRequestDTO contains -> firstName,lastName,specializationa and isActive fields

#### Get Trainer's Trainings Functionality

```http
  GET api/trainers/{username}/trainings
```

| PathVariable | Type     | Description                |
| :-------- | :------- | :------------------------- |
| `periodFrom` | `LocalDate` | **Required**. |
| `periodTo` | `LocalDate` | **Required**. |
| `traineeName` | `string` | **Required**. |







## Image with Link

https://paste.pics/f37d6f1c7ad95cced8525b9f107c984d