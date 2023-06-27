package com.study.sns.fixture;

import com.study.sns.model.entity.PostEntity;
import com.study.sns.model.entity.UserEntity;

// 테스트용 포스트엔티티를 생성해서 반환하는 메서드 생성
public class PostEntityFixture {

    public static PostEntity get(String userName, Integer postId, Integer userId) {
        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setUserName(userName);

        PostEntity result = new PostEntity();
        result.setUser(user);
        result.setId(postId);
        return result;
    }
}
