package com.daltao.service.userinfo;

import com.daltao.api.registration.UserInfoService;
import com.daltao.exception.InvalidInputException;
import com.daltao.model.UserBO;
import com.daltao.model.UserDO;
import com.daltao.repo.UserRepository;
import com.daltao.service.common.UserTransfer;
import com.daltao.util.Action;
import com.daltao.util.BasicAction;
import com.daltao.util.BasicActionListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.regex.Pattern;

@Service
public class UserInfoServiceImpl implements UserInfoService {
    @Resource
    private UserRepository userRepository;
    @Resource
    private UserTransfer userTransfer;
    @Resource
    private TransactionTemplate transactionTemplate;

    private static final Pattern
            EMAIL_PATTERN = Pattern.compile("^([A-Za-z0-9_\\-\\.])+\\@([A-Za-z0-9_\\-\\.])+\\.([A-Za-z]{2,4})$");
    private static final Pattern
            PHONE_PATTERN = Pattern.compile("[\\d-]{1,32}");
    private static final Pattern
            USERNAME_PATTERN = Pattern.compile("[a-zA-Z0-9_-]{4,16}");
    private static final Pattern
            PASSWORD_PATTERN = Pattern.compile("^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z]{6,21}$");
    private static final Pattern
            NAME_PATTERN = Pattern.compile("\\S{2,32}");

    private Action<UserBO, UserBO> registerAction = new BasicAction<UserBO, UserBO>() {
        @Override
        public UserBO invoke0(UserBO input) {
            UserDO userDO = userTransfer.getBo2do().transfer(input, UserDO::new);
            userDO = userRepository.save(userDO);
            return userTransfer.getDo2bo().transfer(userDO, UserBO::new);
        }
    }.addListener(new BasicActionListener<UserBO, UserBO>() {
        @Override
        public void preAction(Action<UserBO, UserBO> action, UserBO param) {
            if (param.getEmail() == null) {
                InvalidInputException.missingField("email").throwSelf();
            }
            if (param.getName() == null) {
                InvalidInputException.missingField("name").throwSelf();
            }
            if (param.getPhone() == null) {
                InvalidInputException.missingField("phone").throwSelf();
            }
            if (param.getUsername() == null) {
                InvalidInputException.missingField("username").throwSelf();
            }
            if (param.getOriginalPassword() == null) {
                InvalidInputException.missingField("password").throwSelf();
            }
        }
    }).addListener(new BasicActionListener<UserBO, UserBO>() {
        @Override
        public void preAction(Action<UserBO, UserBO> action, UserBO param) {
            if (!EMAIL_PATTERN.matcher(param.getEmail()).matches()) {
                InvalidInputException.invalidField("email").throwSelf();
            }
            if (!PHONE_PATTERN.matcher(param.getPhone()).matches()) {
                InvalidInputException.invalidField("phone").throwSelf();
            }
            if (!USERNAME_PATTERN.matcher(param.getUsername()).matches()) {
                InvalidInputException.invalidField("username").throwSelf();
            }
            if (!PASSWORD_PATTERN.matcher(param.getOriginalPassword()).matches()) {
                InvalidInputException.invalidField("password").throwSelf();
            }
            if (!NAME_PATTERN.matcher(param.getName()).matches()) {
                InvalidInputException.invalidField("name").throwSelf();
            }
        }
    }).addListener(new BasicActionListener<UserBO, UserBO>() {
        @Override
        public void preAction(Action<UserBO, UserBO> action, UserBO param) {
            //check uniqueness
            Iterable<UserDO> collision = userRepository.findAllByUsernameOrEmailOrPhone(param.getUsername(),
                    param.getEmail(),
                    param.getPhone());

            for (UserDO userDO : collision) {
                if (userDO.getUsername().equals(param.getUsername())) {
                    InvalidInputException.collisionField("username").throwSelf();
                }
                if (userDO.getPhone().equals(param.getPhone())) {
                    InvalidInputException.collisionField("phone").throwSelf();
                }
                if (userDO.getEmail().equals(param.getEmail())) {
                    InvalidInputException.collisionField("email").throwSelf();
                }
            }
        }
    }).addListener(new BasicActionListener<UserBO, UserBO>() {
        @Override
        public void preAction(Action<UserBO, UserBO> action, UserBO param) {
            param.setCreated(System.currentTimeMillis());
            param.setDeleted(0L);
            param.setUpdated(param.getCreated());
        }
    });

    @PostConstruct
    public void init() {
    }

    @Override
    public UserBO register(UserBO userBO) {
        return registerAction.invoke(userBO);
    }

}
