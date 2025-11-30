# 1. Vi utgår från en färdig bild som har Java 17 installerat
FROM eclipse-temurin:17-jdk-alpine

# 2. Skapa en mapp inuti containern där vi lägger vår app
WORKDIR /app

# 3. Kopiera den byggda .jar-filen från din dator in i containern
# (Vi döper om den till app.jar för enkelhetens skull)
COPY build/libs/*.jar app.jar

# 4. Berätta vilket kommando som ska köras när containern startar
ENTRYPOINT ["java", "-jar", "app.jar"]