$baseDir = Split-Path -Parent $MyInvocation.MyCommand.Path
Set-Location $baseDir

Write-Host "============================================"
Write-Host " Building IWFC Java Project"
Write-Host "============================================"

if (-not (Test-Path "bin")) { New-Item -ItemType Directory -Path "bin" | Out-Null }

$javaFiles = Get-ChildItem -Path "src\main\java" -Recurse -Filter "*.java" | ForEach-Object { '"' + $_.FullName.Replace('\', '/') + '"' }
$sourcesFile = Join-Path $baseDir "sources.txt"
$javaFiles | Out-File -FilePath $sourcesFile -Encoding ascii

& javac -d bin -encoding UTF-8 "@$sourcesFile"
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Compilation failed!" -ForegroundColor Red
    Remove-Item $sourcesFile -ErrorAction SilentlyContinue
    exit $LASTEXITCODE
}
Remove-Item $sourcesFile -ErrorAction SilentlyContinue

& jar cfe iwfc-app.jar com.iwfc.ui.ConsoleApp -C bin .
if ($LASTEXITCODE -ne 0) {
    Write-Host "[ERROR] Packaging JAR failed!" -ForegroundColor Red
    exit $LASTEXITCODE
}

Write-Host "============================================"
Write-Host " [SUCCESS] Build completed! Generated iwfc-app.jar"
Write-Host " Run the project anytime using run.bat"
Write-Host "============================================"
