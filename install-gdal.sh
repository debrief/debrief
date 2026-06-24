OPENJPEG_VERSION=2.3.1
PROJ_INSTALL_PREFIX=/usr/local
PROJ_VERSION=master

# Installing openjpeg

wget -q https://github.com/uclouvain/openjpeg/archive/v${OPENJPEG_VERSION}.tar.gz
tar xzf v${OPENJPEG_VERSION}.tar.gz
rm -f v${OPENJPEG_VERSION}.tar.gz
cd openjpeg-${OPENJPEG_VERSION}
cmake . -DBUILD_SHARED_LIBS=ON  -DBUILD_STATIC_LIBS=OFF -DCMAKE_BUILD_TYPE=Release -DCMAKE_INSTALL_PREFIX=/usr
make -j10
make install
mkdir -p /build_thirdparty/usr/lib 
cp -P /usr/lib/libopenjp2*.so* /build_thirdparty/usr/lib
for i in /build_thirdparty/usr/lib/*; do strip -s $i 2>/dev/null || /bin/true; done
cd ..
rm -rf openjpeg-${OPENJPEG_VERSION}

# Download Proj

mkdir -p /build_projgrids/${PROJ_INSTALL_PREFIX}/share/proj
curl -LOs http://download.osgeo.org/proj/proj-datumgrid-latest.zip
unzip -q -j -u -o proj-datumgrid-latest.zip  -d /build_projgrids/${PROJ_INSTALL_PREFIX}/share/proj
rm -f *.zip

# Build Proj

mkdir proj
wget -q https://github.com/OSGeo/PROJ/archive/${PROJ_VERSION}.tar.gz -O - | tar xz -C proj --strip-components=1 
cd proj
./autogen.sh
CFLAGS='-DPROJ_RENAME_SYMBOLS -O2' CXXFLAGS='-DPROJ_RENAME_SYMBOLS -DPROJ_INTERNAL_CPP_NAMESPACE -O2' ./configure --prefix=${PROJ_INSTALL_PREFIX} --disable-static
make -j10
make install DESTDIR="/build"
cd .. 
rm -rf proj 

PROJ_SO=$(readlink /build${PROJ_INSTALL_PREFIX}/lib/libproj.so | sed "s/libproj\.so\.//")
PROJ_SO_FIRST=$(echo $PROJ_SO | awk 'BEGIN {FS="."} {print $1}')
mv /build${PROJ_INSTALL_PREFIX}/lib/libproj.so.${PROJ_SO} /build${PROJ_INSTALL_PREFIX}/lib/libinternalproj.so.${PROJ_SO} 
ln -s libinternalproj.so.${PROJ_SO} /build${PROJ_INSTALL_PREFIX}/lib/libinternalproj.so.${PROJ_SO_FIRST} 
ln -s libinternalproj.so.${PROJ_SO} /build${PROJ_INSTALL_PREFIX}/lib/libinternalproj.so 
rm /build${PROJ_INSTALL_PREFIX}/lib/libproj.*  
ln -s libinternalproj.so.${PROJ_SO} /build${PROJ_INSTALL_PREFIX}/lib/libproj.so.${PROJ_SO_FIRST} 
strip -s /build${PROJ_INSTALL_PREFIX}/lib/libinternalproj.so.${PROJ_SO} 
for i in /build${PROJ_INSTALL_PREFIX}/bin/*; do strip -s $i 2>/dev/null || /bin/true; done

# Build Gdal
git clone https://github.com/OSGeo/gdal.git
cd gdal/gdal
./configure --prefix=/usr --without-libtool \
    --with-hide-internal-symbols \
    --with-jpeg12 \
    --with-python \
    --with-webp --with-proj=/build${PROJ_INSTALL_PREFIX} \
    --with-libtiff=internal --with-rename-internal-libtiff-symbols \
    --with-geotiff=internal --with-rename-internal-libgeotiff-symbols
make -j10
make install DESTDIR="/build" 

cd ../..
rm -rf gdal 
mkdir -p /build_gdal_python/usr/lib 
mkdir -p /build_gdal_python/usr/bin 
mkdir -p /build_gdal_version_changing/usr/include 
#mv /build/usr/lib/python3            /build_gdal_python/usr/lib 
mv /build/usr/lib                    /build_gdal_version_changing/usr 
mv /build/usr/include/gdal_version.h /build_gdal_version_changing/usr/include 
mv /build/usr/bin/*.py               /build_gdal_python/usr/bin 
mv /build/usr/bin                    /build_gdal_version_changing/usr 
for i in /build_gdal_version_changing/usr/lib/*; do strip -s $i 2>/dev/null || /bin/true; done 
for i in /build_gdal_python/usr/lib/python3/dist-packages/osgeo/*.so; do strip -s $i 2>/dev/null || /bin/true; done 
for i in /build_gdal_version_changing/usr/bin/*; do strip -s $i 2>/dev/null || /bin/true; done


# PROJ dependencies
apt-get update
DEBIAN_FRONTEND=noninteractive apt-get install -y  --no-install-recommends \
        libsqlite3-0 libtiff5 libcurl4 \
        curl unzip ca-certificates

# GDAL dependencies
apt-get update -y
DEBIAN_FRONTEND=noninteractive apt-get install -y  --no-install-recommends \
        python3-numpy libpython3.8 \
        libjpeg-turbo8 libgeos-3.8.0 libgeos-c1v5 \
        libexpat1 \
        libxerces-c3.2 \
        libwebp6 \
        libzstd1 bash libpq5 libssl1.1 libopenjp2-7

cp -r /build_projgrids/usr/* /usr/

cp -r /build${PROJ_INSTALL_PREFIX}/share/proj/* ${PROJ_INSTALL_PREFIX}/share/proj/
cp -r /build${PROJ_INSTALL_PREFIX}/include/* ${PROJ_INSTALL_PREFIX}/include/
cp -r /build${PROJ_INSTALL_PREFIX}/bin/* ${PROJ_INSTALL_PREFIX}/bin/
cp -r /build${PROJ_INSTALL_PREFIX}/lib/* ${PROJ_INSTALL_PREFIX}/lib/

cp -r /build/usr/share/gdal /usr/share/gdal
cp -r /build/usr/include/* /usr/include/
cp -r /build_gdal_python/usr/* /usr/
cp -r /build_gdal_version_changing/usr/* /usr/

ldconfig

# Let's test our success :) 
gdal_create --version






