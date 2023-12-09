package tdr.aoc.utils

class Graph<NODE, LINK>(
    val nodes: MutableMap<NODE, Map<LINK, NODE>> = mutableMapOf()
) {

    fun addNode(key: NODE, links: Map<LINK, NODE>) {
        nodes[key] = links
    }

    fun next(key: NODE, link: LINK): NODE = nodes.getValue(key).getValue(link)

}