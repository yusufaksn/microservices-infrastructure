# 1. Aşama: Uygulamayı hafif bir imajda çalıştırmak için hazır JRE kullanıyoruz
FROM eclipse-temurin:21-jre-alpine

# Çalışma dizini oluştur
WORKDIR /app

# Maven build sonrası oluşan GERÇEK JAR dosyasını kopyala (İsim güncellendi 👇)
COPY target/ticket-0.0.1-SNAPSHOT.jar app.jar

# Uygulamanın çalışacağı portu belirt (Sizin port 8504)
EXPOSE 8504

# Uygulamayı başlat
ENTRYPOINT ["java", "-jar", "app.jar"]