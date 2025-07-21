# GitHub Copilot Prompt: PHP Full-Stack to Java Spring Boot + React Migration

## Complete Migration Prompt for GitHub Copilot

```
Perform a complete full-stack migration from PHP to Java 21 Spring Boot 3.3.x backend + React frontend. Analyze existing PHP codebase for both backend and frontend components.

**Migration Specifications:**
- Source: PHP (Backend + Frontend mixed)
- Target Backend: Java 21 + Spring Boot 3.3.x + Maven
- Target Frontend: React 18+ + TypeScript + Vite
- Database: PostgreSQL on AWS Aurora with Spring Data JPA/Hibernate
- Architecture: RESTful API backend + SPA React frontend
- Security: Spring Security 6 with JWT + React Auth

**Project Structure to Create:**
```
/migrated-app/
├── backend/
│   ├── src/main/java/com/company/app/
│   │   ├── config/
│   │   ├── controller/
│   │   ├── service/
│   │   ├── repository/
│   │   ├── entity/
│   │   ├── dto/
│   │   ├── security/
│   │   └── Application.java
│   ├── src/main/resources/
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── components/
│   │   ├── pages/
│   │   ├── hooks/
│   │   ├── services/
│   │   ├── types/
│   │   ├── utils/
│   │   └── App.tsx
│   ├── package.json
│   └── vite.config.ts
```

**Migration Tasks:**

1. **Backend Migration (PHP → Java Spring Boot)**
   - Convert PHP classes to @Service/@Controller Java classes
   - Transform PHP functions to Spring Boot methods
   - Map PHP arrays/objects to Java DTOs/Records
   - Convert PHP database queries to JPA repositories
   - Migrate PHP sessions to JWT authentication
   - Transform PHP routing to Spring MVC @RequestMapping

2. **Frontend Migration (PHP Views → React Components)**
   - Extract PHP HTML templates to React JSX components
   - Convert PHP echo/print statements to React state/props
   - Transform PHP forms to controlled React components
   - Migrate PHP session handling to React context/state
   - Convert PHP AJAX calls to React API service calls
   - Replace PHP validation with React form libraries

3. **Database Layer Migration**
   - Convert PHP mysqli/PDO queries to @Repository interfaces
   - Transform SQL Server queries to PostgreSQL-compatible syntax
   - Create @Entity classes from database schema
   - Map PHP result arrays to Java DTOs
   - Handle transactions with @Transactional
   - Optimize for AWS Aurora PostgreSQL

**Code Conversion Examples:**

PHP Controller → Java Spring Boot:
```php
// UserController.php
class UserController {
    public function getUser($id) {
        $stmt = $pdo->prepare("SELECT * FROM users WHERE id = ?");
        $stmt->execute([$id]);
        $user = $stmt->fetch(PDO::FETCH_ASSOC);
        return json_encode($user);
    }
}
```

Should become:
```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        UserDto user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }
}
```

PHP View → React Component:
```php
<!-- user.php -->
<div class="user-profile">
    <h1><?php echo $user['name']; ?></h1>
    <p><?php echo $user['email']; ?></p>
    <form method="POST" action="/update-user">
        <input type="text" name="name" value="<?php echo $user['name']; ?>">
        <button type="submit">Update</button>
    </form>
</div>
```

Should become:
```tsx
// UserProfile.tsx
interface User {
  id: number;
  name: string;
  email: string;
}

export const UserProfile: React.FC<{ user: User }> = ({ user }) => {
  const [name, setName] = useState(user.name);
  
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    await userService.updateUser(user.id, { name });
  };

  return (
    <div className="user-profile">
      <h1>{user.name}</h1>
      <p>{user.email}</p>
      <form onSubmit={handleSubmit}>
        <input 
          type="text" 
          value={name} 
          onChange={(e) => setName(e.target.value)} 
        />
        <button type="submit">Update</button>
      </form>
    </div>
  );
};
```

**PHP-Specific Migration Guidelines:**
- Convert PHP superglobals ($_GET, $_POST, $_SESSION) to proper request handling
- Map PHP include/require to ES6 imports and Spring components
- Transform PHP composer dependencies to Maven + npm dependencies
- Convert PHP namespaces to Java packages and TypeScript modules
- Migrate PHP configuration files to application.yml + .env files
- Handle PHP error handling with Java exceptions + React error boundaries

**Frontend Architecture Guidelines:**
- Use TypeScript for type safety
- Implement React Router for client-side routing
- Use React Query/SWR for API state management
- Apply modern CSS (CSS Modules, Styled Components, or Tailwind)
- Implement proper form validation (React Hook Form + Zod)
- Add error boundaries and loading states
- Use Vite for development and building

**For each PHP file found:**
1. Identify if it's backend logic, frontend view, or mixed
2. Separate concerns: business logic → Java, presentation → React
3. Convert PHP classes to appropriate Java services/controllers
4. Extract HTML/templates to React components
5. Transform PHP forms to controlled React components
6. Migrate database operations to Spring Data JPA
7. Create TypeScript interfaces for data models

Start migration by analyzing [SPECIFIC_PHP_FILE] and convert it to complete Java Spring Boot + React implementation.
```

