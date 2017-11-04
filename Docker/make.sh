basepath=$(cd `dirname $0`; pwd)
REGISTRY=registry.wolfpeng.com:5000
echo $1
timestamp=`date +%s`
echo $timestamp
docker build -t wolfpeng/$1:${timestamp} .
docker tag wolfpeng/$1:${timestamp} ${REGISTRY}/wolfpeng/$1:latest
docker push ${REGISTRY}/wolfpeng/$1:latest
