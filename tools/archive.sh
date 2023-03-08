cd ../bin
echo "archiving classes with MANIFEST"
jar cmvf ../META-INF/MANIFEST.MF ../build/SideDroid-Desktop.jar SideDroid/Desktop/*.class
