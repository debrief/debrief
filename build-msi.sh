#!/bin/sh
set -ex
npm install
rm -rf node_modules/msi-packager
git clone https://github.com/saulhidalgoaular/msi-packager.git
mv msi-packager node_modules/
node index.js
