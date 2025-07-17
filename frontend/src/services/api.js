// src/services/api.js
import axios from 'axios';

// 根据环境设置不同的API地址
const getBaseURL = () => {
  // 生产环境
  if (window.location.hostname !== 'localhost') {
    return '/api';  // 使用相对路径，通过nginx代理
  }
  // 开发环境
  return 'http://localhost:8080/api';
};

const api = axios.create({
  baseURL: getBaseURL(),
  timeout: 30000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 可以在这里添加token等
    console.log('Making request to:', config.url);
    return config;
  },
  error => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  response => {
    console.log('Response received:', response.data);
    return response;
  },
  error => {
    console.error('Response error:', error);
    if (error.response) {
      // 服务器返回错误状态码
      console.error('Error data:', error.response.data);
      console.error('Error status:', error.response.status);
    } else if (error.request) {
      // 请求已发送但没有收到响应
      console.error('No response received');
    } else {
      // 请求配置出错
      console.error('Error setting up request:', error.message);
    }
    return Promise.reject(error);
  }
);

// API方法
export const vocabularyAPI = {
  // 生成词汇
  generate: async (scenario, language = 'english', username = null) => {
    try {
      const params = username ? `?username=${username}` : '';
      const response = await api.post(`/vocabulary/generate${params}`, { 
        scenario,
        language 
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  
  // 健康检查
  checkHealth: async () => {
    try {
      const response = await api.get('/health');
      return response.data;
    } catch (error) {
      throw error;
    }
  }
};

// 认证API
export const authAPI = {
  // 注册
  register: async (username, password) => {
    try {
      const response = await api.post('/auth/register', { 
        username,
        password 
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  
  // 登录
  login: async (username, password) => {
    try {
      const response = await api.post('/auth/login', { 
        username,
        password 
      });
      return response.data;
    } catch (error) {
      throw error;
    }
  }
};

// 历史记录API
export const historyAPI = {
  // 获取用户历史记录
  getUserHistory: async (username, page = 0, size = 10, language = '') => {
    try {
      const params = new URLSearchParams({
        username,
        page,
        size
      });
      if (language) {
        params.append('language', language);
      }
      const response = await api.get(`/history?${params}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  
  // 获取最近的历史记录
  getRecentHistory: async (username, limit = 5) => {
    try {
      const response = await api.get(`/history/recent?username=${username}&limit=${limit}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  
  // 删除历史记录
  deleteHistory: async (username, id) => {
    try {
      const response = await api.delete(`/history/${id}?username=${username}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  },
  
  // 获取用户统计
  getUserStats: async (username) => {
    try {
      const response = await api.get(`/history/stats?username=${username}`);
      return response.data;
    } catch (error) {
      throw error;
    }
  }
};

export default api;