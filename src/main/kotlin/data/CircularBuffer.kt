package me.emaryllis.data

import me.emaryllis.Settings.HASH_PRIME

class CircularBuffer(val capacity: Int, numList: List<Int> = emptyList()) : Collection<Int> {
	var buffer: IntArray = IntArray(capacity)
	private var head = 0  // points to next element to read
	override var size = 0
		private set

	init {
		buffer = IntArray(capacity)  // allocate full capacity
		if (numList.isNotEmpty()) {
			require(numList.size <= capacity) { "List size exceeds buffer capacity" }
			numList.forEach { value ->
				buffer[(head + size) % capacity] = value
				size++
			}
		}
	}

	fun swap(): Boolean {
		if (size < 2) return false
		val temp = get(0)
		this[0] = get(1)
		this[1] = temp
		return true
	}

	/** Rotates to the left (Decreases indices for most elements)*/
	fun rotate(): Boolean {
		if (size <= 1) return false
		return enqueue(dequeue())
	}

	/** Rotates both buffers to the left */
	fun rotateBoth(other: CircularBuffer): Boolean = this.rotate() && other.rotate()

	/** Rotates to the right (Increases indices for most elements)*/
	fun reverseRotate(): Boolean {
		if (size <= 1) return false
		val lastIndex = (head + size - 1 + capacity) % capacity
		val last = buffer[lastIndex]
		buffer[lastIndex] = 0
		head = (head - 1 + capacity) % capacity
		buffer[head] = last
		return true
	}

	/** Rotates both buffers to the right */
	fun reverseRotateBoth(other: CircularBuffer): Boolean = this.reverseRotate() && other.reverseRotate()

	fun push(dest: CircularBuffer): Boolean {
		if (isEmpty() || dest.isFull()) return false
		// put into front of dest
		dest.head = (dest.head - 1 + dest.capacity) % dest.capacity
		dest.buffer[dest.head] = dequeue()
		dest.size++
		return true
	}

	fun clone(): CircularBuffer {
		val copy = CircularBuffer(capacity)
		copy.buffer = buffer.copyOf()
		copy.head = head
		copy.size = size
		return copy
	}

	fun peek(): Int? = if (isEmpty()) null else buffer[head]

	val value: List<Int>
		get() = List(size) { get(it) }

	// Overrides for equality and hashing
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is CircularBuffer) return false
		if (this.size != other.size) return false
		for (i in 0 until size) {
			if (this.buffer[(head + i) % capacity] != other.buffer[(other.head + i) % other.capacity]) return false
		}
		return true
	}

	override fun hashCode(): Int {
		var result = size
		for (i in indices) {
			result = HASH_PRIME * result + get(i)
		}
		return result
	}

	// Overrides for Collection interface

	@Suppress("ReplaceSizeZeroCheckWithIsEmpty")
	override fun isEmpty(): Boolean = size == 0 // NOSONAR: Replacing with 'isEmpty' causes inf recursion loop

	fun isFull(): Boolean = size == capacity

	override fun iterator(): Iterator<Int> = value.iterator() // NOSONAR: Can't use 'by' since list size is dynamic

	override fun contains(element: Int): Boolean = value.contains(element)

	override fun containsAll(elements: Collection<Int>): Boolean = elements.all { contains(it) }

	// Overrides for array-like access
	operator fun get(i: Int): Int {
		require(i in indices) { "Index $i out of bounds (size=$size)" }
		return buffer[(head + i) % capacity]
	}

	private operator fun set(i: Int, value: Int) = setIfYouNeedTo(i, value)

	// For testing purposes
	fun setIfYouNeedTo(i: Int, value: Int) {
		require(i in indices) { "Index $i out of bounds (size=$size)" }
		buffer[(head + i) % capacity] = value
	}

	/** Insert at logical tail */
	private fun enqueue(value: Int): Boolean {
		if (isFull()) return false
		buffer[(head + size) % capacity] = value
		size++
		return true
	}

	/** Remove from logical head */
	private fun dequeue(): Int {
		require(isNotEmpty()) { "Buffer is empty" }
		val value = buffer[head]
		buffer[head] = 0 // clear garbage
		head = (head + 1) % capacity
		size--
		return value
	}
}
