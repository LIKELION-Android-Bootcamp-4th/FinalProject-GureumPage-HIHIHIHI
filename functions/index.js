const {onCall} = require("firebase-functions/v2/https");
const auth = require("firebase-functions/v1/auth");
const admin = require("firebase-admin");

admin.initializeApp();

const {createOrUpdateUserDoc} = require("./create-or-update-user-doc");
const kakaoAuthHandler = require("./kakao-auth").handler;
const naverAuthHandler = require("./naver-auth").handler;

exports.kakaoCustomAuth = onCall(kakaoAuthHandler);
exports.naverCustomAuth = onCall(naverAuthHandler);

exports.createOrUpdateUserDoc = auth.user().onCreate(async (user) => {
  const provider = (user.providerData && user.providerData.length > 0 && user.providerData[0].providerId) ?
    user.providerData[0].providerId :
    "unknown";

  await createOrUpdateUserDoc({
    uid: user.uid,
    provider: provider,
  });
});
