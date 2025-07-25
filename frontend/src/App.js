import React, { useState, useEffect } from 'react';
import { ToastContainer, toast } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import Auth from './components/Auth/Auth';
import ScenarioForm from './components/ScenarioForm/ScenarioForm';
import VocabularyDisplay from './components/VocabularyDisplay/VocabularyDisplay';
import History from './components/History/History';
import AdminDashboard from './components/AdminDashboard/AdminDashboard';
import { vocabularyAPI, historyAPI } from './services/api';
import './App.css';

function App() {
  // 状态管理：用户认证
  const [user, setUser] = useState(null);
  // 状态管理：存储用户输入的场景
  const [scenario, setScenario] = useState('');
  // 状态管理：存储生成的词汇结果
  const [vocabulary, setVocabulary] = useState(null);
  // 状态管理：加载状态
  const [loading, setLoading] = useState(false);
  // 状态管理：后端连接状态
  const [backendStatus, setBackendStatus] = useState('checking');
  // 状态管理：语言选择
  const [language, setLanguage] = useState('english');
  // 状态管理：是否显示历史记录
  const [showHistory, setShowHistory] = useState(false);
  // 状态管理：用户统计
  const [userStats, setUserStats] = useState(null);
 //管理员
  const [showAdmin, setShowAdmin] = useState(false);

  // 检查用户登录状态
  useEffect(() => {
    const savedUser = localStorage.getItem('user');
    if (savedUser) {
      setUser(JSON.parse(savedUser));
    }
  }, []);

  // 检查后端连接状态
  useEffect(() => {
    checkBackendHealth();
  }, []);

  // 加载用户统计信息
  useEffect(() => {
    if (user) {
      loadUserStats();
    }
  }, [user]);

  const checkBackendHealth = async () => {
    try {
      const health = await vocabularyAPI.checkHealth();
      if (health.status === 'UP') {
        setBackendStatus('connected');
        toast.success('智能外语服务连接成功！');
      }
    } catch (error) {
      setBackendStatus('disconnected');
      toast.warning('后端服务未启动，将使用模拟数据');
    }
  };

  // 加载用户统计信息
  const loadUserStats = async () => {
    if (backendStatus === 'connected' && user) {
      try {
        const stats = await historyAPI.getUserStats(user.username);
        setUserStats(stats);
      } catch (error) {
        console.error('Failed to load user stats:', error);
      }
    }
  };

  // 处理登录成功
  const handleLoginSuccess = (userData) => {
    setUser(userData);
  };

  // 处理退出登录
  const handleLogout = () => {
    localStorage.removeItem('user');
    setUser(null);
    setVocabulary(null);
    setScenario('');
    toast.info('已退出登录');
  };

  // 处理生成词汇请求
  const handleGenerateVocabulary = async () => {
    if (!scenario.trim()) {
      toast.error('请输入一个场景！');
      return;
    }

    setLoading(true);
    
    try {
      toast.info('正在生成词汇，请稍候...', { autoClose: 10000 }); 
      const response = await vocabularyAPI.generate(scenario, language, user?.username);
      
      if (response && response.words && response.words.length > 0) {
        setVocabulary(response);
        toast.success('词汇生成成功！');
        
        // 如果用户已登录，更新统计信息
        if (user && backendStatus === 'connected') {
          loadUserStats();
        }
      } else {
        toast.warning('未能生成相关词汇，请尝试其他场景');
      }
    } catch (error) {
      console.error('生成词汇失败:', error);
      toast.error('生成词汇失败，请检查网络连接或稍后重试');
    } finally {
      setLoading(false);
    }
  };

  // 处理加载历史记录
  const handleLoadHistory = (historyData) => {
    setVocabulary(historyData);
    setScenario(historyData.scenario);
  };

  // 清除结果
  const handleClear = () => {
    setVocabulary(null);
    setScenario('');
    // 不重置语言选择，保持用户的选择
  };

  // 如果用户未登录，显示登录界面
  if (!user) {
    return (
      <>
        <Auth onLoginSuccess={handleLoginSuccess} />
        <ToastContainer
          position="top-right"
          autoClose={3000}
          hideProgressBar={false}
          newestOnTop={false}
          closeOnClick
          rtl={false}
          pauseOnFocusLoss
          draggable
          pauseOnHover
          theme="light"
        />
      </>
    );
  }

  // 用户已登录，显示主应用
  return (
    <div className="App">
      <header className="App-header">
        <div className="header-content">
          <div>
            <h1>
              {language === 'japanese' 
                ? '🌏 场景词汇生成器' 
                : '🌍 场景词汇生成器'}
            </h1>
            <p>输入一个场景，获取相关的词汇和短语</p>
          </div>
          <div className="user-info">
            <span>👤 {user.username}</span>
            {userStats && (
              <span className="user-stats">
                📊 {userStats.totalQueries} 次查询
              </span>
            )}
            <button 
              onClick={() => setShowHistory(true)} 
              className="history-button"
              title="查看历史记录"
            >
              📚 历史
            </button>
            <button onClick={handleLogout} className="logout-button">
              退出登录
            </button>
            {user.username === 'ljhfafa' && (
            <button 
                onClick={() => setShowAdmin(true)} 
                className="admin-button"
                title="管理员面板"
              >
                ⚙️ 管理
              </button>
            )}
          </div>
        </div>
      </header>

      <main className="App-main">
        {/* 后端连接状态指示器 */}
        <div className="backend-status">
          <span className={`status-indicator ${backendStatus}`}></span>
          <span className="status-text">
            智能外语服务：
            {backendStatus === 'checking' && '检查中...'}
            {backendStatus === 'connected' && '已连接'}
            {backendStatus === 'disconnected' && '未连接'}
          </span>
        </div>

        {/* 使用场景输入表单组件 */}
        <ScenarioForm
          scenario={scenario}
          setScenario={setScenario}
          language={language}
          setLanguage={setLanguage}
          onSubmit={handleGenerateVocabulary}
          loading={loading}
        />

        {/* 使用词汇展示组件 */}
        <VocabularyDisplay
          vocabulary={vocabulary}
          language={language}
          onClear={handleClear}
        />
      </main>

      {/* 历史记录模态框 */}
      {showHistory && (
        <History
          username={user.username}
          onClose={() => setShowHistory(false)}
          onLoadHistory={handleLoadHistory}
        />
      )}
      {showAdmin && (
        <AdminDashboard
          onClose={() => setShowAdmin(false)}
        />
      )}

      {/* Toast通知容器 */}
      <ToastContainer
        position="top-right"
        autoClose={3000}
        hideProgressBar={false}
        newestOnTop={false}
        closeOnClick
        rtl={false}
        pauseOnFocusLoss
        draggable
        pauseOnHover
        theme="light"
      />
    </div>
  );
}

export default App;