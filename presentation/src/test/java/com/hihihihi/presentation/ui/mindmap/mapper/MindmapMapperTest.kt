package com.hihihihi.presentation.ui.mindmap.mapper

import androidx.compose.ui.graphics.Color
import com.hihihihi.domain.model.MindmapNode
import io.github.hanhyo.composemindmap.model.MindMapNode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MindmapMapperTest {

    @Test
    fun `opaque node color survives domain round trip`() {
        val color = Color(0xFFFFCDD2)

        val domain = MindMapNode(
            id = "child",
            title = "child",
            color = color,
        ).toDomain(
            mindmapId = "mindmap",
            userId = "user",
            bookImage = null,
        )

        assertEquals(color, domain.toLibraryModel().node.color)
    }

    @Test
    fun `invalid stored color is ignored`() {
        val domain = MindmapNode(
            userId = "user",
            mindmapNodeId = "child",
            mindmapId = "mindmap",
            nodeTitle = "child",
            nodeEx = "",
            parentNodeId = "root",
            color = "invalid",
            icon = null,
            deleted = false,
            bookImage = null,
        )

        assertNull(domain.toLibraryModel().node.color)
    }
}
