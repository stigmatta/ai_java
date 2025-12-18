package com.odintsov.ai_java;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
class JokeController {
    private final OpenAiService openAiService;

    public JokeController(OpenAiService openAiService) {
        this.openAiService = openAiService;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/joke")
    public String getJoke(@RequestParam("topic") String topic, Model model) {
        if (topic == null || topic.trim().isEmpty()) {
            model.addAttribute("error", "Будь ласка, введіть тему!");
            return "index";
        }
        String joke = openAiService.getJoke(topic);
        model.addAttribute("topic", topic);
        model.addAttribute("joke", joke);
        return "index";
    }
}
