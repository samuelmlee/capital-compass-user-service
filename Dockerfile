FROM eclipse-temurin:11.0.21_9-jre-jammy
VOLUME /tmp
ARG EXTRACTED=build/extracted
COPY ${EXTRACTED}/dependencies/ ./
COPY ${EXTRACTED}/spring-boot-loader/ ./
COPY ${EXTRACTED}/snapshot-dependencies/ ./
COPY ${EXTRACTED}/application/ ./

RUN useradd -m -d /home/cnb -u 1000 cnb && chown -R cnb:cnb /home/cnb
USER cnb

ENTRYPOINT ["java","org.springframework.boot.loader.JarLauncher"]