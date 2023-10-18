package com.cm.cmoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.cmoj.common.ErrorCode;
import com.cm.cmoj.constant.CommonConstant;
import com.cm.cmoj.exception.BusinessException;
import com.cm.cmoj.model.dto.question.QuestionQueryRequest;
import com.cm.cmoj.model.dto.questionsubmit.JudgeInfo;
import com.cm.cmoj.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.cm.cmoj.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.cm.cmoj.model.entity.Question;
import com.cm.cmoj.model.entity.QuestionSubmit;
import com.cm.cmoj.model.entity.User;
import com.cm.cmoj.model.enums.QuestionSubmitLanguageEnum;
import com.cm.cmoj.model.enums.QuestionSubmitStatusEnum;
import com.cm.cmoj.model.vo.QuestionSubmitVO;
import com.cm.cmoj.model.vo.QuestionVO;
import com.cm.cmoj.model.vo.UserVO;
import com.cm.cmoj.service.QuestionService;
import com.cm.cmoj.service.QuestionSubmitService;
import com.cm.cmoj.mapper.QuestionSubmitMapper;
import com.cm.cmoj.service.UserService;
import com.cm.cmoj.utils.SqlUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
    implements QuestionSubmitService{

    @Resource
    private QuestionService questionService;
    @Resource
    private UserService userService;
    /**
     * 提交题目
     *
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAd, User loginUser) {
        //获取问题id 根据问题id查询id
        Long questionId = questionSubmitAd.getQuestionId();
        Question question = questionService.getById(questionId);
        //获取
        String language = questionSubmitAd.getLanguage();
        QuestionSubmitLanguageEnum enumByValue = QuestionSubmitLanguageEnum.getEnumByValue(language);
        //判断编程语言是否合法
        if (enumByValue == null) {
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"编程语言错误");
        }
        //判断请求数据中question是否合法
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 是否已提交题目
        //获取登录用户id
        long userId = loginUser.getId();
        //设置返回数据的属性值 并插入到数据库中
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setUserId(userId);
        questionSubmit.setCode(questionSubmitAd.getCode());
        questionSubmit.setLanguage(language);
        //Todo 初始化状态
//        JudgeInfo judgeInfo = new JudgeInfo();
//        judgeInfo.setMessage("1");
//        judgeInfo.setMemory(1L);
//        judgeInfo.setTime(1L);
        questionSubmit.setJudgeInfo("{}");

        questionSubmit.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        //插入数据
        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR,"数据插入失败");
        }
        //返回提交题目记录的id
        return questionSubmit.getId();
    }

    /**
     * 获取查询包装类
     *
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        Integer status = questionSubmitQueryRequest.getStatus();
        Long userId = questionSubmitQueryRequest.getUserId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq("isDelete", false);
        queryWrapper.eq(QuestionSubmitStatusEnum.getEnumByValue(status)!=null, "status", status);

        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     *
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        if(loginUser.getId().equals(questionSubmit.getUserId())&& loginUser.getUserRole().equals("admin"))
        {
            questionSubmitVO.setCode(null);
        }
        return questionSubmitVO;
    }

    /**
     * 原始分页数据转换为VO分页数据
     */
    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        //获取分页数据的每一条list
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        //把分页的条数等固定数据set到Vo中
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        //检测分页数据是否为空
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        // 遍历填充信息
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit,loginUser))
                .collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }



}




