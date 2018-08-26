basepath=$(cd `dirname $0`; pwd)
REGISTRY=registry.com.wolfpeng.com:5000
timestamp=`date +%s`

docker build -t com.wolfpeng/$1:1.0.0 ${basepath}
docker tag com.wolfpeng/$1:1.0.0 ${REGISTRY}/com.wolfpeng/$1:latest
docker push ${REGISTRY}/com.wolfpeng/$1:latest

ssh com.wolfpeng@project.com.wolfpeng.com sh /home/com.wolfpeng/start.sh