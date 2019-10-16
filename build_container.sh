#!/bin/bash

# We need this to identify the build artefacts. E.g. docker image tag.
mkdir -p ${ARCHIVE_DIR}


BUILD_TIMESTAMP=$(date "+%Y%m%d-%H%M%S")
echo "BUILD_TIMESTAMP=${BUILD_TIMESTAMP}" >> ${ARCHIVE_DIR}/build.properties
echo -e "Build environment variables:"
echo "REGISTRY_URL=${REGISTRY_URL}"
echo "REGISTRY_NAMESPACE=${REGISTRY_NAMESPACE}"
echo "IMAGE_NAME=${IMAGE_NAME}"
echo "BUILD_TIMESTAMP=$BUILD_TIMESTAMP"

echo -e "Checking for Dockerfile at the repository root"
if [ -f Dockerfile ]; then
   echo "Dockerfile found"
else
    echo "Dockerfile not found"
    exit 1
fi

echo -e "Building container image"
set -x
bx cr build \
    -t $REGISTRY_URL/$REGISTRY_NAMESPACE/$IMAGE_NAME:${BUILD_TIMESTAMP} \
    --build-arg SONAR_TOKEN=${SONAR_TOKEN} \
    --build-arg SONAR_HOST=${SONAR_HOST} \
    --build-arg PROJECT_VERSION=${PROJECT_VERSION} \
    .
set +x

BUILD_NAME=$(echo "build-${BUILD_NUMBER}-${BUILD_TIMESTAMP}")
echo -e "Tagging git commit ${GIT_COMMIT} with build tag '${BUILD_NAME}'"
git tag ${BUILD_NAME}
git push origin ${BUILD_NAME}

# By saving MY_APP_NAME and MY_BUILD_NUMBER to the build.properties file,
# we ensure that the app name and build number are consistent across all
# stages of the pipeline.
echo "SERVICE_NAME=${SERVICE_NAME}" >> ${ARCHIVE_DIR}/build.properties
echo "BUILD_NAME=${BUILD_NAME}" >> ${ARCHIVE_DIR}/build.properties

cp -r ./k8s/ ${ARCHIVE_DIR}/
cp  apply_k8s_deployment.sh ${ARCHIVE_DIR}/

ibmcloud doi publishbuildrecord \
    --logicalappname="$SERVICE_NAME" \
    --buildnumber="$BUILD_NAME" \
    --branch="$GIT_BRANCH" \
    --repositoryurl="$GIT_URL" \
    --commitid="$GIT_COMMIT"  \
    --status=pass
