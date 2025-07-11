# Deploy.ps1

# Configuration
$server = "mr83@burnet:~/test/"

# === Backend ===
Write-Host "Building backend WAR..."
cd backend
.\gradlew.bat war
if ($LASTEXITCODE -ne 0) {
    Write-Error "Gradle build failed. Exiting."
    exit 1
}

# Find generated WAR file
$warFile = Get-ChildItem -Recurse -Filter *.war | Sort-Object LastWriteTime -Descending | Select-Object -First 1
if (-not $warFile) {
    Write-Error "WAR file not found. Exiting."
    exit 1
}

Write-Host "Copying backend WAR to server..."
scp $warFile.FullName $server

cd ..

# === Frontend ===
Write-Host "Building frontend..."
cd frontend
npm run build
if ($LASTEXITCODE -ne 0) {
    Write-Error "Frontend build failed. Exiting."
    exit 1
}

# Zip the .output directory using 7z
Write-Host "Zipping frontend output with 7z..."
$zipName = "output.zip"
if (Test-Path $zipName) { Remove-Item $zipName }
& "C:\Program Files\7-Zip\7z.exe" a $zipName ".output\*"
if ($LASTEXITCODE -ne 0) {
    Write-Error "7z compression failed. Exiting."
    exit 1
}

# Copy zip to server
Write-Host "Copying frontend ZIP to server..."
scp $zipName $server

Write-Host "✅ Deployment completed successfully."
