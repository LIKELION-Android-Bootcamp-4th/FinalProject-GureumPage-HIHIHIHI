package com.hihihihi.domain.usecase.userbook

import android.util.Log
import com.hihihihi.domain.model.Mindmap
import com.hihihihi.domain.model.MindmapNode
import com.hihihihi.domain.model.UserBook
import com.hihihihi.domain.operation.NodeEditOperation
import com.hihihihi.domain.repository.MindmapNodeRepository
import com.hihihihi.domain.repository.MindmapRepository
import com.hihihihi.domain.repository.UserBookRepository
import javax.inject.Inject

class AddUserBookUseCase @Inject constructor(
    private val userBookRepository: UserBookRepository,
    private val mindmapRepository: MindmapRepository,
    private val mindmapNodeRepository: MindmapNodeRepository,
) {
    suspend operator fun invoke(
        userId: String,
        userBook: UserBook,
        mindmap: Mindmap,
        rootNode: MindmapNode
    ): Result<Unit> {
        return try {

            // 중복 확인 (정규화된 ISBN 사용)
            if (userBookRepository.checkUserBookExists(userId, userBook.isbn13!!)) {
                return Result.failure(Exception("이미 추가된 책입니다"))
            }

            val userBookId =
                userBookRepository.addUserBook(userId, userBook.isbn13, userBook).getOrThrow()
            val rootNodeId = "${userBookId}Root"

            Log.e("TAG", "invoke: ${userBook.userId}")
            val operations = listOf(
                NodeEditOperation.Add(
                    rootNode.copy(
                        userId = userBook.userId,
                        mindmapId = userBookId,
                        mindmapNodeId = "${userBookId}Root",
                        bookImage = userBook.imageUrl,
                        nodeTitle = userBook.title,
                        parentNodeId = null,
                    )
                ),
                NodeEditOperation.Add(
                    rootNode.copy(
                        userId = userBook.userId,
                        mindmapId = userBookId,
                        mindmapNodeId = "${userBookId}Child1",
                        nodeTitle = "클릭해서 노드를 추가해요",
                        parentNodeId = rootNodeId,
                        nodeEx = "",
                        color = null,
                        icon = null,
                        deleted = false,
                        bookImage = null,
                    )
                ),
                NodeEditOperation.Add(
                    rootNode.copy(
                        userId = userBook.userId,
                        mindmapId = userBookId,
                        mindmapNodeId = "${userBookId}Child2",
                        nodeTitle = "꾹 눌러서 수정·삭제해요",
                        parentNodeId = rootNodeId,
                        nodeEx = "",
                        color = null,
                        icon = null,
                        deleted = false,
                        bookImage = null,
                    )
                )
            )
            mindmapNodeRepository.applyNodeOperation(userBookId, operations).getOrThrow()

            mindmapRepository.createMindmap(
                mindmap.copy(
                    userId = userBook.userId,
                    mindmapId = userBookId,
                    userBookId = userBookId,
                    rootNodeId = rootNodeId,
                )
            ).getOrThrow()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}