package com.cm.cmoj.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cm.cmoj.model.entity.QuestionSubmit;
import org.apache.ibatis.annotations.Mapper;

/**
* @author 86166
* @description 针对表【question_submit(题目提交)】的数据库操作Mapper
* @createDate 2023-09-16 19:17:14
* @Entity com.cm.cmoj.model.entity.QuestionSubmit
*/
@Mapper
public interface QuestionSubmitMapper extends BaseMapper<QuestionSubmit> {

}




