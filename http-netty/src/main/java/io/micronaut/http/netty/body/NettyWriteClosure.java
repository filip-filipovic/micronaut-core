package io.micronaut.http.netty.body;

import io.micronaut.core.annotation.Experimental;
import io.micronaut.core.annotation.Internal;
import io.micronaut.core.annotation.NonNull;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.MutableHttpResponse;
import io.micronaut.http.body.MessageBodyWriter;
import io.micronaut.http.codec.CodecException;

/**
 * Netty-specific write closure.
 */
@Internal
@Experimental
public interface NettyWriteClosure<T> extends MessageBodyWriter.WriteClosure<T> {

    /**
     * Reads an object from the given byte buffer.
     *
     * @param request          The associated request
     * @param outgoingResponse The outgoing response.
     * @param object           The object to write
     * @param nettyContext     The netty context
     * @throws CodecException If an error occurs decoding
     */
    @NonNull
    void writeTo(
        @NonNull HttpRequest<?> request,
        @NonNull MutableHttpResponse<T> outgoingResponse,
        @NonNull T object,
        @NonNull NettyWriteContext nettyContext) throws CodecException;
}
