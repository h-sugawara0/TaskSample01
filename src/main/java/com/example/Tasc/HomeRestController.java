package com.example.Tasc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeRestController {

    record TaskItem(String id, String task, String deadline, boolean done) {
    }

    private List<TaskItem> taskItems = new ArrayList<>();

    @RequestMapping(value = "/resthello") // 表示テスト用
    String hello() {
        return "Hello.It works!現在時刻は" + LocalDateTime.now() + "です。";
    }// 表示テスト用

    @GetMapping("/restadd")
    String addItem(@RequestParam("task") String task, // http://localhost:8080/restadd?task=宿題&deadline=2025-06-10の
            @RequestParam("deadline") String deadline) {// task=[宿題],deadline[2025-06-10]を取ってくる
        String id = UUID.randomUUID().toString().substring(0, 8);
        TaskItem item = new TaskItem(id, task, deadline, false);
        taskItems.add(item);
        return "タスクを追加しました。";
    }

    @GetMapping("/restlist")
    String listItems() {
        String result = taskItems.stream()
                .map(TaskItem::toString)
                .collect(Collectors.joining(","));
        return result;
    }
}
