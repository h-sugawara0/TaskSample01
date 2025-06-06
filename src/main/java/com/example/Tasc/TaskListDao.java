package com.example.Tasc;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import com.example.Tasc.HomeController.TaskItem;

import java.util.List;
import java.util.Map;

@Service
public class TaskListDao {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    TaskListDao(JdbcTemplate jdbcTemplate) {// Springがアプリ起動時に JdbcTemplate のインスタンスを自動で作成。
        this.jdbcTemplate = jdbcTemplate;// そのインスタンスをこのクラスで使えるように this.jdbcTemplate に入れる。
    }

    public void add(TaskItem taskItem) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(taskItem);
        // ↑「taskItem」のitemにあるデータ（id,task,deadline,done）をSQLのパラメータとして扱いやすい形に変換。

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate).withTableName("tasklist");
        // ↑SimpleJdbcInsert は、SQLの INSERT 文を自動で作ってくれる便利なクラス。
        // ↑withTableName("tasklist")で「どのテーブルに追加するか」を指定。
        insert.execute(param);
        // ↑先ほど準備したパラメータparamを使って、自動生成されたINSERT文をデータベースに実行し、タスクの情報を登録。
    }

    public List<TaskItem> findAll() {
        String sql = "SELECT * FROM tasklist";// データベースの「tasklist」から全件取得。
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);// jdbcTemplate を使ってSQLを実行。
        // ↑結果は「リスト」として返却、リストの中身は Map<String, Object> の形で表現される。
        // ↑例）{id=abc123, task=レポート, deadline=2025-06-10, done=false} 1件のデータはこういうMap。

        // ↓取得したリストresultをStream（1つずつ繰り返し処理）として扱う準備。
        // ↓1行ずつ Map<String, Object> を取り出して、TaskItem に変換する処理。
        // ↓例）new TaskItem("abc123","レポート","2025-06-10",false) こういうオブジェクトへ
        List<TaskItem> taskItems = result.stream().map((Map<String, Object> row) -> new TaskItem(
                // ↓row.get("カラム名") で各列の値を取得。.toString() で文字列に変換。（null対策にもなる）
                row.get("id").toString(),
                row.get("task").toString(),
                row.get("deadline").toString(),
                (Boolean) row.get("done")))// doneはbooleanなので(Boolean) でキャスト。
                .toList();// ストリームの処理結果（TaskItemの集まり）をListに変換。「TaskItemが入ったリスト」。
        return taskItems;
    }

    public int delete(String id) {// 指定された id を持つ行を削除。
        int number = jdbcTemplate.update("DELETE FROM tasklist WHERE id = ?", id);
        // update() は変更された行数（int）を返す → 1 なら成功、0 なら対象なし。
        return number;
    }

    public int update(TaskItem taskItem) {
        int number = jdbcTemplate.update(// update() は変更された行数（int）を返す → 1 なら成功、0 なら対象なし。
                "UPDATE tasklist SET task = ?, deadline = ?, done = ? WHERE id = ?",
                taskItem.task(),
                taskItem.deadline(),
                taskItem.done(),
                taskItem.id());
        return number;// 更新=1、未更新=0が入る(今のコードでは重要じゃあない)
    }
}
