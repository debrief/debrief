#!/bin/sh
set -ex
wget http://ftp.gnome.org/pub/gnome/sources/gcab/0.7/gcab-0.7.tar.xz
tar xvf gcab-0.7.tar.xz
cd gcab-0.7 && ./configure --prefix=/usr && make -j10 && sudo make install
cd ..
wget http://ftp.gnome.org/pub/GNOME/sources/msitools/0.98/msitools-0.98.tar.xz
tar xvf msitools-0.98.tar.xz
cd msitools-0.98 && ./configure --prefix=/usr && make -j10 && sudo make install
cd ..
