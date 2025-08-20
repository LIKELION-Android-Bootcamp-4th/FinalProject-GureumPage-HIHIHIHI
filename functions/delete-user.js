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
  const batch = db.batch(); // 배치 작업 사용

  try {
    console.log(`User ${uid} 탈퇴 시작`);

    // 1. users 문서 삭제
    const userRef = db.collection("users").doc(uid);
    batch.delete(userRef);

    // 2. user_books 수집 및 삭제
    const userBooksSnap = await db.collection("user_books")
        .where("user_id", "==", uid)
        .get();

    const userBookIds = [];
    userBooksSnap.docs.forEach((doc) => {
      userBookIds.push(doc.id);
      batch.delete(doc.ref);
    });

    // 3. quotes 삭제
    const quotesSnap = await db.collection("quotes")
        .where("user_id", "==", uid)
        .get();

    quotesSnap.docs.forEach((doc) => {
      batch.delete(doc.ref);
    });

    // 4. histories 삭제
    const historiesSnap = await db.collection("histories")
        .where("user_id", "==", uid)
        .get();

    historiesSnap.docs.forEach((doc) => {
      batch.delete(doc.ref);
    });

    // 5. daily_read_pages 삭제
    const dailyReadPagesSnap = await db.collection("daily_read_pages")
        .where("uid", "==", uid)
        .get();

    dailyReadPagesSnap.docs.forEach((doc) => {
      batch.delete(doc.ref);
    });


    // 5. 첫 번째 배치 커밋 (Firestore 배치는 500개 제한)
    await batch.commit();
    console.log(`User ${uid} 기본 데이터 삭제 완료`);

    // 6. mindmaps와 mindmap_nodes 삭제 (별도 배치로 처리)
    for (const userBookId of userBookIds) {
      const mindmapBatch = db.batch();

      const mindmapsSnap = await db.collection("mindmaps")
          .where("userbook_id", "==", userBookId)
          .get();

      for (const mindmapDoc of mindmapsSnap.docs) {
        // mindmap_nodes 먼저 삭제
        const nodesSnap = await db.collection("mindmap_nodes")
            .where("mindmap_id", "==", mindmapDoc.id)
            .get();

        nodesSnap.docs.forEach((nodeDoc) => {
          mindmapBatch.delete(nodeDoc.ref);
        });

        // mindmap 삭제
        mindmapBatch.delete(mindmapDoc.ref);
      }

      // 배치가 비어있지 않으면 커밋
      if (mindmapsSnap.docs.length > 0) {
        await mindmapBatch.commit();
      }
    }

    console.log(`User ${uid} mindmap 데이터 삭제 완료`);

    // 7. 마지막에 Auth 계정 삭제
    await admin.auth().deleteUser(uid);
    console.log(`User ${uid} Auth 계정 삭제 완료`);

    return {success: true, message: "탈퇴 처리가 완료되었습니다."};
  } catch (error) {
    console.error(`User ${uid} 탈퇴 실패:`, error);

    // 구체적인 에러 메시지 반환
    if (error.code === "auth/user-not-found") {
      throw new https.HttpsError("not-found", "사용자를 찾을 수 없습니다.");
    } else if (error.code === "permission-denied") {
      throw new https.HttpsError("permission-denied", "권한이 없습니다.");
    } else {
      throw new https.HttpsError("internal", "탈퇴 처리 중 오류가 발생했습니다.");
    }
  }
}

module.exports = {deleteUserAccountHandler};
