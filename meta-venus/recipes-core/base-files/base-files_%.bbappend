inherit ve_package

FILESEXTRAPATHS_prepend := "${THISDIR}/files:"

# Add the mount point for the data partition
dirs755 += " /data"

# mount point for the scratch partition
dirs755 += "/scratch"

do_install_append() {
	# Replace home dir with symlink to persistent volume
	if [ -d ${D}/home/root ]; then
		rmdir ${D}/home/root
		ln -s ${permanentdir}/home/root ${D}/home/root
	fi

	# Replace /media with symlink to volatile location
	if [ -d ${D}/media ]; then
		rmdir ${D}/media
		ln -s ${localstatedir}/volatile/media ${D}/media
	fi
}
