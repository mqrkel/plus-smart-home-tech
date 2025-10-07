package ru.yandex.practicum.deserializer;

import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.common.serialization.Deserializer;

public abstract class GeneralAvroDeserializer<T extends SpecificRecordBase>
        implements Deserializer<T> {

    private final DecoderFactory decoderFactory = DecoderFactory.get();
    private final DatumReader<T> reader;

    public GeneralAvroDeserializer(Schema schema) {
        reader = new SpecificDatumReader<>(schema);
    }

    @Override
    public T deserialize(String topic, byte[] data) {
        try {
            if (data != null) {
                BinaryDecoder decoder = decoderFactory.binaryDecoder(data, null);
                return this.reader.read(null, decoder);
            }
            return null;
        } catch (Exception e) {
            throw new DeserializationException("Data deserialization failure from topic [" + topic + "]", e);
        }
    }
}
