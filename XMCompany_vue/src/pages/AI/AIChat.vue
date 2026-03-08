<template>
  <el-card class="chat-card">
    <div class="chat-header">
      <h2 class="chat-title">AI智能助手</h2>
      <el-button size="small" @click="clearMessages" plain>清空对话</el-button>
    </div>
    
    <div class="chat-container" ref="chatContainer">
      <!-- 添加滚动条 -->
      <div v-if="messages.length === 0" class="empty-chat">
        <div class="empty-icon">🤖</div>
        <div class="empty-text">您好！我是AI助手，有什么可以帮您解答的问题吗？</div>
      </div>
      
      <div v-else class="message-list">
        <div 
          v-for="(msg, idx) in [...messages].reverse()" 
          :key="idx" 
          class="message-item" 
          :class="{'message-user': msg.role === 'user', 'message-ai': msg.role === 'ai'}"
        >
          <div class="message-avatar">
            <el-avatar :size="36" :icon="msg.role === 'user' ? 'el-icon-user' : 'el-icon-s-promotion'" />
          </div>
          <div class="message-content">
            <div class="message-sender">{{ msg.role === 'user' ? '我' : 'AI助手' }}</div>
            <div class="message-text" v-html="renderMarkdown(msg.content)"></div>
            <div class="message-time">{{ msg.time }}</div>
          </div>
        </div>
      </div>
    </div>
    
    <div class="chat-input-container">
      <el-input
        v-model="input"
        type="textarea"
        :rows="2"
        placeholder="请输入您的问题，按Enter发送"
        @keyup.enter.prevent="send"
        class="chat-input"
        :disabled="loading"
      />
      <el-button 
        type="primary" 
        @click="send" 
        :loading="loading"
        class="send-button"
        :disabled="!input.trim()"
      >
        发送
      </el-button>
    </div>
  </el-card>
</template>

<script setup>
import { ref, nextTick, onMounted, watch } from 'vue'
import { aiChatStream } from '@/api/ai'
import { marked } from 'marked'

const input = ref('')
const messages = ref([])
const loading = ref(false)
const chatContainer = ref(null)
const eventSource = ref(null)

// 配置marked选项
marked.setOptions({
  breaks: true,
  gfm: true
})

