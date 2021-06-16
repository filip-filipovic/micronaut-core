/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.micronaut.http.client

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.http.hateoas.JsonError
import io.micronaut.test.extensions.spock.annotation.MicronautTest
import jakarta.inject.Inject
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import spock.lang.Specification

@MicronautTest
class ServerErrorSpec extends Specification {

    @Inject
    MyClient myClient

    void "test 500 error"() {
        when:
        myClient.fiveHundred()

        then:
        def e = thrown(HttpClientResponseException)
        e.message == "Bad things happening"
    }

    void "test 500 error - single"() {
        when:
        myClient.fiveHundredMono().block()

        then:
        def e = thrown(HttpClientResponseException)
        e.message == "Bad things happening"
    }

    void "test exception error"() {
        when:
        myClient.exception()

        then:
        def e = thrown(HttpClientResponseException)
        e.message == "Internal Server Error: Bad things happening"
    }

    void "test exception error - mono"() {
        when:
        myClient.exceptionMono().block()

        then:
        def e = thrown(HttpClientResponseException)
        e.message == "Internal Server Error: Bad things happening"
    }

    void "test single error"() {
        when:
        myClient.monoError()

        then:
        def e = thrown(HttpClientResponseException)
        e.message == "Internal Server Error: Bad things happening"
    }

    void "test single error - single"() {
        when:
        myClient.monoErrorMono().block()

        then:
        def e = thrown(HttpClientResponseException)
        e.message == "Internal Server Error: Bad things happening"
    }

    void "test flowable error - flowable"() {
        when:
        def response = myClient.fluxErrorFlux()
                .onErrorResume(throwable -> {
            if (throwable instanceof HttpClientResponseException) {
                return Flux.just(HttpResponse.status(((HttpClientResponseException) throwable).status).body(throwable.message))
            }
            throw throwable
        }).blockFirst()

        then:
        response.body.isPresent()
        response.body.get() == "Internal Server Error"
    }

    @Client('/server-errors')
    static interface MyClient {
        @Get('/five-hundred')
        HttpResponse fiveHundred()

        @Get('/five-hundred')
        Mono fiveHundredMono()

        @Get('/exception')
        HttpResponse exception()

        @Get('/exception')
        Mono exceptionMono()

        @Get('/single-error')
        HttpResponse monoError()

        @Get('/single-error')
        Mono monoErrorMono()

        @Get('/flowable-error')
        Flux fluxErrorFlux()
    }

    @Controller('/server-errors')
    static class ServerErrorController {

        @Get('/five-hundred')
        HttpResponse fiveHundred() {
            HttpResponse.serverError()
                        .body(new JsonError("Bad things happening"))
        }

        @Get('/exception')
        HttpResponse exception() {
            throw new RuntimeException("Bad things happening")
        }

        @Get('/single-error')
        Mono singleError() {
            Mono.error(new RuntimeException("Bad things happening"))
        }

        @Get('/flowable-error')
        Flux flowableError() {
            Flux.error(new RuntimeException("Bad things happening"))
        }

    }
}
