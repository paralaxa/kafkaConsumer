package sk.stopangin;

import com.example.Customer;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.AbstractMessageListenerContainer;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.Acknowledgment;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@EnableKafka
public class DemokafkaApplication2 implements ConsumerSeekAware {

    private final ThreadLocal<ConsumerSeekCallback> seekCallBack = new ThreadLocal<>();

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Customer> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Customer> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConcurrency(1);
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setAckMode(AbstractMessageListenerContainer.AckMode.MANUAL);
        factory.getContainerProperties().setErrorHandler(new SeekToCurrentErrorHandler());
        return factory;
    }

    @Bean
    public Map<String, Object> consumerConfig() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put("key.deserializer", StringDeserializer.class.getName());
        props.put("value.deserializer", MyDeser.class);
        props.put("schema.registry.url", "http://192.168.99.100:8081");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        props.put("ack.mode", AbstractMessageListenerContainer.AckMode.MANUAL_IMMEDIATE);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "customer-consumer-group-v2");
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        return props;
    }

    @Override
    public void registerSeekCallback(ConsumerSeekCallback callback) {
        System.out.println("---------------------------------");
        System.out.println("---------------------------------");
        System.out.println("---------------------------------");
        System.out.println("---------------------------------");
        System.out.println("---------------------------------");
        this.seekCallBack.set(callback);
    }

    @Override
    public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
        System.out.println("---------------------------------");
        System.out.println("---------------------------------");
        System.out.println("---------------------------------");
        System.out.println("---------------------------------");
        this.seekCallBack.set(callback);

    }

    @Override
    public void onIdleContainer(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {

    }

    @Bean
    public ConsumerFactory<String, Customer> consumerFactory() {
        return new DefaultKafkaConsumerFactory(
                consumerConfig(),
                new StringDeserializer(),
                new MyDeser(Customer.class));
    }

    @Value("${kafka.topic.ca}")
    private String kafkaTopic;

    @KafkaListener(topics = "customer-avro")
    public void receive(ConsumerRecord<?, ?> consumerRecord, Acknowledgment ack) {
//        this.seekCallBack.get().seek("customer-avro", consumerRecord.partition(), consumerRecord.offset()-1);
        System.out.println(consumerRecord.toString());
        throw new RuntimeException("ksjds");
    }


    public static void main(String[] args) {
        SpringApplication.run(DemokafkaApplication2.class, args);
    }


}
