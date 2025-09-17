import heapq
from typing import List
from pushswap.dataclass.Stack import Stack

class PriorityQueue:
    queue: List[Stack] = []
    counter = 0

    def push(self, stack: Stack):
        """Push item into the priority queue, ordered by get_cost()."""
        heapq.heappush(self.queue, (stack.get_cost(), self.counter, stack))
        self.counter += 1

    def pop(self) -> Stack:
        """Pop the item with the lowest cost."""
        if not self.queue: raise IndexError("PriorityQueue index out of range")
        return heapq.heappop(self.queue)[2]  # return the Stack object

    def __len__(self):
        return len(self.queue)
