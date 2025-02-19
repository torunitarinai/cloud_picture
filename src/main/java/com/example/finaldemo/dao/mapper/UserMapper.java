package com.example.finaldemo.dao.mapper;

import com.example.finaldemo.dao.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
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

    @Insert("insert into user(user_account,user_password,user_role,salt) value (#{user.userAccount},#{user.userPassword},#{user.userRole},#{user.salt})")
    @Options(useGeneratedKeys = true)
    int insert(User user);

    @Select("select id,user_account,user_role,user_password,salt from user where user_account = #{tmpUser.userAccount}")
    User selectByUserAccount(User tmpUser);
}




