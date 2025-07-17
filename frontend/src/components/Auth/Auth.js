import React, { useState } from 'react';
import { toast } from 'react-toastify';
import { authAPI } from '../../services/api';
import './Auth.css';

function Auth({ onLoginSuccess }) {
  // 状态管理
  const [isLogin, setIsLogin] = useState(true); // true: 登录模式, false: 注册模式
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [showPassword, setShowPassword] = useState(false);

  // 表单验证
  const validateForm = () => {
    if (username.length < 6) {
      toast.error('用户名至少需要6位字符');
      return false;
    }
    
    if (password.length < 8) {
      toast.error('密码至少需要8位字符');
      return false;
    }
    
    if (!isLogin && password !== confirmPassword) {
      toast.error('两次输入的密码不一致');
      return false;
    }
    
    // 用户名只允许字母、数字和下划线
    const usernameRegex = /^[a-zA-Z0-9_]+$/;
    if (!usernameRegex.test(username)) {
      toast.error('用户名只能包含字母、数字和下划线');
      return false;
    }
    
    return true;
  };

  // 处理登录
  const handleLogin = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;
    
    setLoading(true);
    
    try {
      const response = await authAPI.login(username, password);
      
      if (response.success) {
        toast.success('登录成功！');
        // 保存用户信息到localStorage
        const userData = {
          username: response.username,
          token: response.token,
          loginTime: new Date().toISOString()
        };
        localStorage.setItem('user', JSON.stringify(userData));
        
        // 调用父组件的登录成功回调
        onLoginSuccess(userData);
      } else {
        toast.error(response.message || '登录失败');
      }
    } catch (error) {
      console.error('Login error:', error);
      // 如果后端未连接，使用模拟登录
      if (error.code === 'ERR_NETWORK') {
        // 模拟登录
        if (username === 'testuser' && password === 'password123') {
          toast.success('登录成功（模拟模式）！');
          const userData = {
            username: username,
            token: 'mock-jwt-token',
            loginTime: new Date().toISOString()
          };
          localStorage.setItem('user', JSON.stringify(userData));
          onLoginSuccess(userData);
        } else {
          toast.error('用户名或密码错误（测试账号：testuser/password123）');
        }
      } else {
        toast.error('登录失败，请检查用户名和密码');
      }
    } finally {
      setLoading(false);
    }
  };

  // 处理注册
  const handleRegister = async (e) => {
    e.preventDefault();
    
    if (!validateForm()) return;
    
    setLoading(true);
    
    try {
      const response = await authAPI.register(username, password);
      
      if (response.success) {
        toast.success('注册成功！请登录');
        setIsLogin(true);
        setPassword('');
        setConfirmPassword('');
      } else {
        toast.error(response.message || '注册失败');
      }
    } catch (error) {
      console.error('Register error:', error);
      // 如果后端未连接，提示用户
      if (error.code === 'ERR_NETWORK') {
        toast.error('无法连接到服务器，请确保后端服务正在运行');
      } else {
        toast.error('注册失败，请稍后重试');
      }
    } finally {
      setLoading(false);
    }
  };

  // 切换登录/注册模式
  const toggleMode = () => {
    setIsLogin(!isLogin);
    setPassword('');
    setConfirmPassword('');
  };

  return (
    <div className="auth-container">
      <div className="auth-card">
        <div className="auth-header">
          <h2>{isLogin ? '登录' : '注册'}</h2>
          <p>{isLogin ? '登录以使用完整功能' : '创建新账户'}</p>
        </div>
        
        <form onSubmit={isLogin ? handleLogin : handleRegister} className="auth-form">
          <div className="form-group">
            <label htmlFor="username">用户名</label>
            <input
              type="text"
              id="username"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="至少6位字符"
              minLength={6}
              required
              autoComplete="username"
            />
            <small className="form-hint">只能包含字母、数字和下划线</small>
          </div>
          
          <div className="form-group">
            <label htmlFor="password">密码</label>
            <div className="password-input-wrapper">
              <input
                type={showPassword ? "text" : "password"}
                id="password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="至少8位字符"
                minLength={8}
                required
                autoComplete={isLogin ? "current-password" : "new-password"}
              />
              <button
                type="button"
                className="password-toggle"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword ? '🙈' : '👁️'}
              </button>
            </div>
          </div>
          
          {!isLogin && (
            <div className="form-group">
              <label htmlFor="confirmPassword">确认密码</label>
              <input
                type="password"
                id="confirmPassword"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
                placeholder="再次输入密码"
                minLength={8}
                required
                autoComplete="new-password"
              />
            </div>
          )}
          
          <button
            type="submit"
            className="auth-submit-button"
            disabled={loading}
          >
            {loading ? '处理中...' : (isLogin ? '登录' : '注册')}
          </button>
        </form>
        
        <div className="auth-footer">
          <p>
            {isLogin ? '还没有账户？' : '已有账户？'}
            <button
              type="button"
              className="auth-switch-button"
              onClick={toggleMode}
            >
              {isLogin ? '立即注册' : '立即登录'}
            </button>
          </p>
        </div>
        
      </div>
    </div>
  );
}

export default Auth;