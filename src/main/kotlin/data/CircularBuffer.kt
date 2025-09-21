package me.emaryllis.data

class CircularBuffer(private val capacity: Int, numList: List<Int> = emptyList()) {
	var buffer: IntArray = IntArray(capacity)
	private var head = 0  // points to next element to read
	var size = 0
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

	val isEmpty get() = size == 0
	val isFull get() = size == capacity

	/** Insert at logical tail */
	fun enqueue(value: Int): Boolean {
		if (isFull) return false
		buffer[(head + size) % capacity] = value
		size++
		return true
	}

	/** Remove from logical head */
	fun dequeue(): Int? {
		if (isEmpty) return null
		val value = buffer[head]
		buffer[head] = 0 // clear garbage
		head = (head + 1) % capacity
		size--
		return value
	}

	fun swap(): Boolean {
		if (size < 2) return false
		val temp = this[0]
		this[0] = this[1]
		this[1] = temp
		return true
	}

	/** Rotates to the left */
	fun rotate(): Boolean {
		if (size <= 1) return false
		val first = dequeue()!!
		return enqueue(first)
	}

	/** Rotates both buffers to the left */
	fun rotateBoth(other: CircularBuffer): Boolean = this.rotate() && other.rotate()

	/** Rotates to the right */
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
		if (isEmpty || dest.isFull) return false
		val value = dequeue()!!
		// put into front of dest
		dest.head = (dest.head - 1 + dest.capacity) % dest.capacity
		dest.buffer[dest.head] = value
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

	fun peek(): Int? = if (isEmpty) null else buffer[head]

	val value: List<Int>
		get() = List(size) { get(it) }

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
		for (i in 0 until size) {
			result = 31 * result + buffer[(head + i) % capacity]
		}
		return result
	}

	operator fun get(i: Int): Int {
		require(i in 0 until size) { "Index $i out of bounds (size=$size)" }
		return buffer[(head + i) % capacity]
	}

	// For testing purposes - Purposely made it inconvenient to prevent accidental use
	operator fun set(i: Int, value: Int) {
		require(i in 0 until size) { "Index $i out of bounds (size=$size)" }
		buffer[(head + i) % capacity] = value
	}
}
