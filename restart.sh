ps -ef | grep java | grep -v grep | awk '{print $2}' | xargs kill -9 2 > /dev/null

if [ $? -eq 0 ]; then
    echo "some-application Stop Success"
  else
    echo "some-application Not Running"
fi

echo "some-application Restart!"

nohup java -Dspring.profiles.active=prod -jar jar/app.jar &

