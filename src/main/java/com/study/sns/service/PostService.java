package com.study.sns.service;

import com.study.sns.exception.ErrorCode;
import com.study.sns.exception.SnsApplicationException;
import com.study.sns.model.AlarmArgs;
import com.study.sns.model.AlarmType;
import com.study.sns.model.Comment;
import com.study.sns.model.Post;
import com.study.sns.model.entity.*;
import com.study.sns.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostEntityRepository postEntityRepository;
    private final UserEntityRepository userEntityRepository;
    private final LikeEntityRepository likeEntityRepository;
    private final CommentEntityRepository commentEntityRepository;
    private final AlarmEntityRepository alarmEntityRepository;
    private final AlarmService alarmService;


    /**
     * post를 작성후 생성(db에 저장)하는 메서드
     */
    @Transactional
    public void create(String title, String body, String userName) {
        // 1. 유저가 존재하는지 체크(근데 필터과정에 이미 체크를 한다. 즉 셀렉이 2번 일어난다. -> 최적화 가능한 구역)
        UserEntity userEntity = getUserEntityOrException(userName);
        postEntityRepository.save(PostEntity.of(title, body, userEntity));
    }

    /**
     * 작성한 post를 db에 업데이트시키는 메서드
     */
    @Transactional
    public Post modify(String title, String body, String userName, Integer postId) {
        UserEntity userEntity = getUserEntityOrException(userName);
        PostEntity postEntity = getPostEntityOrException(postId);

        //post permission
        if (postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }

        postEntity.setTitle(title);
        postEntity.setBody(body);

        return Post.fromEntity(postEntityRepository.saveAndFlush(postEntity));
    }

    /**
     * post를 db에서 삭제하는 메서드
     */
    @Transactional
    public void delete(String userName, Integer postId) {
        PostEntity postEntity = getPostEntityOrException(postId);
        UserEntity userEntity = getUserEntityOrException(userName);

        // post permission
        if (postEntity.getUser() != userEntity) {
            throw new SnsApplicationException(ErrorCode.INVALID_PERMISSION, String.format("%s has no permission with %s", userName, postId));
        }
        likeEntityRepository.deleteAllByPost(postEntity);
        commentEntityRepository.deleteAllByPost(postEntity);
        postEntityRepository.delete(postEntity);
    }

    /**
     * 전체 피드 list에 뿌려줄 데이터를 가져오는 메서드
     */
    public Page<Post> list(Pageable pageable) {
        return postEntityRepository.findAll(pageable).map(Post::fromEntity);
    }

    /**
     * 나의 작성 list에 뿌려줄 데이터를 가져오는 메서드
     */
    public Page<Post> my(String userName, Pageable pageable) {
        // 1. 유저가 존재하는지 체크
        UserEntity userEntity = getUserEntityOrException(userName);

        return postEntityRepository.findAllByUser(userEntity, pageable).map(Post::fromEntity);
    }

    /**
     * like 를 db에 저장하는 메서드
     */
    @Transactional
    public void like(Integer postId, String userName) {
        PostEntity postEntity = getPostEntityOrException(postId);
        UserEntity userEntity = getUserEntityOrException(userName);

        // 한유저는 하나의 좋아요만 가능하도록 설계하였으니 이미 좋아요를 했다면 여러번 못하도록 예외를 던진다.
        likeEntityRepository.findByUserAndPost(userEntity, postEntity).ifPresent(it -> {
            throw new SnsApplicationException(ErrorCode.ALREADY_LIKED, String.format("userName %s already like post %d", userName, postId));
        });

        // like save
        likeEntityRepository.save(LikeEntity.of(userEntity, postEntity));
        // alarm save
        AlarmEntity alarmEntity = alarmEntityRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_LIKE_ON_POST, new AlarmArgs(userEntity.getId(), postEntity.getId())));
        // browser에 알람발생했으니 refresh하라고 알려주는 함수실행
        alarmService.send(alarmEntity.getId(), postEntity.getUser().getId());
    }

    /**
     * like count를 가져오는 메서 드
     */
    @Transactional
    public long likeCount(Integer postId) {
        // 1. 포스트 존재를 확인한다.
        PostEntity postEntity = getPostEntityOrException(postId);

        // 2. 포스트에있는 like가 몇개인지 가져온다.
        return likeEntityRepository.countByPost(postEntity);
    }

    /**
     * 댓글달기 기능
     * 알람저장 기능
     */
    @Transactional
    public void comment(Integer postId, String userName, String comment) {
        PostEntity postEntity = getPostEntityOrException(postId);
        UserEntity userEntity = getUserEntityOrException(userName);

        // comment save
        commentEntityRepository.save(CommentEntity.of(userEntity, postEntity, comment));
        // alarm save
        AlarmEntity alarmEntity = alarmEntityRepository.save(AlarmEntity.of(postEntity.getUser(), AlarmType.NEW_COMMENT_ON_POST, new AlarmArgs(userEntity.getId(), postEntity.getId())));
        // browser에 알람발생했으니 refresh하라고 알려주는 함수실행
        alarmService.send(alarmEntity.getId(), postEntity.getUser().getId());
    }

    /**
     * 댓글 가져오기 기능
     */
    public Page<Comment> getComments(Integer postId, Pageable pageable) {
        PostEntity postEntity = getPostEntityOrException(postId);
        return commentEntityRepository.findAllByPost(postEntity, pageable).map(Comment::fromEntity);
    }

    /**
     * 포스트 존재를 체크하는 메서드
     */
    private PostEntity getPostEntityOrException(Integer postId) {

        return postEntityRepository.findById(postId).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.POST_NOT_FOUND, String.format("%s not founded", postId)));
    }

    /**
     * 유저 존재를 체크하는 메서드
     */
    private UserEntity getUserEntityOrException(String userName) {

        return userEntityRepository.findByUserName(userName).orElseThrow(() ->
                new SnsApplicationException(ErrorCode.USER_NOT_FOUND, String.format("%s not founded", userName)));
    }
}
