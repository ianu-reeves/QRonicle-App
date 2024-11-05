package com.qronicle.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.qronicle.entity.Image;
import org.springframework.beans.factory.annotation.Value;

import java.io.IOException;

public class ImageSerializer extends StdSerializer<Image> {
    @Value("${aws.cloudfront.url}")
    private String cdnPath;

    protected ImageSerializer(Class<Image> t) {
        super(t);
    }

    public ImageSerializer() {
        this(null);
    }

    @Override
    public void serialize(Image image, JsonGenerator gen, SerializerProvider provider) throws IOException {
        String[] parts = image.getOriginalFileName().split("\\.");
        gen.writeStartObject();
        gen.writeNumberField("id", image.getId());
        gen.writeStringField("imageUrl", cdnPath + image.getFileName());
        gen.writeNumberField("size", image.getFileSize());
        gen.writeStringField("name", image.getOriginalFileName());
        gen.writeStringField("type", "image/" + parts[parts.length - 1]);
        gen.writeEndObject();
    }
}
