echo "compiling files"
cd ../SideDroid/Desktop
javac -classpath SideDroid/*:. -d ../../bin -sourcepath . *.java 
