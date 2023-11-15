package com.cm.cmoj;


import com.cm.cmoj.config.WxOpenConfig;
import javax.annotation.Resource;

import com.cm.cmoj.judge.codesandbox.impl.RemoteCodeSandbox;
import com.cm.cmoj.judge.codesandbox.model.ExecuteCodeRequest;
import com.cm.cmoj.judge.codesandbox.model.ExecuteCodeResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.Arrays;
import java.util.List;

/**
 * 主类测试
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@SpringBootTest
class MainApplicationTests {

    @Resource
    private WxOpenConfig wxOpenConfig;

    @Test
    void contextLoads() {
        RemoteCodeSandbox remoteCodeSandbox = new RemoteCodeSandbox();
        String code = "import java.util.Scanner;\n" +
                "\n" +
                "public class Main{\n" +
                "    public static void main(String[] args){\n" +
                "        System.out.println(\"结果为：\"+(args[0]+args[1]));\n" +
                "    }\n" +
                "}\n";
        String language = "JAVA";
        List<String> inputList = Arrays.asList("1 2","3 4");
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(code)
                .language(language)
                .inputList(inputList)
                .build();
        ExecuteCodeResponse executeCodeResponse = remoteCodeSandbox.executeCode(executeCodeRequest);
        Assertions.assertNotNull(executeCodeResponse);

    }


}
