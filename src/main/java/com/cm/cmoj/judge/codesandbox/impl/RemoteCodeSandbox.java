package com.cm.cmoj.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import com.cm.cmoj.exception.BusinessException;
import com.cm.cmoj.judge.codesandbox.CodeSandbox;
import com.cm.cmoj.common.ErrorCode;
import com.cm.cmoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.cm.cmoj.judge.codesandbox.model.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * 远程代码沙箱（实际调用接口的沙箱）
 */
@Component
public class RemoteCodeSandbox implements CodeSandbox {

    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");
        String url = "http://localhost:8090/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "executeCode remoteSandbox error, message = " + responseStr);
        }
        System.out.println(JSONUtil.toBean(responseStr, ExecuteCodeResponse.class));
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
