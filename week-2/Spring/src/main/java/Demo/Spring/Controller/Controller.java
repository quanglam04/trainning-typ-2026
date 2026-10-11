/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Demo.Spring.Controller;
import Demo.Spring.Modul.RequestStudent;
import Demo.Spring.Modul.Response;
import Demo.Spring.Modul.Student;
import Demo.Spring.Modul.StudentSql;
import Demo.Spring.Service.StudentService;
import Demo.Spring.Service.StudentSqlService;
import java.util.Arrays;
import java.util.HashMap;
import java.util.*;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
/**
 *
 * @author Tran Anh Tuan
 */

@RestController
public class Controller {
    @Autowired
    private StudentService StudentService;
    private StudentSqlService StSqlSv;
    // Lay danh sach sinh vien khong phan trang
//    @GetMapping("/students")
//    public ResponseEntity<Response> get(){
//        return StudentService.getStudents();
//    }
    
    // Them Sinh Vien
    @PostMapping("/new-student")
    public ResponseEntity<Object> add(@RequestBody Student Student){
        return StudentService.create(Student);
    }
    
    // Xoa Sinh Vien
    @DeleteMapping("/delete-student/{id}")
    public ResponseEntity<Object> remove(@PathVariable String id){
        return StudentService.remove(id);
    }
    
    // Sua thong gpa cho Sinh Vien
    @PatchMapping("/replace-gpa/{id}")
    public ResponseEntity<Object> replace(@PathVariable String id, @RequestBody double gpa){
        return StudentService.replace(id, gpa);
    }
    
    // Lay danh sach Sinh Vien phan trang
    @GetMapping("/students")
    public ResponseEntity<Object> get(@RequestParam int page, @RequestParam int size){
        return StudentService.getP(page, size);
    }
    
    // Tim kiem Sinh Vien theo id
    @GetMapping("/students/{id}")
    public ResponseEntity<Object> search(@PathVariable String id){
        return StudentService.search(id);
    }
    
    //
    @PostMapping("/new-student-sql")
    public StudentSql add(@RequestBody RequestStudent RequestStudent){
        return StSqlSv.create(RequestStudent);
    }
}
