package com.qronicle.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.qronicle.entity.Role;
import com.qronicle.entity.User;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class UserSerializer extends StdSerializer<User> {
    private static final DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public UserSerializer() {
        this(null);
    }

    public UserSerializer(Class<User> t) {
        super(t);
    }

    @Override
    public void serialize(User user, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("username", user.getUsername());
        gen.writeStringField("firstName", user.getFirstName());
        gen.writeStringField("lastName", user.getLastName());
        gen.writeStringField("email", user.getEmail());
        gen.writeStringField("privacyStatus", user.getPrivacyStatus().toString());
        gen.writeStringField("bio", user.getBio());
        gen.writeStringField("signupDate", user.getSignupDate().format(dateFormat));
        // write roles
        gen.writeFieldName("roles");
        gen.writeStartArray();
        for (Role role: user.getRoles()) {
            gen.writeString(role.getName());
        }
        gen.writeEndArray();
        gen.writeNumberField("itemCount", user.getItems().size());
        gen.writeEndObject();
    }
}
