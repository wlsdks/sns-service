package com.study.sns.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class AlarmArgs {

    // 알람을 발생시킨 사람
    private Integer fromUserId;
    // 알람을 발생시킨 주체의 아이디
    private Integer targetId;

}
