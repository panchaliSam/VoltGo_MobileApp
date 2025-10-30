# ⚡ VoltGo Mobile App

**VoltGo** is an Android-based **EV Charging Station Booking Application** developed as part of the **Enterprise Application Development (SE4040)** module at the **Sri Lanka Institute of Information Technology (SLIIT)**.

This mobile app allows **Electric Vehicle (EV) owners** to easily locate nearby charging stations, reserve time slots, and manage their charging sessions. It connects with a centralized **C# Web API** hosted on IIS, where all business logic and data are processed using a **NoSQL database**.

---

## 🚗 Key Features

- **User Management** – EV owners can register, update, and deactivate their profiles (using NIC as the unique ID).  
- **Slot Reservation** – Create, modify, or cancel charging reservations within allowed time limits.  
- **QR Code Integration** – Generate and scan QR codes to confirm approved bookings.  
- **Dashboard Overview** – View pending and upcoming reservations, and track charging history.  
- **Map Integration** – Display nearby charging stations using the **Google Maps API**.  
- **Operator Mode** – Station operators can log in, scan QR codes, and finalize charging sessions.

---

## 🧠 Architecture

VoltGo follows a **client–server architecture**:

- **Mobile App:** Pure Android with **SQLite** for local storage (no frameworks).
- **Web API:** Handles all business logic and communication.
- **Database:** NoSQL (e.g., MongoDB) for server-side storage.

---

## 🛠️ Technologies Used

- **Language:** Kotlin / Java  
- **UI:** Android XML + Material Components  
- **Local DB:** SQLite  
- **Backend:** ASP.NET Core Web API (C#)  
- **Database:** NoSQL (MongoDB)  
- **Maps:** Google Maps SDK for Android  
- **QR Code:** ZXing Library  

---

## 📱 About the App

VoltGo Mobile provides a seamless experience for EV users and station operators to manage their charging schedules efficiently.  
By integrating **maps, QR code scanning, and RESTful API communication**, the app ensures a smooth and eco-friendly charging management process across Sri Lanka.

---

## 👩‍💻 Developer

**Developed by:** Panchali Samarasinghe  
**University:** Sri Lanka Institute of Information Technology (SLIIT)  
**Module:** SE4040 – Enterprise Application Development  
**Year:** 4 | **Semester:** 1 | **2025**

---

**© 2025 VoltGo | All Rights Reserved**
