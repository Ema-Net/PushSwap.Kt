from enum import Enum
from .Heap import Heap

class Move(Enum):
    SA = "sa"
    SB = "sb"
    SS = "ss"
    PA = "pa"
    PB = "pb"
    RA = "ra"
    RB = "rb"
    RR = "rr"
    RRA = "rra"
    RRB = "rrb"
    RRR = "rrr"

    def __str__(self):
        return self.value

    def inverse(self) -> "Move":
        inverse_map = {
            Move.SA: Move.SA,
            Move.SB: Move.SB,
            Move.SS: Move.SS,
            Move.PA: Move.PB,
            Move.PB: Move.PA,
            Move.RA: Move.RRA,
            Move.RB: Move.RRB,
            Move.RR: Move.RRR,
            Move.RRA: Move.RA,
            Move.RRB: Move.RB,
            Move.RRR: Move.RR,
        }
        return inverse_map[self]

    @staticmethod
    def swap(heap: Heap) -> bool:
        if len(heap) >= 2:
            heap[0], heap[1] = heap[1], heap[0]
            return True
        return False

    @staticmethod
    def rotate(heap: Heap) -> bool:
        if heap and len(heap) >= 2:
            heap.append(heap.popleft())
            return True
        return False

    @staticmethod
    def reverse_rotate(heap: Heap) -> bool:
        if heap and len(heap) >= 2:
            heap.appendleft(heap.pop())
            return True
        return False

    @staticmethod
    def push(src: Heap, dst: Heap) -> bool:
        if src:
            dst.appendleft(src.popleft())
            return True
        return False
