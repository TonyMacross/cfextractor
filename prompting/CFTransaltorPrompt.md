**Act as a senior software architect specializing in legacy system modernization.**

I need to analyze the Adobe ColdFusion application located in the `/path/to/your/coldfusion/app` directory to plan its migration to a modern technology stack.

**The target architecture is:**
* **Backend:** Target: Java 21 + Spring Boot 3.3.x + Maven 3.9.1
* **Frontend:** React 20.x with TypeScript.
* **Database:** Microsoft SQL Server (The current database is also SQL Server, so focus on analyzing interactions, not migrating the data schema itself unless you find business logic in stored procedures).

**Your primary task is to analyze the entire ColdFusion codebase and categorize its components to facilitate the migration. Please perform the following steps:**

**1. Overall Structure Analysis:**
   - Scan the entire directory structure of the ColdFusion application.
   - Identify and list the main folders and their apparent purpose (e.g., `cfcs/` for components, `includes/` for templates, `assets/` for static files, `api/` for endpoints).

**2. Backend Logic Extraction (Java/Spring Boot):**
   - Analyze all `.cfc` (ColdFusion Components) and `.cfm` files.
   - Identify all business logic, data access logic, and service layers. This includes:
     - Database queries (`<cfquery>`).
     - Business rules and calculations.
     - Interactions with external services (e.g., `<cfhttp>`).
     - Authentication and authorization logic.
     - Scheduled tasks (`<cfschedule>`).
   - For each piece of backend logic, suggest a corresponding Java/Spring Boot implementation pattern. For example:
     - **Data Access:** Map `<cfquery>` blocks to Java Persistence API (JPA) Entities and Spring Data JPA Repositories.
     - **Business Logic:** Map CFC methods to Spring `@Service` components.
     - **API Endpoints:** Map public-facing CFC methods (especially those with `access="remote"`) or specific `.cfm` files acting as APIs to Spring `@RestController` endpoints.
     - **Security:** Identify where `<cflogin>` or custom security logic is implemented and suggest using Spring Security.

**3. Frontend Component Identification (React):**
   - Analyze all `.cfm` files that generate HTML.
   - Identify sections of the code responsible for the user interface (UI) and user interaction (UX). This includes:
     - HTML forms (`<cfform>`, `<input>`).
     - Data display structures (`<cfoutput>`, `<table>`, `<cfloop>`).
     - Client-side scripts (inline JavaScript or `<script>` tags).
     - UI templates and reusable include files (`<cfinclude>`).
   - For each UI section, propose a corresponding React component structure. For example:
     - A `.cfm` page displaying a list of products could become a `ProductListPage` component containing a `ProductTable` component, which in turn renders multiple `ProductRow` components.
     - A form for user registration could become a `RegistrationForm` component with state management for its fields.
     - Identify where the frontend will need to call the new Java backend API to fetch or submit data.

**4. Database Interaction Analysis (SQL Server):**
   - Scan for all `<cfquery>` and `<cfstoredproc>` tags.
   - List all stored procedures that are being called from the ColdFusion code.
   - Analyze the SQL inside the `<cfquery>` tags. Identify any complex queries or business logic embedded directly in the SQL that might need to be moved into the Java service layer.
   - Flag any use of dynamic SQL (`<cfqueryparam>` is fine, but look for query strings built manually) as a potential security risk to be addressed during migration.

**5. Output Generation:**
   - Please generate a structured report in Markdown format.
   - Organize the report into the following main sections:
     - **Backend Migration Plan (Java/Spring Boot):** List of CFCs/CFMs to be converted, with their proposed Java class/component counterparts.
     - **Frontend Migration Plan (React):** List of CFM pages to be converted, with their proposed React component hierarchy.
     - **Database Interaction Summary:** List of all tables and stored procedures referenced in the code.
     - **Proposed Output Folder Structure:** A tree view of the recommended folder structure for the new backend and frontend projects.

**Here is the proposed final folder structure for your reference:**


/migrated-app
├── /backend-springboot
│   ├── src/main/java/com/yourcompany/app
│   │   ├── controller/      // Spring RestControllers
│   │   ├── service/         // Spring Services (Business Logic)
│   │   ├── repository/      // Spring Data JPA Repositories
│   │   ├── model/           // JPA Entities
│   │   ├── config/          // Spring Configuration (e.g., Security)
│   │   └── dto/             // Data Transfer Objects
│   └── src/main/resources
│       └── application.properties
├── /frontend-react
│   ├── public/
│   └── src/
│       ├── components/      // Reusable React components
│       ├── pages/           // Page-level components
│       ├── services/        // API call services (e.g., using Axios)
│       ├── hooks/           // Custom React hooks
│       ├── context/         // React Context for state management
│       └── App.tsx
└── README.md


**Please begin the analysis now.**
