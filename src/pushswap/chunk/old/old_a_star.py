from time import sleep
from typing import Dict, List

from pushswap.dataclass.Heap import ImmutableHeap
from pushswap.dataclass.Move import Move
from pushswap.chunk.small_sort import explore_three, sort_three, sort_three_descending
from pushswap.dataclass.Stack import Stack
from pushswap.dataclass.PriorityQueue import PriorityQueue
from pushswap.utils.debug import iteration_info, sort_finish_info, stack_info

def small_sort_a(stack: Stack, open_set: PriorityQueue,
                 visited: List[int], visited_order: Dict[int, int],
                 iteration: int) -> bool:
    if len(stack.a) == 2 or len(stack.a) == 3:
        explore_three(stack, list(Move), len(stack.a),
                      open_set, visited, visited_order, iteration)
        moves = sort_three(stack)
        if not moves: return False
        stack.apply_all(moves)
        if hash(stack) in visited:
            return False
        stack.compute_chunk_heuristic()
        open_set.push(stack)
        # print("Applied sort_three on stack A. ", end="")
        stack_info(stack)
        return True
    return False

def small_sort_b(stack: Stack, open_set: PriorityQueue,
                 visited: List[int], visited_order: Dict[int, int],
                 iteration: int) -> bool:
    if len(stack.b) == 2 or len(stack.b) == 3:
        explore_three(stack, list(Move), len(stack.a),
                      open_set, visited, visited_order, iteration)
        moves = sort_three_descending(stack)
        if not moves: return False
        stack.apply_all(moves)
        if hash(stack) in visited:
            return False
        stack.apply_all([Move.PA] * len(stack.b))  # Push all from B to A
        explore_three(stack, list(Move), len(stack.a),
                      open_set, visited, visited_order, iteration)
        stack.compute_chunk_heuristic()
        open_set.push(stack)
        # print("Applied sort_three_descending on stack B. ", end="")
        stack_info(stack)
        return True
    return False

def small_sort_shortcut(stack: Stack, open_set: PriorityQueue,
                        visited: List[int], visited_order: Dict[int, int],
                        iteration: int) -> bool:
    if small_sort_a(stack, open_set, visited, visited_order, iteration):
        return True
    if small_sort_b(stack, open_set, visited, visited_order, iteration):
        return True
    return False

def compute_all_moves(stack: Stack, open_set: PriorityQueue) -> None:
    for move in Move:  # iterate over all possible Push Swap moves
        new_stack = stack.copy()
        if stack.move_path and stack.move_path[-1] == move.inverse():  # Skip the inverse of last move
            print(f"Skipping invalid move: {move}. Inverse of last move {stack.move_path[-1]}")
            continue
        if not new_stack.apply(move):
            continue  # skip invalid moves
        stack_info(stack)

        # Compute heuristic for the new stack
        new_stack.compute_chunk_heuristic()

        # Push into open set
        open_set.push(new_stack)

def push_and_sort(stack: Stack, goal_heap: ImmutableHeap) -> None:
    """
    Use A* algorithm to find the shortest sequence of moves to move numbers
    from Stack A to Stack B, while partially sorting Stack B in the process.
    """
    open_set: PriorityQueue = PriorityQueue()
    open_set.push(stack)
    visited: List[int] = []

    # Debug variables
    visited_order: Dict[int, int] = {}
    iteration = 0

    while open_set:
        iteration += 1
        stack = open_set.pop()
        sleep(0.000001)  # Make output print correctly.
        iteration_info(iteration, stack)

        if tuple(stack.a) == goal_heap:
            sort_finish_info(iteration, stack)
            return

        stack_hash = hash(stack)
        if stack_hash in visited:
            print(f"{list(stack.b)} is already explored (First seen in iteration {visited_order[stack_hash]})\n")
            continue
        visited.append(stack_hash)
        visited_order[stack_hash] = iteration

        # If it took the shortcut, reset open set.
        if small_sort_shortcut(stack, open_set, visited, visited_order, iteration):
            continue
        compute_all_moves(stack, open_set)
