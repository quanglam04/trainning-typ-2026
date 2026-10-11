/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Demo.Spring.Modul;

/**
 *
 * @author Tran Anh Tuan
 */
public class Student {
    private String name, id;
    private double gpa;
    public Student(){
    
    }
    public Student(String id, String name, double gpa){
        this.name = name;
        this.id = id;
        this.gpa = gpa;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getGpa() {
        return gpa;
    }

    public void setGpa(double gpa) {
        this.gpa = gpa;
    }
    
}
