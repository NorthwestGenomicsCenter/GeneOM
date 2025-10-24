#!/usr/bin/env bash
set -euo pipefail

# === Configuration ===
server="mr83@burnet:~/test/"

# === Backend ===
echo "Building backend WAR..."
cd backend
./gradlew war
echo "Backend build complete."

# Find most recent WAR
warFile=$(find . -type f -name "*.war" -printf '%T@ %p\n' | sort -nr | head -n1 | awk '{print $2}')
if [[ -z "$warFile" ]]; then
    echo "❌ WAR file not found. Exiting."
    exit 1
fi

echo "Copying backend WAR to server..."
scp "$warFile" "$server"

cd ..

# === Frontend ===
echo "Building frontend..."
cd frontend
npm run build

# Zip the .output directory
echo "Zipping frontend output..."
zipName="output.zip"
rm -f "$zipName"
zip -r "$zipName" .output

echo "Copying frontend ZIP to server..."
scp "$zipName" "$server"

echo "✅ Deployment completed successfully."