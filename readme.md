## TODO
```shell
./deploy.sh
  build
  rsync
  
```

## Build
```shell
  ./gradlew clean bootjar -Pprofile=prod
```

## Run
```shell
  java -Dspring.profiles.active=prod -jar build/libs/hobby-log-0.0.1-SNAPSHOT.jar
  
  # in docker
  bash
  java -Dspring.profiles.active=prod -jar app.jar &
```
