/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Demo.Spring.Service;

import Demo.Spring.Modul.RepositoryStudent;
import Demo.Spring.Modul.RequestStudent;
import Demo.Spring.Modul.StudentSql;

/**
 *
 * @author Tran Anh Tuan
 */
public class StudentSqlService {
    private RepositoryStudent Student;
    
    public StudentSql create(RequestStudent request){
        StudentSql nw = new StudentSql(request.getName(), request.getGpa());
        return Student.save(nw);
    }
}
