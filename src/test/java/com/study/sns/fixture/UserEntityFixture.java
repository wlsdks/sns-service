package com.study.sns.fixture;

import com.study.sns.model.entity.UserEntity;

// 테스트용 유저엔티티를 생성해서 반환하는 메서드 생성
public class UserEntityFixture {

    public static UserEntity get(String userName, String password) {
        UserEntity result = new UserEntity();
        result.setId(1);
        result.setUserName(userName);
        result.setPassword(password);

        return result;
    }
}
