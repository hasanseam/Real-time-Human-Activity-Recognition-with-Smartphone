# Real-time Human Activity Recognition with Smartphone

## Overview
This Android application is designed for real-time human activity recognition using smartphone sensors. It focuses on capturing sensor data (like accelerometer, gyroscope) corresponding to different physical activities (e.g., walking, running) and can predict these activities using machine learning models.

## Architecture & Structure
The app is built using **Java** and follows a standard **MVVM (Model-View-ViewModel)** architectural pattern. 

### Key Components

- **UI Layer (Fragments & Activities)**:
  - `MainActivity.java`: The main entry point of the app, likely hosting the fragments.
  - `DataAcquisitionFragment.java`: Handles the UI and logic for recording sensor data for various activities.
  - `DataVisualizationFragment.java`: Responsible for visualizing the collected or real-time sensor data.
  - `ConfigureFragment.java`, `SensorConfigureFragment.java`, `ActivityConfigureFragment.java`: Fragments for configuring which sensors to use and managing activity types.
  - `ProfileFragment.java`: Manages user profile or settings.

- **ViewModel Layer**:
  - `DataAcquisitionViewModel.java`, `DataVisualizationViewModel.java`, `SensorConfigureViewModel.java`, etc.: Handle the business logic and prepare data for the UI layers, separating logic from UI.

- **Data & Model Layer**:
  - `SensorData.java`, `SensorChannel.java`, `FileDetails.java`: POJOs representing the entities in the app.
  - `ActivityDbHelper.java`, `SensorDbHelper.java`: SQLite database helpers used for local persistence of configured activities and sensor metadata.

- **Services**:
  - `KeepAliveService.java`: A foreground/background service likely used to continuously collect sensor data even when the app is not in active focus.

- **Adapters**:
  - `ConfigureViewPageAdapter.java`, `FileAdapter.java`: RecyclerView or ViewPager adapters for rendering lists of configurations or collected files.

## Tech Stack
- **Language**: Java
- **Framework**: Android SDK (Android Studio)
- **Architecture**: MVVM
- **Local Storage**: SQLite (via `SQLiteOpenHelper`)
- **Background Processing**: Android Services

## Usage & Flow
1. **Configuration**: Users can configure which sensors to track and define the activities they want to record via the configuration screens.
2. **Data Acquisition**: Users start recording sessions, which triggers background services to log data from the device's hardware sensors.
3. **Data Prediction/Visualization**: The application analyzes the data (or prepares it for machine learning models) and visualizes the results.
