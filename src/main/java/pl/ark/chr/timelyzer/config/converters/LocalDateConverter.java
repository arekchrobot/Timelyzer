package pl.ark.chr.timelyzer.config.converters;

import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import static java.time.ZoneId.systemDefault;

public class LocalDateConverter extends TypeConverter implements SimpleValueConverter {
    /**
     * Creates the Converter.
     */
    public LocalDateConverter() {
        super(LocalDate.class);
    }

    @Override
    public Object decode(final Class<?> targetClass, final Object val, final MappedField optionalExtraInfo) {
        if (val == null) {
            return null;
        }

        if (val instanceof LocalDate) {
            return val;
        }

        if (val instanceof Date) {
            return LocalDateTime.ofInstant(((Date) val).toInstant(), systemDefault()).toLocalDate();
        }

        if (val instanceof Number) {
            return LocalDateTime.ofInstant(new Date(((Number) val).longValue()).toInstant(), systemDefault()).toLocalDate();
        }

        throw new IllegalArgumentException("Can't convert to LocalDate from " + val);
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        if (value == null) {
            return null;
        }
        LocalDate date = (LocalDate) value;
        return date.atStartOfDay()
                .atZone(systemDefault())
                .toInstant().toEpochMilli();
    }
}