// 格式化时间
function formatTime() {
  const now = new Date()
  const hours = now.getHours().toString().padStart(2, '0')
  const minutes = now.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

// 渲染Markdown内容
function renderMarkdown(content) {
  if (!content) return ''
  return marked.parse(content)
}

// 滚动到顶部
async function scrollToBottom() {
  await nextTick()
  if (chatContainer.value) {
    chatContainer.value.scrollTop = 0
  }
}

// 发送消息
async function send() {
  if (!input.value.trim()) return;
  
  const userMessage = { 
    role: 'user', 
    content: input.value, 
    time: formatTime() 
  };
  
  // 立即推送用户消息
  messages.value.push(userMessage);
  const userInput = input.value;
  input.value = '';
  
  // 等待消息渲染后滚动到底部
  await nextTick(() => {
    scrollToBottom();
  });

  loading.value = true;
  
  try {
    // 创建AI消息占位符
    const aiMessage = { 
      role: 'ai', 
      content: '', 
      time: formatTime() 
    };
    messages.value.push(aiMessage);
    
    // 使用流式处理 - 实现逐字显示效果
    let accumulatedContent = '';
    await aiChatStream(
      { content: userInput },
      // 处理每个数据块
      async (chunk) => {
        if (chunk) {
          accumulatedContent += chunk;
          
          // 逐字符显示，模拟打字效果
          for (let i = 0; i < chunk.length; i++) {
            aiMessage.content += chunk[i];
            
            // 等待DOM更新后滚动到底部
            await nextTick();
            scrollToBottom();
            
            // 添加轻微延迟营造打字效果（可以根据需要调整）
            await new Promise(resolve => setTimeout(resolve, 20));
          }
        }
      }
    );
    
    // 确保所有内容都已显示
    aiMessage.content = accumulatedContent;
    
    // AI回复完成后手动触发一次保存
    saveMessages();
  } catch (error) {
    console.error('AI回复失败:', error);
    // 更新最后一条消息（应该是AI消息）
    const lastMessage = messages.value[messages.value.length - 1];
    if (lastMessage.role === 'ai') {
      lastMessage.content = '抱歉，我遇到了一些问题，请稍后再试。';
    }
  } finally {
    loading.value = false;
    // 等待AI消息渲染后滚动到顶部
    await nextTick(() => {
      scrollToBottom();
    });
  }
}

// 清空对话
function clearMessages() {
  messages.value = []
  // 同时清除localStorage中的数据
  localStorage.removeItem('aiChatMessages')
  console.log('对话已清空，localStorage已清除')
}

// 初始化时从本地存储加载历史消息
onMounted(() => {
  console.log('组件挂载，尝试加载历史消息')
  const savedMessages = localStorage.getItem('aiChatMessages')
  console.log('localStorage中的数据:', savedMessages)
  
  if (savedMessages) {
    try {
      const parsedMessages = JSON.parse(savedMessages)
      console.log('解析后的消息:', parsedMessages)
      messages.value = parsedMessages
      console.log('消息已加载到组件中')
    } catch (e) {
      console.error('解析历史消息失败:', e)
      // 解析失败时清除损坏的数据
      localStorage.removeItem('aiChatMessages')
    }
  } else {
    console.log('localStorage中没有找到历史消息')
  }
})

// 监听消息变化，保存到本地存储
function saveMessages() {
  try {
    // 过滤掉内容为空的AI消息（可能还在流式生成中）
    const filteredMessages = messages.value.filter(msg => {
      // 保留所有用户消息和有内容的AI消息
      return msg.role === 'user' || (msg.role === 'ai' && msg.content && msg.content.trim() !== '');
    });
    
    const messagesToSave = JSON.stringify(filteredMessages);
    localStorage.setItem('aiChatMessages', messagesToSave);
    console.log('消息已保存到localStorage，保存的消息数量:', filteredMessages.length, '原始数量:', messages.value.length);
  } catch (e) {
    console.error('保存消息到localStorage失败:', e);
  }
}

// 使用watch监听messages变化
watch(messages, saveMessages, { deep: true })
</script>


<style scoped>
.chat-card {
  height: 77vh; /* 将高度设置为视口高度 */
  display: flex;
  flex-direction: column;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;
}

.chat-title {
  margin: 0;
  font-size: 18px;
  color: #303133;
}

.chat-container {
  flex: 1; /* 占据剩余空间 */
  overflow-y: auto; /* 启用垂直滚动 */
  padding: 10px 0;
  max-height: 450px;
  margin-bottom: 16px;
  display: flex;
  flex-direction: column;
  position: relative;
}

.empty-chat {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100%;
  color: #909399;
}

.empty-icon {
  font-size: 48px;
  margin-bottom: 16px;
}

.empty-text {
  font-size: 16px;
  text-align: center;
  max-width: 300px;
}

.message-list {
  display: flex;
  flex-direction: column-reverse;
  gap: 16px;
  overflow-y: auto;
  flex: 1;
}

.message-item {
  display: flex;
  margin-bottom: 8px;
}

.message-user {
  flex-direction: row-reverse;
}

.message-avatar {
  margin: 0 12px;
}

.message-content {
  max-width: 70%;
  display: flex;
  flex-direction: column;
}

.message-sender {
  font-size: 12px;
  color: #909399;
  margin-bottom: 4px;
}

.message-text {
  padding: 10px 14px;
  border-radius: 8px;
  word-break: break-word;
  line-height: 1.5;
}

.message-user .message-text {
  background-color: #ecf5ff;
  color: #303133;
  border-top-right-radius: 0;
}

.message-ai .message-text {
  background-color: #f5f7fa;
  color: #303133;
  border-top-left-radius: 0;
}

.message-time {
  font-size: 12px;
  color: #c0c4cc;
  margin-top: 4px;
  align-self: flex-end;
}

/* 修改：固定输入框位置 */
.chat-input-container {
  position: fixed; /* 使输入框固定 */
  bottom: 0; /* 固定在底部 */
  right: 0; /* 向左对齐 */
  width: 85%; /* 全宽 */
  display: flex;
  gap: 10px;
  padding: 10px;
  background-color: white;
  border-top: 1px solid #ebeef5;
  z-index: 1;
}

.input-area {
  display: flex;
  flex-direction: column;
  position: relative;
}

.chat-input {
  flex: 1;
}

.input-buttons {
  display: flex;
  align-items: center;
  margin-top: 5px;
}

.input-buttons .el-button {
  margin-right: 5px;
}

.send-button {
  align-self: flex-end;
}

</style>