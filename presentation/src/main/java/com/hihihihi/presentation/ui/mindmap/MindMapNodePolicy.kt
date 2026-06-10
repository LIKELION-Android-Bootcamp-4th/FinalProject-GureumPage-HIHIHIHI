package com.hihihihi.presentation.ui.mindmap

import io.github.hanhyo.composemindmap.model.MindMapNode

internal fun canDeleteMindMapNode(node: MindMapNode): Boolean = node.parentId != null
