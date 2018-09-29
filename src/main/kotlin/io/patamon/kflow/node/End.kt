package io.patamon.kflow.node

import io.patamon.kflow.core.ExecuteContext
import io.patamon.kflow.core.KFlowException
import io.patamon.kflow.node.NodeType.END

/**
 * Built-in end node
 */
class End(
        override val name: String = "__END__",
        override var type: NodeType = END
) : BaseNode() {

    /**
     * end node should not have next node
     */
    override fun addNext(node: Node) {
        throw KFlowException("end node should not have next node of [${node.name}].")
    }

    /**
     * just add prev
     */
    override fun addPrev(node: Node) {
        this.prevNodes.add(node)
    }

    override fun execute(context: ExecuteContext) {
        // 1. check current join nodes
        if (context.countDownJoinLocks(name)) {
            return
        }

        // 2. release main thread
        context.release()
    }

}