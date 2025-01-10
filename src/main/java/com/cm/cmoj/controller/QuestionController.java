package com.cm.cmoj.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cm.cmoj.annotation.AuthCheck;
import com.cm.cmoj.common.BaseResponse;
import com.cm.cmoj.common.DeleteRequest;
import com.cm.cmoj.common.ErrorCode;
import com.cm.cmoj.common.ResultUtils;
import com.cm.cmoj.constant.UserConstant;
import com.cm.cmoj.exception.BusinessException;
import com.cm.cmoj.exception.ThrowUtils;
import com.cm.cmoj.model.dto.question.*;
import com.cm.cmoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.cm.cmoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.cm.cmoj.model.entity.Question;
import com.cm.cmoj.model.entity.QuestionSubmit;
import com.cm.cmoj.model.entity.User;
import com.cm.cmoj.model.vo.QuestionSubmitVO;
import com.cm.cmoj.model.vo.QuestionVO;
import com.cm.cmoj.service.QuestionService;
import com.cm.cmoj.service.QuestionSubmitService;
import com.cm.cmoj.service.UserService;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 问题接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/question")
@Slf4j
@Api("题目controller")
public class QuestionController {

    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private UserService userService;

    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @ApiOperation("添加题目")
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCase();
        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        if (judgeCase!=null ){
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        BeanUtils.copyProperties(questionAddRequest, question);
        List<String> tags = questionAddRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        questionService.validQuestion(question, true);
        User loginUser = userService.getLoginUser(request);
        question.setUserId(loginUser.getId());
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @ApiOperation("删除题目")
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param questionUpdateRequest
     * @return
     */
    @ApiOperation("修改题目")
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        List<String> tags = questionUpdateRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionUpdateRequest.getJudgeCase();
        for (JudgeCase judge: judgeCase) {
            System.out.println(judge.getInput());
        }
        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        if (judgeCase!=null ){
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        question.setDifficulty(questionUpdateRequest.getDifficulty());
        // 参数校验
        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }
    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @ApiOperation("管理员分页展示数据")
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                   HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }
    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @ApiOperation("根据题目id获取题目")
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }
    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @ApiOperation("根据题目id获取题目")
    @GetMapping("/get")
    public BaseResponse<Question> getQuestionById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        User loginuser = userService.getLoginUser(request);
        if(!loginuser.getId().equals(question.getUserId())&& userService.isAdmin(loginuser))
        {

            return new BaseResponse(ErrorCode.PARAMS_ERROR);
        }
        return ResultUtils.success(question);
    }
    @ApiOperation("获取随机题目")
    @GetMapping("/getRangeQuestion")
    public BaseResponse<QuestionVO> getQuestionById(HttpServletRequest request) {
        long totalQuestions = questionService.count();
        if (totalQuestions == 0) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        int randomPage = (int) (Math.random() * (totalQuestions / 1)) + 1; // assuming 1 question per page for simplicity
        Page<Question> page = new Page<>(randomPage, 1);
        List<Question> questions = questionService.page(page).getRecords();
        if (questions.isEmpty()) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        QuestionVO questionVO = QuestionVO.objToVo(questions.get(0));
        questionVO.setUserVO(userService.getUserVO(userService.getLoginUser(request)));
        return ResultUtils.success(questionVO);
    }
    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @ApiOperation("分页获取题目")
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        Page<QuestionVO> questionVOPage = questionService.getQuestionVOPage(questionPage, request);
        return ResultUtils.success(questionVOPage);
    }
    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @ApiOperation("分页获取当前用户创建的资源列表")
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
            HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    // endregion


    /**
     * 编辑（用户）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @ApiOperation("编辑用户")
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        List<String> tags = questionEditRequest.getTags();
        if (tags != null) {
            question.setTags(GSON.toJson(tags));
        }
        List<JudgeCase> judgeCase = questionEditRequest.getJudgeCase();
        for (JudgeCase judge: judgeCase) {
            System.out.println(judge.getInput());
        }
        JudgeConfig judgeConfig = questionEditRequest.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(GSON.toJson(judgeConfig));
        }
        if (judgeCase!=null ){
            question.setJudgeCase(GSON.toJson(judgeCase));
        }
        // 参数校验
        questionService.validQuestion(question, false);
        User loginUser = userService.getLoginUser(request);
        long id = questionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }


    /**
     * 除了管理员，只有自己和管理员可以看到自己提交的代码
     *
     */
    @ApiOperation("管理员分页展示数据")
    @PostMapping("/question_submit/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionByPage(@RequestBody QuestionSubmitQueryRequest questionQueryRequest,
                                                                   HttpServletRequest request) {
        log.info("listQuestionByPage questionQueryRequest:" + questionQueryRequest.toString());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionQueryRequest));
        final User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage,loginUser));
    }

    /**
     * 用户提交题目
     */
    @ApiOperation("用户提交题目")
    @PostMapping("/question_submit/do")
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
