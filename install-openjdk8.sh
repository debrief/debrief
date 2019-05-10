#!/bin/sh
set -ex

sudo add-apt-repository ppa:openjdk-r/ppa -y

sudo apt-get -y update
sudo apt-get -y install openjdk-8-jdk
