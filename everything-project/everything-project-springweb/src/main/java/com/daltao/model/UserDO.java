package com.daltao.model;

import lombok.Data;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;

@Data
@Table(name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "email"),
                @UniqueConstraint(columnNames = "phone"),
                @UniqueConstraint(columnNames = "username"),
        }
)
@Entity
public class UserDO implements Identity<Long> {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
    private String email;
    private String phone;
    private Byte gender;
    private String username;
    private String password;
    private Long created;
    private Long updated;
    private Long deleted;
}
