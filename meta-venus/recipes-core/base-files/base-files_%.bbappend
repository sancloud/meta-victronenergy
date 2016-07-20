inherit ve_package

# Add the mount point for the data partition
dirs755 += " /data"

# Replace home dir with symlink to persistent volume
do_install_append() {
    rmdir ${D}/home/root
    ln -s ${permanentdir}/home/root ${D}/home/root
}
