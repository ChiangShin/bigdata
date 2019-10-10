package com.chiangshin.kafka;

import java.util.Arrays;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

/**
 * @Author jx
 * @Date 2019/10/10 22:26
 */
public class KafkaDemo {
    private final static String TOPIC = "first_topic";
    private final static String GROUP_ID = "chiangshin_1";

    private Producer<String,String> createProducer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "hadoop100:9092,hadoop101:9092,hadoop102:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("zookeeper.coonect", "hadoop102:2181,hadoop103:2181,hadoop104:2181");
        props.put("key.serializer", StringSerializer.class.getName());
        props.put("value.serializer", StringSerializer.class.getName());
        KafkaProducer<String, String> producer = new KafkaProducer<String, String>(props);
        return producer;
    }

    private Consumer createConsumer(){
        Properties props = new Properties();
        props.put("bootstrap.servers", "hadoop100:9092,hadoop101:9092,hadoop102:9092");
        props.put("group.id", GROUP_ID);
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        KafkaConsumer<String, String> consumer = new KafkaConsumer<String, String>(props);
        consumer.subscribe(Arrays.asList(TOPIC));
        return consumer;
    }


    @Test
    public void product1(){
        Producer<String, String> producer = createProducer();
        for (int i = 0; i < 100000; i++) {
            String msg = "I-love-you-so-much_"+i;
            System.out.println("product   ###   "+msg);
            producer.send(new ProducerRecord<String,String>(TOPIC, Integer.toString(i), msg));
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    @Test
    public void consumer1(){

        Consumer consumer = createConsumer();
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(100);
            for (ConsumerRecord<String, String> msg : records) {
                System.out.println("consumer   ###   key:"+msg.key()+" value:"+msg.value());
            }
        }
    }

    public static void main(String[] args) {

    }
}