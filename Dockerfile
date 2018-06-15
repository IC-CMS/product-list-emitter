FROM openjdk:8-alpine
MAINTAINER David Hessler <davidh.092705@gmail.com>

ENTRYPOINT ["/usr/bin/java", "-jar", "/usr/share/myservice/myservice.jar"]

ADD target/product-list-emitter.jar /usr/share/myservice/myservice.jar