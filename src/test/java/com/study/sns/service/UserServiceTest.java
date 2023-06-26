package com.study.sns.service;

import com.study.sns.exception.SnsApplicationException;
import com.study.sns.fixture.UserEntityFixture;
import com.study.sns.model.entity.UserEntity;
import com.study.sns.repository.UserEntityRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService userService;

    @MockBean
    private UserEntityRepository userEntityRepository;


    @Test
    void 회원가입이_정상적으로_동작하는경우() {
        String userName = "userName";
        String password = "password";

        //mocking
        when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.empty()); //회원가입이 된적이 없으니 empty여야한다.
        when(userEntityRepository.save(any())).thenReturn(Optional.of(UserEntityFixture.get(userName, password)));

        Assertions.assertDoesNotThrow(() -> userService.join(userName, password));
    }

    @Test
    void 회원가입시_userName으로_가입한유저가_이미_있는경우() {
        String userName = "userName";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(userName, password);

        //mocking
        when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(fixture));
        when(userEntityRepository.save(any())).thenReturn(Optional.of(fixture));

        Assertions.assertThrows(SnsApplicationException.class, () -> userService.join(userName, password));
    }

    @Test
    void 로그인이_정상적으로_동작하는경우() {
        String userName = "userName";
        String password = "password";

        UserEntity fixture = UserEntityFixture.get(userName, password);
        //mocking
        when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(fixture));

        //로그인시 정상동작이면 error을 throw하면 안된다.
        Assertions.assertDoesNotThrow(() -> userService.login(userName, password));
    }

    @Test
    void 로그인시_userName으로_가입한유저가_이미_있는경우() {
        String userName = "userName";
        String password = "password";

        //mocking
        when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.empty());

        Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, password));
    }

    @Test
    void 로그인시_패스워드가_틀린_경우() {
        String userName = "userName";
        String password = "password";
        String wrongPassword = "wrongPassword";

        UserEntity fixture = UserEntityFixture.get(userName, password);

        //mocking
        when(userEntityRepository.findByUsername(userName)).thenReturn(Optional.of(fixture));

        Assertions.assertThrows(SnsApplicationException.class, () -> userService.login(userName, wrongPassword));
    }
}
