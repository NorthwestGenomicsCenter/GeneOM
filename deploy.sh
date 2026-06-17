#!/usr/bin/env bash
set -euo pipefail

# === Configuration ===
server="mr83@burnet:~/test/"

# === Flag Parsing ===
run_frontend=false
run_backend=false

for arg in "$@"; do
    case "$arg" in
        --frontend|-f) run_frontend=true ;;
        --backend|-b)  run_backend=true ;;
        *) echo "Unknown flag: $arg"; exit 1 ;;
    esac
done

# Default: run both if no flags were given
if ! $run_frontend && ! $run_backend; then
    run_frontend=true
    run_backend=true
fi

# === Backend ===
deploy_backend() {
    echo "Building backend WAR..."
    cd backend
    ./gradlew war
    echo "Backend build complete."

    warFile=$(find . -type f -name "*.war" -printf '%T@ %p\n' | sort -nr | head -n1 | awk '{print $2}')
    if [[ -z "$warFile" ]]; then
        echo "❌ WAR file not found. Exiting."
        exit 1
    fi

    echo "Copying backend WAR to server..."
    scp "$warFile" "$server"
    cd ..
}

# === Frontend ===
deploy_frontend() {
    echo "Building frontend..."
    cd frontend
    npm run build

    echo "Zipping frontend output..."
    cd .output
    zipName="output.zip"
    rm -f "$zipName"
    zip -r "$zipName" *

    echo "Copying frontend ZIP to server..."
    scp "$zipName" "$server"
    cd ../..
}

# === Run ===
$run_backend  && deploy_backend
$run_frontend && deploy_frontend

echo "✅ Deployment completed successfully."
