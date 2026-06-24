#!/bin/bash
# Script to download all LLM documentation files from this folder
# Usage: ./download-docs.sh [output-directory]

set -e

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
OUTPUT_DIR="${1:-./llm-docs-download}"

echo "==================================="
echo "LLM Documentation Download Helper"
echo "==================================="
echo ""
echo "Source directory: $SCRIPT_DIR"
echo "Output directory: $OUTPUT_DIR"
echo ""

# Create output directory
mkdir -p "$OUTPUT_DIR"

# List of files to copy
FILES=(
    "data-model.md"
    "plan.md"
    "quickstart.md"
    "research.md"
    "tasks.md"
)

echo "Copying files..."
for file in "${FILES[@]}"; do
    if [ -f "$SCRIPT_DIR/$file" ]; then
        cp "$SCRIPT_DIR/$file" "$OUTPUT_DIR/"
        echo "  ✓ $file"
    else
        echo "  ✗ $file (not found)"
    fi
done

echo ""
echo "==================================="
echo "Download complete!"
echo "Files saved to: $OUTPUT_DIR"
echo "==================================="
echo ""
echo "Files included:"
ls -lh "$OUTPUT_DIR"
