# 📚 Library Management System

This is a simple **Object-Oriented Programming (OOP) project** built with **Kotlin** that demonstrates a basic library management system. It connects to a **SQLite database** and allows you to manage users, books, and borrow/return operations.

---

## 🚀 Features

- ✅ User registration & login (with password hashing)
- 📖 Add, list, and manage books
- 🔄 Borrow & return books (updates stock & availability)
- 🗂️ See all borrowed books
- 🛡️ Input validation & error handling

---

## 🛠️ Technologies Used

- **Kotlin**
- **SQLite (JDBC)**
- **Gradle**
- **BCrypt** for password hashing

---

## ⚙️ Setup Instructions

1️⃣ **Clone the repository:**

```bash
git clone https://github.com/your-username/Library_Management_System_Gradle.git
cd Library_Management_System_Gradle
```
2️⃣ Run the project:

Open the project in IntelliJ IDEA (recommended) or any Kotlin-supported IDE.

Build & run using Gradle tasks or your IDE's run option.

3️⃣ Database:

The project uses library_management.db (SQLite).

Tables should be auto-created by your scripts, or you can run the provided SQL manually.

4️⃣ Dependencies:

Gradle handles dependencies. The key ones:
```
dependencies {
    implementation("org.xerial:sqlite-jdbc:3.34.0")
    implementation("at.favre.lib:bcrypt:0.9.0")
}
```

## 🔑 Example Usage

- Register a user:
  
       Username, password, email, and balance are required.

- Login:
  
      Check your credentials securely.

- Borrow/return books:

      Books are updated in stock automatically.

## 📄 License
This project is open-source for learning purposes.

