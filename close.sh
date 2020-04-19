container_id=`head -1 /proc/self/cgroup|cut -d/ -f3|cut -c1-12`
docker --tlsverify --tlscacert=/home/certs/ca.pem --tlscert=/home/certs/cert.pem --tlskey=/home/certs/key.pem -H tcp://120.53.27.31:2376 stop ${container_id}
