package com.vokerg.taskplanner.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UiController {

    @GetMapping({"/ui", "/ui/"})
    public String ui() {
        return "redirect:/ui/index.html";
    }
}
