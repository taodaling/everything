package com.daltao.service.userinfo;

import com.daltao.model.UserBO;
import com.daltao.model.UserDO;
import com.daltao.repo.UserRepository;
import com.daltao.service.common.UserTransfer;
import com.daltao.util.BasicAction;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class RegisterAction extends BasicAction<UserBO, UserBO> {
    @Resource
    UserTransfer userTransfer;
    @Resource
    UserRepository repository;

    @Override
    public UserBO invoke0(UserBO input) {
        UserDO userDO = userTransfer.getBo2do().transfer(input, UserDO::new);
        userDO = repository.save(userDO);
        return userTransfer.getDo2bo().transfer(userDO, UserBO::new);
    }
}
