cd ../webapp/jartmp/%1
cd /D ../webapp/jartmp/%1 & del /Q /S "*.java"
cd /D ../webapp/jartmp/%1 & del /Q /S "*.bat"
echo. >"%date:~0,4%%date:~5,2%%date:~8,2%%h%%time:~3,2%%time:~6,2%.txt"
jar -cvf  %2 *.*