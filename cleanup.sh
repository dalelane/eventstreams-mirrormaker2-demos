#!/bin/bash

oc delete project --ignore-not-found ${NAMESPACE_PREFIX}north-america
oc delete project --ignore-not-found ${NAMESPACE_PREFIX}south-america
oc delete project --ignore-not-found ${NAMESPACE_PREFIX}europe

# oc delete --ignore-not-found -f common/install-eventstreams/templates/operator-subscription.yaml
# oc delete --ignore-not-found -f common/install-eventstreams/templates/catalog-source.yaml

# oc delete --ignore-not-found -n openshift-operators csv ibm-eventstreams.v3.3.1

echo "deleted"
