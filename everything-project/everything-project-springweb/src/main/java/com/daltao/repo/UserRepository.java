package com.daltao.repo;

import com.daltao.model.UserDO;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserDO, Long> {
    Iterable<UserDO> findAllByUsernameOrEmailOrPhone(String name, String email, String phone);
}
