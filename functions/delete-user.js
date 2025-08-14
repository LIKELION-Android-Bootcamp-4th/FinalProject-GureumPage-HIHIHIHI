const admin = require("firebase-admin");


/**
 * 클라이언트에서 호출되는 사용자 탈퇴 함수
 * Auth 계정과 관련 Firestore 데이터를 모두 삭제합니다.
 *
 * @param {Object} data - 클라이언트에서 전달한 데이터 (현재는 사용하지 않음)
 * @param {Object} context - 호출 컨텍스트, 인증 정보 포함
 * @param {Object} context.auth - 호출한 사용자의 인증 정보
 * @param {string} context.auth.uid - 호출한 사용자의 UID
 * @ret {Object} 성공 여부 { success: true }
 * @throws {Error} 인증되지 않았거나 탈퇴 처리 실패 시
 */
async function deleteUserAccountHandler(data, context) {
  if (!context.auth) {
    throw new Error("unauthenticated");
  }

  const uid = context.auth.uid;
  const db = admin.firestore();

  try {
    // 1. Firestore 삭제
    await db.collection("users").doc(uid).delete();

    const userBooksSnap = await db.collection("user_books")
        .where("user_id", "==", uid)
        .get();

    const userBookIds = [];
    for (const doc of userBooksSnap.docs) {
      userBookIds.push(doc.id);
      await doc.ref.delete();
    }

    const quotesSnap = await db.collection("quotes")
        .where("user_id", "==", uid)
        .get();
    for (const doc of quotesSnap.docs) {
      await doc.ref.delete();
    }

    const historiesSnap = await db.collection("histories")
        .where("user_id", "==", uid)
        .get();
    for (const doc of historiesSnap.docs) {
      await doc.ref.delete();
    }

    for (const userBookId of userBookIds) {
      const mindmapsSnap = await db.collection("mindmaps")
          .where("userbook_id", "==", userBookId)
          .get();

      for (const mindmapDoc of mindmapsSnap.docs) {
        const nodesSnap = await db.collection("mindmap_nodes")
            .where("mindmap_id", "==", mindmapDoc.id)
            .get();

        for (const nodeDoc of nodesSnap.docs) {
          await nodeDoc.ref.delete();
        }
        await mindmapDoc.ref.delete();
      }
    }

    // 2. Auth 계정 삭제 (Admin SDK 사용)
    await admin.auth().deleteUser(uid);

    console.log(` User ${uid} 탈퇴 완료`);
    return {success: true};
  } catch (error) {
    console.error(`User ${uid} 탈퇴 실패:`, error);
    throw new Error("탈퇴 처리 실패");
  }
}

module.exports = {deleteUserAccountHandler};
