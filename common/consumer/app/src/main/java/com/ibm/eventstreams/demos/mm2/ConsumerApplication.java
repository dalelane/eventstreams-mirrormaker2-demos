package com.ibm.eventstreams.demos.mm2;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerApplication {
    private static final Logger log = LoggerFactory.getLogger(ConsumerApplication.class);

    private static final String BOOTSTRAP_SERVERS = System.getenv("BOOTSTRAP_SERVERS");
    private static final String GROUP_ID = System.getenv("GROUP_ID");
    private static final String OFFSETS_COMMIT_INTERVAL = "10000";
    private static final String TOPICS_PATTERN = System.getenv("TOPICS_PATTERN");
    private static final String SASL_MECHANISM = System.getenv("SASL_MECHANISM");
    private static final String SASL_JAAS_CONFIG = System.getenv("SASL_JAAS_CONFIG");
    private static final String TRUSTSTORE_TYPE = System.getenv("TRUSTSTORE_TYPE");
    private static final String TRUSTSTORE_FILE = System.getenv("TRUSTSTORE_FILE");
    private static final String TRUSTSTORE_PASSWORD = System.getenv("TRUSTSTORE_PASSWORD");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        log.info("-------------------------------------------------------------");
        log.info("Config:");
        log.info("-------------------------------------------------------------");
        log.info("Bootstrap server: {}", BOOTSTRAP_SERVERS);
        log.info("Group id: {}", GROUP_ID);
        log.info("Auto commit interval: {} ms", OFFSETS_COMMIT_INTERVAL);
        log.info("Topics pattern: {}", TOPICS_PATTERN);
        log.info("SASL mechanism: {}", SASL_MECHANISM);
        log.info("Truststore type: {}", TRUSTSTORE_TYPE);
        log.info("Truststore file: {} (exists? {})", TRUSTSTORE_TYPE, new File(TRUSTSTORE_FILE).exists());
        log.info("Truststore password: {}", TRUSTSTORE_PASSWORD);
        log.info("-------------------------------------------------------------");


        Properties properties = new Properties();
        properties.setProperty(CommonClientConfigs.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        properties.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, OFFSETS_COMMIT_INTERVAL);

        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        properties.put(SaslConfigs.SASL_MECHANISM, SASL_MECHANISM);
        properties.put(SaslConfigs.SASL_JAAS_CONFIG, SASL_JAAS_CONFIG);

          properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_SSL");
        properties.put(SslConfigs.SSL_TRUSTSTORE_TYPE_CONFIG, TRUSTSTORE_TYPE);
        properties.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, TRUSTSTORE_FILE);
        properties.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, TRUSTSTORE_PASSWORD);

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties);

        final Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                consumer.wakeup();
                try {
                    mainThread.join();
                }
                catch (InterruptedException e) {}
            }
        });

        try {
            consumer.subscribe(Pattern.compile(TOPICS_PATTERN));
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(5000));
                for (ConsumerRecord<String, String> record : records) {
                    log.info("Topic: " + record.topic() +
                            "  /  Partition: " + record.partition() +
                            "  /  Offset: " + record.offset() +
                            "  /  Timestamp: " + DATE_FORMAT.format(new Date(record.timestamp()))  +
                            "  /  Data: " + record.value());
                }
            }
        }
        catch (WakeupException e) {
            log.info("Shutting down");
        }
        catch (Exception e) {
            log.error("ERROR", e);
        }
        finally {
            consumer.close();
        }
    }
}
