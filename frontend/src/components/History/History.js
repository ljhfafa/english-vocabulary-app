import React, { useState, useEffect } from 'react';
import { toast } from 'react-toastify';
import { historyAPI } from '../../services/api';
import './History.css';

function History({ username, onClose, onLoadHistory }) {
  const [histories, setHistories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const [selectedLanguage, setSelectedLanguage] = useState(''); // 空字符串表示全部

  useEffect(() => {
    loadHistories();
  }, [currentPage, selectedLanguage]);

  const loadHistories = async () => {
    setLoading(true);
    try {
      const response = await historyAPI.getUserHistory(
        username, 
        currentPage, 
        10, 
        selectedLanguage
      );
      
      setHistories(response.records);
      setTotalPages(response.totalPages);
      setTotalElements(response.totalElements);
    } catch (error) {
      console.error('Failed to load history:', error);
      toast.error('加载历史记录失败');
    } finally {
      setLoading(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('确定要删除这条记录吗？')) {
      return;
    }

    try {
      await historyAPI.deleteHistory(username, id);
      toast.success('删除成功');
      loadHistories(); // 重新加载
    } catch (error) {
      console.error('Failed to delete history:', error);
      toast.error('删除失败');
    }
  };

  const handleLoadHistory = (history) => {
    // 将历史记录加载到主界面
    onLoadHistory({
      scenario: history.scenario,
      words: history.vocabulary
    });
    onClose();
    toast.info('已加载历史记录');
  };

  const formatDate = (dateString) => {
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    });
  };

  const getLanguageLabel = (language) => {
    const labels = {
      'english': '🇬🇧 英语',
      'japanese': '🇯🇵 日语'
    };
    return labels[language] || language;
  };

  return (
    <div className="history-modal-overlay" onClick={onClose}>
      <div className="history-modal" onClick={(e) => e.stopPropagation()}>
        <div className="history-header">
          <h2>📚 查询历史</h2>
          <button className="close-button" onClick={onClose}>✕</button>
        </div>

        <div className="history-filters">
          <select 
            value={selectedLanguage} 
            onChange={(e) => {
              setSelectedLanguage(e.target.value);
              setCurrentPage(0);
            }}
            className="language-filter"
          >
            <option value="">全部语言</option>
            <option value="english">🇬🇧 英语</option>
            <option value="japanese">🇯🇵 日语</option>
          </select>
          
          <div className="history-stats">
            共 {totalElements} 条记录
          </div>
        </div>

        <div className="history-content">
          {loading ? (
            <div className="loading-spinner">加载中...</div>
          ) : histories.length === 0 ? (
            <div className="empty-history">
              <p>暂无历史记录</p>
            </div>
          ) : (
            <div className="history-list">
              {histories.map((history) => (
                <div key={history.id} className="history-item">
                  <div className="history-item-header">
                    <div className="history-info">
                      <h3>{history.scenario}</h3>
                      <div className="history-meta">
                        <span className="history-language">
                          {getLanguageLabel(history.language)}
                        </span>
                        <span className="history-date">
                          {formatDate(history.createdAt)}
                        </span>
                      </div>
                    </div>
                    <div className="history-actions">
                      <button 
                        className="load-button"
                        onClick={() => handleLoadHistory(history)}
                        title="加载此记录"
                      >
                        📖 查看
                      </button>
                      <button 
                        className="delete-button"
                        onClick={() => handleDelete(history.id)}
                        title="删除此记录"
                      >
                        🗑️
                      </button>
                    </div>
                  </div>
                  
                  <div className="history-preview">
                    {history.vocabulary && history.vocabulary.slice(0, 2).map((category, idx) => (
                      <div key={idx} className="preview-category">
                        <strong>{category.type}:</strong>
                        <span> {category.items.slice(0, 3).join(', ')}...</span>
                      </div>
                    ))}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {totalPages > 1 && (
          <div className="history-pagination">
            <button 
              onClick={() => setCurrentPage(currentPage - 1)}
              disabled={currentPage === 0}
              className="pagination-button"
            >
              上一页
            </button>
            <span className="page-info">
              第 {currentPage + 1} / {totalPages} 页
            </span>
            <button 
              onClick={() => setCurrentPage(currentPage + 1)}
              disabled={currentPage >= totalPages - 1}
              className="pagination-button"
            >
              下一页
            </button>
          </div>
        )}
      </div>
    </div>
  );
}

export default History;