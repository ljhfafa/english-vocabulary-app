import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import './AdminDashboard.css';

function AdminDashboard({ onClose }) {
  const [loading, setLoading] = useState(true);
  const [dashboardData, setDashboardData] = useState(null);
  const [refreshing, setRefreshing] = useState(false);

  useEffect(() => {
    fetchDashboardData();
    // 每分钟自动刷新
    const interval = setInterval(fetchDashboardData, 60000);
    return () => clearInterval(interval);
  }, []);

  const fetchDashboardData = async () => {
    try {
      setRefreshing(true);
      const response = await fetch('http://localhost:8080/api/admin/dashboard');
      
      if (!response.ok) {
        throw new Error('Failed to fetch dashboard data');
      }
      
      const data = await response.json();
      setDashboardData(data);
      setLoading(false);
    } catch (error) {
      console.error('Error fetching dashboard:', error);
      toast.error('加载统计数据失败');
      setLoading(false);
    } finally {
      setRefreshing(false);
    }
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return `${date.getMonth() + 1}/${date.getDate()}`;
  };

  if (loading) {
    return (
      <div className="admin-dashboard">
        <div className="loading">加载中...</div>
      </div>
    );
  }

  return (
    <div className="admin-dashboard-overlay">
      <div className="admin-dashboard">
        <div className="dashboard-header">
          <h1>📊 管理员仪表板</h1>
          <div className="dashboard-actions">
            <button 
              onClick={fetchDashboardData} 
              className="refresh-button"
              disabled={refreshing}
            >
              {refreshing ? '刷新中...' : '🔄 刷新'}
            </button>
            <button onClick={onClose} className="close-button">✕</button>
          </div>
        </div>

        {/* 用户统计卡片 */}
        <div className="stats-section">
          <h2>👥 用户统计</h2>
          <div className="stats-cards">
            <div className="stat-card">
              <div className="stat-value">{dashboardData?.userStats?.totalUsers || 0}</div>
              <div className="stat-label">总用户数</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{dashboardData?.userStats?.todayNewUsers || 0}</div>
              <div className="stat-label">今日新增</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{dashboardData?.userStats?.monthlyNewUsers || 0}</div>
              <div className="stat-label">本月新增</div>
            </div>
            <div className="stat-card">
              <div className="stat-value">{dashboardData?.userStats?.weeklyActiveUsers || 0}</div>
              <div className="stat-label">周活跃用户</div>
            </div>
          </div>
        </div>

        {/* 热门查询场景 */}
        <div className="scenarios-section">
          <h2>🔥 热门查询场景 (最近30天)</h2>
          <div className="scenarios-list">
            {dashboardData?.topScenarios?.length > 0 ? (
              <table className="data-table">
                <thead>
                  <tr>
                    <th>排名</th>
                    <th>场景</th>
                    <th>查询次数</th>
                  </tr>
                </thead>
                <tbody>
                  {dashboardData.topScenarios.map((item, index) => (
                    <tr key={index}>
                      <td className="rank">#{index + 1}</td>
                      <td className="scenario">{item.scenario}</td>
                      <td className="count">{item.count}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              <p className="no-data">暂无数据</p>
            )}
          </div>
        </div>

        {/* 查询趋势和语言统计 */}
        <div className="trends-section">
          <div className="trend-chart">
            <h2>📈 每日查询趋势 (最近30天)</h2>
            <div className="simple-chart">
              {dashboardData?.dailyTrend?.length > 0 ? (
                <div className="chart-container">
                  <div className="chart-bars">
                    {dashboardData.dailyTrend.map((item, index) => {
                      const maxCount = Math.max(...dashboardData.dailyTrend.map(d => d.count));
                      const height = (item.count / maxCount) * 100;
                      
                      return (
                        <div key={index} className="chart-bar-wrapper">
                          <div 
                            className="chart-bar"
                            style={{ height: `${height}%` }}
                            title={`${formatDate(item.date)}: ${item.count}次`}
                          >
                            <span className="bar-value">{item.count}</span>
                          </div>
                          <div className="bar-date">{formatDate(item.date)}</div>
                        </div>
                      );
                    })}
                  </div>
                </div>
              ) : (
                <p className="no-data">暂无数据</p>
              )}
            </div>
          </div>

          <div className="language-stats">
            <h2>🌐 语言使用统计</h2>
            <div className="language-cards">
              {dashboardData?.languageStats && Object.entries(dashboardData.languageStats).map(([lang, count]) => (
                <div key={lang} className="language-card">
                  <div className="language-icon">
                    {lang === 'english' ? '🇬🇧' : '🇯🇵'}
                  </div>
                  <div className="language-name">
                    {lang === 'english' ? '英语' : '日语'}
                  </div>
                  <div className="language-count">{count} 次</div>
                </div>
              ))}
            </div>
          </div>
        </div>

        <div className="dashboard-footer">
          <p>数据每分钟自动刷新 | 最后更新: {new Date().toLocaleString('zh-CN')}</p>
        </div>
      </div>
    </div>
  );
}

export default AdminDashboard;