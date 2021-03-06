require ${COREBASE}/meta/recipes-bsp/u-boot/u-boot.inc

LICENSE = "GPLv2+"
LIC_FILES_CHKSUM = "file://Licenses/README;md5=0507cd7da8e7ad6d6701926ec9b84c95"

UBOOT_LOCALVERSION = "-venus"
UBOOT_ENV = "uEnv"

S = "${WORKDIR}/u-boot-${PV}"

SRC_URI = " \
	https://github.com/victronenergy/u-boot/archive/v${PV}.tar.gz \
	file://uEnv.txt \
"
SRC_URI[md5sum] = "58c92bf2c46dc82f1b57817f09ca8bd8"
SRC_URI[sha256sum] = "37f7ffc75ec3c38ea3125350cc606d3ceac071ab68811c9fb0cfc25d70592e22"
