#!/bin/bash

oc logs -f -n ${NAMESPACE_PREFIX}south-america -l app=consumer-southamerica
