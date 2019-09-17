package cn.xue.service.user;

import cn.xue.model.user.User;
import cn.xue.repo.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    public User getUserById(Long id) {
        Optional<User> userData = userRepository.findById(id);
        return userData.orElse(null);
    }
    public User saveUser(User user) {
        return userRepository.save(user);
    }
}
