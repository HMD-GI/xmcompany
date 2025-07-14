package com.xm;

import com.xm.assistant.Assistant;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LLMTest {

    @Autowired
    private OpenAiChatModel openAiChatModel;

    @Test
    public void test() {
        //向模型提问
        String answer = openAiChatModel.chat("你好");
        //输出结果
        System.out.println(answer);
    }

    /**
     * 通义千问大模型
     */
    @Autowired
    public Assistant assistant;
    @Test
    public void testDashScopeQwen() {
        //向模型提问
//        String answer1 = assistant.chat(1,"今天几号");
//        System.out.println(answer1);
//        String answer2 = assistant.chat(1,"我是谁");
//        System.out.println(answer2);
//        String answer3 = assistant.chat(2,"我是谁");
//        System.out.println(answer3);
    }
}
