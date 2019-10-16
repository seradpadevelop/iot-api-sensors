if [ -e server.cer.pem ]
then
    echo "Installing self-signed RabbitMQ Root CA certificate."
    keytool \
        -trustcacerts \
        -cacerts \
        -noprompt \
        -storepass changeit \
        -alias amqps-cert \
        -import \
        -file server.cer.pem
else
    echo "Did not find self-signed RabbitMQ Root CA certificate."
fi

echo "Starting Opera IoT Sensors"
java \
-Djava.security.egd=file:/dev/./urandom \
-Duser.timezone=UTC \
-jar iotapisensors.jar