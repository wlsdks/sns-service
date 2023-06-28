package com.study.sns.util;

import java.util.Optional;

// 타입 변환(캐스팅)을 해주는 클래스
public class ClassUtils {

    public static <T> Optional<T> getSafeCastInstance(Object o, Class<T> clazz) {
        return clazz != null && clazz.isInstance(o) ? Optional.of(clazz.cast(o)) : Optional.empty();
    }

}
