package com.study.sns.repository;

import com.study.sns.model.entity.PostEntity;
import com.study.sns.model.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostEntityRepository extends JpaRepository<PostEntity, Integer> {

    // 아래처럼 index를 안걸어주면 굉장히 느려진다.
    Page<PostEntity> findAllByUser(UserEntity entity, Pageable pageable);
}
