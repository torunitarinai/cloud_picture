package com.example.finaldemo.dao.mapper;

import com.example.finaldemo.dao.domain.Picture;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PictureMapper {
    @Select("select count(*) from picture where id=#{pictureId} and is_delete = 0")
    long count(long pictureId);

    @Insert("insert into picture(url, name, pic_size, pic_width, pic_height, pic_scale, pic_format, user_id, is_delete) value " +
            "(#{url},#{name},#{picSize},#{picWidth},#{picHeight},#{picScale},#{picFormat},#{account},0)")
    int insert(Picture picture);
}
