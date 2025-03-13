# Transistor

A public transit and walking route planner application for Maastricht.

---

## About This Project

This project was originally developed as part of a **university group assignment** and has been **migrated from my university GitLab account**.  
Due to this migration, commit history from my fellow contributors was lost.  

I want to explicitly acknowledge and credit my **team members**, without whom this project would not have been possible.

### Contributors

| Name                   | Email                                       |
|------------------------|---------------------------------------------|
| **Magdy Fares**        | m.fares@student.maastrichtuniversity.nl     |
| **Vjosa Shabanaj**     | v.shabanaj@student.maastrichtuniversity.nl  |
| **Mika Hagenbeek**     | m.hagenbeek@student.maastrichtuniversity.nl |
| **Emily Kate Proctor** | e.proctor@student.maastrichtuniversity.nl   |
| **Cristian Nitu**      | cristian.nitu@student.maastrichtuniversity.nl |
| **Lakshana Sivaprakash** | l.sivaprakash@student.maastrichtuniversity.nl |

I **do not** and **never have** claimed to have completed this project alone.  
Credit is **shared equally** with my respective group members.

---

## Features

- ✅ **Multimodal Route Planning**: Combines walking and public transit routes for optimal travel.
- ✅ **Interactive Map Interface**: Visualizes routes on an interactive map of Maastricht.
- ✅ **Postal Code Search**: Enter origin and destination postal codes to find routes.
- ✅ **Real-time Journey Information**: Displays estimated travel times, distances, and required transfers.
- ✅ **Fallback Mechanisms**: Provides walking routes when transit is unavailable.
- ✅ **Database Integration**: Uses MySQL for storing and retrieving transit data.

---

## Getting Started

### Prerequisites
- Java 22
- MySQL Server
- Maven

### Clone the Repository
```sh
git clone https://github.com/kabelah/Transistor.git
cd Transistor
```

### Database Setup
1. Create a MySQL database named `gtfs`
2. Create a user with username `gtfs` and password `gtfs`
3. Grant this user all privileges on the `gtfs` database
4. The application will automatically connect to this database

### Run the Application
```sh
mvn clean javafx:run
```

### Using the Application
1. Enter postal codes in the format `6211AB` in both fields
2. Click "Calculate" to find the route
3. View the route on the map and journey details below

