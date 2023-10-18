package com.cm.cmoj.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cm.cmoj.model.dto.question.QuestionQueryRequest;
import com.cm.cmoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.cm.cmoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.cm.cmoj.model.entity.Question;
import com.cm.cmoj.model.entity.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cm.cmoj.model.entity.User;
import com.cm.cmoj.model.vo.QuestionSubmitVO;
import com.cm.cmoj.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author 86166
* @description 针对表【question_submit(题目提交)】的数据库操作Service
* @createDate 2023-09-16 19:17:14
*/
public interface QuestionSubmitService extends IService<QuestionSubmit> {
    /**
     * 点赞
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser);

    /**
     * 帖子点赞（内部服务）
     *
     * @param userId
     * @param questionId
     * @return
     */
//    int doQuestionSubmitInner(long userId, long questionId);
    /**
     * 获取查询条件
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest);
    /**
     * 获取帖子封装
     *
     * @param  questionSubmit
     * @param request
     * @return
     */
    QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param  questionSubmitPage
     * @param request
     * @return
     */
    Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, HttpServletRequest request);
}
