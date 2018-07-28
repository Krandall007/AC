package com.rockyrunstream.ac.web;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.FromStringDeserializer;
import org.bson.types.ObjectId;

import java.io.IOException;

public class ObjectIdDeserializer extends FromStringDeserializer<ObjectId> {

    public ObjectIdDeserializer() {
        super(ObjectId.class);
    }

    @Override
    protected ObjectId _deserialize(String value, DeserializationContext ctxt) throws IOException {
        return new ObjectId(value);
    }
}
