package Demo.Spring.Service;

import Demo.Spring.Modul.ErrorResponse;
import Demo.Spring.Modul.Response;
import Demo.Spring.Modul.ResponsePagination;
import Demo.Spring.Modul.Student;
import org.springframework.stereotype.Service;
import java.util.*;
import org.springframework.http.ResponseEntity;
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author Tran Anh Tuan
 */
@Service
public class StudentService {
    
    private ArrayList<Student> arr = new ArrayList<>(Arrays.asList(
            new Student("B24DCCN600", "Tran Anh Tuan", 2.6),
            new Student("B24DCCN100", "nguyen Van A", 3),
            new Student("B24DCCN200", "Do Van V", 3),
            new Student("B24DCCN101", "Le Minh Hoang", 3.2),
            new Student("B24DCCN102", "Pham Thu Huong", 3.8),
            new Student("B24DCCN103", "Vu Duc Thang", 2.4),
            new Student("B24DCCN104", "Hoang Thi Lan", 3.5),
            new Student("B24DCCN105", "Bui Quang Huy", 2.9),
            new Student("B24DCCN106", "Dang Ngoc Mai", 3.9),
            new Student("B24DCCN107", "Nguyen Hai Nam", 2.1),
            new Student("B24DCCN108", "Tran Thi Bich", 3.4),
            new Student("B24DCCN109", "Ly Van Khanh", 3.0),
            new Student("B24DCCN110", "Pham Gia Bao", 3.7)
    ));
    
    public ResponseEntity<Response> getStudents(){
        return ResponseEntity.ok(new Response("200", "Danh Sach Sinh Vien: ", arr));
    }
   
    public ResponseEntity<Object> create(Student newStudent){
        String id = newStudent.getId();
        int check = 0;
        Student Stdl = new Student();
        for(int i = 0; i< arr.size(); i++){
            if(arr.get(i).getId().compareTo(id) == 0){
                check = 1;
                break;
            }
        }
        if(check == 0){
            arr.add(newStudent);
            return ResponseEntity.ok(new Response("200", "Them Sinh Vien thanh cong", newStudent));
        }
        else return ResponseEntity.ok(new ErrorResponse("409", "Conflict", "Sinh Vien co id: " + id + " da ton tai"));
    }
    
    public ResponseEntity<Object> remove(String id){
        int check = 0;
        Student Stdl = new Student();
        for(int i = 0; i< arr.size(); i++){
            if(arr.get(i).getId().compareTo(id) == 0){
                Stdl = arr.get(i);
                arr.remove(i);
                check = 1;
                break;
            }
        }
        if(check == 1) return ResponseEntity.ok(new Response("200", "Xoa thanh cong: ", Stdl));
        else return ResponseEntity.ok(new ErrorResponse("404", "Not Found", "Sinh Vien co id: " + id + " khong ton tai"));
    }
    
    public ResponseEntity<Object> replace(String id, double gpa){
        int check = 0;
        Student Stdl = new Student();
        for(int i = 0; i< arr.size(); i++){
            if(arr.get(i).getId().compareTo(id) == 0){
                arr.get(i).setGpa(gpa);
                Stdl = arr.get(i);
                check = 1;
                break;
            }
        }
        if(check == 1) return ResponseEntity.ok(new Response("200", "Sua thanh cong: ", Stdl));
        else return ResponseEntity.ok(new ErrorResponse("404", "Not Found", "Sinh Vien co id: " + id + " khong ton tai"));
    }
    
    public ResponseEntity<Object> search(String id){
        int check = 0;
        Student Stdl = new Student();
        for(int i = 0; i< arr.size(); i++){
            if(arr.get(i).getId().compareTo(id) == 0){
                Stdl = arr.get(i);
                check = 1;
                break;
            }
        }
        if(check == 1) return ResponseEntity.ok(new Response("200", "Tim thanh cong: ", Stdl));
        else return ResponseEntity.ok(new ErrorResponse("404", "Not Found", "Sinh Vien co id: " + id + " khong ton tai"));
    }
    
    public ResponseEntity<Object> getP(int page, int size){
        int p = page;
        int s = size;
        int totalPages = (int)Math.ceil(arr.size()/size);
        int totalElement = arr.size();
        if((p-1) * s > arr.size() || p <= 0) return ResponseEntity.ok(new ErrorResponse("404", "Not Found", "page or size khong hop le"));
        ArrayList<Student> tmp = new ArrayList<>();
        for(int i = (p-1) * s; i < Math.min(arr.size(), p*s); i++){
            tmp.add(arr.get(i));
        }
        return ResponseEntity.ok(new ResponsePagination(tmp, p, s, totalPages, totalElement));
    }
    
    
}
