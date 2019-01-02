package com.daltao.api.registration;

import com.daltao.model.UserBO;
import com.daltao.util.Action;
import org.springframework.stereotype.Service;


public interface UserInfoService {
    UserBO register(UserBO userBO);
}
