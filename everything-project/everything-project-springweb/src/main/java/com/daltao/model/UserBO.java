package com.daltao.model;

import com.daltao.constant.Gender;
import lombok.Data;

@Data
public class UserBO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Gender gender;
    private String username;
    private String password;
    private String originalPassword;
    private Long created;
    private Long updated;
    private Long deleted;
}
