# falaris-client

A Java-based Minecraft client built with Gradle.

# IMPORTANT RECOMMENDATION
It is strongly recommended to use IntelliJ IDEA Community Edition (free),
as it includes built-in Gradle integration and significantly simplifies project setup,
dependency management, and build execution.

# REQUIREMENTS
- Java Development Kit (JDK) 21
- Git
- IntelliJ IDEA Community Edition (recommended)

# INSTALLATION

# 1. Clone the repository
git clone https://github.com/YOUR_USERNAME/falaris-client.git
cd falaris-client

# 2. Install Java 21
# Recommended distributions:
# - Eclipse Adoptium: https://adoptium.net/
# - Oracle JDK: https://www.oracle.com/java/technologies/downloads/

# BUILD INSTRUCTIONS

# Option A: IntelliJ IDEA (Recommended)
# 1. Open IntelliJ IDEA Community Edition
# 2. Select "Open" and choose the project directory
# 3. Allow Gradle to import and synchronize dependencies
# 4. Ensure JDK 21 is selected as the Project SDK
# 5. Use the Gradle tool window to run build tasks

# Option B: Command Line

# Windows
gradlew build

# macOS / Linux
./gradlew build

# OUTPUT ARTIFACT
# After a successful build, the compiled JAR file will be located in:
build/libs/

# TROUBLESHOOTING
# - Ensure JDK 21 is correctly installed and configured in your PATH
# - If Gradle sync fails, re-import the project in IntelliJ IDEA
# - Run a clean build if issues persist:
#     ./gradlew clean build
