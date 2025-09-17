from typing import Dict, List

from ..dataclass.PriorityQueue import PriorityQueue
from ..dataclass.Heap import Heap
from ..dataclass.Move import Move
from ..dataclass.Stack import Stack

def sort_three(stack: Stack) -> List[Move]:
    """Used to sort stack a with 3 or fewer elements."""
    if stack.a == sorted(stack.a):
        return []
    if len(stack.a) < 2:
        return []
    if len(stack.a) == 2:
        if stack.a[0] > stack.a[1]:
            return [Move.SA]
        return []

    a = stack.a[0]
    b = stack.a[1]
    c = stack.a[2]

    if a < b < c:
        return []
    if b < a < c and b < c:
        return [Move.SA]
    elif a > b > c:
        return [Move.SA, Move.RRA]
    elif a > b and b < c < a:
        return [Move.RA]
    elif a < b and b > c > a:
        return [Move.SA, Move.RA]
    elif b > a > c and b > c:
        return [Move.RRA]
    raise ValueError("Invalid state:", stack.a)

def sort_three_descending(stack: Stack) -> List[Move]:
    """
    Used to sort stack b with 3 or fewer elements in descending order.
    This is useful when preparing to push elements back to stack a.
    """
    if stack.b == sorted(stack.b, reverse=True):
        return []
    if len(stack.b) < 2:
        return []
    if len(stack.b) == 2:
        if stack.b[0] < stack.b[1]:
            return [Move.SB]
        return []

    a = stack.b[0]
    b = stack.b[1]
    c = stack.b[2]

    if a > b > c:
        return []
    elif b > a > c and b > c:
        return [Move.SB]
    elif a < b < c:
        return [Move.SB, Move.RRB]
    elif a < b and b > c > a:
        return [Move.RB]
    elif a > b and b < c < a:
        return [Move.SB, Move.RB]
    elif b < a < c and b < c:
        return [Move.RRB]

    raise ValueError("Invalid state:", stack.b)

def sort_three_permutations(heap: Heap) -> List[List[int]]:
    """Return all permutations of 2 or 3 elements manually."""
    result = []
    n = len(heap)
    if n == 2:
        result.append([heap[0], heap[1]])
        result.append([heap[1], heap[0]])
    elif n == 3:
        a, b, c = heap
        result.append([a, b, c])
        result.append([a, c, b])
        result.append([b, a, c])
        result.append([b, c, a])
        result.append([c, a, b])
        result.append([c, b, a])
    else:
        result.append(heap[:])  # single element
    return result

def explore_three(stack: Stack, moves: List[Move],
                  depth: int, open_set: PriorityQueue,
                  visited: List[int], visited_order: Dict[int, int],
                  iteration: int) -> None:
    if depth == 0:
        return
    for move in moves:
        new_stack = stack.copy()
        if new_stack.apply(move):
            stack_hash = hash(new_stack)
            if stack_hash in visited:
                continue
            visited.append(stack_hash)
            visited_order[stack_hash] = iteration
            open_set.push(new_stack)
            iteration += 1
            explore_three(new_stack, moves, depth - 1, open_set, visited, visited_order, iteration)
