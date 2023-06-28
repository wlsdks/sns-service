package com.study.sns.repository;

import com.study.sns.model.entity.AlarmEntity;
import com.study.sns.model.entity.PostEntity;
import com.study.sns.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AlarmEntityRepository extends JpaRepository<AlarmEntity, Integer> {
//    Page<AlarmEntity> findAllByUser(UserEntity user, Pageable pageable);

    Page<AlarmEntity> findAllByUserId(Integer userId, Pageable pageable);
}
