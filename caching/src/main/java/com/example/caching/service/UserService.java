package com.example.caching.service;

import com.example.caching.entity.Users;
import com.example.caching.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final String USER_NOT_FOUND_ERROR_MESSAGE = "کاربری پیدا نشد";
    private final UserRepository userRepository;
    private final PasswordService passwordService;

    public Users login(String username, String password, String scope) throws NoSuchUserFound {
        var user = findActiveUserByUsernameAndScope(username, scope);
        checkCorrectPassword(user, password, "نام کاربری یا پسورد اشتباه است");
        return user;
    }

    private void checkCorrectPassword(Users user, String password, String message) throws NoSuchUserFound {
        var encryptedPassword = passwordService.getEncryptedPassword(password, user.getSalt());
        if (!Arrays.equals(user.getPassword(), encryptedPassword)) {
            throw new NoSuchUserFound(message);
        }
    }

    private Users findActiveUserByUsernameAndScope(String username, String scope) throws NoSuchUserFound {
        return userRepository.findUserByUsernameAndScopeAndDeletedAndDisabled(username.toLowerCase(), scope, false, false)
                .orElseThrow(() -> new NoSuchUserFound(USER_NOT_FOUND_ERROR_MESSAGE));
    }
}
