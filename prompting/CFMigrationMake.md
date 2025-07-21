# GitHub Copilot Prompt: ColdFusion Backend to Java Spring Boot Migration

## Complete Migration Prompt for GitHub Copilot

```
Perform a complete backend migration from Adobe ColdFusion to Java 21 with Spring Boot 3.3.x. Use the analysis from the previous migration-analysis folder as reference.

**Migration Specifications:**
- Source: Adobe ColdFusion (CFML/CFCs)
- Target: Java 21 + Spring Boot 3.3.x + Maven 3.9.1
- Database: PostgreSQL on AWS Aurora with Spring Data JPA/Hibernate
- Architecture: RESTful microservices with layered architecture
- Security: Spring Security 6 with JWT authentication

**Project Structure to Create:**
```
/backend-java/
├── src/main/java/com/company/app/
│   ├── config/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── entity/
│   ├── dto/
│   ├── security/
│   ├── util/
│   └── Application.java
├── src/main/resources/
│   ├── application.yml
│   ├── db/migration/
│   └── static/
├── src/test/java/
└── pom.xml
```

**Migration Tasks:**

1. **Project Setup & Dependencies**
   - Generate Spring Boot 3.3.x project structure with Maven
   - Configure dependencies for: Web, JPA, Security, Validation, PostgreSQL
   - Set up application.yml with AWS Aurora PostgreSQL configuration
   - Create main Application class with proper annotations

2. **ColdFusion Components (CFCs) → Java Services**
   - Convert each .cfc file to corresponding Java service class
   - Map cffunction to @Service annotated methods
   - Transform cfproperty to Java class fields with proper annotations
   - Convert access="remote" methods to @RestController endpoints
   - Handle ColdFusion returntype to Java generics and return types
   - Map cferror and try/catch to proper Java exception handling

3. **Database Layer Migration**
   - Convert cfquery tags to @Repository interfaces extending JpaRepository
   - Transform SQL Server queries to PostgreSQL-compatible queries with @Query annotations
   - Create @Entity classes from database tables using PostgreSQL data types
   - Map ColdFusion query results to Java DTOs/Entities
   - Convert stored procedures to PostgreSQL functions with @Procedure or native queries
   - Handle database transactions with @Transactional
   - Implement connection pooling optimized for AWS Aurora

4. **Data Transfer Objects (DTOs)**
   - Create record classes (Java 21) for request/response objects
   - Map ColdFusion structs to Java DTOs with validation annotations
   - Use @JsonProperty for JSON serialization mapping
   - Implement proper validation with @Valid, @NotNull, @Size annotations

5. **Authentication & Security**
   - Convert ColdFusion session management to JWT tokens
   - Map cflogin/cfloginuser to Spring Security authentication
   - Transform ColdFusion roles to Spring Security authorities
   - Create SecurityConfig class with proper endpoint security
   - Implement UserDetailsService for user authentication

6. **Configuration Management**
   - Convert Application.cfc settings to application.yml properties
   - Map ColdFusion datasources to Spring DataSource configuration for PostgreSQL
   - Transform custom settings to @ConfigurationProperties classes
   - Handle environment-specific configurations for AWS Aurora

7. **Utility Classes & Custom Functions**
   - Convert ColdFusion UDFs to static utility methods using Java 21 features
   - Map custom tags to Spring components or utility classes
   - Transform ColdFusion date/string functions to Java equivalents
   - Handle file operations and email functionality

**Code Conversion Examples:**

ColdFusion Component → Java Service:
```coldfusion
// UserService.cfc
component {
    remote function getUser(required numeric userId) returntype="struct" {
        var user = queryExecute("SELECT * FROM users WHERE id = ?", [userId]);
        return {"id": user.id, "name": user.name};
    }
}
```

Should become:
```java
@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    public UserDto getUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        return new UserDto(user.getId(), user.getName());
    }
}
```

**PostgreSQL Migration Guidelines:**
- Convert SQL Server specific syntax to PostgreSQL equivalents
- Map SQL Server data types to PostgreSQL types (int → INTEGER, varchar → TEXT/VARCHAR, etc.)
- Transform SQL Server functions to PostgreSQL functions (GETDATE() → NOW(), etc.)
- Handle sequence generation for auto-increment fields
- Optimize queries for PostgreSQL performance characteristics
- Configure AWS Aurora specific connection settings

**Migration Guidelines:**
- Use Java 21 features: virtual threads, pattern matching, sealed classes, records where appropriate
- Implement proper exception handling with custom exceptions
- Add comprehensive logging with SLF4J
- Use Spring Boot 3.3.x auto-configuration wherever possible
- Follow REST API conventions for endpoint naming
- Implement proper validation and error responses
- Add unit tests for each service method
- Use Spring profiles for environment-specific configuration
- Configure for AWS Aurora PostgreSQL cluster endpoints

**For each ColdFusion file found:**
1. Identify the component type (service, utility, etc.)
2. Create corresponding Java class with appropriate annotations
3. Convert all methods with proper parameter mapping
4. Handle database operations with Spring Data JPA and PostgreSQL
5. Add proper exception handling and logging
6. Create corresponding unit tests

Start migration by analyzing [SPECIFIC_CFC_FILE] and convert it to complete Java Spring Boot implementation.
```

