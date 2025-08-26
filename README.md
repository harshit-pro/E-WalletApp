# E-Wallet Application

## 🌟 Overview

Welcome to the E-Wallet Application, a secure, scalable, and user-friendly digital payment solution built on a robust microservices architecture. This application empowers users to manage their funds, conduct transactions, and receive real-time notifications, providing a seamless and efficient digital wallet experience.

### Key Features:

  * **User Management**: Secure user registration and authentication.
  * **Wallet Services**: Create and manage digital wallets with ease.
  * **Peer-to-Peer Transactions**: Instantly send and receive money.
  * **Transaction History**: Keep track of all your payments and receipts.
  * **Real-time Notifications**: Stay updated with instant transaction alerts.

-----

## 🏛️ Architecture

This application is designed using a microservices architecture, promoting scalability, flexibility, and maintainability. Each service is an independent component with a specific business capability, communicating with others through a combination of REST APIs and an event-driven approach using Apache Kafka.

-----
<img width="1102" height="633" alt="image" src="https://github.com/user-attachments/assets/15ab4a1c-5bb8-4543-8eae-3b5a09cad8ad" />


## 🚀 Microservices

The application is composed of the following microservices:

### Eureka Service Registry

  * **Description**: The central discovery server that allows other microservices to register themselves and discover others. It acts as the "phone book" for our services, enabling dynamic communication and load balancing.
  * **Technologies**: Spring Cloud Netflix Eureka.

### User Service

  * **Description**: Manages all user-related functionalities, including user registration, authentication, and profile management.
  * **Technologies**: Spring Boot, Spring Data JPA, MySQL.

### Wallet Service

  * **Description**: Handles the core wallet functionalities, such as wallet creation upon user registration, balance inquiries, and top-ups.
  * **Technologies**: Spring Boot, Spring Data JPA, MySQL, Apache Kafka.

### Transaction Service

  * **Description**: Orchestrates financial transactions between users. It communicates with the Wallet Service to update balances and pushes transaction events to Kafka for notification purposes.
  * **Technologies**: Spring Boot, Spring Data JPA, Spring Security, Feign Client, Apache Kafka, MySQL.

### Notification Service

  * **Description**: Consumes events from Kafka topics to send real-time notifications to users. It currently supports email notifications for transaction status updates and OTP verification.
  * **Technologies**: Spring Boot, Spring for Apache Kafka, JavaMail Sender.

-----

## 🛠️ Technologies Used

This project is built with a modern technology stack to ensure performance and reliability.

  * **Backend**: Java, Spring Boot
  * **Database**: MySQL
  * **Messaging**: Apache Kafka
  * **Service Discovery**: Spring Cloud Netflix Eureka
  * **API Communication**: REST APIs, Feign Client
  * **Dependencies**: Maven

-----

## ⚙️ Setup and Installation

To get the E-Wallet Application up and running on your local machine, follow these steps:

1.  **Prerequisites**:

      * Java JDK 21 or higher
      * Apache Maven
      * MySQL Server
      * Apache Kafka

2.  **Clone the repository**:

    ```bash
    git clone https://github.com/your-username/e-wallet-application.git
    cd e-wallet-application
    ```

3.  **Database Configuration**:

      * Create a MySQL database for each service that requires one (User, Wallet, Transaction).
      * Update the `application.properties` file in each service's `src/main/resources` directory with your database credentials.

4.  **Start the Services**:

      * Start your MySQL server and Apache Kafka.
      * Launch the `service-registry` first.
      * Start the other microservices in any order.

    You can run each service using the following Maven command:

    ```bash
    mvn spring-boot:run
    ```

-----

## 🤝 Contributing

We welcome contributions to the E-Wallet Application\! If you'd like to contribute, please fork the repository and create a pull request. For major changes, please open an issue first to discuss what you would like to change.

-----

## 📄 License

This project is licensed under the MIT License - see the LICENSE.md file for details.
