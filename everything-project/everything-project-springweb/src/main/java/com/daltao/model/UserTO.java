package com.daltao.model;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
public class UserTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String gender;
    private String username;
    private String password;
    private Long created;
    private Long updated;
    private Long deleted;
}
