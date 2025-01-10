package com.cm.cmoj.model.vo;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.cm.cmoj.model.dto.question.JudgeConfig;
import com.cm.cmoj.model.entity.Question;
import com.google.gson.Gson;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 帖子视图
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Data
public class QuestionVO implements Serializable {
    private final static Gson GSON = new Gson();
    /**
     * id
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;
    /**
     * 难度
     */
    private String difficulty;
    /**
     * 标签列表（json 数组）
     */
    private List<String> tags;
    /**
     * 提交数量
     */
    private Integer submitNum;
    /**
     * 通过数
     */
    private Integer acceptedNum;

    /**
     * 创建用户id
     */
    private Long userId;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建时间
     */
    private Date updateTime;
    /**
     * 判题配置 （json数组）
     */
    private JudgeConfig judgeConfig;
    /**
     * 用户信息
     */
    private UserVO userVO;

    /**
     * 包装类转对象
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        //question里面tags为String vo里面为List数组
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);
        List<String> tagList = questionVO.getTags();
        if (tagList != null) {
            question.setTags(JSONUtil.toJsonStr(tagList));
        }
        JudgeConfig judgeConfig = questionVO.getJudgeConfig();
        if (judgeConfig != null) {
            question.setJudgeConfig(JSONUtil.toJsonStr(judgeConfig));
        }


        return question;
    }

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    /**
     * 对象转VO
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        questionVO.setTags(JSONUtil.toList(question.getTags(),String.class));
        String judgeConfig = question.getJudgeConfig();
        questionVO.setJudgeConfig(JSONUtil.toBean(judgeConfig,JudgeConfig.class));
        return questionVO;
    }
}
