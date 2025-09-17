from dataclasses import dataclass

@dataclass
class ChunkInfo:
    min_value: int
    max_value: int
    mid_value: int

    def size(self) -> int:
        return self.max_value - self.min_value + 1
