@echo off
chcp 65001 > nul
if not exist iwfc-app.jar (
    if exist target\iwfc-project.jar (
        java -Dfile.encoding=UTF-8 -jar target\iwfc-project.jar
        goto end
    )
    echo iwfc-app.jar not found! Running build.bat first...
    call build.bat
)
java -Dfile.encoding=UTF-8 -jar iwfc-app.jar
:end
pause
