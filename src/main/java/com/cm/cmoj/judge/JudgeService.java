package com.cm.cmoj.judge;


import com.cm.cmoj.model.entity.QuestionSubmit;

/**
 * 判题服务
 */
public interface JudgeService {


    QuestionSubmit doJudge(long questionSubmitId);
}
