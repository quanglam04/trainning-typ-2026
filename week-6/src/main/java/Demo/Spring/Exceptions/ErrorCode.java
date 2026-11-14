/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Enum.java to edit this template
 */
package Demo.Spring.Exceptions;

/**
 *
 * @author Tran Anh Tuan
 */
public enum ErrorCode {
    USER_NOT_EXIST(404, "NOT FOUND", "tên người dùng hoặc mật khẩu chưa đúng"),
    USER_EXIST(409, "CONFLICT", "Tên đăng nhâp hoặc email đã tồn tại"),
    TOKEN_ERROR(401, "Unauthorized", "request không hợp lệ"),
    PRODUCT_NOT_EXIST(404, "NOT FOUND", "Không tìm thấy sản phẩm"),
    USER_ERROR(401, "UNAUTHORIZED", "request không hợp lệ"),
    INVALID_QUANTITY(400, "BAD REQUEST", "Số lượng phải lớn hơn 0"),
    OUT_OF_STOCK(400, "BAD REQUEST", "Số lượng sản phẩm trong kho không đủ"),
    CART_ITEM_NOT_EXIST(404, "NOT FOUND", "Không tìm thấy sản phẩm trong giỏ hàng")
    ;
    
    private int code;
    private String message;
    private String details;

    private ErrorCode(int code, String message, String details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }
    
    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }
    
}
