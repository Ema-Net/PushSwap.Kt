from collections import deque
from .old_a_star import push_and_sort
from pushswap.chunk.small_sort import sort_three
from pushswap.dataclass.ChunkInfo import ChunkInfo
from typing import List
from pushswap.chunk.init_chunks import define_chunks_values
from pushswap.dataclass.Stack import Stack
from pushswap.dataclass.Heap import ImmutableHeap

def chunk_goal(goal_heap: ImmutableHeap, chunk: ChunkInfo) -> ImmutableHeap:
    chunk_goal_heap = []
    for num in goal_heap:
        if chunk.min_value <= num <= chunk.max_value:
            chunk_goal_heap.append(num)
    return tuple(chunk_goal_heap)

def chunk_sort_hybrid(numbers: List[int]) -> None:
    chunks = define_chunks_values(numbers)
    #list_chunks(chunks)
    current_chunk: ChunkInfo = chunks[0]
    stack = Stack(current_chunk, deque(numbers), deque(), [], 0)
    goal_heap: ImmutableHeap = tuple(sorted(numbers))

    if tuple(numbers) == goal_heap:
        print("The stack is already sorted.")
        return

    if len(numbers) <= 3:
        # Just mutate the original stack since we are done after this.
        for move in sort_three(stack):
            stack.apply(move)
        print("Sequence of moves:", " ".join(str(move) for move in stack.move_path))
        print("Final sorted stack:", list(stack.a))
        return

    push_and_sort(stack, goal_heap)