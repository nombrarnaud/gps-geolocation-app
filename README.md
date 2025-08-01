# GPS Geolocation Application

This project is a GPS geolocation application with two types of users: business users and simple users. It allows users to register, login, and track vehicles with geolocation and additional metrics such as speed, altitude, and weight. Users can manage their account, add vehicles, and view the history of vehicle movements with pagination.

## Technology Stack

- Frontend: Vue.js (Vite)
- Backend: Spring Boot (Maven)
- Database: PostgreSQL
- Authentication: JWT (JSON Web Tokens)
- Mapping: Leaflet with OpenStreetMap tiles
- GPS Data: Simulated free GPS tracking API integration

## Features

- User registration for business and simple users with appropriate fields
- User login with email and password, with "remember me" functionality
- Home page displaying a map with all tracked vehicles and their metrics
- Vehicle detail page showing current metrics and paginated movement history (10 records per page)
- User account management with profile editing and password change
- Backend services for authentication, vehicle management, GPS data simulation, and history tracking
- Secure REST API with JWT authentication and session management

## Setup Instructions

### Backend

1. Ensure you have Java 17+ and Maven installed.
2. Configure PostgreSQL database and update `src/main/resources/application.properties` with your database credentials.
3. Run the backend server:

```bash
cd gps-tracking-app/backend
mvn spring-boot:run
```

### Frontend

1. Ensure you have Node.js and npm installed.
2. Install dependencies and run the frontend development server:

```bash
cd gps-tracking-app/frontend
npm install
npm run dev
```

3. Access the frontend at `http://localhost:8000`

## Notes

- The backend currently simulates GPS data for vehicles. Integration with real GPS tracking APIs can be added in the `GPSIntegrationService`.
- The PostgreSQL database must be running and accessible for the backend to start successfully.
- The frontend uses Vue Router for navigation and Axios for API calls.
- All UI components are styled with Tailwind CSS and use Google Fonts for typography.
- No external icon libraries or image services are used; placeholders are used for images.

## Future Improvements

- Add real GPS tracking device integration (e.g., Notecard Cellular)
- Enhance UI with more detailed vehicle analytics and charts
- Add notifications and alerts for vehicle status
- Implement role-based access control for different user types
- Add unit and integration tests for backend and frontend

## License

This project is open source and free to use.
