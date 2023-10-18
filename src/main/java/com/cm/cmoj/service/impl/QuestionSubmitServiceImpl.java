package com.cm.cmoj.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.cmoj.common.ErrorCode;
import com.cm.cmoj.constant.CommonConstant;
import com.cm.cmoj.exception.BusinessException;
import com.cm.cmoj.model.dto.question.QuestionQueryRequest;
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

/**
* @author 86166
* @description 针对表【question_submit(题目提交)】的数据库操作Service实现
* @createDate 2023-09-16 19:17:14
*/
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
     * @param  questionSubmitAd
     * @param loginUser
     * @return
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
     * @param questionSubmitQueryRequest
     * @return
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
    
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, HttpServletRequest request) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        long questionSubmitId = questionSubmit.getId();
        // 1. 关联查询用户信息
        Long userId = questionSubmit.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        questionSubmitVO.setUserId(userId);
        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, HttpServletRequest request) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();
        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = questionSubmitList.stream().map(QuestionSubmit::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream().map(questionSubmit -> {
            QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
            Long userId = questionSubmit.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            return questionSubmitVO;
        }).collect(Collectors.toList());
        questionSubmitVOPage.setRecords(questionSubmitVOList);
        return questionSubmitVOPage;
    }

    /**
     * 封装了事务的方法
     *
     * @param userId
     * @param questionId
     * @return
     */
//    @Override
//    @Transactional(rollbackFor = Exception.class)
//    public int doQuestionSubmitInner(long userId, long questionId) {
//        QuestionSubmit questionSubmit = new QuestionSubmit();
//        questionSubmit.setUserId(userId);
//        questionSubmit.setQuestionId(questionId);
//        QueryWrapper<QuestionSubmit> questionQueryWrapper = new QueryWrapper<>(questionSubmit);
//        QuestionSubmit oldQuestionSubmit = this.getOne(questionQueryWrapper);
//        boolean result;
//        // 已提交题目
//        if (oldQuestionSubmit != null) {
//            result = this.remove(questionQueryWrapper);
//            if (result) {
//                // 提交题目数 - 1
//                result = questionService.update()
//                        .eq("id", questionId)
//                        .gt("thumbNum", 0)
//                        .setSql("thumbNum = thumbNum - 1")
//                        .update();
//                return result ? -1 : 0;
//            } else {
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
//            }
//        } else {
//            // 未提交题目
//            result = this.save(questionSubmit);
//            if (result) {
//                // 提交题目数 + 1
//                result = questionService.update()
//                        .eq("id", questionId)
//                        .setSql("thumbNum = thumbNum + 1")
//                        .update();
//                return result ? 1 : 0;
//            } else {
//                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
//            }
//        }
//    }
}




