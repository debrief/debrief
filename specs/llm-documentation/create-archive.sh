#!/bin/bash
# Script to create an archive of all LLM documentation files
# Usage: ./create-archive.sh [archive-name]

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
ARCHIVE_NAME="${1:-llm-documentation}"
OUTPUT_FILE="${ARCHIVE_NAME}.tar.gz"

echo "==================================="
echo "LLM Documentation Archive Creator"
echo "==================================="
echo ""
echo "Source directory: $SCRIPT_DIR"
echo "Archive name: $OUTPUT_FILE"
echo ""

# Create temporary directory for clean packaging
TEMP_DIR=$(mktemp -d)
PACKAGE_DIR="$TEMP_DIR/$ARCHIVE_NAME"
mkdir -p "$PACKAGE_DIR"

# List of files to include
FILES=(
    "data-model.md"
    "plan.md"
    "quickstart.md"
    "research.md"
    "tasks.md"
    "README.md"
    "download-docs.sh"
)

echo "Collecting files..."
for file in "${FILES[@]}"; do
    if [ -f "$SCRIPT_DIR/$file" ]; then
        cp "$SCRIPT_DIR/$file" "$PACKAGE_DIR/"
        echo "  ✓ $file"
    else
        echo "  ⚠ $file (not found, skipping)"
    fi
done

# Create the archive
echo ""
echo "Creating archive..."
cd "$TEMP_DIR"
tar -czf "$OUTPUT_FILE" "$ARCHIVE_NAME"
mv "$OUTPUT_FILE" "$SCRIPT_DIR/"

# Cleanup
rm -rf "$TEMP_DIR"

echo ""
echo "==================================="
echo "Archive created successfully!"
echo "==================================="
echo ""
ls -lh "$SCRIPT_DIR/$OUTPUT_FILE"
echo ""
echo "To extract: tar -xzf $OUTPUT_FILE"
