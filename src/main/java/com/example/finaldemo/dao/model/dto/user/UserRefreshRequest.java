package com.example.finaldemo.dao.model.dto.user;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserRefreshRequest implements Serializable {
    private String refreshToken;
}
