package com.cm.cmoj.model.dto.question;

import io.swagger.annotations.Api;
import lombok.Data;

@Api
@Data
public class JudgeConfig {

    /**
     * 时间限制
     */
    private Long timeLimit;
    /**
     * 内存限制(KB)
     */
    private Long memoryLimit;
    /**
     * 堆栈限制(KB)
     */
    private Long stackLimit;

}
