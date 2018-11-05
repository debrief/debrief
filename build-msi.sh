#!/bin/sh
set -ex
rm -rf node_modules/msi-packager
git clone https://github.com/saulhidalgoaular/msi-packager.git
mv msi-packager node_modules/
version=$(grep "product.*version"  org.mwc.debrief.product/debriefng.product  | sed 's/^.*version="\([^"]*\)".*$/\1/')
sed -i '.bak' "s/versionReplacement/$version/g" build-x86.js
sed -i '.bak' "s/versionReplacement/$version/g" build-x64.js
node build-x86.js
node build-x64.js
