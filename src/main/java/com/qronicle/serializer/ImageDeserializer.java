package com.qronicle.serializer;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.qronicle.entity.Image;

import java.io.IOException;

public class ImageDeserializer extends StdDeserializer<Image> {
    protected ImageDeserializer(Class<Image> t) {
        super(t);
    }

    public ImageDeserializer() {
        this(null);
    }

    @Override
    public Image deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException, JacksonException {
        JsonNode node = jp.getCodec().readTree(jp);
        System.out.println(node);


        return null;
    }
}
