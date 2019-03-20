FROM frolvlad/alpine-oraclejdk8:slim
VOLUME /tmp
ADD ipfsgateway-0.0.1-SNAPSHOT.jar app.jar
RUN sh -c 'touch /app.jar'
RUN cp /etc/hosts /tmp/hosts
WORKDIR /root
ENV IPFS_HOME=/root/go-ipfs
ENV IPFS_CMD=/root/go-ipfs/ipfs
EXPOSE 5001
EXPOSE 4001
EXPOSE 80
EXPOSE 8080
#RUN wget https://dist.ipfs.io/go-ipfs/v0.4.13/go-ipfs_v0.4.13_linux-amd64.tar.gz
#RUN tar xfz go-ipfs_v0.4.13_linux-amd64.tar.gz
#RUN cd go-ipfs 
COPY go-ipfs.tar.gz go-ipfs.tar.gz
RUN tar zvxf go-ipfs.tar.gz
#RUN /root/go-ipfs/install.sh
RUN /root/go-ipfs/ipfs init
#RUN nohup /root/go-ipfs/ipfs daemon>/tmp/nohup.out
ENV JAVA_OPTS="-server -Xms32m -Xmx512m"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app.jar" ]
CMD ["/root/go-ipfs/ipfs daemon", "--migrate=true"]

