from copy import deepcopy
from dataclasses import dataclass
from typing import List
from .ChunkInfo import ChunkInfo
from .Heap import Heap
from .Move import Move

@dataclass
class Stack:
    current_chunk: ChunkInfo
    a: Heap
    b: Heap
    move_path: List[Move]
    current_cost: int
    heuristic: int = 0

    def __post_init__(self):
        pass
        # self.compute_chunk_heuristic()

    def __eq__(self, other):
        if not isinstance(other, Stack):
            return False
        if not self.b:
            return not other.b and tuple(self.a) == tuple(other.a)
        return tuple(self.b) == tuple(other.b)

    def __hash__(self) -> int:
        data = self.b if self.b else self.a
        prime = 31
        return sum(val * (prime ** i) for i, val in enumerate(data))

    def get_cost(self) -> int:
        return self.current_cost + self.heuristic

    def copy(self) -> "Stack":
        """Return a deep copy of this Stack instance."""
        return Stack(
            deepcopy(self.current_chunk),
            deepcopy(self.a),
            deepcopy(self.b),
            deepcopy(self.move_path),
            self.current_cost,
            self.heuristic
        )

    def apply(self, move: Move) -> bool:
        """Apply a Push Swap move to the current stack and append to move_path."""
        success = False
        match move:
            case Move.SA: success = Move.swap(self.a)
            case Move.SB: success = Move.swap(self.b)
            case Move.SS: success = Move.swap(self.a) and Move.swap(self.b)
            case Move.PA: success = Move.push(self.b, self.a)
            case Move.PB: success = Move.push(self.a, self.b)
            case Move.RA: success = Move.rotate(self.a)
            case Move.RB: success = Move.rotate(self.b)
            case Move.RR: success = Move.rotate(self.a) and Move.rotate(self.b)
            case Move.RRA: success = Move.reverse_rotate(self.a)
            case Move.RRB: success = Move.reverse_rotate(self.b)
            case Move.RRR: success = Move.reverse_rotate(self.a) and Move.reverse_rotate(self.b)
        if success:
            self.move_path.append(move)
            self.current_cost += 1

        # Debug #
        # if not success:
        #     print(f"Invalid move: {move}. ", end="")
        #     match move:
        #         case Move.SA | Move.RA | Move.RRA | Move.PB:
        #             if not self.a:
        #                 print("Stack A is empty.")
        #             elif len(self.a) == 1:
        #                 print("Stack A has only one element.")
        #         case Move.SB | Move.RB | Move.RRB | Move.PA:
        #             if not self.b:
        #                 print("Stack B is empty.")
        #             elif len(self.b) == 1:
        #                 print("Stack B has only one element.")
        #         case Move.SS | Move.RR | Move.RRR:
        #             if not self.a:
        #                 print("Stack A is empty.")
        #             elif len(self.a) == 1:
        #                 print("Stack A has only one element.")
        #             elif not self.b:
        #                 print("Stack B is empty.")
        #             elif len(self.b) == 1:
        #                 print("Stack B has only one element.")
        # End Debug #
        return success

    def apply_all(self, moves: List[Move]) -> None:
        """Apply a list of Push Swap moves to the current stack."""
        for move in moves:
            self.apply(move)
