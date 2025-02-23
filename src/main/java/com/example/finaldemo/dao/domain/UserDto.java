package com.example.finaldemo.dao.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDto {
    private Long id;
    private String userAccount;
    private String userName;
    private String userRole;
    private String userPassword;
    private String salt;
}
