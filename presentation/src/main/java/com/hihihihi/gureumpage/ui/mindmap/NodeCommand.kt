package com.hihihihi.gureumpage.ui.mindmap

import com.gyso.treeview.TreeViewEditor

class AddNodeCommand(
    private val editor: TreeViewEditor,
    private val parent: TreeNode,
    private val child: TreeNode,
) : Command {
    override fun undo() = editor.removeNode(child)
    override fun redo() = editor.addChildNodes(parent, child)
}

class RemoveNodeCommand(
    private val editor: TreeViewEditor,
    private val node: TreeNode,
    private val parent: TreeNode,
) : Command {
    override fun undo() = editor.addChildNodes(parent, node)
    override fun redo() = editor.removeNode(node)
}