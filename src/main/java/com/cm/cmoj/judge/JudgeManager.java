package com.cm.cmoj.judge;


import com.cm.cmoj.judge.codesandbox.model.JudgeInfo;
import com.cm.cmoj.judge.strategy.DefaultJudgeStrategy;
import com.cm.cmoj.judge.strategy.JavaLanguageJudgeStrategy;
import com.cm.cmoj.judge.strategy.JudgeContext;
import com.cm.cmoj.judge.strategy.JudgeStrategy;
import com.cm.cmoj.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化调用）
 */
@Service
public class JudgeManager {

    /**
     * 执行判题
     *
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext) {
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy = new DefaultJudgeStrategy();
        if ("java".equals(language)) {
            judgeStrategy = new JavaLanguageJudgeStrategy();
        }
        return judgeStrategy.doJudge(judgeContext);
    }

}
