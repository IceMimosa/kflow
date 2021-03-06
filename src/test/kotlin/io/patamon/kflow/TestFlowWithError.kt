package io.patamon.kflow

import io.patamon.kflow.core.KFlowException
import org.junit.Test

/**
 * Test simple flow
 */
@Suppress("DIVISION_BY_ZERO")
class TestFlowWithError {

    /**
     * start ---> node1 ---> node2 ---> end
     */
    @Test(expected = KFlowException::class)
    fun testSimpleFlow() {
        val flow = flow {
            start to "node1"
            "node1" to "node2"
            "node2" to end

            "node1" {
                handler { flowData ->
                    // throw error
                    if (flowData["error"] as Boolean) 1 / 0

                    println("${Thread.currentThread().name} -> node1 handle")
                }
            }

            "node2" {
                handler {
                    println("${Thread.currentThread().name} -> node2 handle")
                }
            }
        }
        flow.execute(mutableMapOf("error" to true))
        // flow.execute(mutableMapOf("error" to false))
    }

    /**
     * start ---> node1 ------> node2 ---> node3 ----> end
     *              |                                  ↑
     *              |       |-----> f_node1 ---        |
     *              |---> f_node              |---> j_node
     *                      |-----> f_node2 ---
     */
    @Test
    fun testForkJoinFlow() {
        val flow = flow {
            start to "node1"
            "node1" to "node2"
            "node1" to "f_node"

            "node2" to "node3"
            "node3" to end

            "f_node" to "f_node1"
            "f_node" to "f_node2"

            "f_node1" to "j_node"
            "f_node2" to "j_node"

            "j_node" to end

            "node1" {
                handler {
                    println("${Thread.currentThread().name} -> node1 handle")
                }
            }

            "node2" {
                handler {
                    Thread.sleep(2000)
                    println("${Thread.currentThread().name} -> node2 handle")
                }
            }

            "node3" {
                handler {
                    println("${Thread.currentThread().name} -> node3 handle")
                }
            }

            "f_node" {
                handler { flowData ->
                    // throw error
                    if (flowData["error"] as Boolean) 1 / 0

                    println("${Thread.currentThread().name} -> f_node handle")
                }
            }

            "f_node1" {
                handler {
                    println("${Thread.currentThread().name} -> f_node1 handle")
                }
            }

            "f_node2" {
                handler {
                    println("${Thread.currentThread().name} -> f_node2 handle")
                }
            }

            "j_node" {
                handler {
                    println("${Thread.currentThread().name} -> j_node handle")
                }
            }
        }

        try {
            // TODO: interrupt node2
            flow.execute(mutableMapOf("error" to true))
        } catch (e: Exception) {
            // ignore
        }
        flow.execute(mutableMapOf("error" to false))
    }

}