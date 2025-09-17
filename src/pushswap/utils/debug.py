import sys
from typing import Tuple

from ..dataclass import Stack
from ..dataclass.ChunkInfo import ChunkInfo

def list_chunk(chunk: ChunkInfo) -> None:
    print(f"Chunk: {chunk.min_value}-{chunk.max_value}({chunk.mid_value})", end="")


def list_chunks(chunks: Tuple[ChunkInfo, ...]) -> None:
    print("Defined Chunks: [", end="")
    no_of_chunks = len(chunks)
    for i in range(no_of_chunks - 1):
        list_chunk(chunks[i])
        print(", ", end="")
    list_chunk(chunks[no_of_chunks - 1])
    print("]")


def iteration_info(iteration: int, stack: Stack):
    print("-" * 50)
    print(f"Iteration {iteration}")
    print("Current ", end="")
    list_chunk(stack.current_chunk)
    print(f" A={stack.a}, B={stack.b}, ", end="")
    cost_info(stack)

def cost_info(stack: Stack):
    print(f"g={stack.current_cost}, h={stack.heuristic}, f={stack.get_cost()}", end="")

def stack_info(stack: Stack):
    print(f"New state: A={stack.a}, B={stack.b}, ", end="")
    cost_info(stack)
    print(".")

def sort_finish_info(iteration: int, stack: Stack):
    if stack.b:
        print("Warning: Stack B is not empty!", stack.a, stack.b, file=sys.stderr)
    elif tuple(stack.a) != tuple(sorted(stack.a)):
        print("Warning: Stack A is not sorted!", stack.a, stack.b, file=sys.stderr)
    else:
        print(f"\nGoal reached in {stack.current_cost} moves after {iteration} iterations!")
    print("Sequence of moves:", ", ".join(str(move) for move in stack.move_path))
    print("-" * 50)

def sort_finish_chunk_info(iteration: int, stack: Stack):
    print(f"\nChunk sorted in {stack.current_cost} moves after {iteration} iterations!")
    print("Sequence of moves:", ", ".join(str(move) for move in stack.move_path))
    print("Final Stack:", stack.a, stack.b)
    print("-" * 50)

