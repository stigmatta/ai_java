# Етап 1 — збірка
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Копіюємо Gradle
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# Виправляємо проблему з Windows-закінченнями рядків (якщо актуально)
RUN chmod +x gradlew

# Збірка (додано прапор для детальних логів у разі помилки)
COPY src src
RUN ./gradlew build -x test --no-daemon --stacktrace

# Етап 2 — фінальний образ
FROM eclipse-temurin:17-jre AS runtime
WORKDIR /app

# Копіюємо JAR (використовуємо маску для надійності)
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

ENV PORT=8080
EXPOSE ${PORT}

ENTRYPOINT ["sh", "-c", "java -Dserver.port=${PORT} -jar app.jar"]