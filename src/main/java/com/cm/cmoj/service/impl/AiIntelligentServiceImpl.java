package com.cm.cmoj.service.impl;

import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cm.cmoj.common.BaseResponse;
import com.cm.cmoj.common.ErrorCode;
import com.cm.cmoj.exception.BusinessException;
import com.cm.cmoj.manager.SparkClient;
import com.cm.cmoj.manager.model.SparkMessage;
import com.cm.cmoj.manager.model.SparkSyncChatResponse;
import com.cm.cmoj.manager.model.request.SparkRequest;
import com.cm.cmoj.manager.model.response.SparkTextUsage;
import com.cm.cmoj.mapper.AiIntelligentMapper;
import com.cm.cmoj.model.entity.AiIntelligent;
import com.cm.cmoj.service.AiIntelligentService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * @author xiaobaitiao
 * @description 针对表【t_ai_intelligent】的数据库操作Service实现
 * @createDate 2023-08-27 18:44:26
 */
@Service
public class AiIntelligentServiceImpl extends ServiceImpl<AiIntelligentMapper, AiIntelligent>
        implements AiIntelligentService {


    @Resource
    @Lazy
    private AiIntelligentService aiIntelligentService;
    /**
     * 客户端实例，线程安全
     */
    SparkClient sparkClient = new SparkClient();

    // todo 图书管理系统 1.2 版本设置认证信息 讯飞星火
    {
        sparkClient.appid = "16bb66e3";
        sparkClient.apiKey = "b9fb3e4b5d20fce542e8eb4c6fb08a1f";
        sparkClient.apiSecret = "YjYzYzk0MGJmMzdjNzI0NzIzZTU3ZTM1";
    }
    @Override
    public BaseResponse<String> getGenResult(AiIntelligent aiIntelligent) {
        // 判断用户输入文本是否过长，超过128字，直接返回，防止资源耗尽
        String message = aiIntelligent.getInputMessage();
        if(StringUtils.isBlank(message)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"文本不能为空");
        }
        if (message.length() > 12800) {
            throw  new BusinessException(ErrorCode.PARAMS_ERROR,"文本字数过长");
        }
        Long user_id = aiIntelligent.getUserId();
        StringBuilder stringBuilder = new StringBuilder();
        HashSet<String> hashSet = new HashSet<>();
        String presetInformation = "我将询问你有关代码的相关问题，请阅读问题并给出回答：";
        stringBuilder.append(presetInformation).append(message);
//        list.forEach(System.out::println);
//        System.out.println(stringBuilder.toString());
        // 发送请求给AI，进行对话 由讯飞星火模型切换为阿里AI模型
        // 超时判断 利用ExecutorService
        // 调用之前先获取该用户最近的五条历史记录
//        BaseResponse<List<AiIntelligent>> history = aiIntelligentService.getAiInformationByUserId(user_id);
//        List<AiIntelligent> historyData = history.getData();
//
//        historyData.forEach(item->{
//            messages.add(SparkMessage.userContent(item.getInputMessage()));
//            messages.add(SparkMessage.assistantContent(item.getAiResult()));
//        });
        String response;
        List<SparkMessage> messages = new ArrayList<>();
        ExecutorService executor = Executors.newSingleThreadExecutor();
        messages.add(SparkMessage.userContent(stringBuilder.toString()));
        // 构造请求
        SparkRequest sparkRequest = SparkRequest.builder()
                // 消息列表
                .messages(messages)
                // 模型回答的tokens的最大长度,非必传，默认为2048。
                // V1.5取值为[1,4096]
                // V2.0取值为[1,8192]
                // V3.0取值为[1,8192]
                .maxTokens(2048)
                // 核采样阈值。用于决定结果随机性,取值越高随机性越强即相同的问题得到的不同答案的可能性越高 非必传,取值为[0,1],默认为0.5
                .temperature(0.2)
                .build();
        int timeout = 25; // 超时时间，单位为秒
        Future<String> future = executor.submit(() -> {
            try {
                // 同步调用
                StopWatch stopWatch = new StopWatch();
                stopWatch.start();
                SparkSyncChatResponse chatResponse = sparkClient.chatSync(sparkRequest);
                SparkTextUsage textUsage = chatResponse.getTextUsage();
                stopWatch.stop();
                long total = stopWatch.getTotal(TimeUnit.SECONDS);
                System.out.println("本次接口调用耗时:"+total+"秒");
                System.out.println("\n回答：" + chatResponse.getContent());
                System.out.println("\n提问tokens：" + textUsage.getPromptTokens()
                        + "，回答tokens：" + textUsage.getCompletionTokens()
                        + "，总消耗tokens：" + textUsage.getTotalTokens());
                return chatResponse.getContent();
//                return AlibabaAIModel.doChatWithHistory(stringBuilder.toString(),recentHistory);
            } catch (Exception exception) {
                throw new RuntimeException("遇到异常");
            }
        });

        try {
            response = future.get(timeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new RuntimeException("遇到异常");
        }
//        // 关闭ExecutorService
        executor.shutdown();
        // 得到消息
        System.out.println(response);
        AiIntelligent saveResult = new AiIntelligent();
        saveResult.setInputMessage(aiIntelligent.getInputMessage());
        saveResult.setAiResult(response);
        saveResult.setUserId(user_id);
        return new BaseResponse<>(200,response,"获取AI推荐信息成功");
    }

    @Override
    public BaseResponse<List<AiIntelligent>> getAiInformationByUserId(Long userId) {
        List<AiIntelligent> dataList = new ArrayList<>();
        AiIntelligent data1 = new AiIntelligent();
        data1.setId(1L);
        data1.setInputMessage("Input message 1");
        data1.setAiResult("AI result 1");
        data1.setUserId(1001L);
        data1.setCreateTime("2024-04-30 10:00:00");
        data1.setUpdateTime("2024-04-30 10:00:00");
        dataList.add(data1);
        AiIntelligent data2 = new AiIntelligent();
        data2.setId(2L);
        data2.setInputMessage("Input message 2");
        data2.setAiResult("AI result 2");
        data2.setUserId(1002L);
        data2.setCreateTime("2024-04-30 11:00:00");
        data2.setUpdateTime("2024-04-30 11:00:00");
        dataList.add(data2);
        BaseResponse<List<AiIntelligent>> BaseResponse;
        return new BaseResponse<>(200,dataList,"获取AI历史信息成功");
    }


}




