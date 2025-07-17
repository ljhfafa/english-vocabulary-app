import React from 'react';
import './ScenarioForm.css';

// 这是一个独立的表单组件
// props 是父组件传递给子组件的数据
function ScenarioForm({ scenario, setScenario, language, setLanguage, onSubmit, loading }) {
  // 处理表单提交
  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(); // 调用父组件传递的提交函数
  };

  // 语言选项
  const languages = [
    { value: 'english', label: '🇬🇧 英语', flag: '🇬🇧' },
    { value: 'japanese', label: '🇯🇵 日语', flag: '🇯🇵' }
  ];

  return (
    <form onSubmit={handleSubmit} className="scenario-form">
      {/* 语言选择下拉框 */}
      <select 
        value={language} 
        onChange={(e) => setLanguage(e.target.value)}
        className="language-select"
      >
        {languages.map(lang => (
          <option key={lang.value} value={lang.value}>
            {lang.label}
          </option>
        ))}
      </select>
      
      <input
        type="text"
        value={scenario}
        onChange={(e) => setScenario(e.target.value)}
        placeholder={
          language === 'japanese' 
            ? "例如：在餐厅点餐、机场值机、商务会议..."
            : "例如：在餐厅点餐、机场值机、商务会议..."
        }
        className="scenario-input"
      />
      <button type="submit" disabled={loading} className="submit-button">
        {loading ? '生成中...' : '生成词汇'}
      </button>
    </form>
  );
}

export default ScenarioForm;