#!/bin/bash

# Android Starter App - Quick Setup Script
# This script helps you quickly customize the app name and package name

echo "======================================"
echo "Android Starter App - Quick Setup"
echo "======================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Current values
CURRENT_PACKAGE="ug.global.temp"
CURRENT_APP_NAME="Temp"
CURRENT_PACKAGE_PATH="ug/global/temp"

# Get new package name
echo -e "${YELLOW}Current package name:${NC} $CURRENT_PACKAGE"
read -p "Enter new package name (e.g., com.yourcompany.yourapp): " NEW_PACKAGE

if [ -z "$NEW_PACKAGE" ]; then
    echo -e "${RED}Package name cannot be empty. Exiting.${NC}"
    exit 1
fi

# Get new app name
echo ""
echo -e "${YELLOW}Current app name:${NC} $CURRENT_APP_NAME"
read -p "Enter new app name (e.g., MyAwesomeApp): " NEW_APP_NAME

if [ -z "$NEW_APP_NAME" ]; then
    echo -e "${RED}App name cannot be empty. Exiting.${NC}"
    exit 1
fi

# Convert package name to path
NEW_PACKAGE_PATH=$(echo $NEW_PACKAGE | tr '.' '/')

echo ""
echo "======================================"
echo "Summary of Changes:"
echo "======================================"
echo -e "${YELLOW}Package:${NC} $CURRENT_PACKAGE → $NEW_PACKAGE"
echo -e "${YELLOW}App Name:${NC} $CURRENT_APP_NAME → $NEW_APP_NAME"
echo ""
read -p "Proceed with these changes? (y/n): " CONFIRM

if [ "$CONFIRM" != "y" ]; then
    echo -e "${RED}Cancelled.${NC}"
    exit 0
fi

echo ""
echo -e "${GREEN}Starting customization...${NC}"

# 1. Update build.gradle.kts
echo "1. Updating build.gradle.kts..."
sed -i "s/namespace = \"$CURRENT_PACKAGE\"/namespace = \"$NEW_PACKAGE\"/" app/build.gradle.kts
sed -i "s/applicationId = \"$CURRENT_PACKAGE\"/applicationId = \"$NEW_PACKAGE\"/" app/build.gradle.kts

# 2. Update strings.xml
echo "2. Updating strings.xml..."
sed -i "s/<string name=\"app_name\">$CURRENT_APP_NAME<\/string>/<string name=\"app_name\">$NEW_APP_NAME<\/string>/" app/src/main/res/values/strings.xml

# 3. Update URLS.kt
echo "3. Updating URLS.kt..."
URLS_FILE="app/src/main/java/$CURRENT_PACKAGE_PATH/util/URLS.kt"
if [ -f "$URLS_FILE" ]; then
    sed -i "s/const val APP_NAME = \"$CURRENT_APP_NAME\"/const val APP_NAME = \"$NEW_APP_NAME\"/" "$URLS_FILE"
    sed -i "s/const val PACKAGE_NAME = \"$CURRENT_PACKAGE\"/const val PACKAGE_NAME = \"$NEW_PACKAGE\"/" "$URLS_FILE"
fi

# 4. Update package declarations in all Kotlin files
echo "4. Updating package declarations in Kotlin files..."
find app/src/main/java/$CURRENT_PACKAGE_PATH -name "*.kt" -type f -exec sed -i "s/package $CURRENT_PACKAGE/package $NEW_PACKAGE/" {} \;

# 5. Update imports in all Kotlin files
echo "5. Updating imports in Kotlin files..."
find app/src/main/java/$CURRENT_PACKAGE_PATH -name "*.kt" -type f -exec sed -i "s/import $CURRENT_PACKAGE/import $NEW_PACKAGE/g" {} \;

# 6. Move package directories
echo "6. Moving package directories..."
NEW_DIR="app/src/main/java/$NEW_PACKAGE_PATH"
mkdir -p "$NEW_DIR"
cp -r "app/src/main/java/$CURRENT_PACKAGE_PATH/"* "$NEW_DIR/"

# 7. Clean up old directory if different
if [ "$CURRENT_PACKAGE_PATH" != "$NEW_PACKAGE_PATH" ]; then
    echo "7. Cleaning up old package directory..."
    # Only remove if the move was successful
    if [ -d "$NEW_DIR" ]; then
        rm -rf "app/src/main/java/$CURRENT_PACKAGE_PATH"
        # Clean up empty parent directories
        rmdir --ignore-fail-on-non-empty -p "app/src/main/java/$(dirname $CURRENT_PACKAGE_PATH)" 2>/dev/null || true
    fi
fi

echo ""
echo -e "${GREEN}======================================"
echo "Customization Complete!"
echo "======================================${NC}"
echo ""
echo "Next steps:"
echo "1. Open the project in Android Studio"
echo "2. Click 'File' → 'Sync Project with Gradle Files'"
echo "3. Clean and rebuild the project"
echo "4. Run the app!"
echo ""
echo -e "${YELLOW}Note:${NC} You may need to manually update some import statements in Android Studio"
echo ""
