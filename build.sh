# Install Rabbit MQ
brew install rabbitmq

# Start the Rabbit MQ
CONF_ENV_FILE="/opt/homebrew/etc/rabbitmq/rabbitmq-env.conf" /opt/homebrew/opt/rabbitmq/sbin/rabbitmq-server

cd ~/Masters_Syr/2025-winter/cse681_projects/project2/

# use maven to clean and compile
mvn clean compile -U

# Build the fat jar based on pom.xml
mvn clean package

# run the jar files
# open terminal#1
java -jar target/sports-stats-client.jar

# open terminal#2
java -jar target/sports-stats-server.jar

