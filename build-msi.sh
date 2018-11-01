#!/bin/sh
set -ex
npm i -g npm
npm uninstall npm
npm install
rm -rf node_modules/msi-packager
git clone https://github.com/saulhidalgoaular/msi-packager.git
mv msi-packager node_modules/
version=$(grep "product.*version"  /home/travis/build/debrief/debrief/org.mwc.debrief.product/debriefng.product  | sed 's/^.*version="\([^"]*\)".*$/\1/')
sed -i "s/versionReplacement/$version/g" build-x86.js
sed -i "s/versionReplacement/$version/g" build-x64.js
node build-x86.js
node build-x64.js
