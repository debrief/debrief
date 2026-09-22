#!/bin/bash
# Increment the versions of every bundle changed since the last release, and
# bump the Debrief marketing version, product and about box to match.
#
# This also syncs the Maven poms, so update_pom.sh no longer needs to be run
# separately. See RELEASING.md, or use the /release skill in Claude Code.
#
# Preview without changing anything:
#   python3 contribs/PyScripts/release_versions.py plan
set -e
python3 contribs/PyScripts/release_versions.py apply "$@"
