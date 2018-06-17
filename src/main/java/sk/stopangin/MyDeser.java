package sk.stopangin;

import com.example.Customer;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import org.apache.kafka.common.serialization.Deserializer;
import org.springframework.util.Assert;

import java.util.Map;

public class MyDeser<T> implements Deserializer<Warpper<T>> {

    protected final ObjectMapper objectMapper;

    protected final Class targetType;

    private volatile ObjectReader reader;

    public MyDeser() {
        this(Customer.class);

    }

    public MyDeser(Class targetType) {
        this(targetType, new ObjectMapper());
        this.objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, false);
        this.objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @SuppressWarnings("unchecked")
    public MyDeser(Class targetType, ObjectMapper objectMapper) {
        Assert.notNull(objectMapper, "'objectMapper' must not be null.");
        this.objectMapper = objectMapper;

        Assert.notNull(targetType, "'targetType' cannot be resolved.");
        this.targetType = targetType;
    }

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {
        // No-op
    }

    @Override
    public Warpper<T> deserialize(String topic, byte[] data) {
        if (this.reader == null) {
            this.reader = this.objectMapper.readerFor(this.targetType);
        }
        Warpper<T> w = new Warpper<>();
        try {
            T result = null;
            if (data != null) {
                result = this.reader.readValue(data);
            }
            w.setPayload(result);
            return w;
        } catch (Exception e) {
            w.setError(true);
            return w;
        }
    }

    @Override
    public void close() {
        // No-op
    }


}
