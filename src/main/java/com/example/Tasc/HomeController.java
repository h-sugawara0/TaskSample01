package com.example.Tasc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {
    record TaskItem(String id, String task, String deadline, boolean done) {
    }

    private List<TaskItem> taskItems = new ArrayList<>();

    @RequestMapping(value = "/hello")
    @ResponseBody
    String hello() {
        return "<html>" +
                "<head><title>Hello</title></head>" +
                "<body>" +
                "<h1>Hello</h1>" +
                "It works!<br>" +
                "現在時刻は " + LocalDateTime.now() + " です。" +
                "</body>" +
                "</html>";

    }

    @GetMapping("/list")
    String listItems(Model model) {
        model.addAttribute("taskList", taskItems);
        return "home";
    }

    @GetMapping("/add")
    String addItem(@RequestParam("task") String task,
            @RequestParam("deadline") String deadline) {
        String id = UUID.randomUUID().toString().substring(0, 8);
        TaskItem item = new TaskItem(id, task, deadline, false);
        taskItems.add(item);

        return "redirect:/list";
    }
}
