#!/bin/sh
# Installs wixl / wixl-heat (msitools), used by contribs/msi/make_x64msi.sh.
#
# Uses the distro package from the signed Ubuntu archive instead of compiling
# a source tarball and running `make install` as root. Ubuntu 24.04 ships
# msitools 0.103, newer than the 0.100 this script used to build.
set -ex

MIN_VERSION=0.100

i=1
until sudo apt-get -y -o Acquire::Retries=3 install wixl; do
  if [ "$i" -ge 3 ]; then
    echo "failed to install wixl after $i attempts" >&2
    exit 1
  fi
  i=$((i + 1))
  sleep 15
done

version=$(wixl --version)
if [ "$(printf '%s\n%s\n' "$MIN_VERSION" "$version" | sort -V | head -n1)" != "$MIN_VERSION" ]; then
  echo "wixl $version is older than the required $MIN_VERSION" >&2
  exit 1
fi
command -v wixl-heat
