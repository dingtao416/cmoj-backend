package com.cm.cmoj.model.dto.questionsubmit;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 题目提交请求
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class QuestionSubmitAddRequest implements Serializable {
    /**
     * 提交的编程语言
     */
    private String language;
    /**
     * 提交人userid
     */
    private Long userid;
    /**
     * 提交代码
     */
    private String code;
    /**
     * 问题id
     */
    private  Long questionId;

}