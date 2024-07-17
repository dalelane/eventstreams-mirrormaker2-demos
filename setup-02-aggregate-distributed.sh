#!/bin/bash

# exit when any command fails
set -e

# allow this script to be run from other locations, despite the
#  relative file paths used in it
if [[ $BASH_SOURCE = */* ]]; then
    cd -- "${BASH_SOURCE%/*}/" || exit
fi

# check that a username is available
if [ -z "$STUDENT_USERNAME" ]; then
    echo "Error: Required environment variable STUDENT_USERNAME is missing."
    exit 1
fi

# use the Python environment pre-created for ansible
source ./venv/bin/activate

# run the playbook
ansible-playbook   \
    -e ansible_python_interpreter="$(pwd)/venv/bin/python3" \
    -e ibm_entitlement_key=$ENTITLEMENTKEY \
    -e storage_class=ocs-storagecluster-ceph-rbd \
    -e namespace_prefix="$STUDENT_USERNAME-" \
    02-aggregate-distributed/setup.yaml

# display URL and username/password for each Event Streams cluster
NAMESPACE_PREFIX="$STUDENT_USERNAME-" ./common/scripts/display-ui-details.sh
