package com.example.Tasc;

import org.springframework.beans.factory.annotation.Autowired;
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

    private final TaskListDao dao;

    @Autowired
    HomeController(TaskListDao dao) {// TaskListDaoをdaoに保存するコンストラクタ
        this.dao = dao;
    }

    record TaskItem(String id, String task, String deadline, boolean done) {
    }// TaskItemはタスクのデータをまとめた箱（クラス）。
     // ID,タスク内容,期限,完了したかどうか,の4つの情報を持っている。

    private List<TaskItem> taskItems = new ArrayList<>();

    @RequestMapping(value = "/hello") // 表示テスト用
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

    }// 表示テスト用

    @GetMapping("/list")
    String listItems(Model model) {
        List<TaskItem> taskItems = dao.findAll();// DBから全件取得
        model.addAttribute("taskList", taskItems);
        // データをビューに渡す。model.addAttribute()が無いとhtmlでリストを表示できない
        return "home";// home.htmlへ遷移
    }

    @GetMapping("/add")
    String addItem(@RequestParam("task") String task, // task=タスク名を取得
            @RequestParam("deadline") String deadline) {// deadline=期日を取得
        String id = UUID.randomUUID().toString().substring(0, 8);// id=ユニークID頭8桁取得
        TaskItem item = new TaskItem(id, task, deadline, false);// 3つとT/Fを設定
        dao.add(item);// DBに追加

        return "redirect:/list";// DB全件取得＆htmlへ
    }

    @GetMapping("/delete")
    String deleteItem(@RequestParam("id") String id) {
        dao.delete(id);// DBからidでタスクを削除
        return "redirect:/list";// DB全件取得＆htmlへ
    }

    @GetMapping("/update")
    String updateItem(@RequestParam("id") String id,
            @RequestParam("task") String task,
            @RequestParam("deadline") String deadline,
            @RequestParam("done") boolean done) {// パラメータから4つの値を取得
        TaskItem taskItem = new TaskItem(id, task, deadline, done);// TaskItemに格納
        dao.update(taskItem);// 作成したtaskItemを使ってDBの該当タスクを更新。
        return "redirect:/list";// DB全件取得＆htmlへ
    }

}
