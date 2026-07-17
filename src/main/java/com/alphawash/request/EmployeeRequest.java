package com.alphawash.request;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record EmployeeRequest(
        @Size(max = 100, message = "Tên nhân viên không được vượt quá 100 ký tự") String name,
        @Pattern(regexp = "^0\\d{9,10}$", message = "Số điện thoại không hợp lệ") String phone,
        String bankName,
        @Pattern(regexp = "^\\d{6,20}$", message = "Số tài khoản ngân hàng không hợp lệ (6-20 chữ số)")
                String bankAccount,
        @Past(message = "Ngày sinh phải nhỏ hơn ngày hiện tại") LocalDate dateOfBirth,
        @Pattern(regexp = "^\\d{12}$", message = "Số căn cước công dân phải gồm đúng 12 chữ số") String identityNumber,
        @PastOrPresent(message = "Ngày vào làm không được lớn hơn ngày hiện tại") LocalDate joinDate,
        @Pattern(regexp = "^(Working|OnLeave|Resigned)$", message = "Trạng thái làm việc không hợp lệ")
                String workStatus,
        String note) {}
