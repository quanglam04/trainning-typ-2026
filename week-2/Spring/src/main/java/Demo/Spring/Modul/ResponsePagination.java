/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Demo.Spring.Modul;

import java.util.*;

/**
 *
 * @author Tran Anh Tuan
 */
public class ResponsePagination {
    private Object data;
    private int page, size, totalPages, totalElement;

    public ResponsePagination(Object data, int page, int size, int totalPages, int totalElement) {
        this.data = data;
        this.page = page;
        this.size = size;
        this.totalPages = totalPages;
        this.totalElement = totalElement;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getTotalElement() {
        return totalElement;
    }

    public void setTotalElement(int totalElement) {
        this.totalElement = totalElement;
    }
    
}
