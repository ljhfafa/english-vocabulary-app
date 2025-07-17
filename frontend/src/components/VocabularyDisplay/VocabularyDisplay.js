import React from 'react';
import WordCategory from '../WordCategory/WordCategory';
import './VocabularyDisplay.css';

function VocabularyDisplay({ vocabulary, language, onClear }) {
  if (!vocabulary) return null; // 如果没有词汇数据，不渲染任何内容

  return (
    <div className="vocabulary-display">
      <div className="display-header">
        <h2>
          {'📚 场景'}：{vocabulary.scenario}
        </h2>
        <button className="clear-button" onClick={onClear}>
          清除结果
        </button>
      </div>
      
      <div className="categories-container">
        {vocabulary.words.map((category, index) => (
          <WordCategory key={index} category={category} language={language} />
        ))}
      </div>
      
      {/* 添加学习提示 */}
      <div className="learning-tip">
        <p>
          💡 {'学习建议：点击 🔊 按钮可以听到单词发音'}
        </p>
      </div>
    </div>
  );
}

export default VocabularyDisplay;