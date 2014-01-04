package com.lambdaworks.redis.codec;

import static java.nio.charset.CoderResult.OVERFLOW;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.fasterxml.jackson.databind.ObjectMapper.DefaultTypeResolverBuilder;
import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectMapper.DefaultTyping;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;

public class JsonCodec extends RedisCodec<Object, Object> {

    private final Charset charset = Charset.forName("UTF-8");
    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonCodec() {
        objectMapper.setSerializationInclusion(Include.NON_NULL);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN, true);
        objectMapper.configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true);

        // type info inclusion
        TypeResolverBuilder<?> typer = new DefaultTypeResolverBuilder(DefaultTyping.NON_FINAL);
        typer.init(JsonTypeInfo.Id.CLASS, null);
        typer.inclusion(JsonTypeInfo.As.PROPERTY);
        objectMapper.setDefaultTyping(typer);
    }

    @Override
    public Object decodeKey(ByteBuffer bytes) {
        return decode(bytes);
    }

    @Override
    public Object decodeValue(ByteBuffer bytes) {
        return decode(bytes);
    }

    private Object decode(ByteBuffer bytes) {
        CharBuffer chars  = CharBuffer.allocate(bytes.limit());
        bytes.mark();

        CharsetDecoder decoder = charset.newDecoder();
        while (decoder.decode(bytes, chars, true) == OVERFLOW || decoder.flush(chars) == OVERFLOW) {
            chars = CharBuffer.allocate(chars.capacity() * 2);
            bytes.reset();
        }

        String res = chars.flip().toString();
        try {
            return objectMapper.readValue(res, Object.class);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public byte[] encodeKey(Object key) {
        return encodeValue(key);
    }

    @Override
    public byte[] encodeValue(Object value) {
        try {
            return objectMapper.writeValueAsBytes(value);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

}