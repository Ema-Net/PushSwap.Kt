package me.emaryllis.data

import me.emaryllis.Settings.HASH_PRIME

class CircularBuffer(val capacity: Int, numList: List<Int> = emptyList()) : Collection<Int>, Cloneable {
	var buffer: IntArray = IntArray(capacity)
	private var head = 0
	override var size = 0
		private set

	private var maxValue: Int? = null
	private var maxDirty: Boolean = false

	init {
		buffer = IntArray(capacity)
		if (numList.isNotEmpty()) {
			require(numList.size <= capacity) { "List size exceeds buffer capacity" }
			numList.forEach { value ->
				buffer[(head + size) % capacity] = value
				size++
			}
			recalculateMax()
		}
	}

	private fun recalculateMax() {
		maxValue = if (isEmpty()) null else indices.maxOfOrNull { get(it) }
		maxDirty = false
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
		if (size < 2) return false
		return enqueue(dequeue())
	}

	/** Rotates both buffers to the left */
	fun rotateBoth(other: CircularBuffer): Boolean = this.rotate() && other.rotate()

	/** Rotates to the right (Increases indices for most elements)*/
	fun reverseRotate(): Boolean {
		if (size < 2) return false
		val lastIndex = (head + size - 1 + capacity) % capacity
		val last = buffer[lastIndex]
		buffer[lastIndex] = 0
		head = (head - 1 + capacity) % capacity
		buffer[head] = last
		// last could be new max
		if (maxValue == null || last > maxValue!!) {
			maxValue = last
			maxDirty = false
		}
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

	public override fun clone(): CircularBuffer {
		val copy = CircularBuffer(capacity)
		copy.buffer = buffer.copyOf()
		copy.head = head
		copy.size = size
		copy.maxValue = maxValue
		copy.maxDirty = maxDirty
		return copy
	}

	fun peek(): Int? = if (isEmpty()) null else buffer[head]

	val value: List<Int>
		get() = List(size) { get(it) }

	fun first() = if (isEmpty()) throw NoSuchElementException("CircularBuffer is empty.") else get(0)

	fun last() = if (isEmpty()) throw NoSuchElementException("CircularBuffer is empty.") else get(size - 1)

	fun max(): Int {
		if (maxDirty) recalculateMax()
		return maxValue ?: throw NoSuchElementException("CircularBuffer is empty.")
	}

	fun maxOrNull(): Int? {
		if (maxDirty) recalculateMax()
		return maxValue
	}

	/**
	 * Returns the logical index of the first occurrence of [element], or -1 if not found.
	 */
	fun indexOf(element: Int?): Int {
		for (i in indices) {
			if (get(i) == element) return i
		}
		return -1
	}

	// Overrides for equality and hashing
	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is CircularBuffer) return false
		if (this.size != other.size) return false
		for (i in indices) {
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
		val idx = (head + i) % capacity
		if (buffer[idx] == maxValue) maxDirty = true
		buffer[idx] = value
		if (maxValue == null || value > maxValue!!) {
			maxValue = value
			maxDirty = false
		}
	}

	/** Insert at logical tail */
	private fun enqueue(value: Int): Boolean {
		if (isFull()) return false
		buffer[(head + size) % capacity] = value
		size++
		if (maxValue == null || value > maxValue!!) {
			maxValue = value
			maxDirty = false
		}
		return true
	}

	/** Remove from logical head */
	private fun dequeue(): Int {
		require(isNotEmpty()) { "Buffer is empty" }
		val value = buffer[head]
		buffer[head] = 0 // clear garbage
		head = (head + 1) % capacity
		size--
		if (maxValue == value) maxDirty = true
		return value
	}
}
