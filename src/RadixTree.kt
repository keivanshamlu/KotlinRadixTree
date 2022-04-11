import java.util.*

/**
 * (In the following comparisons, it is assumed that the keys are of length k
 * and the data structure contains n members.)
 */
class RadixTree<T>(private val root: Node<T> = Node(false)) {

    fun getAllChildern(): List<T>{

        return root.getAllChildren("")
    }
    /**
     * checks whether tree is empty or not
     */
    fun isEmpty(): Boolean = root.edges.isEmpty()
    
    /**
     * compare two strings and will return first index which two strings differ
     */
    private fun firstDiff(word: String, edgeWord: String): Int {
        for (i in 1 until word.length.coerceAtMost(edgeWord.length)) {
            if (word[i] != edgeWord[i]) return i
        }
        return NO_MISMATCH
    }

    /**
     * get list of objects and will insert them all into radix tree
     */
    fun insertAll(items : List<Item<T>>) = items.map { insert(it) }
    /**
     * get an object and will insert it to radix tree
     */
    fun insert(item: Item<T>) {

        var current = root
        var currIndex = 0

        //Iterative approach
        while (currIndex < item.label.length) {
            val transitionChar = item.label.toLowerCase()[currIndex]
            val curEdge = current.getTransition(transitionChar)
            //Updated version of the input word
            val currStr = item.label.toLowerCase().substring(currIndex)

            //There is no associated edge with the first character of the current string
            //so simply add the rest of the string and finish
            if (curEdge == null) {
                current.addEdge(transitionChar,Edge(currStr, Node(true, item)))
                break
            }
            var splitIndex = firstDiff(currStr, curEdge.label)
            if (splitIndex == NO_MISMATCH) {
                //The edge and leftover string are the same length
                //so finish and update the next node as a word node
                if (currStr.length == curEdge.label.length) {
                    curEdge.next.isLeaf = true
                    curEdge.next.item = item
                    break
                } else if (currStr.length < curEdge.label.length) {
                    //The leftover word is a prefix to the edge string, so split
                    val suffix = curEdge.label.substring(currStr.length)
                    curEdge.label = currStr
                    val newNext = Node<T>(true, item)
                    val afterNewNext = curEdge.next
                    curEdge.next = newNext
                    newNext.addEdge(suffix, afterNewNext)
                    break
                } else { 
                    //There is leftover string after a perfect match
                    splitIndex = curEdge.label.length
                }
            } else {
                //The leftover string and edge string differed, so split at point
                val suffix = curEdge.label.substring(splitIndex)
                curEdge.label = curEdge.label.substring(0, splitIndex)
                val prevNext = curEdge.next
                curEdge.next = Node(false)
                curEdge.next.addEdge(suffix, prevNext)
            }

            //Traverse the tree
            current = curEdge.next
            currIndex += splitIndex
        }
    }

    /**
     * searches for a prefix in radix tree
     * first of all search for particular node and then calls [getAllChildren]
     * and it will get all children of that node which is found data
     */
    fun search(word: String): List<T> {

        if(word.isEmpty())return emptyList()
        var current = root
        var currIndex = 0
        while (currIndex < word.length) {
            val transitionChar = word.toLowerCase()[currIndex]
            val edge = current.getTransition(transitionChar) ?: kotlin.run {
                return listOf()
            }
            currIndex += edge.label.length
            current = edge.next
        }
        return current.getAllChildren(word.toLowerCase())
    }

    companion object {
        private const val NO_MISMATCH = -1
    }

}

/**
 * represents edges of a node, containts the lable
 * of edge and the node the edge is referring to
 */
class Edge<T>(var label: String, var next: Node<T>)

/**
 * holds item and the key that radix tree works with
 */
class Item<T>(var label: String, var item: T)

/**
 * represents node of the radix tree, contains group
 * of edges that are hold a tree map so it's sorted
 * alphanumeric all the time so whenever we call [getAllChildren]
 * we get a list of <T> which is in alphanumeric order
 */
class Node<T>(var isLeaf: Boolean, var item: Item<T>? = null) {

    // i used TreeMap so it can keep everything sorted
    var edges: TreeMap<Char, Edge<T>> = TreeMap()

    /**
     * get the edge that a Char is referring in treemap
     */
    fun getTransition(transitionChar: Char): Edge<T>? {
        return edges[transitionChar]
    }

    /**
     * adds a edge in edges treemap
     */
    fun addEdge(label: String, next: Node<T>) {
        edges[label[0]] = Edge(label, next)
    }
    /**
     * adds a edge in edges treemap
     */
    fun addEdge(char : Char , edge: Edge<T>){
        edges[char] = edge
    }


    /**
     * gets a node in radix tree and the prefix text of that node
     * recursively calls itself until it reaches leaves and then
     * it will add them to a list
     */
    fun getAllChildren(tillNow: String): List<T> {

        val list = mutableListOf<T>()
        if (isLeaf && item?.label?.toLowerCase()?.startsWith(tillNow) == true && item?.item != null) {
            item?.item?.let { return listOf(it) }
        }
        edges.map {
            if (it.value.next.isLeaf) list.add(it.value.next.item!!.item)
            else list.addAll(
                it.value.next.getAllChildren(
                    StringBuilder()
                        .append(tillNow)
                        .append(it.value.label)
                        .toString()
                )
            )
        }
        return list
    }
}