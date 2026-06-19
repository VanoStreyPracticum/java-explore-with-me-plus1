# ==============================
# 1. Создание структуры папок
# ==============================
mkdir -p infra/config-server/src/main/resources/configurations
mkdir -p infra/discovery-server/src/main/resources
mkdir -p infra/gateway-server/src/main/resources
mkdir -p core

# ==============================
# 2. pom.xml для infra (группирующий)
# ==============================
cat > infra/pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>ru.practicum</groupId>
        <artifactId>explore-with-me</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <artifactId>infra</artifactId>
    <packaging>pom</packaging>

    <modules>
        <module>config-server</module>
        <module>discovery-server</module>
        <module>gateway-server</module>
    </modules>
</project>
EOF

# ==============================
# 3. pom.xml для core (группирующий)
# ==============================
cat > core/pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>ru.practicum</groupId>
        <artifactId>explore-with-me</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <artifactId>core</artifactId>
    <packaging>pom</packaging>

    <modules>
        <module>main-service</module>
    </modules>
</project>
EOF

# ==============================
# 4. pom.xml для config-server
# ==============================
cat > infra/config-server/pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>ru.practicum</groupId>
        <artifactId>infra</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <artifactId>config-server</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-config-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
    </dependencies>
</project>
EOF

# ==============================
# 5. pom.xml для discovery-server
# ==============================
cat > infra/discovery-server/pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>ru.practicum</groupId>
        <artifactId>infra</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <artifactId>discovery-server</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
        </dependency>
    </dependencies>
</project>
EOF

# ==============================
# 6. pom.xml для gateway-server
# ==============================
cat > infra/gateway-server/pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>ru.practicum</groupId>
        <artifactId>infra</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <artifactId>gateway-server</artifactId>

    <dependencies>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
    </dependencies>
</project>
EOF

# ==============================
# 7. Классы приложений для новых сервисов
# ==============================

# Config Server Application
mkdir -p infra/config-server/src/main/java/ru/practicum/configserver
cat > infra/config-server/src/main/java/ru/practicum/configserver/ConfigServerApplication.java << 'EOF'
package ru.practicum.configserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;

@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}
EOF

# Discovery Server Application
mkdir -p infra/discovery-server/src/main/java/ru/practicum/discoveryserver
cat > infra/discovery-server/src/main/java/ru/practicum/discoveryserver/DiscoveryServerApplication.java << 'EOF'
package ru.practicum.discoveryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);
    }
}
EOF

# Gateway Server Application
mkdir -p infra/gateway-server/src/main/java/ru/practicum/gateway
cat > infra/gateway-server/src/main/java/ru/practicum/gateway/GatewayServerApplication.java << 'EOF'
package ru.practicum.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GatewayServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(GatewayServerApplication.class, args);
    }
}
EOF

# ==============================
# 8. Конфигурации (application.yml)
# ==============================

# Config Server (native, classpath:/configurations)
cat > infra/config-server/src/main/resources/application.yml << 'EOF'
server:
  port: 0

spring:
  application:
    name: config-server
  cloud:
    config:
      server:
        native:
          searchLocations: classpath:/configurations
  profiles:
    active: native

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
  instance:
    instance-id: ${spring.application.name}:${random.value}
EOF

# Discovery Server (Eureka)
cat > infra/discovery-server/src/main/resources/application.yml << 'EOF'
server:
  port: 8761

spring:
  application:
    name: discovery-server

eureka:
  client:
    registerWithEureka: false
    fetchRegistry: false
EOF

# Gateway Server
cat > infra/gateway-server/src/main/resources/application.yml << 'EOF'
server:
  port: 8080

