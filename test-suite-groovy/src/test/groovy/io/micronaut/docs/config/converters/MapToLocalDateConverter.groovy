package io.micronaut.docs.config.converters

// tag::imports[]
import io.micronaut.core.convert.ConversionContext
import io.micronaut.core.convert.ConversionService
import io.micronaut.core.convert.TypeConverter

import javax.inject.Singleton
import java.time.DateTimeException
import java.time.LocalDate
// end::imports[]

// tag::class[]
@Singleton
class MapToLocalDateConverter implements TypeConverter<Map, LocalDate> { // <1>
    @Override
    Optional<LocalDate> convert(Map propertyMap, Class<LocalDate> targetType, ConversionContext context) {
        Optional<Integer> day = ConversionService.SHARED.convert(propertyMap.get("day"), Integer.class)
        Optional<Integer> month = ConversionService.SHARED.convert(propertyMap.get("month"), Integer.class)
        Optional<Integer> year = ConversionService.SHARED.convert(propertyMap.get("year"), Integer.class)
        if (day.isPresent() && month.isPresent() && year.isPresent()) {
            try {
                return Optional.of(LocalDate.of(year.get(), month.get(), day.get())) // <2>
            } catch (DateTimeException e) {
                context.reject(propertyMap, e) // <3>
                return Optional.empty()
            }
        }
        return Optional.empty()
    }
}
// end::class[]