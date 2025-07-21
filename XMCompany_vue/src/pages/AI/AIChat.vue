<template>
  <el-card class="chat-card">
    <div class="chat-header">
      <h2 class="chat-title">AI智能助手</h2>
      <el-button size="small" @click="clearMessages" plain>清空对话</el-button>
    </div>
    
    <div class="chat-container" ref="chatContainer">
      <div v-if="messages.length === 0" class="empty-chat">
        <div class="empty-icon">🤖</div>
        <div class="empty-text">您好！我是AI助手，有什么可以帮您解答的问题吗？</div>
      </div>
      
      <div v-else class="message-list">
        <div 
          v-for="(msg, idx) in messages" 
          :key="idx" 
          class="message-item" 
          :class="{'message-user': msg.role === 'user', 'message-ai': msg.role === 'ai'}"
        >
          <div class="message-avatar">
            <el-avatar :size="36" :icon="msg.role === 'user' ? 'el-icon-user' : 'el-icon-s-promotion'" />
          </div>
          <div class="message-content">
            <div class="message-sender">{{ msg.role === 'user' ? '我' : 'AI助手' }}</div>
            <div class="message-text">{{ msg.content }}</div>
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
import { aiChat } from '@/api/ai'

const input = ref('')
const messages = ref([])
const loading = ref(false)
const chatContainer = ref(null)

// 格式化时间
function formatTime() {
  const now = new Date()
  const hours = now.getHours().toString().padStart(2, '0')
  const minutes = now.getMinutes().toString().padStart(2, '0')
  return `${hours}:${minutes}`
}

// 滚动到底部
async function scrollToBottom() {
  await nextTick()
  if (chatContainer.value) {
    chatContainer.value.scrollTop = chatContainer.value.scrollHeight
  }
}

// 发送消息
async function send() {
  if (!input.value.trim()) return
  
  const userMessage = { 
    role: 'user', 
    content: input.value, 
    time: formatTime() 
  }
  
  messages.value.push(userMessage)
  const userInput = input.value
  input.value = ''
  
  await scrollToBottom()
  loading.value = true
  
  try {
    const res = await aiChat({ messageId: Date.now() + '', content: userInput })
    messages.value.push({ 
      role: 'ai', 
      content: res.data, 
      time: formatTime() 
    })
  } catch (error) {
    console.error('AI回复失败:', error)
    messages.value.push({ 
      role: 'ai', 
      content: '抱歉，我遇到了一些问题，请稍后再试。', 
      time: formatTime() 
    })
  } finally {
    loading.value = false
    await scrollToBottom()
  }
}

// 清空对话
function clearMessages() {
  messages.value = []
}

// 初始化时从本地存储加载历史消息
onMounted(() => {
  const savedMessages = localStorage.getItem('aiChatMessages')
  if (savedMessages) {
    try {
      messages.value = JSON.parse(savedMessages)
    } catch (e) {
      console.error('解析历史消息失败:', e)
    }
  }
})

// 监听消息变化，保存到本地存储
function saveMessages() {
  localStorage.setItem('aiChatMessages', JSON.stringify(messages.value))
}

// 使用watch监听messages变化
watch(messages, saveMessages, { deep: true })
</script>

<style scoped>
.chat-card {
  height: calc(100vh - 140px);
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
  flex: 1;
  overflow-y: auto;
  padding: 10px 0;
  margin-bottom: 16px;
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
  flex-direction: column;
  gap: 16px;
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

.chat-input-container {
  display: flex;
  gap: 10px;
  margin-top: auto;
}

.chat-input {
  flex: 1;
}

.send-button {
  align-self: flex-end;
}
</style>