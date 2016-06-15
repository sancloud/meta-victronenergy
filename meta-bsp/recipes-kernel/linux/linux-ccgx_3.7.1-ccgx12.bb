# fix behavior of base do_install_prepend - its overwrite ready to use uImage by uncompresses Image
require linux-ccgx.inc

# Mind it, this recipe is not installed itself but provides kernel-image etc.
# Hence RPEDEND on that one....
RDEPENDS_kernel-image += "linux-backports"
RDEPENDS_kernel-image += "kernel-modules"
RDEPENDS_kernel-image += "mtd-utils"

SRC_URI = "https://github.com/victronenergy/linux/archive/v${PV}.tar.gz"
SRC_URI[md5sum] = "08b5aa1413f9578defbb04dbcaa66815"
SRC_URI[sha256sum] = "9fdb5714b82848ce4018ced9eb9adc2b96a7aaaac209ed360e7b5d16aa60bcba"

SRC_URI += " \
	file://0001-enable-CONFIG_DEVTMPFS-for-newer-udev.patch \
	file://0002-ARM-7668-1-fix-memset-related-crashes-caused-by-rece.patch \
	file://0003-ARM-7670-1-fix-the-memset-fix.patch \
	file://0004-import-compiler-gcc5.h-from-v4.1-it-gets-merge-there.patch \
	file://0005-ARM-8158-1-LLVMLinux-use-static-inline-in-ARM-ftrace.patch \
	file://0006-compiler-gcc-integrate-the-various-compiler-gcc-345-.patch \
	file://0007-compiler-gcc.h-Add-gcc-recommended-GCC_VERSION-macro.patch \
	file://0008-kbuild-remove-deprecated-use-of-defined-in-timeconst.patch \
"

# This was introduced to remove uImage from /boot and save 3MB
KERNEL_DROPIMAGE = ""

PR = "r2"

S = "${WORKDIR}/linux-${PV}"
