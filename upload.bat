echo Compiling %1.java
call nxjc %1.java
IF %ERRORLEVEL% NEQ 0 (
    echo Could not compile %1.java properly
    goto :end
)
echo Linking %1.class to %1.nxj
call nxjlink -o %1.nxj %1
IF %ERRORLEVEL% NEQ 0 (
    echo Could not link to %1.nxj properly
    goto :end
)
echo Uploading %1.nxj to NXT-device
call nxjupload %1.nxj
IF %ERRORLEVEL% NEQ 0 (
    echo Could not upload %1.nxj to NXT-device
    goto :end
)

:end
