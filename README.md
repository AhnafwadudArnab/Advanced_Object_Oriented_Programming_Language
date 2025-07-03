                                                MissingDiary
                                         (Missing Person Alert System)
 ❖❖ Step-by-Step Features Breakdown:❖❖
     
 This project aims to create a system for broadcasting missing person reports and collecting community
  responses. The system will use JavaFX for the admin panel and require additional technologies for full
  implementation.
  ❖ 1. User Roles & Authentication
• Admin (Law Enforcement / NGO Officials)
• General Users (Public/Volunteers, Families)
• Login & Registration
o JavaFX UI for Admins
o Web/App for General Users
• Secure Authentication (MySQL + JavaFX, if possible)
• Role-Based Access Control (Only verified officials can post alerts)

  ❖ 2. Reporting a Missing Person
Admin Interface (JavaFX):
• Add a Missing Person (Name, Age, Gender, Last Seen Location, Photo, Description)
• Upload Images (Missing person photo)
• Geo-tagging Support (Mark last known location on a map, if possible)
Public Reporting:
• User-submitted Reports (With optional photo and location)
• Verification System (Admins review and approve public reports)

  ❖ 3. Alert Notification System
Real-time Alerts:
• Location-based Alerts (Sent only to people near the last seen location)
• Push Notifications (Firebase Cloud Messaging)
Alert Dashboard (JavaFX):
• View & Manage Active Alerts
• Update Case Status (Found / Still Missing)

  ❖ 4. Community Involvement & Sightings
Report Sightings (Public App/Web):
• Submit location details if someone is seen
• Attach Images/Videos
Verify Information (Admin Panel - JavaFX):
• View Reports & Validate Sightings
• Cross-check with Law Enforcement

  ❖ 5. AI & Image Recognition (Optional Advanced Feature)
Facial Recognition System:
• Compare uploaded images with existing database of missing persons
• Use OpenCV + TensorFlow to identify people from CCTV footage or images

  ❖ 6. Data Management & Analytics
Admin Panel Features (JavaFX):
• Dashboard Overview (Total cases, Active alerts, Found cases)
• Generate Reports (Missing person statistics, recovery rate)
• Search & Filter Cases (By location, age, or status)

  ❖ 7. Security & Privacy
• Data Encryption (Protect user information)
• Role-Based Permissions (Only verified officials can post alerts)
• User Verification (Phone/email verification for public reports) [Optional]

  ❖ Technologies Used
• Frontend: JavaFX (Admin Panel)
• Backend: Spring Boot
• Database: MySQL
• AI/ML: (Optional - For image recognition)

  ❖ Development Steps
1. Set up JavaFX Admin Panel (Login, Dashboard, Report Management)
2. Backend API for Missing Person Data (MySQL)
3. Implement Geo-location Alerts Notification System
4. Build Web & Mobile Apps for Public Reports
5. Add Community Sightings Verification System
6. Integrate AI-based Image Recognition (Optional Advanced Feature)
7. Testing & Deployment