## Step-by-Step Migration Process

### Phase 1: Project Initialization
1. **Create Spring Boot Project**
   ```bash
   spring init --dependencies=web,data-jpa,security,validation,postgresql \
              --java-version=21 \
              --packaging=jar \
              --group-id=com.company \
              --artifact-id=backend-app \
              --boot-version=3.3.0 \
              backend-java
   ```

2. **Setup Database Configuration**
   - Configure application.yml with AWS Aurora PostgreSQL connection
   - Add Flyway for database migrations
   - Set up HikariCP connection pooling optimized for Aurora
   - Configure read/write cluster endpoints

### Phase 2: Core Infrastructure
3. **Security Configuration**
   - Create JWT token service using Java 21 features
   - Implement UserDetailsService
   - Configure Spring Security 6 filter chain
   - Set up CORS configuration

4. **Exception Handling Setup**
   - Create global exception handler (@ControllerAdvice)
   - Define custom exception classes using sealed classes
   - Implement proper error response DTOs as records

### Phase 3: Database Migration & Data Layer
5. **Database Schema Migration**
   - Convert SQL Server schema to PostgreSQL
   - Create Flyway migration scripts
   - Handle data type conversions
   - Migrate stored procedures to PostgreSQL functions

6. **Entity Creation**
   - Convert database tables to JPA entities with PostgreSQL types
   - Define relationships (@OneToMany, @ManyToOne, etc.)
   - Add validation annotations
   - Use PostgreSQL-specific features where beneficial

7. **Repository Layer**
   - Create repository interfaces extending JpaRepository
   - Add custom query methods with PostgreSQL syntax
   - Implement complex queries with @Query

### Phase 4: Business Logic Migration
8. **Service Layer Conversion**
   - Convert each CFC to @Service class using Java 21 patterns
   - Map business logic methods
   - Implement transaction management
   - Add proper logging and validation

9. **Controller Layer Creation**
   - Create REST controllers for remote CFC methods
   - Define proper HTTP methods and paths
   - Add request/response validation using records
   - Implement proper status codes

### Phase 5: Integration and Testing
10. **Unit Testing**
    - Create test classes for each service
    - Mock dependencies with @MockBean
    - Test edge cases and error scenarios

11. **Integration Testing**
    - Create integration tests for controllers
    - Test PostgreSQL database operations
    - Validate security implementation

## Maven Dependencies Template

```xml
<properties>
    <java.version>21</java.version>
    <spring-boot.version>3.3.0</spring-boot.version>
</properties>

<dependencies>
    <!-- Spring Boot Starters -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    
    <!-- Database Migration -->
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-database-postgresql</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.12.3</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.testcontainers</groupId>
        <artifactId>postgresql</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## PostgreSQL Configuration Template

```yaml
spring:
  datasource:
    url: jdbc:postgresql://aurora-cluster.cluster-xxxxx.us-east-1.rds.amazonaws.com:5432/dbname
    username: ${DB_USERNAME:admin}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      idle-timeout: 300000
      connection-timeout: 20000
      validation-timeout: 5000
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
        show_sql: false
    database-platform: org.hibernate.dialect.PostgreSQLDialect
  flyway:
    enabled: true
    locations: classpath:db/migration
    baseline-on-migrate: true
```

## SQL Server to PostgreSQL Conversion Guide

### Data Type Mappings:
- `INT` → `INTEGER`
- `BIGINT` → `BIGINT`
- `VARCHAR(n)` → `VARCHAR(n)`
- `NVARCHAR(n)` → `TEXT` or `VARCHAR(n)`
- `DATETIME` → `TIMESTAMP`
- `BIT` → `BOOLEAN`
- `MONEY` → `DECIMAL(19,4)`
- `UNIQUEIDENTIFIER` → `UUID`

### Function Mappings:
- `GETDATE()` → `NOW()`
- `LEN()` → `LENGTH()`
- `ISNULL()` → `COALESCE()`
- `CHARINDEX()` → `POSITION()`
- `SUBSTRING()` → `SUBSTR()`

## Migration Quality Checklist

- [ ] All CFC components converted to Spring services with Java 21 features
- [ ] Database operations using JPA/Hibernate with PostgreSQL
- [ ] SQL Server queries converted to PostgreSQL syntax
- [ ] Proper exception handling with sealed classes
- [ ] Security configuration with Spring Security 6
- [ ] Unit tests with >80% coverage using Testcontainers
- [ ] Integration tests for critical paths
- [ ] API documentation (OpenAPI/Swagger)
- [ ] AWS Aurora connection optimization
- [ ] Performance optimization for PostgreSQL
- [ ] Logging and monitoring configured
- [ ] Environment-specific configurations for AWS