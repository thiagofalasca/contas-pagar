# Usa o JDK 21 e instala o Maven manualmente
FROM eclipse-temurin:21-jdk AS builder

# Define a versão do Maven
ARG MAVEN_VERSION=3.9.0

# Instala dependências necessárias para baixar e extrair o Maven
RUN apt-get update && apt-get install -y curl tar && rm -rf /var/lib/apt/lists/*

# Baixa e instala o Maven
RUN mkdir -p /usr/share/maven && \
    curl -fsSL https://archive.apache.org/dist/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz -o /tmp/maven.tar.gz && \
    tar -xzvf /tmp/maven.tar.gz -C /usr/share/maven --strip-components=1 && \
    rm /tmp/maven.tar.gz

ENV MAVEN_HOME=/usr/share/maven
ENV PATH=$MAVEN_HOME/bin:$PATH

WORKDIR /app

# Copia os arquivos do projeto
COPY pom.xml .
COPY src/ src/

# Compila o projeto
RUN mvn clean package -DskipTests

# Imagem para executar o aplicativo
FROM eclipse-temurin:21-jdk
WORKDIR /app

# Copia o JAR gerado no build
COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]