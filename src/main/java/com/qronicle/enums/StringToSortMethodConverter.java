package com.qronicle.enums;

import org.springframework.core.convert.converter.Converter;

// converts all values for sort method to upper case for enum class
public class StringToSortMethodConverter implements Converter<String, SortMethod> {
    @Override
    public SortMethod convert(String s) {
        return SortMethod.valueOf(s.toUpperCase());
    }
}
