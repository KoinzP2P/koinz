## Building KOINZ

1. **Clone KOINZ**

   ```sh
   git clone https://github.com/koinz-network/koinz
   # if you intend to do testing on the latest release, you can clone the respective branch selectively, without downloading the whole repository
   # for the 1.9.18 release, you would do it like this:
   git clone --recurse-submodules --branch release/v1.9.19 https://github.com/koinz-network/koinz
   cd koinz
   ```

2. **Build KOINZ**

   On macOS and Linux, execute:
   ```sh
   ./gradlew build
   ```

   On Windows:
   ```cmd
   gradlew.bat build
   ```

   If you prefer to skip tests to speed up the building process, just append _-x test_ to the previous commands.

### Important notes

1. You do _not_ need to install Gradle to build KOINZ. The `gradlew` shell script will install it for you, if necessary.

2. KOINZ currently works with JDK 11 and JDK 15. You can find out which
   version you have with:

   ```sh
   javac -version
   ```

   If you have multiple JDK versions installed, check which one Gradle will use, with:

   ```sh
   ./gradlew --version
   ```

   and if the version number on the JVM line is not a supported one, you can pick the correct JDK at runtime with this syntax (verify your system path):

   ```sh
   ./gradlew build -Dorg.gradle.java.home=/usr/lib/jvm/java-11-openjdk-amd64/
   ```

If you do not have JDK 11 installed, check out scripts in the [scripts](../scripts) directory or download it manually from https://jdk.java.net/archive/.

## Running KOINZ

Once KOINZ is installed, its executables will be available in the root project directory. Run **KOINZ Desktop** as follows:

On macOS and Linux:
```sh
./koinz-desktop
```
or, to select a specific version of Java:
```sh
env JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 ./koinz-desktop
```

On Windows:
```cmd
koinz-desktop.bat
```

## See also

 - [Importing KOINZ into IntelliJ IDEA](./idea-import.md)
 - [KOINZ development environment setup guide](./dev-setup.md)
