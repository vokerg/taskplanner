package com.vokerg.taskplanner.api;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToTaskSortByConverter implements Converter<String, TaskSortBy> {

    @Override
    public TaskSortBy convert(String source) {
        return TaskSortBy.fromValue(source);
    }
}
