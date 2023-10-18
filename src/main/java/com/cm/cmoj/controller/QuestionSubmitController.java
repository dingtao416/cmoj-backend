package com.cm.cmoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cm.cmoj.annotation.AuthCheck;
import com.cm.cmoj.common.BaseResponse;
import com.cm.cmoj.common.ErrorCode;
import com.cm.cmoj.common.ResultUtils;
import com.cm.cmoj.constant.UserConstant;
import com.cm.cmoj.exception.BusinessException;
import com.cm.cmoj.model.dto.question.QuestionQueryRequest;
import com.cm.cmoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.cm.cmoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.cm.cmoj.model.entity.Question;
import com.cm.cmoj.model.entity.QuestionSubmit;
import com.cm.cmoj.model.entity.User;
import com.cm.cmoj.service.QuestionSubmitService;
import com.cm.cmoj.service.QuestionSubmitService;
import com.cm.cmoj.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 题目提交接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/question_submit")
@Slf4j
public class QuestionSubmitController {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;


    @ApiOperation("管理员分页展示数据")
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionSubmit>> listQuestionByPage(@RequestBody QuestionSubmitQueryRequest questionQueryRequest,
                                                           HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionSubmitPage);
    }
    /**
     * 提交题目
     *
     * @param questionSubmitAddRequest
     * @param request
     * @return resultNum 本次题目变化数
     */
    @ApiOperation("用户提交题目")
    @PostMapping("/")
    public BaseResponse<Integer> doThumb(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
            HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交题目
        final User loginUser = userService.getLoginUser(request);
        long postId = questionSubmitAddRequest.getQuestionId();
        //提交的题目记录id
        long questionSubmitId = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(null);
    }

}
