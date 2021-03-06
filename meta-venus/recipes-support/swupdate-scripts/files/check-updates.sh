#!/bin/bash

. $(dirname $0)/functions.sh

get_setting() {
    dbus-send --print-reply=literal --system --type=method_call \
              --dest=com.victronenergy.settings \
              "/Settings/System/$1" \
              com.victronenergy.BusItem.GetValue |
        awk '{ print $3 }'
}

get_swu_version() {
    curl -s -r 0-999 -m 30 --retry 3 "$1" |
        cpio --quiet -i --to-stdout sw-description 2>/dev/null |
        sed -n '/version/ {
            s/.*"\(.*\)".*/\1/
            p
            q
        }'
}

swu_status() {
    printf '%s\n%s\n' "$1" "$2" >$status_file
}

status_file=/var/run/swupdate-status

# location of named pipe
named_pipe=/var/tmp/check-updates

# create named pipe
# if it exists, an update is already in progress
mkfifo $named_pipe || exit

# remove pipe on the exit signal
trap "rm -f $named_pipe" EXIT

# start multilog process in background with stdin coming from named pipe
exec 3>&1
tee <$named_pipe /dev/fd/3 | multilog t s99999 n8 /log/swupdate &

# redirect stderr and stdout to named_pipe
exec 1>$named_pipe 2>&1

echo "Checking for updates"

for arg; do
    case $arg in
        -auto)   update=auto ;;
        -check)  update=2    ;;
        -update) update=1    ;;
        -delay)  delay=y     ;;
        -force)  force=y     ;;
        *)       echo "Invalid option $arg"
                 exit 1
                 ;;
    esac
done

if [ "${update:-auto}" = auto ]; then
    update=$(get_setting AutoUpdate)
    case $update in
        0) echo "Auto-update disabled"
           exit
           ;;
        1) ;;
        2) ;;
        *) echo "Invalid AutoUpdate value $update"
           exit 1
           ;;
    esac
fi

if [ "$delay" = y ]; then
    DELAY=$[ $RANDOM % 3600 ]
    echo "Sleeping for $DELAY seconds"
    sleep $DELAY
fi

feed=$(get_setting ReleaseType)

case $feed in
    0) feed=release   ;;
    1) feed=candidate ;;
    2) feed=testing   ;;
    3) feed=develop   ;;
    *) echo "Invalid release type"
       exit 1
       ;;
esac

machine=$(cat /etc/venus/machine)

URL_BASE=https://updates.victronenergy.com/feeds/venus/swu/${feed}
SWU=${URL_BASE}/venus-swu-${machine}.swu

echo "Retrieving latest version"
swu_status 1

cur_version=$(get_version)
swu_version=$(get_swu_version "$SWU")

if [ -z "$swu_version" ]; then
    echo "Unable to retrieve latest software version"
    swu_status -1
    exit 1
fi

cur_build=${cur_version%% *}
swu_build=${swu_version%% *}

SWU=${URL_BASE}/venus-swu-${machine}-${swu_build}.swu

echo "installed: $cur_version, available: $swu_version"

if [ "$force" != y -a "${swu_build}" -le "${cur_build}" ]; then
    swu_status 0
    exit
fi

if [ "$update" != 1 ]; then
    swu_status 0 "$swu_version"
    exit
fi

altroot=$(get_altrootfs)

if [ -z "$altroot" ]; then
    echo "Unable to determine rootfs"
    swu_status -2 "$swu_version"
    exit 1
fi

if ! lock; then
    swu_status 0 "$swu_version"
    exit
fi

echo "Installing version $swu_version"
swu_status 2 "$swu_version"

# backup rootfs is about to be replaced, remove its version entry
get_version >/var/run/versions

if swupdate -b "0 1 2 3 4 5 6 7 8 9 10 11" -d "$SWU" -e "stable,copy$altroot" \
            -t 30 -r 3; then
    reboot
else
    swu_status -2 "$swu_version"
fi

unlock
