# Wallet-Watcher

WalletWatcher is an Android application designed to help users track their income and expenses conveniently.

## Introduction

WalletWatcher offers a user-friendly interface to monitor financial transactions effectively. It guides users through a seamless onboarding process, ensuring a smooth transition into managing their finances.

## Functionalities included

1. **Multi-Factor-Authentication**: Users will receive a one-time password (OTP) on their phone for verification. This adds an extra layer of security to the authentication process.

2. **Pie Chart**: Users are allowed to record expenses, categorized them with predefined type, and visualized spending patterns using pie chart.

3. **Session**: Users session is managed on every page of the application.

## Security and Others

1. **Encryption**: User's identity number and phone number are encrypted using Cipher AES/CBC/PKCS7Padding algorithm. Keys are stored in a secure place inside the project.
2. **Design**: Configuration changes and themes such as dark and light are managed throughout the application.
3. **Validation**: All the textfields are validated with proper message in the application.

## Getting Started

To get started with WalletWatcher, follow these steps:

1. Clone and open the project in the android studio.

2. Run the project. If you receive any compilation error then see the problems and solutions below.

3. A welcome page opens on the application click on explore.
   
5. You will be prompted to enter the phone number for authentication purposes.

6. Verify your phone number using the OTP sent to your device or try using the provided test credential.

7. Fill out the form with your income and expenses details. **Note: Category listed can be expandable. Click on the arrow of the given category to expand.** 

8. Explore the home screen to view the expense pie chart and track your financial progress.

## Problems and Solutions

1. You may receive agp version error while syncing project. Goto the project directory and find **libs.versions.toml** file. Change the version of the agp as recommended.
2. You may not receive sms code on your phone. On that case try using this test credentials. phone: **9800818055** code: **777777** of 6-digit.
3. The reasons behind not receiving sms code may be due to old play service version installed on the device or otp quota limit exceeded or firebase-config sha fingerprint mismatch.

## Technologies Used

- Android Studio
- Kotlin
- View Model
- Dagger Hilt library for dependency injection
- Firebase Authentication library for multi-factor authentication
- Room Database library for storing user credentials
- MPAndroidChart library for chart visualization
- Neumorphism library for intuitive design

## Contact and Support
  
- For any issues encountered while using Wallet-Watcher, you can contact with me at [engr.siddhartha@gmail.com](mailto:engr.siddhartha@gmail.com).
