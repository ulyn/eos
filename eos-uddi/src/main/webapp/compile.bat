@echo on & setlocal enabledelayedexpansion
set LIB_JARS=""
for %%i in (%1\*) do set LIB_JARS=!LIB_JARS!;%%i
cd %1

