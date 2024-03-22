import subprocess
import os

def run_mvn_clean_package():
    # Change directory to backend
    os.chdir("backend")

    # Run mvn clean package
    subprocess.run(["mvn", "clean", "package"])

def run_java_spring():
    # Run java -Dspring.profiles.active=datagen -jar target/e12219400-0.0.1-SNAPSHOT.jar in the background
    subprocess.Popen(["java", "-Dspring.profiles.active=datagen", "-jar", "target/e12219400-0.0.1-SNAPSHOT.jar"])

def run_npm_start():
    # Change directory to frontend
    os.chdir("../frontend")

    # Run npm start in the background
    subprocess.Popen(["npm", "start"])

# Main function to execute the tasks
def main():
    run_mvn_clean_package()
    run_java_spring()
    run_npm_start()

if __name__ == "__main__":
    main()
