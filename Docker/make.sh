basepath=$(cd `dirname $0`; pwd)
REGISTRY=registry.wolfpeng.com:5000
timestamp=`date +%s`

docker build -t wolfpeng/$1:1.0.0 ${basepath}
docker tag wolfpeng/$1:1.0.0 ${REGISTRY}/wolfpeng/$1:latest
docker push ${REGISTRY}/wolfpeng/$1:latest

ssh wolfpeng@project.wolfpeng.com sh /home/wolfpeng/start.sh