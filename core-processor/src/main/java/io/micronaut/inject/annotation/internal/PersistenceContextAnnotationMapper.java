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
package io.micronaut.inject.annotation.internal;

import io.micronaut.core.annotation.AnnotationUtil;
import io.micronaut.core.annotation.AnnotationValue;
import io.micronaut.core.annotation.Internal;
import io.micronaut.inject.annotation.NamedAnnotationMapper;
import io.micronaut.inject.visitor.VisitorContext;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Allows using the `javax.persistence.PersistenceContext` annotation in Micronaut.
 *
 * @author graemerocher
 * @since 1.0
 */
@Internal
public final class PersistenceContextAnnotationMapper implements NamedAnnotationMapper {

    private static final String SOURCE_ANNOTATION = "javax.persistence.PersistenceContext";

    @Override
    public String getName() {
        return SOURCE_ANNOTATION;
    }

    @Override
    public List<AnnotationValue<?>> map(AnnotationValue<Annotation> annotation, VisitorContext visitorContext) {
        final String name = annotation.stringValue("name").orElse(null);
        if (name != null) {
            return Arrays.asList(
                AnnotationValue.builder(AnnotationUtil.INJECT).build(),
                AnnotationValue.builder(AnnotationUtil.NAMED).value(name).build()
            );
        } else {
            return Collections.singletonList(
                AnnotationValue.builder(AnnotationUtil.INJECT).build()
            );
        }
    }
}
