package com.cm.cmoj.service;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cm.cmoj.common.BaseResponse;
import com.cm.cmoj.model.entity.AiIntelligent;
import java.util.List;


    public interface AiIntelligentService extends IService<AiIntelligent> {
        /**
         * 调用AI接口，获取推荐的图书信息字符串
         * @param aiIntelligent
         * @return
         */
        BaseResponse<String> getGenResult(AiIntelligent aiIntelligent);

        /**
         * 根据用户ID 获取该用户和AI聊天的最近的五条消息
         * @param userId
         * @return
         */
        BaseResponse<List<AiIntelligent>> getAiInformationByUserId(Long userId);
    }


