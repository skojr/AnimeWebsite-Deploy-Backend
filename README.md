# *Anime Discovery & Social Hub Backend*
Created by: **Samuel Obeng**

## Features
- **Manage user authentication and authorization w/ Spring Security**
  - Users can sign up, log in, and securely manage their accounts
  - JWT tokens are issued for authenticated sessions
  - Protected API endpoints restrict access based on authentication status

- **User CRUD operations**
  - Create new user accounts and store them securely in the database
  - Retrieve user details for profile management
  - Update user information such as username and password
  - Delete user accounts along with associated data if needed
    
- **Post CRUD operations**
  - Create new community posts with titles and content
  - Retrieve posts for display on the frontend
  - Update existing posts by authorized users
  - Delete posts created by users

 - **Database access and management**
  - Manages all data persistence using a relational database (MySQL)
  - Efficient data retrieval with Spring Data JPA repositories
  - Enforces data integrity and relationships between users and posts

## Tech Stack
- Spring Boot
- Spring Security 
- Spring Data JPA
- MySQL 
- JWT Authentication

## License

    Copyright [2025] [Samuel Obeng]

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
