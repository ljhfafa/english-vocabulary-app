import React, { useState } from 'react';
import './WordCategory.css';

function WordCategory({ category, language = 'english' }) {
  // 为每个单词添加发音功能（使用浏览器内置的语音合成）
  const speak = (text) => {
    // 根据语言类型处理文本
    let textToSpeak = text;
    let lang = 'en-US'; // 默认英语
    
    if (language === 'japanese') {
      // 日语：提取括号前的日文部分
      textToSpeak = text.split('(')[0].trim();
      // 移除假名注释（如果有）
      textToSpeak = textToSpeak.replace(/（[^）]*）/g, '').trim();
      lang = 'ja-JP';
    } else {
      // 英语：提取括号前的英文部分
      textToSpeak = text.split('(')[0].trim();
    }
    
    // 使用Web Speech API
    const utterance = new SpeechSynthesisUtterance(textToSpeak);
    utterance.lang = lang; // 设置语言
    utterance.rate = 0.9; // 稍微慢一点的语速
    
    speechSynthesis.speak(utterance);
  };

  return (
    <div className="word-category">
      <h3 className="category-title">
        <span className="category-icon">{getCategoryIcon(category.type, language)}</span>
        {category.type}
      </h3>
      <ul className="word-list">
        {category.items.map((word, index) => (
          <li key={index} className="word-item">
            <span className="word-text">{word}</span>
            <button 
              className="speak-button"
              onClick={() => speak(word)}
              title="点击发音"
            >
              🔊
            </button>
          </li>
        ))}
      </ul>
    </div>
  );
}

// 根据词汇类型返回对应的图标
function getCategoryIcon(type, language) {
    const icons = {
      '名词': '📝',
      '动词': '🏃',
      '短语': '💬',
      '常用句': '🗣️',
      '形容词': '🎨',
      '副词': '⚡',
      '通用词汇': '📚',
      '学习建议': '💡'
    };
    return icons[type] || '📌';
}

export default WordCategory;