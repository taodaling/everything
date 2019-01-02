package com.daltao.service.common;

import com.daltao.constant.Gender;
import com.daltao.model.UserBO;
import com.daltao.model.UserDO;
import com.daltao.model.UserTO;
import com.daltao.util.CglibTransfer;
import com.daltao.util.Transfer;
import com.google.common.base.Charsets;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.function.Supplier;

@Service
public class UserTransfer {
    private Transfer<UserDO, UserBO> do2bo = new CglibTransfer<UserDO, UserBO>(UserDO.class, UserBO.class) {
        @Override
        protected UserBO transferNotNull(UserDO src, Supplier<UserBO> supplier) {
            UserBO bo = super.transferNotNull(src, supplier);
            if (src.getGender() != null) {
                bo.setGender(Gender.of((int) src.getGender()));
            } else {
                bo.setGender(null);
            }
            return bo;
        }
    };
    private Transfer<UserBO, UserTO> bo2to = new CglibTransfer<UserBO, UserTO>(UserBO.class, UserTO.class) {
        @Override
        protected UserTO transferNotNull(UserBO src, Supplier<UserTO> supplier) {
            UserTO to = super.transferNotNull(src, supplier);
            if (src.getGender() != null) {
                to.setGender(src.getGender().getName());
            } else {
                to.setGender(null);
            }
            to.setPassword(null);
            return to;
        }
    };
    private Transfer<UserTO, UserBO> to2bo = new CglibTransfer<UserTO, UserBO>(UserTO.class, UserBO.class) {
        @Override
        protected UserBO transferNotNull(UserTO src, Supplier<UserBO> supplier) {
            UserBO bo = super.transferNotNull(src, supplier);
            if (src.getGender() != null) {
                bo.setGender(Gender.of(src.getGender()));
            } else {
                bo.setGender(null);
            }
            if (src.getPassword() != null) {
                bo.setPassword(DigestUtils.md5DigestAsHex(src.getPassword().getBytes(Charsets.UTF_8)));
                bo.setOriginalPassword(src.getPassword());
            }
            return bo;
        }
    };
    private Transfer<UserBO, UserDO> bo2do = new CglibTransfer<UserBO, UserDO>(UserBO.class, UserDO.class) {
        @Override
        protected UserDO transferNotNull(UserBO src, Supplier<UserDO> supplier) {
            UserDO userDO = super.transferNotNull(src, supplier);
            if (src.getGender() != null) {
                userDO.setGender(src.getGender().getId().byteValue());
            } else {
                userDO.setGender(null);
            }
            return userDO;
        }
    };

    public Transfer<UserDO, UserBO> getDo2bo() {
        return do2bo;
    }

    public Transfer<UserBO, UserTO> getBo2to() {
        return bo2to;
    }

    public Transfer<UserTO, UserBO> getTo2bo() {
        return to2bo;
    }

    public Transfer<UserBO, UserDO> getBo2do() {
        return bo2do;
    }
}
