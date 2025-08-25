const auth = require("firebase-functions/v1/auth");
const functions = require('firebase-functions');
const admin = require("firebase-admin");
const {onCall} = require("firebase-functions/v2/https");

admin.initializeApp();

const {createOrUpdateUserDoc} = require("./create-or-update-user-doc");
const kakaoAuthHandler = require("./kakao-auth").handler;
const naverAuthHandler = require("./naver-auth").handler;
const {deleteUserAccountHandler} = require("./delete-user");
const { dailyReminder } = require('./utils/triggers');

exports.kakaoCustomAuth = onCall(kakaoAuthHandler);
exports.naverCustomAuth = onCall(naverAuthHandler);


exports.deleteUserAccount = onCall(deleteUserAccountHandler);


exports.createOrUpdateUserDoc = auth.user().onCreate(async (user) => {
  const provider = (user.providerData && user.providerData.length > 0 && user.providerData[0].providerId) ?
    user.providerData[0].providerId :
    "unknown";

  await createOrUpdateUserDoc({
    uid: user.uid,
    provider: provider,
  });
});

// https.onCall 함수 정의
exports.pushTest = functions.https.onCall(async (data, ctx) => {
  const token = data.token;
  await dailyReminder(token); // api 호출 시 전달 받은 token으로 해당 함수 실행
  return { ok: true };
});