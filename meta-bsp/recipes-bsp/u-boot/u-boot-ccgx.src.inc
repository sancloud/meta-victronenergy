LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://COPYING;md5=1707d6db1d42237583f50183a5651ecb"

SRC_URI += "https://github.com/victronenergy/u-boot/archive/v2013.01.01-ccgx-v4.tar.gz"
SRC_URI[sha256sum] = "d1f3cdb1af122e63c6fdd25ddd4d284ff4480ea129d150f0339e422039bd2179"
SRC_URI[md5sum] = "b3d953ddf8d5c595a2a924ce539b6b1d"
S = "${WORKDIR}/u-boot-2013.01.01-ccgx-v4"

FILESEXTRAPATHS_prepend := "${THISDIR}/u-boot-ccgx:"

SRC_URI += " \
	file://0005-add-libubootenv.a-needed-by-swupdate.patch \
"
