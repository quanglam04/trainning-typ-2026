package com.courseshop.courseshop.controller;

import org.springframework.web.bind.annotation.RestController;

import com.courseshop.courseshop.dto.ResultPaginationDTO;
import com.courseshop.courseshop.model.Todo;
import com.courseshop.courseshop.service.TodoService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;


@RestController
@RequestMapping("/todos")
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService){
        this.todoService = todoService;
    }
    // GET /todos/{id}: lấy chi tiết một todo theo ID.
    @GetMapping("/{id}")
    public ResponseEntity<Todo> getTodoById(@PathVariable Long id) {
        Todo todoData = this.todoService.getTodoById(id);
        // Cách cũ: controller tự kiểm tra null và tự tạo response 404.
        // if (todoData == null) {
        //     return ResponseEntity.status(HttpStatus.NOT_FOUND)
        //             .body(new RestErrorDTO(404, "Not Found", "Không tìm thấy Todo với id = " + id));
        // }
        return ResponseEntity.ok(todoData);
    }

    // GET /todos?page=1&size=10: lấy danh sách todo theo từng trang.
    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getTodos(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        
        ResultPaginationDTO result = this.todoService.fetchAllTodosWithPagination(page, size);
        return ResponseEntity.ok().body(result);
    }

    // GET /todos/all: lấy toàn bộ todo không phân trang.
    @GetMapping("/all")
    public ResponseEntity<List<Todo>> getTodo() {
        List<Todo> listtodo = this.todoService.handleGetTodo();
        return ResponseEntity.ok().body(listtodo);
    }


    // POST /todos: tạo todo mới từ dữ liệu JSON gửi trong request body.
    @PostMapping
    public ResponseEntity<Todo> createTodo(@RequestBody Todo input) {
        Todo newTodo = this.todoService.handleCreateTodo(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(newTodo);
    }


    // PUT /todos/{id}: cập nhật nội dung và trạng thái của todo theo ID.
    @PutMapping("/{id}")
    public ResponseEntity<Todo> updateTodo(@PathVariable Long id, @RequestBody Todo input) {
        Todo updatedTodo = this.todoService.handleUpdateTodo(id, input);
        // Cách cũ: service trả null nếu không tìm thấy; controller tự trả 404.
        // if (updatedTodo == null) {
        //     return ResponseEntity.status(HttpStatus.NOT_FOUND)
        //             .body(new RestErrorDTO(404, "Not Found", "Không tìm thấy Todo để cập nhật với id = " + id));
        // }
        return ResponseEntity.ok(updatedTodo);
    }



    // DELETE /todos/{id}: xóa todo theo ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTodo(@PathVariable Long id) {
        this.todoService.handleDeleteTodo(id);
        // Cách cũ: service trả false nếu không tìm thấy; controller tự trả 404.
        // boolean isDeleted = this.todoService.handleDeleteTodo(id);
        // if (!isDeleted) {
        //     return ResponseEntity.status(HttpStatus.NOT_FOUND)
        //             .body(new RestErrorDTO(404, "Not Found", "Không tìm thấy Todo để xóa với id = " + id));
        // }
        return ResponseEntity.ok("Xóa thành công todo có id = " + id);
    }
    



}
