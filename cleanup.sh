#!/bin/bash

# check that a username is available
if [ -z "$STUDENT_USERNAME" ]; then
    echo "Error: Required environment variable STUDENT_USERNAME is missing."
    exit 1
fi

allspaces=("apps" "north-america" "south-america" "europe")
allthings=("kafkamirrormaker2" "kafkaconnector" "kafkaconnect" "eventstreams" "kafkatopic" "kafkauser")

for space in "${allspaces[@]}"
do
    echo "resetting namespace $STUDENT_USERNAME-$space"

    oc delete deployment -n $STUDENT_USERNAME-$space consumer-$space --ignore-not-found

    for thing in "${allthings[@]}"
    do
        oc delete -n $STUDENT_USERNAME-$space `oc get -n $STUDENT_USERNAME-$space -o name $thing`
    done
done
