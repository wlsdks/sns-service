package com.study.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.sns.controller.request.PostCreateRequest;
import com.study.sns.controller.request.UserJoinRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // api테스트
public class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser // 인증된 유저로 요청을 날린다.
    void 포스트작성() throws Exception {
        String title = " title";
        String body = "body";

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        // 주어진 자바객체(PostCreateRequest)를 JSON바이트 배열로 직렬화시킨다.
                        // writeValueAsBytes를 사용하여 JSON 데이터를 바이트 배열로 변환하면, 네트워크 전송이나 데이터 저장에 효율적으로 사용할 수 있다.
                        // .content() 부분은 요청의 본문(content)에 JSON 데이터를 설정하는 부분이다.
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithAnonymousUser // 익명의 유저로 요청을 날린다.
    void 포스트작성시_로그인을_하지않은경우() throws Exception {
        String title = " title";
        String body = "body";

        mockMvc.perform(post("/api/v1/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new PostCreateRequest(title, body)))
                ).andDo(print())
                .andExpect(status().isUnauthorized());
    }
}
