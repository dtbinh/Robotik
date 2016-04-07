echo Compiling %1.java
call nxjc %1.java
echo Linking %1.class to %1.nxj
call nxjlink -o %1.nxj %1
echo Uploading %1.nxj to NXT-Device
call nxjupload %1.nxj
