package com.study.sns.service;

import com.study.sns.exception.ErrorCode;
import com.study.sns.exception.SnsApplicationException;
import com.study.sns.model.Alarm;
import com.study.sns.model.User;
import com.study.sns.model.entity.UserEntity;
import com.study.sns.repository.AlarmEntityRepository;
import com.study.sns.repository.UserCacheRepository;
import com.study.sns.repository.UserEntityRepository;
import com.study.sns.util.JwtTokenUtils;
import io.jsonwebtoken.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserEntityRepository userEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final BCryptPasswordEncoder encoder;
    private final UserCacheRepository userCacheRepository;

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expired-time-ms}")
    private Long expiredTimeMs;

    /**
     * 1.캐시를 통해 유저정보를 가져온다.
     * 2.없으면 findByUserName으로 조회해서 가져온다.
     * 3.그래도없으면 예외를 던진다.
     */
    public User loadUserByUserName(String userName) {
        // 만약 캐시에 user가없으면 orElseGet을 통해 가져온다.
        return userCacheRepository.getUser(userName).orElseGet(() ->
                userEntityRepository.findByUserName(userName).map(User::fromEntity).orElseThrow(() ->
                        new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName))
                ));
    }

    //join을 하다 exception이 발생하면 rollback이 된다.
    @Transactional
    public User join(String userName, String password) {
        //회원가입하려는 userName으로 회원가입된 user가 있는지 조회한다.
        userEntityRepository.findByUserName(userName).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s is duplicated", userName)); // 커스텀 에러코드 추가
        });

        //회원가입 진행 = user를 등록한다. // password는 encoder를 통해 암호화해서 저장하도록 한다.
        UserEntity userEntity = userEntityRepository.save(UserEntity.of(userName, encoder.encode(password)));
        return User.fromEntity(userEntity); //dto로 반환된 결과를 받는다.
    }

    public String login(String userName, String password) {

        // 회원가입 여부를 체크(캐시체크)하고 이후 캐시에 유저를 세팅하는 처리를 한다.
        User user = loadUserByUserName(userName);
        userCacheRepository.setUser(user);

        // 비밀번호 체크
        if (!encoder.matches(password, user.getPassword())) {
            throw new SnsApplicationException(ErrorCode.INVALID_PASSWORD);
        }

        // jwt 토큰 생성
        return JwtTokenUtils.generateToken(userName, secretKey, expiredTimeMs);
    }

    // 알람리스트를 반환
    public Page<Alarm> alarmList(Integer userId, Pageable pageable) {
        return alarmEntityRepository.findAllByUserId(userId, pageable).map(Alarm::fromEntity);
    }
}
