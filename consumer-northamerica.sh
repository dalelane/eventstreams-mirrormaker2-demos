#!/bin/bash

oc logs -f -n ${NAMESPACE_PREFIX}north-america -l app=consumer-northamerica
