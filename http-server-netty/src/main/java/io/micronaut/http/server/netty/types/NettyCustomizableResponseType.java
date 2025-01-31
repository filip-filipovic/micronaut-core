/*
 * Copyright 2017-2020 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.http.server.netty.types;

import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.server.types.CustomizableResponseType;
import io.netty.handler.codec.http.HttpResponse;

/**
 * A special type that allows writing data in Netty.
 *
 * @author James Kleeh
 * @since 1.0
 */
@Internal
public interface NettyCustomizableResponseType extends CustomizableResponseType {

    /**
     * Write this instance to Netty.
     *
     * @param request  The request
     * @param response The response
     * @return The netty response
     */
    CustomResponse write(HttpRequest<?> request, MutableHttpResponse<?> response);

    /**
     * Wrapper class for a netty response with a special body type, like
     * {@link io.netty.handler.codec.http.HttpChunkedInput} or
     * {@link io.netty.channel.FileRegion}.
     *
     * @param response The response
     * @param body     The body, or {@code null} if there is no body
     * @param needLast Whether to finish the response with a
     *                 {@link io.netty.handler.codec.http.LastHttpContent}
     */
    record CustomResponse(HttpResponse response, @Nullable Object body, boolean needLast) {
    }
}
