package com.cm.cmoj.controller;


import com.cm.cmoj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.cm.cmoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.cm.cmoj.judge.codesandbox.model.ExecuteCodeResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
@RestController("/")
public class QuestionJudgeController {
  @Resource
    private RemoteCodeSandbox remoteCodeSandbox;
    @PostMapping("/executeCode")
    public ExecuteCodeResponse executeCode(@RequestBody ExecuteCodeRequest request){
        ExecuteCodeResponse executeCodeResponse = remoteCodeSandbox.executeCode(request);
        return executeCodeResponse;
    }
}
