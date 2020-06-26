#!/bin/bash
set -eo pipefail

export PROJECT=$(gcloud config get-value project)
export REGION="us-central1"
export ZONE="us-central1-a"
export CLUSTER="test-cluster-microservice"
export DB="ecommerce"
export DB_INSTANCE="test-instance-microservice"

bold=$(tput bold)
normal=$(tput sgr0)

# Delete cluster
echo ${bold}"Deleting cluster..."${normal}
gcloud container clusters delete $CLUSTER --zone $ZONE --async --quiet

# Delete DB
echo ${bold}"Deleting database..."${normal}
gcloud sql databases delete $DB --instance=$DB_INSTANCE --quiet
gcloud sql instances delete $DB_INSTANCE --quiet

# Delete container image
echo ${bold}"Deleting container image from the registry..."${normal}
gcloud container images list-tags \
gcr.io/$PROJECT/hipster-microservice \
    	--format 'value(digest)' | \
    	xargs -I {} gcloud container images delete \
    	--force-delete-tags --quiet \
    	gcr.io/${PROJECT}/hipster-microservice@sha256:{}

gcloud container images delete \
	--force-delete-tags \
	--quiet gcr.io/${PROJECT}/hipster-recommendation

gcloud container images delete \
	--force-delete-tags \
	--quiet gcr.io/${PROJECT}/hipster-payment

gcloud container images delete \
	--force-delete-tags \
	--quiet gcr.io/${PROJECT}/hipster-ads
