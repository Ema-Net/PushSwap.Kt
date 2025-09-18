from typing import List, Tuple
from ..dataclass.ChunkInfo import ChunkInfo

MAX_CHUNK_SIZE = 8  # Maximum size of each chunk

def define_chunks_values(stack: List[int]) -> Tuple[ChunkInfo, ...]:
    """
    Compute chunks based on actual values in the stack.
    - Maximum chunk size = MAX_CHUNK_SIZE
    - Returns a tuple of ChunkInfo with min/max/mid values
    """
    if not stack:
        return ()

    sorted_values = sorted(stack)
    stack_size = len(sorted_values)
    chunk_size = min(MAX_CHUNK_SIZE, stack_size)
    num_chunks = (stack_size + chunk_size - 1) // chunk_size  # ceiling division

    chunks = []
    start_idx = 0
    for _ in range(num_chunks):
        size = min(chunk_size, stack_size - start_idx)
        min_value = sorted_values[start_idx]
        max_value = sorted_values[start_idx + size - 1]
        mid_value = (min_value + max_value) // 2
        chunks.append(ChunkInfo(min_value, max_value, mid_value))
        start_idx += size

    return tuple(chunks)
