const admin = require("firebase-admin");
const {https} = require("firebase-functions/v2");

/**
 * v2 방식: request 객체 하나만 받음
 * @param {Object} request - 클라이언트 요청 객체
 * @param {Object} request.auth - 인증 정보
 * @param {string} request.auth.uid - 사용자 UID
 * @param {Object} request.data - 클라이언트에서 전달한 데이터
 * @return {Object} 성공 여부와 메시지
 */
async function deleteUserAccountHandler(request) {
  // v2에서는 request.auth로 접근
  if (!request.auth) {
    throw new https.HttpsError("unauthenticated", "사용자 인증이 필요합니다.");
  }

  const uid = request.auth.uid;
  const db = admin.firestore();

  try {
    console.log(`User ${uid} 탈퇴 시작`);

    await Promise.all([
      deleteCollection(db, "users", uid, "doc"),
      deleteCollection(db, "user_books", uid, "user_id"),
      deleteCollection(db, "quotes", uid, "user_id"),
      deleteCollection(db, "histories", uid, "user_id"),
      deleteCollection(db, "daily_read_pages", uid, "user_id"),
      deleteCollection(db, "mindmaps", uid, "user_id"),
      deleteCollection(db, "mindmap_nodes", uid, "user_id"),
    ]);

    console.log(`User ${uid} Firestore 데이터 삭제 완료`);

    // 마지막에 Auth 계정 삭제
    await admin.auth().deleteUser(uid);
    console.log(`User ${uid} Auth 계정 삭제 완료`);

    return {success: true, message: "탈퇴 처리가 완료되었습니다."};
  } catch (error) {
    console.error(`User ${uid} 탈퇴 실패:`, error);
    throw handleError(error);
  }
}

/**
 * 컬렉션의 문서들을 배치로 삭제 함수
 * @param {FirebaseFirestore.Firestore} db - Firestore 인스턴스
 * @param {string} collectionName - 컬렉션 이름
 * @param {string} uid - 사용자 UID
 * @param {string} queryField - 조회할 필드명 ("doc"면 단일 문서, 그 외는 where 조건)
 */
async function deleteCollection(db, collectionName, uid, queryField) {
  const BATCH_SIZE = 500; // Firestore 배치 제한

  try {
    let query;
    if (queryField === "doc") {
      // users 컬렉션처럼 단일 문서인 경우
      const docRef = db.collection(collectionName).doc(uid);
      const docSnap = await docRef.get();
      if (docSnap.exists) {
        await docRef.delete();
      }
      return;
    } else {
      // where 조건으로 조회하는 경우
      query = db.collection(collectionName).where(queryField, "==", uid);
    }

    let hasMore = true;
    let deletedCount = 0;

    while (hasMore) {
      const snapshot = await query.limit(BATCH_SIZE).get();

      if (snapshot.empty) {
        hasMore = false;
        break;
      }

      const batch = db.batch();
      snapshot.docs.forEach((doc) => {
        batch.delete(doc.ref);
      });

      await batch.commit();
      deletedCount += snapshot.docs.length;

      // 배치 크기보다 적으면 더 이상 삭제할 문서가 없음
      hasMore = snapshot.docs.length === BATCH_SIZE;
    }

    if (deletedCount > 0) {
      console.log(`${collectionName}에서 ${deletedCount}개 문서 삭제 완료`);
    }
  } catch (error) {
    console.error(`${collectionName} 삭제 중 오류:`, error);
    throw error;
  }
}

/**
 * 에러 처리 함수
 * @param {Error} error - 발생한 에러
 * @return {https.HttpsError} 처리된 HttpsError
 */
function handleError(error) {
  if (error instanceof https.HttpsError) {
    return error; // 이미 HttpsError면 그대로 반환
  }

  switch (error.code) {
    case "auth/user-not-found":
      return new https.HttpsError("not-found", "사용자를 찾을 수 없습니다.");
    case "permission-denied":
      return new https.HttpsError("permission-denied", "권한이 없습니다.");
    case "deadline-exceeded":
      return new https.HttpsError("deadline-exceeded", "요청 시간이 초과되었습니다. 다시 시도해주세요.");
    default:
      return new https.HttpsError("internal", "탈퇴 처리 중 오류가 발생했습니다.");
  }
}

module.exports = {deleteUserAccountHandler};