## Migration Process

### Phase 1: Analysis & Setup
1. **Analyze PHP Codebase Structure**
   - Identify MVC patterns vs mixed PHP/HTML files
   - Map database connections and queries
   - Document API endpoints and form submissions
   - Catalog shared functions and classes

2. **Initialize Projects**
   ```bash
   # Backend
   spring init --dependencies=web,data-jpa,security,validation,postgresql \
              --java-version=21 --boot-version=3.3.0 backend
   
   # Frontend
   npm create vite@latest frontend -- --template react-ts
   ```

### Phase 2: Backend Migration
3. **Database Layer**
   - Convert PHP database connections to Spring DataSource
   - Transform mysqli/PDO queries to JPA repositories
   - Create entities from database schema
   - Set up PostgreSQL connection for AWS Aurora

4. **Business Logic**
   - Convert PHP classes to @Service components
   - Map PHP functions to Java methods
   - Implement proper exception handling
   - Add validation and logging

### Phase 3: API Layer
5. **REST Controllers**
   - Extract API endpoints from PHP files
   - Create @RestController classes
   - Implement proper HTTP methods and status codes
   - Add request/response DTOs

6. **Security Implementation**
   - Convert PHP session handling to JWT
   - Implement Spring Security configuration
   - Add authentication endpoints
   - Handle CORS for React frontend

### Phase 4: Frontend Migration
7. **Component Structure**
   - Extract PHP HTML to React components
   - Create component hierarchy
   - Implement proper props and state management
   - Add TypeScript interfaces

8. **API Integration**
   - Create API service layer
   - Implement HTTP client (Axios/Fetch)
   - Add error handling and loading states
   - Implement authentication flow

### Phase 5: Data Flow
9. **State Management**
   - Convert PHP session variables to React state
   - Implement context for global state
   - Add form state management
   - Handle authentication state

## Dependencies

### Backend (pom.xml)
```xml
<properties>
    <java.version>21</java.version>
    <spring-boot.version>3.3.0</spring-boot.version>
</properties>

<dependencies>
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
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.12.3</version>
    </dependency>
</dependencies>
```

### Frontend (package.json)
```json
{
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.8.0",
    "@tanstack/react-query": "^4.24.0",
    "axios": "^1.3.0",
    "react-hook-form": "^7.43.0",
    "zod": "^3.20.0"
  },
  "devDependencies": {
    "@types/react": "^18.0.0",
    "@types/react-dom": "^18.0.0",
    "@vitejs/plugin-react": "^3.1.0",
    "typescript": "^4.9.0",
    "vite": "^4.1.0"
  }
}
```

## PHP to Java/React Conversion Patterns

### PHP Class → Java Service
```php
class UserService {
    private $db;
    
    public function createUser($data) {
        $stmt = $this->db->prepare("INSERT INTO users (name, email) VALUES (?, ?)");
        return $stmt->execute([$data['name'], $data['email']]);
    }
}
```

→

```java
@Service
@Transactional
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    public UserDto createUser(CreateUserRequest request) {
        User user = new User(request.name(), request.email());
        User saved = userRepository.save(user);
        return new UserDto(saved.getId(), saved.getName(), saved.getEmail());
    }
}
```

### PHP Form → React Component
```php
<form method="POST" action="/create-user">
    <input type="text" name="name" required>
    <input type="email" name="email" required>
    <button type="submit">Create User</button>
</form>
```

→

```tsx
const CreateUserForm: React.FC = () => {
  const { register, handleSubmit } = useForm<CreateUserData>();
  
  const onSubmit = async (data: CreateUserData) => {
    await userService.createUser(data);
  };
  
  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      <input {...register('name', { required: true })} />
      <input {...register('email', { required: true })} type="email" />
      <button type="submit">Create User</button>
    </form>
  );
};
```

## Migration Quality Checklist

- [ ] All PHP classes converted to Java services/controllers
- [ ] PHP views extracted to React components
- [ ] Database queries migrated to JPA with PostgreSQL
- [ ] Authentication converted from sessions to JWT
- [ ] Forms converted to controlled React components
- [ ] API endpoints properly defined and documented
- [ ] TypeScript interfaces for all data models
- [ ] Error handling implemented on both frontend and backend
- [ ] Unit tests for Java services and React components
- [ ] Integration tests for API endpoints