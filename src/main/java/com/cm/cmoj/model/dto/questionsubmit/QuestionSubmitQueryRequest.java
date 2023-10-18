package com.cm.cmoj.model.dto.questionsubmit;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.cm.cmoj.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.cache.annotation.EnableCaching;

import java.io.Serializable;
import java.util.Date;
@Data
@EqualsAndHashCode(callSuper = true)
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 题目id
     */
    private Long questionId;
    /**
     * 题目状态
     */
    private Integer status;
    /**
     * 用户id
     */
    private Long userId;


    public static final long serialVersionUID = 1L;
}
