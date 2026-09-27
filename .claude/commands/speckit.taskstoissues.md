---
description: Convert existing tasks into actionable, dependency-ordered GitHub issues for the feature based on available design artifacts.
tools: ['github/github-mcp-server/issue_write']
---

## User Input

```text
$ARGUMENTS
```

The user input above, when present, is part of the request.

## Outline

1. Run `.specify/scripts/bash/check-prerequisites.sh --json --require-tasks --include-tasks` from repo root and parse FEATURE_DIR and AVAILABLE_DOCS list. All paths must be absolute. For single quotes in args like "I'm Groot", use escape syntax: e.g 'I'\''m Groot' (or double-quote if possible: "I'm Groot").
1. From the executed script, extract the path to **tasks**.
1. Get the Git remote by running:

```bash
git config --get remote.origin.url
```

> [!CAUTION]
> Continue only if the remote is a GitHub URL; there is nowhere else to create the issues.

1. For each task in the list, use the GitHub MCP server to create a new issue in the repository that is representative of the Git remote.

> [!CAUTION]
> Create issues only in the repository the remote points at. Issues are public and cannot be un-created, so an issue filed in the wrong repository is visible to strangers until someone closes it.
