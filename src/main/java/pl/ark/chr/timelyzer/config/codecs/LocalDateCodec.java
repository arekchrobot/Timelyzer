package pl.ark.chr.timelyzer.config.codecs;

import org.bson.BsonReader;
import org.bson.BsonWriter;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.EncoderContext;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

public class LocalDateCodec implements Codec<LocalDate> {

    @Override
    public LocalDate decode(BsonReader bsonReader, DecoderContext decoderContext) {
        return Instant.ofEpochMilli(bsonReader.readDateTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @Override
    public void encode(BsonWriter bsonWriter, LocalDate date, EncoderContext encoderContext) {
        bsonWriter.writeDateTime(date.atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    @Override
    public Class<LocalDate> getEncoderClass() {
        return LocalDate.class;
    }
}
