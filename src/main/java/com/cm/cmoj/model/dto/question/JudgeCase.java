package com.cm.cmoj.model.dto.question;

import io.swagger.annotations.Api;
import lombok.Data;

@Api
@Data
public class JudgeCase {
    /**
     * 输入用例
     */
    private String input;
    /**
     * 输出用例
     */
    private String output;

}
