container_id=`head -1 /proc/self/cgroup|cut -d/ -f3|cut -c1-12`
docker -H tcp://${HOST_IP}:2376 stop ${container_id}

