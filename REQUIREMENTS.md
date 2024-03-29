To create these demos, you will need an OpenShift cluster with persistent storage available.

On your computer, you will need these to run the scripts to deploy the demos:
- the `oc` OpenShift CLI
- Python 3
- Ansible

You will need an entitlement key from the IBM Container software library - https://myibm.ibm.com/products-services/containerlibrary

Note that the playbooks in this repository will install the IBM Event Streams operator in all namespaces - so it is not suitable for running against an OpenShift cluster that already has Event Streams installed in individual namespaces.
