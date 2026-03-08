import request from './request'

// 原有的非流式API调用
export function aiChat(data) {
  return request.post('/xm/ai/chat', data)
}

// 新增流式API调用 - 修复SSE格式解析
export function aiChatStream(data, onChunk) {
  return new Promise((resolve, reject) => {
    // 创建一个POST请求
    const xhr = new XMLHttpRequest();
    xhr.open('POST', `${request.defaults.baseURL}/xm/ai/chat`);
    
    // 设置请求头
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.setRequestHeader('Accept', 'text/event-stream');
    xhr.setRequestHeader('Cache-Control', 'no-cache');
    
    const token = localStorage.getItem('token');
    if (token) {
      xhr.setRequestHeader('Authorization', `Bearer ${token}`);
    }
    
    let receivedLength = 0;
    let buffer = '';
    
    // 处理响应
    xhr.onreadystatechange = function() {
      if (xhr.readyState === 3) { // 正在接收数据
        // 只处理新增的数据
        const newData = xhr.responseText.slice(receivedLength);
        receivedLength = xhr.responseText.length;
        
        if (newData) {
          // 处理SSE格式数据
          buffer += newData;
          
          // 按行分割处理
          const lines = buffer.split('\n');
          buffer = lines.pop() || ''; // 保留不完整的最后一行
          
          for (const line of lines) {
            if (line.startsWith('data:')) {
              const content = line.substring(5).trim(); // 移除 'data:' 前缀
              if (content && content !== '[DONE]') { // 忽略结束标记和空内容
                onChunk(content);
              }
            }
          }
        }
      }
    };
    
    // 请求完成时处理剩余缓冲区
    xhr.onload = function() {
      if (xhr.status === 200) {
        // 处理剩余缓冲区数据
        if (buffer && buffer.startsWith('data:')) {
          const content = buffer.substring(5).trim();
          if (content && content !== '[DONE]') {
            onChunk(content);
          }
        }
        resolve(xhr.responseText);
      } else {
        reject(new Error(`HTTP ${xhr.status}: ${xhr.statusText}`));
      }
    };
    

    
    // 请求错误
    xhr.onerror = function() {
      reject(new Error('网络错误'));
    };
    
    // 发送请求
    xhr.send(JSON.stringify(data));
  });
}