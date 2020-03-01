FROM gradle
WORKDIR /var/gradle
COPY .gradle /root/.gradle
#COPY . . 
RUN git clone https://github.com/nfrpaiva/cache-demo.git
WORKDIR /var/gradle/cache-demo
RUN gradle clean build -x test --info

FROM openjdk:8-alpine
WORKDIR /app
COPY --from=0 /var/gradle/cache-demo/build/libs/*.jar app.jar
ENTRYPOINT ["java","-jar", "app.jar"]
