FROM eclipse-temurin:21-jre
RUN apt-get update && apt-get install -y curl && rm -rf /var/lib/apt/lists/*
WORKDIR /app
COPY target/*.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
# force rebuild Fri Jun 19 16:24:54 MSK 2026
# force rebuild Fri Jun 19 16:35:33 MSK 2026
