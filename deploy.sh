./gradlew clean bootjar -Pprofile=prod

cp ./build/libs/hobby-log-0.0.1-SNAPSHOT.jar app.jar

rsync -az app.jar sunday00@211.184.119.6:/volume1/web/hobby-log/backend/jar/

echo "java -Dspring.profiles.active=prod -jar jar/app.jar &"
