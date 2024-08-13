package com.cm.cmoj.judge.codesandbox.impl;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;

import com.cm.cmoj.exception.BusinessException;
import com.cm.cmoj.judge.codesandbox.CodeSandbox;
import com.cm.cmoj.common.ErrorCode;
import com.cm.cmoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.cm.cmoj.judge.codesandbox.model.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 远程代码沙箱（实际调用接口的沙箱）
 */
@Component
@Slf4j
public class RemoteCodeSandbox implements CodeSandbox {
    public static void main(String[] args) {
        HashMap<String, String> map = new HashMap<>();
        map.put("chenmo", "沉默");
        map.put("wanger", "王二");
        map.put("chenqingyang", "陈清扬");
        map.put("xiaozhuanling", "小转铃");
        map.put("fangxiaowan", "方小婉");

// 遍历 HashMap
        for (String key : map.keySet()) {
            int h, n = 16;
            int hash = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
            int i =hash & (n-1);
            // 打印 key 的 hash 值 和 索引 i
            System.out.println(key + "的hash值 : " + hash +" 的索引 : " + i);
        }
    }


    // 定义鉴权请求头和密钥
    private static final String AUTH_REQUEST_HEADER = "auth";

    private static final String AUTH_REQUEST_SECRET = "secretKey";


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {

        log.info("调用远程沙箱： {}",executeCodeRequest);
        String url = "http://localhost:8106/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        String responseStr = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute()
                .body();
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "executeCode remoteSandbox error, message = " + responseStr);
        }
        log.info("远程沙箱返回结果：" + responseStr);
        return JSONUtil.toBean(responseStr, ExecuteCodeResponse.class);
    }
}
