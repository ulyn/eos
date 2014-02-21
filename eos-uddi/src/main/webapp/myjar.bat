cd ../webapp/jartmp/%1
cd /D ../webapp/jartmp/%1 & del /Q /S "*.java"
cd /D ../webapp/jartmp/%1 & del /Q /S "*.bat"
jar -cvf  %2 *.*