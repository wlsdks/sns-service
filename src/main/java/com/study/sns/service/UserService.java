package com.study.sns.service;

import com.study.sns.exception.ErrorCode;
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
        userEntityRepository.findByUsername(username).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", username)); // 커스텀 에러코드 추가
        });

        //회원가입 진행 = user를 등록한다.
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(username, password));
        return User.fromEntity(userEntity); //dto로 반환된 결과를 받는다.
    }

    //TODO: implement > jwt 사용예정 토큰은 String
    public String login(String username, String password) {

        // 회원가입 여부 체크
        UserEntity userEntity = userEntityRepository.findByUsername(username).orElseThrow(() -> new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, ""));

        // 비밀번호 체크
        if (!userEntity.getPassword().equals(password)) {
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, "");
        }

        // jwt 토큰 생성

        return "";
    }
}
