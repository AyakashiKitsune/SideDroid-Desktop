DIRECTORY="build/libs"
FILE="SideDroid-Desktop-jvm-1.3.0.jar"

if [ -e "${DIRECTORY}/${FILE}" ]; then
    echo "running"
    sudo java -jar ${DIRECTORY}/${FILE}
else
    echo "building"
    ./gradlew build 
    sudo java -jar ${DIRECTORY}/${FILE}
fi
