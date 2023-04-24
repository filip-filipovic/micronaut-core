/*
 * Copyright 2017-2023 original authors
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
package io.micronaut.http.netty.body;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.type.Argument;
import io.micronaut.core.type.Headers;
import io.micronaut.core.type.MutableHeaders;
import io.micronaut.http.HttpHeaders;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MediaType;
import io.micronaut.http.MutableHttpHeaders;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.body.MessageBodyHandler;
import io.micronaut.http.body.TextPlainHandler;
import io.micronaut.http.codec.CodecException;
import io.micronaut.http.netty.NettyHttpHeaders;
import io.micronaut.http.netty.NettyHttpResponseBuilder;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import jakarta.inject.Singleton;

import java.io.InputStream;
import java.io.OutputStream;

@Singleton
@Replaces(TextPlainHandler.class)
@Internal
final class NettyTextPlainHandler implements MessageBodyHandler<String>, NettyMessageBodyWriter<String> {
    private final TextPlainHandler defaultHandler = new TextPlainHandler();

    @Override
    public void writeTo(HttpRequest<?> request, MutableHttpResponse<String> outgoingResponse, Argument<String> type, String object, MediaType mediaType, NettyWriteContext nettyContext) throws CodecException {
        MutableHttpHeaders headers = outgoingResponse.getHeaders();
        ByteBuf byteBuf = Unpooled.wrappedBuffer(object.getBytes(getCharset(headers)));
        NettyHttpHeaders nettyHttpHeaders = (NettyHttpHeaders) headers;
        io.netty.handler.codec.http.HttpHeaders nettyHeaders = nettyHttpHeaders.getNettyHeaders();
        if (!nettyHttpHeaders.contains(HttpHeaders.CONTENT_TYPE)) {
            nettyHttpHeaders.set(HttpHeaders.CONTENT_TYPE, mediaType);
        }
        FullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(
            HttpVersion.HTTP_1_1,
            HttpResponseStatus.OK,
            byteBuf,
            nettyHeaders,
            EmptyHttpHeaders.INSTANCE
        );
        nettyContext.writeFull(fullHttpResponse);
    }

    @Override
    public String read(Argument<String> type, MediaType mediaType, Headers httpHeaders, InputStream inputStream) throws CodecException {
        return defaultHandler.read(type, mediaType, httpHeaders, inputStream);
    }

    @Override
    public void writeTo(Argument<String> type, String object, MediaType mediaType, MutableHeaders outgoingHeaders, OutputStream outputStream) throws CodecException {
        defaultHandler.writeTo(type, object, mediaType, outgoingHeaders, outputStream);
    }
}
