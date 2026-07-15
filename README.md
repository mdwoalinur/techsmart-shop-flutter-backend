# TechSmart Shop Flutter Backend

Spring Boot REST API backend for the **TechSmart Shop Flutter E-Commerce Application**.

<p>
  <img src="https://img.shields.io/badge/Java-17-orange?style=flat-square" alt="Java 17">
  <img src="https://img.shields.io/badge/Spring%20Boot-Backend-6DB33F?style=flat-square&logo=springboot&logoColor=white" alt="Spring Boot">
  <img src="https://img.shields.io/badge/MySQL-Database-4479A1?style=flat-square&logo=mysql&logoColor=white" alt="MySQL">
  <img src="https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?style=flat-square&logo=springsecurity&logoColor=white" alt="Spring Security">
  <img src="https://img.shields.io/badge/Maven-Build-C71A36?style=flat-square&logo=apachemaven&logoColor=white" alt="Maven">
</p>

## Overview

TechSmart Shop Flutter Backend provides secure REST APIs for customer authentication, product browsing, cart and checkout workflows, mobile wallet payments, cash on delivery, order processing, fulfillment, delivery tracking, notifications, returns, cancellations, and customer account management.

This backend is designed to work with the TechSmart Shop Flutter frontend.

## Related Repository

- Flutter Frontend: https://github.com/mdwoalinur/techsmart-shop-flutter-frontend

## Main Features

- Customer authentication and secured API access
- Product and product variation management
- Product browsing and search
- Shopping cart and checkout support
- Customer profile management
- Order creation and order history
- Order details and status timeline
- Order cancellation workflow
- Return request workflow
- Mobile wallet payment simulation
- Cash on delivery support
- Payment verification and status management
- Fulfillment and operational order processing
- Stock deduction with duplicate-protection logic
- Delivery tracking
- COD collection and reconciliation
- Customer notifications
- Notification preferences
- Email communication foundation
- Customer data isolation
- File upload and secured download support

## Technology Stack

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Hibernate
- MySQL
- Maven
- JWT Authentication
- REST API
- AOP Auditing
- SQL-based schema migration scripts

## Requirements

- Java 17
- MySQL
- Maven 3.9+
- A configured database

## Required Environment Variables

```text
TRADEMASTER_DB_URL=jdbc:mysql://localhost:3306/trademaster
TRADEMASTER_DB_USERNAME=root
TRADEMASTER_DB_PASSWORD=<database password>
TRADEMASTER_JWT_SECRET=<long random HS512 secret containing at least 64 characters>
