#!/bin/bash

oc logs -f -n ${NAMESPACE_PREFIX}europe -l app=consumer-europe
