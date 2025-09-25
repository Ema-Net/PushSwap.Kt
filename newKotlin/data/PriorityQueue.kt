package me.emaryllis.data

class PriorityQueue {
	private val heap = mutableListOf<Stack>()

	val size: Int get() = heap.size
	fun isNotEmpty(): Boolean = heap.isNotEmpty()

	fun peek(): Stack = heap.first()

	fun push(element: Stack) {
		heap.add(element)
		siftUp(heap.lastIndex)
	}

	fun pop(): Stack {
		if (heap.isEmpty()) error("Stack is empty")
		val top = heap.first()
		val last = heap.removeAt(heap.lastIndex)
		if (heap.isNotEmpty()) {
			heap[0] = last
			siftDown(0)
		}
		return top
	}

	fun retainAll(predicate: (Stack) -> Boolean) {
		heap.retainAll(predicate)
	}

	private fun siftUp(index: Int) {
		var i = index
		val value = heap[i]
		while (i > 0) {
			val parent = (i - 1) / 2
			if (value.currentCost >= heap[parent].currentCost) break
			heap[i] = heap[parent]
			i = parent
		}
		heap[i] = value
	}

	private fun siftDown(index: Int) {
		var i = index
		val value = heap[i]
		val n = heap.size
		while (2 * i + 1 < n) {
			val left = 2 * i + 1
			val right = 2 * i + 2
			var smallestChild = left
			if (right < n && heap[right].currentCost < heap[left].currentCost) {
				smallestChild = right
			}
			if (heap[smallestChild].currentCost >= value.currentCost) break
			heap[i] = heap[smallestChild]
			i = smallestChild
		}
		heap[i] = value
	}
}
