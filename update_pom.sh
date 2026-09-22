#!/bin/bash
# Sync every module pom.xml version from its MANIFEST.MF / feature.xml.
#
# update_versions.sh now does this for you; this script remains for the times
# you have hand-edited a version and need the poms brought back into line.
set -e
mvn org.eclipse.tycho:tycho-versions-plugin:update-pom
