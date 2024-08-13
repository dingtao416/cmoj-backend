package com.cm.cmoj.controller;

import com.cm.cmoj.common.BaseResponse;
import com.cm.cmoj.model.entity.AiIntelligent;
import com.cm.cmoj.service.AiIntelligentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/")
@Slf4j
@Api("题目controller")
public class AiController {
    @Resource
    private AiIntelligentService aiIntelligentService;

    @PostMapping("ai_intelligent")
    @ApiOperation("推荐图书")
    public BaseResponse<String> aiRecommend(@RequestBody AiIntelligent aiIntelligent){
        log.info("aiIntelligent:{}",aiIntelligent);
        return aiIntelligentService.getGenResult(aiIntelligent);
    }
}
