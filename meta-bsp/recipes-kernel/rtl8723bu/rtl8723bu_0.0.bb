SUMMARY = "Add the rtl8723bu driver from Larry Finger as an out-of-tree module"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe"

inherit module

SRC_URI = " \
	gitsm://github.com/lwfinger/rtl8723bu.git;protocol=https;rev=master \
	file://0001-WIP-fix-makefile-for-openembedded.patch \
"

S = "${WORKDIR}/git"

# The inherit of module.bbclass will automatically name module packages with
# "kernel-module-" prefix as required by the oe-core build environment.

do_install() {
    # Module
    install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/net/wireless
    install -m 0644 8723bu.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/net/wireless/8723bu.ko
}
