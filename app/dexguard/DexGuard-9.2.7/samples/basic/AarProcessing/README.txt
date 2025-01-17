This sample illustrates how DexGuard can be used to protect
an aar file in standalone mode.

To execute it run the following command (assuming the environment
variable DEXGUARD_HOME is set to point to the DexGuard installation
directory and the environment variable ANDROID_HOME is set to the 
Android SDK directory):

Windows:

    java -DANDROID_HOME=%ANDROID_HOME% -jar %DEXGUARD_HOME%\lib\dexguard.jar @dexguard-library-standalone.txt

Linux/Mac:

    java -DANDROID_HOME=$ANDROID_HOME -jar $DEXGUARD_HOME/lib/dexguard.jar @dexguard-library-standalone.txt

After the execution has finished successfully, the protected
library will be available as `Library-protected.aar`.
