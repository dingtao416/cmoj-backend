package com.cm.cmoj.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;

@TableName(value ="ai_intelligent")
@Data
public class AiIntelligent implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户输入信息
     */
    private String inputMessage;
    /**
     * AI生成的信息
     */
    private String aiResult;

    /**
     * 用户id，标识是哪个用户的信息 可以为Null
     */
    private Long userId;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
