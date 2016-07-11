SRC_URI = "file://sw-description"

inherit swupdate

LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.MIT;md5=3da9cfbcb788c80a0384361b4de20420"

# IMAGE_DEPENDS: list of images that contains a root filesystem
# it will be ensured they are built before creating swupdate image
IMAGE_DEPENDS = "bpp3-rootfs"

# SWUPDATE_IMAGES: list of images that will be part of the compound image
# the list can have any binaries - images must be in the DEPLOY directory
SWUPDATE_IMAGES = "bpp3-rootfs uImage u-boot.img MLO splash.bgra"

SWUPDATE_IMAGES_FSTYPES[bpp3-rootfs] = ".ubifs"

SWUPDATE_IMAGES_NOAPPEND_MACHINE[uImage] = "1"
SWUPDATE_IMAGES_NOAPPEND_MACHINE[u-boot.img] = "1"
SWUPDATE_IMAGES_NOAPPEND_MACHINE[MLO] = "1"
SWUPDATE_IMAGES_NOAPPEND_MACHINE[splash.bgra] = "1"

do_version() {
    sed -e "s/version = .*;/version = \"${BUILDNAME} ${DISTRO_VERSION}\";/" \
        -i ${WORKDIR}/sw-description
}

addtask do_version after do_unpack before do_swuimage

do_version[depends] += "bpp3-rootfs:do_rootfs"
