# LLM Documentation Specifications

This folder contains specification and planning documents for creating comprehensive LLM-friendly documentation for the Debrief codebase.

## Files in This Folder

| File | Description |
|------|-------------|
| **plan.md** | Master implementation plan with phased delivery, technical context, and project structure |
| **research.md** | Codebase structure findings, key class inventory, and architecture insights |
| **data-model.md** | Documentation entity model with templates and validation criteria |
| **quickstart.md** | Step-by-step workflow for creating LLM documentation |
| **tasks.md** | Detailed task breakdown organized by documentation phase |

## Purpose

These documents guide the creation of documentation that enables LLMs to:
1. Navigate the legacy Debrief codebase efficiently
2. Perform emergency hotfixes during transition to DebriefNG
3. Extract knowledge for algorithm porting and domain capture

## How to Download These Files

### Option 1: Using the Download Script

```bash
# Make the script executable
chmod +x download-docs.sh

# Run the script (downloads to ./llm-docs-download by default)
./download-docs.sh

# Or specify a custom output directory
./download-docs.sh /path/to/output/directory
```

### Option 2: Manual Download via Git

```bash
# Clone the repository
git clone https://github.com/debrief/debrief.git

# Navigate to this folder
cd debrief/specs/llm-documentation

# Copy files to your desired location
cp *.md /your/destination/path/
```

### Option 3: Download Individual Files from GitHub

Navigate to this folder on GitHub:
`https://github.com/debrief/debrief/tree/main/specs/llm-documentation`

Click on each file and use the "Download raw file" button.

### Option 4: Using wget or curl

```bash
# Create a directory for the files
mkdir llm-documentation
cd llm-documentation

# Download each file
BASE_URL="https://raw.githubusercontent.com/debrief/debrief/main/specs/llm-documentation"

wget $BASE_URL/plan.md
wget $BASE_URL/research.md
wget $BASE_URL/data-model.md
wget $BASE_URL/quickstart.md
wget $BASE_URL/tasks.md

# Or using curl
curl -O $BASE_URL/plan.md
curl -O $BASE_URL/research.md
curl -O $BASE_URL/data-model.md
curl -O $BASE_URL/quickstart.md
curl -O $BASE_URL/tasks.md
```

### Option 5: Using GitHub CLI

```bash
# Install GitHub CLI if not already installed: https://cli.github.com/

# Download the folder
gh repo clone debrief/debrief
cd debrief/specs/llm-documentation

# Or download specific files
gh api repos/debrief/debrief/contents/specs/llm-documentation/plan.md \
  --jq '.content' | base64 -d > plan.md
```

## Quick Start

After downloading these files, start with:
1. **plan.md** - Overview of the documentation project
2. **quickstart.md** - How to begin creating documentation
3. **data-model.md** - Understanding the documentation structure

## Documentation Constitution

All documentation follows these principles:
- **Navigability Over Comprehensiveness** - Help LLMs find code quickly
- **Signal Over Ceremony** - Concise, actionable information
- **Logic and Algorithms Are Primary** - Focus on computational behavior
- **Explore Before Asking** - Code inspection first, interviews fill gaps
- **Document What Is, Not What Should Be** - Describe existing behavior

## Related Documentation

The output of this specification work will be:
- `CLAUDE.md` - Entry point for LLM navigation (at repository root)
- `DOMAIN_GLOSSARY.md` - Maritime analysis terminology
- `ARCHITECTURE.md` - Module dependencies and data flow
- `KEY_CLASSES.md` - Critical classes with roles and relationships
- README files distributed throughout the codebase

## Contact

For questions about this documentation project, refer to the main repository README or open an issue on GitHub.
