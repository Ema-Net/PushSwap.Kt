from pushswap.dataclass.ChunkInfo import ChunkInfo
from pushswap.dataclass.Heap import ImmutableHeap

def swap_improves_sortedness(a: ImmutableHeap, chunk: ChunkInfo) -> bool:
    """Check if swapping top two in A improves sortedness (can peek at all of A)."""
    if len(a) < 2:
        return False

    original_prefix = longest_desc_prefix_chunk(a, chunk)
    original_lss = longest_sorted_subseq_chunk(a, chunk)

    swapped = (a[1], a[0]) + a[2:]

    swapped_prefix = longest_desc_prefix_chunk(swapped, chunk)
    swapped_lss = longest_sorted_subseq_chunk(swapped, chunk)

    return max(swapped_prefix, swapped_lss) > max(original_prefix, original_lss)

def best_insertion_positions(stack_a: ImmutableHeap, stack_b: ImmutableHeap, chunk: ChunkInfo) -> int:
    """
    Estimate minimal moves to insert numbers from A into B
    such that B remains partially sorted.
    """
    cost = 0
    a_len = len(stack_a)
    b_len = len(stack_b)

    for x in stack_a:
        if not (chunk.min_value <= x <= chunk.max_value):
            continue

        # Rotations needed in A to bring x to top
        idx_a = stack_a.index(x)
        rotations_a = min(idx_a, a_len - idx_a)

        # Find best insertion position in B to keep B partially sorted
        if b_len == 0:
            rotations_b = 0
        else:
            # Find first element in B smaller than x (assuming descending B)
            insert_idx = next((i for i, val in enumerate(stack_b) if val < x), b_len)
            rotations_b = min(insert_idx, b_len - insert_idx)

        # Add moves for push + rotations
        cost += rotations_a + rotations_b + 1  # +1 for the push itself

    return cost

def longest_desc_prefix_chunk(a: ImmutableHeap, chunk: ChunkInfo) -> int:
    """Longest descending prefix in stack A within the current chunk."""
    count = 0
    prev = float('-inf')
    for x in a:
        if chunk.min_value <= x <= chunk.max_value:
            if x >= prev:
                count += 1
                prev = x
            else:
                break
    return count

def longest_sorted_subseq_chunk(a: ImmutableHeap, chunk: ChunkInfo) -> int:
    """Longest ascending subsequence in A within the current chunk."""
    values = [x for x in a if chunk.min_value <= x <= chunk.max_value]
    if not values:
        return 0
    dp = [1] * len(values)
    for i in range(len(values)):
        for j in range(i):
            if values[j] <= values[i]:
                dp[i] = max(dp[i], dp[j] + 1)
    return max(dp)

def min_rotations_b_to_a_chunk(a: ImmutableHeap, b: ImmutableHeap, chunk: ChunkInfo) -> int:
    """Compute minimal rotations to insert B elements into A, chunk-aware."""
    total_cost = 0
    a_len = len(a)
    for x in b:
        if not (chunk.min_value <= x <= chunk.max_value):
            continue

        # Find the best insertion index in descending A
        insert_idx = a_len
        for i, val in enumerate(a):
            if val > x:
                insert_idx = i
                break
        rotations = min(insert_idx, a_len - insert_idx)
        total_cost += rotations + 1  # +1 for the push
    return total_cost

def sorted_order_penalty_chunk(a: ImmutableHeap, b: ImmutableHeap, chunk: ChunkInfo) -> int:
    """Penalty if inserting B would break sortedness in A, chunk-aware."""
    penalty = 0
    lss_len = longest_sorted_subseq_chunk(a, chunk)
    chunk_values = [x for x in b if chunk.min_value <= x <= chunk.max_value]
    for x in chunk_values:
        if lss_len > 0 and x < min(a[:lss_len]):
            penalty += 1
    return penalty

def compute_chunk_heuristic(a: ImmutableHeap, b: ImmutableHeap, chunk: ChunkInfo) -> int:
    """
    Full enhanced heuristic:
    - Longest descending prefix in A
    - Longest sorted subsequence in A
    - Min rotations from B → A
    - Penalty for breaking A's sorted order
    - Only numbers in the current chunk are considered
    """
    prefix_len = longest_desc_prefix_chunk(a, chunk)
    lss_len = longest_sorted_subseq_chunk(a, chunk)
    insert_cost = min_rotations_b_to_a_chunk(a, b, chunk)
    penalty = sorted_order_penalty_chunk(a, b, chunk)

    heuristic = (len(a) - max(prefix_len, lss_len)) + insert_cost + penalty

    if swap_improves_sortedness(a, chunk):
        heuristic -= 1  # discount for beneficial swap

    return max(0, heuristic)