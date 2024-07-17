#!/bin/bash

# check that a username is available
if [ -z "$STUDENT_USERNAME" ]; then
    echo "Error: Required environment variable STUDENT_USERNAME is missing."
    exit 1
fi

oc logs -f -n ${STUDENT_USERNAME}-europe -l app=consumer-europe
