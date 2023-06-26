package com.study.sns.service;

import com.study.sns.exception.SnsApplicationException;
import com.study.sns.model.User;
import com.study.sns.model.entity.UserEntity;
import com.study.sns.repository.UserEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;

    //TODO: implement
    public User join(String username, String password) {
        //회원가입하려는 userName으로 회원가입된 user가 있는지
        Optional<UserEntity> userEntity = userEntityRepository.findByUsername(username);

        //회원가입 진행 = user를 등록한다.
        userEntityRepository.save(new UserEntity());

        return new User();
    }

    //TODO: implement > jwt 사용예정 토큰은 String
    public String login(String username, String password) {

        // 회원가입 여부 체크
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> new SnsApplicationException());

        // 비밀번호 체크
        if (!userEntity.getPassword().equals(password)) {
            throw new SnsApplicationException();
        }

        // jwt 토큰 생성

        return "";
    }
}
