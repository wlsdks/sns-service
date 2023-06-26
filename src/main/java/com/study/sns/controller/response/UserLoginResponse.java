package com.study.sns.controller.response;

import com.study.sns.model.User;
import com.study.sns.model.UserRole;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserLoginResponse {

    private String token;

}
