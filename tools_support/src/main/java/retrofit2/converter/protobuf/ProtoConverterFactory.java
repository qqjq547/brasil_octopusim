/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package retrofit2.converter.protobuf;

import androidx.annotation.Nullable;

import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * A {@linkplain Converter.Factory converter} which uses Protocol Buffers.
 * <p>
 * This converter only applies for types which extend from {@link MessageLite} (or one of its
 * subclasses).
 */
public final class ProtoConverterFactory extends Converter.Factory {
    public static ProtoConverterFactory create() {
        return new ProtoConverterFactory(null);
    }

    /**
     * Create an instance which uses {@code registry} when deserializing.
     */
    public static ProtoConverterFactory createWithRegistry(@Nullable ExtensionRegistryLite registry) {
        return new ProtoConverterFactory(registry);
    }

    private final @Nullable
    ExtensionRegistryLite registry;

    private ProtoConverterFactory(@Nullable ExtensionRegistryLite registry) {
        this.registry = registry;
    }

    @Override
    public @Nullable
    Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (!(type instanceof Class<?>)) {
            return null;
        }
        Class<?> c = (Class<?>) type;
        if (!MessageLite.class.isAssignableFrom(c)) {
            return null;
        }

        Parser<MessageLite> parser;
        try {
            Method method = c.getDeclaredMethod("parser");
            //noinspection unchecked
            parser = (Parser<MessageLite>) method.invoke(null);
        } catch (Exception e) {
            if (e instanceof NoSuchMethodException || e instanceof IllegalAccessException) {
                // If the method is missing, fall back to original static field for pre-3.0 support.
                try {
                    Field field = c.getDeclaredField("PARSER");
                    //noinspection unchecked
                    parser = (Parser<MessageLite>) field.get(null);
                } catch (Exception e1) {
                    throw new IllegalArgumentException("Found a protobuf message but "
                            + c.getName()
                            + " had no parser() method or PARSER field.");
                }
            } else {
                throw new RuntimeException(e.getCause());
            }
        }
        return new ProtoResponseBodyConverter<>(parser, registry);
    }

    @Override
    public @Nullable
    Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new ProtoRequestBodyConverter();
    }
}
