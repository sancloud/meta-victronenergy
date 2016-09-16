SUMMARY = "Add the rtl8723bu driver from Larry Finger as an out-of-tree module"
LICENSE = "GPLv2"
LIC_FILES_CHKSUM = "file://${COREBASE}/meta/COPYING.GPLv2;md5=751419260aa954499f7abaabaa882bbe"

# When building on openembedded Jethro, or older versions, make sure to apply
# this commit:
# https://github.com/openembedded/openembedded-core/commit/afcea61e8eb39234d336c706fdfd4680dea7c060
# to prevent warnings exactly like mentioned in that commit message.

# And, though wifi seems to work fine now, there are still at least two issues:
# 1) the reboot command doesn't work any more (it gets stuck, last messages on console are:
#    Rebooting... [  335.629452] musb-hdrc musb-hdrc.1.auto: remove, state 1
#    [  335.635012] usb usb1: USB disconnect, device number 1
#    [  335.640312] usb 1-1: USB disconnect, device number 2
#    [  335.645536] usb 1-1.4: USB disconnect, device number 3
#
# 2) a kernel deadlock during bootup

inherit module

SRC_URI = " \
	gitsm://github.com/lwfinger/rtl8723bu.git;protocol=https;rev=master \
	file://0001-WIP-fix-makefile-for-openembedded.patch \
	file://0002-autoconf.h-enable-CONFIG_CONCURRENT_MODE.patch \
	file://0003-concurrent_mode-fix-ioctl_cfg80211.c-compile-errors.patch \
	file://0004-concurrent_mode-fix-rtw_p2p.c-compile-errors.patch \
"

S = "${WORKDIR}/git"

# The inherit of module.bbclass will automatically name module packages with
# "kernel-module-" prefix as required by the oe-core build environment.

do_install() {
    # Module
    install -d ${D}/lib/modules/${KERNEL_VERSION}/kernel/net/wireless
    install -m 0644 8723bu.ko ${D}/lib/modules/${KERNEL_VERSION}/kernel/net/wireless/8723bu.ko
}
