package com.example.finaldemo.dao.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
* @author zx
* @description 针对表【user(用户表)】的数据库操作Mapper
* @createDate 2025-02-16 17:12:08
*/
@Mapper
public interface UserMapper {
    @Select("select count(*) from user where user_account=#{userAccount}")
    long countByAccount(String userAccount);
}




