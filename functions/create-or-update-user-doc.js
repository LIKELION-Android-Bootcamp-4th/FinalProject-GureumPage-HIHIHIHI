const admin = require("firebase-admin");

/**
 * Firestore의 users/{uid} 문서를 생성하거나 업데이트합니다.
 * 신규 사용자일 경우만 nickname, appellation, daily_goal_time을 초기화합니다.
 * @param {Object} params
 * @param {string} params.uid - 사용자 UID (필수)
 * @param {string} params.provider - 로그인 제공자 (예: "kakao", "naver", "google")
 */
async function createOrUpdateUserDoc({uid, provider}) {
  try {
    const userDocRef = admin.firestore().collection("users").doc(uid);
    const existingDoc = await userDocRef.get();

    const userData = {
      provider: provider,
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
    };

    if (!existingDoc.exists) {
      userData.nickname = "";
      userData.appellation = "";
      userData.daily_goal_time = 0;
    }

    await userDocRef.set(userData, {merge: true});
    console.log(`✅ Firestore 사용자 문서 생성/업데이트 완료: ${uid}`);
  } catch (error) {
    console.error("🔥 Firestore 사용자 문서 생성 실패:", error);
    throw error;
  }
}

module.exports = {createOrUpdateUserDoc};
