package com.rockyrunstream.ac.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rockyrunstream.ac.store.EnumOrdinalConverter;
import org.bson.types.ObjectId;
import org.hibernate.validator.constraints.Length;
import org.mongodb.morphia.annotations.Converters;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Field;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Index;
import org.mongodb.morphia.annotations.IndexOptions;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Indexes;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Valid
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Entity(value = "planes", noClassnameStored = true)
@Converters(EnumOrdinalConverter.class)
@Indexes({
        @Index(fields = {
                @Field("type"),
                @Field("size"),
                @Field("timestamp"),
                @Field(value = "id")},
                options = @IndexOptions(name = "priorityIndex"))
})
public class ACPlane {

    public enum Type {EMERGENCY, VIP, PASSENGER, CARGO}

    public enum Size {LARGE, SMALL}

    @Id
    private ObjectId id;

    private long timestamp;

    @NotNull
    @Indexed
    private Type type;

    @NotNull
    @Indexed
    private Size size;

    @NotNull
    @Length(max = 255)
    @Indexed
    private String label;

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return "ACPlane{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", type=" + type +
                ", size=" + size +
                ", label='" + label + '\'' +
                '}';
    }
}
