package com.cm.cmoj.service.impl;

import cn.hutool.core.date.StopWatch;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.cm.cmoj.common.BaseResponse;
import com.cm.cmoj.common.ErrorCode;
import com.cm.cmoj.exception.BusinessException;
import com.cm.cmoj.manager.model.SparkMessage;
import com.cm.cmoj.manager.model.SparkSyncChatResponse;
import com.cm.cmoj.manager.model.request.SparkRequest;
import com.cm.cmoj.manager.model.response.SparkTextUsage;
import com.cm.cmoj.model.entity.AiIntelligent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * author DingTao
 * Date 2024/9/15 10:25
 */
public class testService extends AiIntelligentServiceImpl{
    @Override
    public BaseResponse<String> getGenResult(AiIntelligent aiIntelligent) {
        return new BaseResponse<>(200, "success");
    }


}
