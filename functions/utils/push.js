// 푸시 알림 발송 빌더
// 스키마 준수 검증 + 메시지 전송

const admin = require('firebase-admin');

const CH = new Set(['reminder','progress','summary','activity']);

function normalizeUri(uri) {
  if (!uri) return 'app://home';
  if (uri.startsWith('app://') || uri.startsWith('gureum://')) return uri;
  return `app://${uri}`;
}

function buildData({ ch, title, body = '', uri, ck, v = '1', tz }) {
  if (!CH.has(ch)) throw new Error(`invalid ch=${ch}`);
  if (!title) throw new Error('missing title');
  const data = { ch, title, uri: normalizeUri(uri), v };
  if (body) data.body = body;
  if (ck) data.ck = ck;
  if (tz) data.tz = tz;
  return data;
}

async function sendToToken(token, payload) {
  const data = buildData(payload);
  const message = {
    token,
    data,
    android: { priority: 'high' } // data-only
  };
  return admin.messaging().send(message);
}

module.exports = { sendToToken, buildData };
