#!/bin/sh
set -ex
wget http://ftp.gnome.org/pub/GNOME/sources/msitools/0.98/msitools-0.98.tar.xz
tar xvf msitools-0.98.tar.xz
cd msitools-0.98 && ./configure --prefix=/usr && make && sudo make install
