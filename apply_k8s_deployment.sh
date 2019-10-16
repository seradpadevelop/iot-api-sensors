#!/bin/bash

# Make sure the cluster is running and get the ip_address
ip_addr=$(bx cs workers $PIPELINE_KUBERNETES_CLUSTER_NAME | grep normal | awk '{ print $2 }')
if [ -z $ip_addr ]; then
  echo "$PIPELINE_KUBERNETES_CLUSTER_NAME not created or workers not ready"
  exit 1
fi

# Initialize script variables
export CLUSTER_NAME_LOWERCASE=$(echo ${PIPELINE_KUBERNETES_CLUSTER_NAME} | tr '[:upper:]' '[:lower:]')
echo ""
echo "Deploy environment variables:"
echo "IMAGE=$IMAGE"
echo "SERVICE_NAME=${SERVICE_NAME}"
echo "BUILD_NAME=${BUILD_NAME}"
echo "DEPLOYMENT_ENV=${DEPLOYMENT_ENV}"
echo ""

DEPLOYMENT_FILE="deployment.yml"
echo "Creating deployment manifest $DEPLOYMENT_FILE"

# Build the deployment file and replace environment variables
# with current values.
envsubst < ./k8s/deployment.yml > $DEPLOYMENT_FILE

# Show the file that is about to be executed
echo ""
echo "DEPLOYING USING MANIFEST:"
echo "***"
cat $DEPLOYMENT_FILE
echo "***"

# Execute the file
echo "KUBERNETES COMMAND:"
echo "kubectl apply -f $DEPLOYMENT_FILE"
kubectl apply -f $DEPLOYMENT_FILE
echo ""

echo "Waiting for Kubernetes until deployment is rolled-out"
APP_NAME=iot-api-sensors-deployment
kubectl rollout status deployment ${APP_NAME} -n iot
ROLLOUT_STATUS=$?
echo $ROLLOUT_STATUS

if [ ${ROLLOUT_STATUS} = "0" ]; then
  STATUS=pass;
else
    STATUS=fail;
fi;

echo "Set ROLLOUT_STATUS to '${STATUS}'."

ibmcloud doi publishdeployrecord \
  --logicalappname="${SERVICE_NAME}" \
  --buildnumber="${BUILD_NAME}" \
  --env="${DEPLOYMENT_ENV}" \
  --status=$STATUS

if [ ${ROLLOUT_STATUS} != "0" ]; then
  echo "Rolling back the deployment"
  kubectl rollout undo deployment ${SERVICE_NAME}
  kubectl rollout status deployment ${SERVICE_NAME}
  exit 1
fi
