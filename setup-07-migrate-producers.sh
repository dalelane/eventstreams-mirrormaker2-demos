#!/bin/bash

# exit when any command fails
set -e

# allow this script to be run from other locations, despite the
#  relative file paths used in it
if [[ $BASH_SOURCE = */* ]]; then
    cd -- "${BASH_SOURCE%/*}/" || exit
fi

# set up a Python environment that ansible can use
if [ ! -d venv ]; then
    echo "Creating virtual environment for use by Ansible"
    python3 -m venv venv
    source ./venv/bin/activate
    pip3 install packaging
fi

# run the playbook
ansible-playbook   \
    -e ansible_python_interpreter="$(pwd)/venv/bin/python3" \
    -e ibm_entitlement_key=$ENTITLEMENTKEY \
    -e storage_class=ocs-storagecluster-ceph-rbd \
    -e namespace_prefix="student07-" \
    07-migration/migrate-producers.yaml
