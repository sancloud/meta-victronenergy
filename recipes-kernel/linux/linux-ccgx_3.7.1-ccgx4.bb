# fix behavior of base do_install_prepend - its overwrite ready to use uImage by uncompresses Image
require linux-ccgx.inc

# Mark archs/machines that this kernel supports
#DEFAULT_PREFERENCE = "-1"
#DEFAULT_PREFERENCE_mcx = "1"

RDEPENDS_${PN} = "linux-backports"
BB_FETCH_PREMIRRORONLY = "0"
PREMIRRORS = ""

SRC_URI = "https://github.com/victronenergy/linux/archive/v${PV}.tar.gz"
SRC_URI[md5sum] = "3f372efb1d3d59388fdea50f16284cb6"
SRC_URI[sha256sum] = "2b6a7371219c328db31d5ee8413a79df542ee0323ef554bd15f857c95747adbe"

# This was introduced to remove uImage from /boot and save 3MB
KERNEL_DROPIMAGE = ""

PR = "r1"

S = "${WORKDIR}/linux-${PV}"

pkg_postinst_kernel-base_append() {
	if [ "x$D" == "x" ]; then
		if [ -e /proc/mtd ]; then
			MTD_DEV=`grep kernel /proc/mtd`
			LINUX_DEV=${MTD_DEV:0:4}
		else
			echo "ERROR: No MTD device"
			exit 1;
		fi
	
		if [ -f /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION} ] && [ -n $LINUX_DEV ]; then
			echo "INFO: Erasing $LINUX_DEV"
			flash_erase  /dev/$LINUX_DEV 0 0
			echo "INFO: Write Linux kernel > $LINUX_DEV"
			nandwrite -p /dev/$LINUX_DEV /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION}
			rm /${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION}
			echo "Update finished!"
		else
			echo "ERROR: No kernel (/${KERNEL_IMAGEDEST}/${KERNEL_IMAGETYPE}-${KERNEL_VERSION}) image found!"
		fi
	else
		# Exit 1 is used to set the status of the package on unpacked in rootfs image
		# The result is that the package will be installed on first boot
		exit 1
	fi
}