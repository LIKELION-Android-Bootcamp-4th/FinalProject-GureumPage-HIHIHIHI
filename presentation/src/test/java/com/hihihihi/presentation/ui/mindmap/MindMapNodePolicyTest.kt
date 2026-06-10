package com.hihihihi.presentation.ui.mindmap

import io.github.hanhyo.composemindmap.model.MindMapNode
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MindMapNodePolicyTest {

    @Test
    fun `root node cannot be deleted`() {
        assertFalse(canDeleteMindMapNode(MindMapNode(id = "root", title = "root")))
    }

    @Test
    fun `child node can be deleted`() {
        assertTrue(
            canDeleteMindMapNode(
                MindMapNode(id = "child", title = "child", parentId = "root"),
            ),
        )
    }
}
