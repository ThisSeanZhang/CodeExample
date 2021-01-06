package io.whileaway.kafka;

import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class Producer {

    public static KafkaProducer<String, String> createProducer() {
        Properties config = new Properties();
        config.put("bootstrap.servers", "10.1.12.41:9092");
        return new KafkaProducer<String, String>(config, new StringSerializer(), new StringSerializer());
    }
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        KafkaProducer<String, String> producer = createProducer();
        ProducerRecord<String, String> record = new ProducerRecord<String, String>(
                "test-http-flume",
                "t1",
                "d1"
        );
        for (int i = 0; i < 10; i++) {
            producer.send(record, new Callback() {
                @Override
                public void onCompletion(RecordMetadata metadata, Exception exception) {
                    if (exception != null) {
                        System.out.println("Failed");
                        exception.printStackTrace();
                    }
                }
            }).get();
        }

    }
}