spring:
  application:
    name: gateway-server
  config:
    import: optional:configserver:http://localhost:8888
  cloud:
    gateway:
      discovery:
        locator:
          enabled: true
          lowerCaseServiceId: true
      routes:
        - id: main-service
          uri: lb://main-service
          predicates:
            - Path=/users/**,/admin/**,/categories/**,/events/**,/compilations/**

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
  instance:
    instance-id: ${spring.application.name}:${random.value}
EOF

# ==============================
# 9. Централизованные конфиги для main-service и stats-server
# ==============================
cat > infra/config-server/src/main/resources/configurations/main-service.yml << 'EOF'
server:
  port: 0

spring:
  application:
    name: main-service
  datasource:
    driverClassName: org.postgresql.Driver
    url: ${MAIN_DB_URL:jdbc:postgresql://localhost:5434/main_db}
    username: ${MAIN_DB_USER:postgres}
    password: ${MAIN_DB_PASSWORD:postgres}
  jpa:
    hibernate:
      ddl-auto: update
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
    show-sql: false
    defer-datasource-initialization: true

stats-server:
  url: ${STATS_SERVER_URL:http://localhost:9090}

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
  instance:
    instance-id: ${spring.application.name}:${random.value}
EOF

cat > infra/config-server/src/main/resources/configurations/stats-service.yml << 'EOF'
server:
  port: 0

spring:
  application:
    name: stats-service
  datasource:
    driverClassName: org.postgresql.Driver
    url: ${STATS_DB_URL:jdbc:postgresql://localhost:5433/stats}
    username: ${STATS_DB_USER:postgres}
    password: ${STATS_DB_PASSWORD:postgres}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

eureka:
  client:
    serviceUrl:
      defaultZone: http://localhost:8761/eureka/
  instance:
    instance-id: ${spring.application.name}:${random.value}
EOF

# ==============================
# 10. Перемещение main-service в core
# ==============================
mv main-service core/

# ==============================
# 11. Обновление корневого pom.xml
# ==============================
cat > pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.5.0</version>
        <relativePath/>
    </parent>

    <name>Explore With Me</name>

    <groupId>ru.practicum</groupId>
    <artifactId>explore-with-me</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <packaging>pom</packaging>

    <properties>
        <java.version>21</java.version>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <spring-cloud.version>2025.0.0</spring-cloud.version>
        <tomcat.version>10.1.47</tomcat.version>
        <logback.version>1.5.18</logback.version>
        <postgresql.version>42.7.5</postgresql.version>
    </properties>

    <modules>
        <module>infra</module>
        <module>core</module>
        <module>stats-service</module>
    </modules>

    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>org.springframework.cloud</groupId>
                <artifactId>spring-cloud-dependencies</artifactId>
                <version>${spring-cloud.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>

    <build>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-surefire-plugin</artifactId>
                    <configuration>
                        <systemPropertyVariables>
                            <spring.profiles.active>test</spring.profiles.active>
                        </systemPropertyVariables>
                    </configuration>
                </plugin>
                <plugin>
                    <groupId>org.apache.maven.plugins</groupId>
                    <artifactId>maven-checkstyle-plugin</artifactId>
                    <version>3.1.2</version>
                    <configuration>
                        <configLocation>checkstyle.xml</configLocation>
                        <failOnViolation>true</failOnViolation>
                        <logViolationsToConsole>true</logViolationsToConsole>
                        <includeTestSourceDirectory>true</includeTestSourceDirectory>
                    </configuration>
                    <executions>
                        <execution>
                            <goals>
                                <goal>check</goal>
                            </goals>
                            <phase>compile</phase>
                        </execution>
                    </executions>
                    <dependencies>
                        <dependency>
                            <groupId>com.puppycrawl.tools</groupId>
                            <artifactId>checkstyle</artifactId>
                            <version>10.3</version>
                        </dependency>
                    </dependencies>
                </plugin>
                <plugin>
                    <groupId>com.github.spotbugs</groupId>
                    <artifactId>spotbugs-maven-plugin</artifactId>
                    <version>4.8.5.0</version>
                    <configuration>
                        <effort>Max</effort>
                        <threshold>High</threshold>
                    </configuration>
                    <executions>
                        <execution>
                            <goals>
                                <goal>check</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
                <plugin>
                    <groupId>org.jacoco</groupId>
                    <artifactId>jacoco-maven-plugin</artifactId>
                    <version>0.8.12</version>
                    <configuration>
                        <output>file</output>
                    </configuration>
                    <executions>
                        <execution>
                            <id>jacoco-initialize</id>
                            <goals>
                                <goal>prepare-agent</goal>
                            </goals>
                        </execution>
                        <execution>
                            <id>jacoco-check</id>
                            <goals>
                                <goal>check</goal>
                            </goals>
                            <configuration>
                                <rules>
                                    <rule>
                                        <element>BUNDLE</element>
                                        <limits>
                                            <limit>
                                                <counter>INSTRUCTION</counter>
                                                <value>COVEREDRATIO</value>
                                                <minimum>0.01</minimum>
                                            </limit>
                                            <limit>
                                                <counter>LINE</counter>
                                                <value>COVEREDRATIO</value>
                                                <minimum>0.2</minimum>
                                            </limit>
                                            <limit>
                                                <counter>BRANCH</counter>
                                                <value>COVEREDRATIO</value>
                                                <minimum>0.2</minimum>
                                            </limit>
                                            <limit>
                                                <counter>COMPLEXITY</counter>
                                                <value>COVEREDRATIO</value>
                                                <minimum>0.2</minimum>
                                            </limit>
                                            <limit>
                                                <counter>METHOD</counter>
                                                <value>COVEREDRATIO</value>
                                                <minimum>0.2</minimum>
                                            </limit>
                                            <limit>
                                                <counter>CLASS</counter>
                                                <value>MISSEDCOUNT</value>
                                                <maximum>1</maximum>
                                            </limit>
                                        </limits>
                                    </rule>
                                </rules>
                            </configuration>
                        </execution>
                        <execution>
                            <id>jacoco-site</id>
                            <phase>install</phase>
                            <goals>
                                <goal>report</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>

    <profiles>
        <profile>
            <id>check</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.apache.maven.plugins</groupId>
                        <artifactId>maven-checkstyle-plugin</artifactId>
                    </plugin>
                    <plugin>
                        <groupId>com.github.spotbugs</groupId>
                        <artifactId>spotbugs-maven-plugin</artifactId>
                    </plugin>
                </plugins>
            </build>
            <reporting>
                <plugins>
                    <plugin>
                        <groupId>com.github.spotbugs</groupId>
                        <artifactId>spotbugs-maven-plugin</artifactId>
                    </plugin>
                </plugins>
            </reporting>
        </profile>
        <profile>
            <id>coverage</id>
            <build>
                <plugins>
                    <plugin>
                        <groupId>org.jacoco</groupId>
                        <artifactId>jacoco-maven-plugin</artifactId>
                    </plugin>
                </plugins>
            </build>
        </profile>
    </profiles>
</project>
EOF

# ==============================
# 12. Обновление pom.xml в main-service (смена родителя на core)
# ==============================
cat > core/main-service/pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>ru.practicum</groupId>
        <artifactId>core</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <artifactId>main-service</artifactId>
    <name>Main Service</name>
    <packaging>jar</packaging>

    <dependencies>
        <!-- Spring Boot Web -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <!-- Spring Boot Data JPA -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <!-- Spring Boot Validation -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Spring Boot Actuator -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <!-- PostgreSQL Driver -->
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
            <scope>runtime</scope>
        </dependency>

        <!-- Stats Client -->
        <dependency>
            <groupId>ru.practicum</groupId>
            <artifactId>stats-client</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- Stats DTO -->
        <dependency>
            <groupId>ru.practicum</groupId>
            <artifactId>stats-dto</artifactId>
            <version>${project.version}</version>
        </dependency>

        <!-- Lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <!-- MapStruct -->
        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
            <version>1.5.5.Final</version>
        </dependency>

        <!-- Spring Cloud Eureka Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

        <!-- Spring Cloud Config Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>

        <!-- Testing -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!-- H2 for testing -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
                <configuration>
                    <excludes>
                        <exclude>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                        </exclude>
                    </excludes>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.11.0</version>
                <configuration>
                    <source>21</source>
                    <target>21</target>
                    <annotationProcessorPaths>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok</artifactId>
                            <version>1.18.30</version>
                        </path>
                        <path>
                            <groupId>org.mapstruct</groupId>
                            <artifactId>mapstruct-processor</artifactId>
                            <version>1.5.5.Final</version>
                        </path>
                        <path>
                            <groupId>org.projectlombok</groupId>
                            <artifactId>lombok-mapstruct-binding</artifactId>
                            <version>0.2.0</version>
                        </path>
                    </annotationProcessorPaths>
                </configuration>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-checkstyle-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
EOF

# ==============================
# 13. Обновление application.properties в main-service (переход на config server)
# ==============================
cat > core/main-service/src/main/resources/application.properties << 'EOF'
spring.application.name=main-service
spring.config.import=optional:configserver:http://localhost:8888
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/
EOF

# ==============================
# 14. Обновление stats-server для Eureka и Config
# ==============================
# Добавим зависимости в stats-server pom.xml (если ещё не добавлены)
cat > stats-service/stats-server/pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <parent>
        <groupId>ru.practicum</groupId>
        <artifactId>stats-service</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <modelVersion>4.0.0</modelVersion>
    <artifactId>stats-server</artifactId>

    <dependencies>
        <dependency>
            <groupId>ru.practicum</groupId>
            <artifactId>stats-dto</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>

        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>

        <dependency>
            <groupId>org.mapstruct</groupId>
            <artifactId>mapstruct</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>

        <!-- Eureka Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
        </dependency>

        <!-- Config Client -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-config</artifactId>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
EOF

# ==============================
# 15. Обновление application.properties в stats-server
# ==============================
cat > stats-service/stats-server/src/main/resources/application.properties << 'EOF'
spring.application.name=stats-service
spring.config.import=optional:configserver:http://localhost:8888
eureka.client.serviceUrl.defaultZone=http://localhost:8761/eureka/
EOF

# ==============================
# 16. Добавление spring-cloud-commons в stats-client
# ==============================
cat > stats-service/stats-client/pom.xml << 'EOF'
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
         http://maven.apache.org/xsd/maven-4.0.0.xsd">

    <parent>
        <groupId>ru.practicum</groupId>
        <artifactId>stats-service</artifactId>
        <version>0.0.1-SNAPSHOT</version>
    </parent>

    <modelVersion>4.0.0</modelVersion>
    <artifactId>stats-client</artifactId>

    <dependencies>
        <dependency>
            <groupId>ru.practicum</groupId>
            <artifactId>stats-dto</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <scope>provided</scope>
        </dependency>

        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-web</artifactId>
        </dependency>

        <!-- Spring Cloud Commons (DiscoveryClient) -->
        <dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-commons</artifactId>
        </dependency>
    </dependencies>

</project>
EOF

# ==============================
# 17. Обновление StatsClient для работы с DiscoveryClient
# ==============================
cat > stats-service/stats-client/src/main/java/ru/practicum/client/StatsClient.java << 'EOF'
package ru.practicum.client;

import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.DateTimeFormatConstants;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.StatsRequestDto;
import ru.practicum.dto.StatsResponseDto;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Component
public class StatsClient {

    private static final String STATS_SERVICE_ID = "stats-service";
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(DateTimeFormatConstants.DATE_TIME_PATTERN);

    private final DiscoveryClient discoveryClient;
    private final RestTemplate restTemplate;

    public StatsClient(DiscoveryClient discoveryClient, RestTemplate restTemplate) {
        this.discoveryClient = discoveryClient;
        this.restTemplate = restTemplate;
    }

    public void hit(EndpointHitDto endpointHitDto) {
        URI uri = makeUri("/hit");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EndpointHitDto> request = new HttpEntity<>(endpointHitDto, headers);
        restTemplate.exchange(uri, HttpMethod.POST, request, Void.class);
    }

    public List<StatsResponseDto> getStats(StatsRequestDto requestDto) {
        String startEncoded = URLEncoder.encode(requestDto.getStart().format(FORMATTER), StandardCharsets.UTF_8);
        String endEncoded = URLEncoder.encode(requestDto.getEnd().format(FORMATTER), StandardCharsets.UTF_8);

        URI uri = makeUri("/stats");
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(uri)
                .queryParam("start", startEncoded)
                .queryParam("end", endEncoded)
                .queryParam("unique", requestDto.getUnique());

        if (requestDto.getUris() != null && !requestDto.getUris().isEmpty()) {
            builder.queryParam("uris", String.join(",", requestDto.getUris()));
        }

        ResponseEntity<List<StatsResponseDto>> response = restTemplate.exchange(
                builder.toUriString(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<StatsResponseDto>>() {}
        );
        return response.getBody();
    }

    private URI makeUri(String path) {
        List<ServiceInstance> instances = discoveryClient.getInstances(STATS_SERVICE_ID);
        if (instances.isEmpty()) {
            throw new RuntimeException("No instances of " + STATS_SERVICE_ID + " found in Discovery");
        }
        ServiceInstance instance = instances.get(0);
        return URI.create("http://" + instance.getHost() + ":" + instance.getPort() + path);
    }
}
EOF

# ==============================
# 18. Обновление конфигурации RestTemplateConfig (переносим в stats-client, если нужно)
# уже существует

# ==============================
# 19. Удаляем старые application.properties (если остались)
# ==============================
rm -f stats-service/stats-server/src/main/resources/application.properties.old 2>/dev/null
rm -f core/main-service/src/main/resources/application.properties.old 2>/dev/null

echo "=== СТРУКТУРА ОБНОВЛЕНА. ЗАПУСТИТЕ СБОРКУ В INTELLIJ IDEA ==="