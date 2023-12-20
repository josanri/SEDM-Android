# SEDM-Android
SEDM-Android is an Android development exercise created for the Android Development subject. This project serves as a practical application of Android development concepts and practices.
# Listeners
In this project, we have opted to use listeners directly within the activities rather than specifying them at the individual views.
# Activities
There are three activities:
- The **main activity** serves as the central hub where all tasks are listed. Users can interact with the tasks to view the details or add a new task.
- The **create task activity** allows users to input details and create a new task. This screen provides a user-friendly interface for adding tasks to the system.
- The **details task activity** provides a comprehensive view of a specific task, including its name, description, and associated timestamps. Users can explore the details and add a new timestamp.
# Dynamic Colors (Android 12+)
For enhanced visual appeal and a more immersive user experience, this project leverages dynamic colors. The use of dynamic colors ensures that the application adapts to the system-wide color scheme, providing a cohesive and modern design.
> [!WARNING]
> Versions lower than Android 12 will utilize the default color palette, as dynamic colors are specifically designed for Android 12 and above.
# Entities
The SEDM-Android project revolves around two main entities in its database:
## Task
- ID: Unique identifier for each task.
- Name: Descriptive name of the task.
- Description: Detailed information or instructions related to the task.
## Timestamp
- ID: Unique identifier for each timestamp entry.
- Timestamp (date): Date associated with a specific event or task.
- TaskId: Foreign key linking the timestamp to the corresponding task.
These entities form the core data structure of the application, allowing users to manage and organize tasks efficiently.
# Getting Started
To set up the SEDM-Android project locally, follow these steps:
- Clone the repository: git clone https://github.com/josanri/SEDM-Android
- Open the project in Android Studio.
- Ensure that your development environment is configured properly.
- Build and run the application on an Android emulator or physical device.