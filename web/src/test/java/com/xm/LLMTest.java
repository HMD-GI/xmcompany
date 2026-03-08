package com.xm;

import com.xm.assistant.Assistant;
import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

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

    /**
     * 上传知识库到Pinecone
     */

    @Autowired
    private EmbeddingStore embeddingStore;
    @Autowired
    private EmbeddingModel embeddingModel;
    @Test
    public void testUploadKnowledgeLibrary() {
        //使用FileSystemDocumentLoader读取指定目录下的知识库文档
        //并使用默认的文档解析器对文档进行解析
        Document document1 = FileSystemDocumentLoader.loadDocument("E:/BaiduNetdiskDownload/XMcompany/资料/系统信息.md");
        Document document2 = FileSystemDocumentLoader.loadDocument("E:/BaiduNetdiskDownload/XMcompany/资料/系统介绍.md");
        List<Document> documents = Arrays.asList(document1, document2);
        //文本向量化并存入向量数据库：将每个片段进行向量化，得到一个嵌入向量
        EmbeddingStoreIngestor
                .builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build()
                .ingest(documents);
    }


}
