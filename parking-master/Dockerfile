FROM java:8

EXPOSE 8071

RUN mkdir -p /files

COPY public_key.der /files/public_key.der

ADD /target/PARKING.jar PARKING.jar

ENTRYPOINT ["java","-Dspring.profiles.active=prod", "-jar", "PARKING.jar", "prod-entrypoint" ]
#ENTRYPOINT ["java","-Dspring.profiles.active=dev", "-jar", "ussd-0.0.1-SNAPSHOT.jar", "prod-entrypoint" ]
