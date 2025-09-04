package com.major.userservice.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.Properties;

@Configuration
public class KafkaConfig {
    // user service will act as a kafka producer in user onboarding flow
    // user service will send the user data to the kafka topic
    // kafka topic will be used by the other services to consume the user data
// This is the configuration for the kafka producer
    @Bean
    ProducerFactory getProducerFactory() { // this
        Properties properties = new Properties();
        // TODO - move values to application.properties
        // issse hoga yeh ki
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // konse kafka server se connect
        // hona hai
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class); // key ko serialize karne ke liye
        //matlab yeh hai ki key ko string me convert karna hai
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        // value ko serialize karne ke liye
        // matlab yeh hai ki value ko string me convert karna hai

        return new DefaultKafkaProducerFactory(properties); // it returns the producer factory object
    }

    @Bean
    KafkaTemplate<String, String> getKafkaTemplate() { // This method returns the KafkaTemplate object
        // KafkaTemplate is used to send messages to the kafka topic
        // KafkaTemplate is a high-level abstraction for sending messages to Kafka topics
        // agar ise use nahi karte to hume khud se producer create karna padegaa
        return new KafkaTemplate<>(getProducerFactory());
    }

}