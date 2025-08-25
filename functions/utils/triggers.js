// push.js - sendToToken 함수로 조건에 맞는 푸시 알람을 쉽게 보낼 수 있는 헬퍼 정의

const { sendToToken } = require('./push');

// 트리거별 헬퍼. 필요 시 조건 계산 로직에서 호출.
function dailyReminder(token) {
  return sendToToken(token, {
    ch: 'reminder',
    title: '오늘 읽기 시작할까요?',
    uri: 'app://read/start',
    ck: 'reminder:daily'
  });
}

function goal80(token) {
  return sendToToken(token, {
    ch: 'progress',
    title: '오늘 목표 80% 달성',
    body: '10분만 더!',
    uri: 'app://goals/today',
    ck: 'goals:today'
  });
}

module.exports = { dailyReminder, goal80 };
