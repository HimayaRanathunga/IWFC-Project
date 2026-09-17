@echo off
if not exist iwfc-app.jar (
    if exist target\iwfc-project.jar (
        java -jar target\iwfc-project.jar
        goto end
    )
    echo iwfc-app.jar not found! Running build.bat first...
    call build.bat
)
java -jar iwfc-app.jar
:end
pause
