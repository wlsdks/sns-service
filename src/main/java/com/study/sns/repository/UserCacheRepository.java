package com.study.sns.repository;

import com.study.sns.model.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.Optional;

/**
 * 레디스 캐싱설정 리포지토리
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class UserCacheRepository {

    private final RedisTemplate<String, User> userRedisTemplate;
    // 3일동안만 캐시하도록 설정한다.
    private final static Duration USER_CACHE_TTL = Duration.ofDays(3);

    public void setUser(User user) {
        String key = getKey(user.getUsername());
        log.info("Set User to Redis {}:{}", key, user);
        userRedisTemplate.opsForValue().set(key, user, USER_CACHE_TTL);
    }

    // null처리를 쉽게하기위해 Optional로 감싸준다.
    public Optional<User> getUser(String userName) {
        String key = getKey(userName);
        User user = userRedisTemplate.opsForValue().get(key);
        log.info("Get data from Redis {} , {}", key, user);
        return Optional.ofNullable(user);
    }

    private String getKey(String userName) {
        return "USER:" + userName;
    }

    // USER: admin

}
