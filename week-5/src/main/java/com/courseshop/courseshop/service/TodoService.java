package com.courseshop.courseshop.service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.courseshop.courseshop.dto.ResultPaginationDTO;
import com.courseshop.courseshop.model.Todo;
import com.courseshop.courseshop.repository.TodoRepository;
import com.courseshop.courseshop.service.error.IdInvalidException;

@Service 
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository){
        this.todoRepository = todoRepository;
    }

    // Lưu todo mới vào database và trả về bản ghi sau khi database tạo ID.
    public Todo handleCreateTodo(Todo todo) {
        return this.todoRepository.save(todo);
    }
    
    // public void handleGetTodo(){
    //     // List <Todo> todos = this.todoRepository.findAll();
    //     // todos.forEach(System.out::println);
    //     Optional <Todo> todoOptional = this.todoRepository.findByname("Huy Vu1");
    //     if(todoOptional.isPresent()){
    //         System.out.println(todoOptional.get().toString());
    //     }
    // }

    // Lấy toàn bộ todo không phân trang.
    public List<Todo> handleGetTodo(){
        return this.todoRepository.findAll();
    }
    // Tìm todo theo ID; nếu thiếu thì ném exception để ExceptionHandle trả 404.
    public Todo getTodoById(Long id){
        // Cách cũ: trả null để controller tự xử lý trường hợp không tìm thấy.
        // Optional<Todo> todoOptional = this.todoRepository.findById(id);
        // return todoOptional.isPresent() ? todoOptional.get() : null;
        return this.todoRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Todo với id = " + id));
    }
    

    // public void handleUpdateTodo(){
    //     Optional <Todo> todoOptional = this.todoRepository.findById(1L);
    //     if(todoOptional.isPresent()){
    //         Todo currentTodo = todoOptional.get();
    //         currentTodo.setCompleted(false);
    //         currentTodo.setName("Vu Huy dep trai qua di mat");
            
    //         this.todoRepository.save(currentTodo);
    //     }
    // }

    // Cập nhật tên và trạng thái của todo đang có; báo 404 nếu không tìm thấy ID.
    public Todo handleUpdateTodo(Long id, Todo inputTodo) {
        // Cách cũ: nếu không có Todo thì trả null về controller.
        // Optional<Todo> todoOptional = this.todoRepository.findById(id);
        // if (todoOptional.isPresent()) {
        //     Todo currentTodo = todoOptional.get();
        //     currentTodo.setCompleted(inputTodo.isCompleted());
        //     currentTodo.setName(inputTodo.getName());
        //     return this.todoRepository.save(currentTodo);
        // }
        // return null;
        Todo currentTodo = this.todoRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy Todo để cập nhật với id = " + id));
        currentTodo.setCompleted(inputTodo.isCompleted());
        currentTodo.setName(inputTodo.getName());
        return this.todoRepository.save(currentTodo);
    }
   
    // Kiểm tra tồn tại trước khi xóa; trả true sau khi xóa thành công.
    public boolean handleDeleteTodo(Long id) {
        // Cách cũ: trả false khi không tìm thấy Todo.
        // if (this.todoRepository.existsById(id)) {
        //     this.todoRepository.deleteById(id);
        //     return true;
        // }
        // return false;
        if (!this.todoRepository.existsById(id)) {
            throw new IdInvalidException("Không tìm thấy Todo để xóa với id = " + id);
        }
        this.todoRepository.deleteById(id);
        return true;
    }

    // Lấy một trang todo, kiểm tra page/size hợp lệ và trả kèm metadata phân trang.
    public ResultPaginationDTO fetchAllTodosWithPagination(int page, int size) {
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("page và size phải lớn hơn hoặc bằng 1");
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Todo> pageTodo = this.todoRepository.findAll(pageable);

        ResultPaginationDTO dto = new ResultPaginationDTO();
        dto.setData(pageTodo.getContent());
        dto.setPage(page);
        dto.setSize(size);
        dto.setTotalPages(pageTodo.getTotalPages());
        dto.setTotalElements(pageTodo.getTotalElements());

        return dto;
    }
}
