#!/bin/sh
set -ex
wget http://ftp.gnome.org/pub/GNOME/sources/msitools/0.101/msitools-0.100.tar.xz
tar xvf msitools-0.101.tar.xz
cd msitools-0.101 && ./configure --prefix=/usr && make -j10 && sudo make install
cd ..
