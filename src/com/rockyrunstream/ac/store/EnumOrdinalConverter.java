package com.rockyrunstream.ac.store;

import com.rockyrunstream.ac.service.ACPlane;
import org.mongodb.morphia.converters.SimpleValueConverter;
import org.mongodb.morphia.converters.TypeConverter;
import org.mongodb.morphia.mapping.MappedField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnumOrdinalConverter extends TypeConverter implements SimpleValueConverter {
    private static final Logger log = LoggerFactory.getLogger(EnumOrdinalConverter.class);

    public EnumOrdinalConverter() {
        super(ACPlane.Type.class, ACPlane.Size.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object decode(final Class targetClass, final Object fromDBObject, final MappedField optionalExtraInfo) {
        if (fromDBObject == null) {
            return null;
        }
        final int ordinal = (Integer) fromDBObject;
        final Object[] values;
        if (targetClass == ACPlane.Size.class) {
            values = ACPlane.Size.values();
        } else if (targetClass == ACPlane.Type.class) {
            values = ACPlane.Type.values();
        } else {
            log.warn("Invalid class {} ", targetClass);
            return null;
        }
        if (ordinal < 0 || ordinal >= values.length) {
            log.warn("Invalid ordinal {} ", ordinal);
        }
        return values[ordinal];
    }

    @Override
    public Object encode(final Object value, final MappedField optionalExtraInfo) {
        if (value == null) {
            return null;
        }

        return ((Enum) value).ordinal();
    }

}
