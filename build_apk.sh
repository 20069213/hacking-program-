#!/bin/bash
# build_apk.sh - Compile the RAT via CLI
echo "[*] Cleaning previous build..."
./gradlew clean

echo "[*] Building Release APK..."
./gradlew assembleRelease

echo "[*] Locating APK..."
find app/build/outputs/apk/release/ -name "*.apk"

echo "[+] Done. Check the path above to grab your payload."
