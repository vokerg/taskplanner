package com.vokerg.taskplanner.api;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToTaskSortDirectionConverter implements Converter<String, TaskSortDirection> {

    @Override
    public TaskSortDirection convert(String source) {
        return TaskSortDirection.fromValue(source);
    }
}
