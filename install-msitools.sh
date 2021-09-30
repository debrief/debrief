#!/bin/sh
set -ex
wget http://ftp.gnome.org/pub/GNOME/sources/msitools/0.100/msitools-0.100.tar.xz
tar xvf msitools-0.100.tar.xz
cd msitools-0.100 && ./configure --prefix=/usr && make -j10 && sudo make install
cd ..
ls /usr/bin/wixl
ls /usr/bin/wixl-heat
