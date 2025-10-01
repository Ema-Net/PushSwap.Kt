# A* Overview (High-Level)

## Purpose

Use A* search to integrate one chunk of values at a time into stack A so that all values of chunks 0..k form protected,
contiguous (circular) ascending blocks that can later be globally sorted by rotation without re‑touching earlier chunks.

## Glossary (Quick)

- Stack A / B: Working buffer (A) and auxiliary buffer (B) for pushes.
- Chunk: Dense ascending slice of the globally sorted list (size ≤ MAX_CHUNK_SIZE).
- Processed Chunk: Its values are all in A as one contiguous ascending block (circular allowed).
- Protected Block: A processed chunk’s block; no internal reordering, no fragmentation, no removal.
- Natural Gap: Original, untouched region of unprocessed values that happens to sit between processed blocks; disappears
  once those values’ chunk is processed.
- Normalizing Rotation: Conceptual rotation placing the smallest processed value at index 0 to inspect block order.

## High-Level Flow

1. Partition input into dense chunks (sorted order slices).
2. For each chunk k:
    - Run A* to position all values ≤ chunk_k.max so processed blocks remain/ become contiguous ascending (modulo
      natural gaps still unprocessed).
    - When goal for chunk k is met, mark chunk k protected; forbid disruptive moves.
3. Final rotation (optional) yields globally sorted A.

## State Cost (f = g + h)

- g = move count (encoded in history / packed list).
- h = MixedHeuristic (estimates remaining work for current chunk):
    * Ascending prefix length of current chunk values.
    * Inversions (local) + descending disorder in B.
    * Min push / pull rotation cost for next chunk value movement.

## Goal Condition (Chunk k)

B is empty AND all values ≤ chunk_k.max:

- Are strictly ascending in A (when read in their in‑buffer order) AND
- Form a single contiguous block (circular) OR contiguous processed blocks separated only by natural gaps of unprocessed
  future values (accepted until those values' chunk is processed).
- No processed value appears outside its block.

## Successor Generation & Pruning (Summary)

- Disallow inverse backtracking (move immediately undoing last).
- PB only when the first index of A is an unprocessed current-chunk value.
- For processed blocks: forbid PB (cannot remove), forbid swaps touching processed elements, forbid PA that would inject
  into or between processed blocks.
- Rotation pruning can reject rotations fragmenting a processed block.

## Protection Rules (Enforced Once Processed)

- Block must stay contiguous & ascending (circular allowed).
- Only moves that keep the block intact (rotations) are permitted; no internal swaps, no partial extractions.
- Natural gaps may exist only while their values remain unprocessed.

## Invariants (Must Hold After Each Chunk Finalization)

1. Each processed chunk = one protected ascending circular block.
2. Processed chunk blocks (after normalizing rotation) appear in ascending chunk order.
3. No new gap introduced between two processed blocks (any gap present is natural & still unprocessed).
4. No processed value resides in B.
5. Goal test for a chunk implies invariants 1–4.

---

## Rules:

1. Fully processed chunk
    - Definition: All values of that chunk are in buffer A, in strictly ascending order, stored in one contiguous run (
      may wrap end→start circularly).
    - Once a chunk is marked as fully processed. It is considered protected. See rule 6.
    - Rules:
        * Must remain a single contiguous (possibly circular) ascending run of exactly its values.
        * Relativity cannot be disrupted (no swapping of its elements).
        * Its elements cannot be pushed to B (no PB applied when head is processed value).
    - Valid (chunk C1 = 6..10): A = [17,18,6,7,8,9,10,25,30] (6..10 contiguous ascending).
    - Invalid: A = [17,6,7,9,8,10,25] (order broken: 9,8).
    - Invalid: A = [17,6,7,25,8,9,10] (not contiguous: 8..10 split by 25).

2. Circular contiguity
    - Since a Circular Buffer is used, the last index of A is logically adjacent to index 0. Therefore:
        * The start of the buffer can be read from the smallest value. This is different from the head index of the
          circular buffer, which can be changed by rotations.
        * Reading the array circularly, you must encounter its values as one run.
    - Valid: A = [12,13,14,1,2,3,4,5,6] with chunk C0 = {1,2,3,4,5,6}. Circular order contains 1..6 consecutively (
      indices 3..8 without interruption).
    - Invalid: A = [12,13,14,1,2,14,3,4,5,6] (value 14 intrudes inside what should be the single contiguous run
      1,2,3,4,5,6 – fragmentation inside block).

3. “Behind previous chunk” (relative order)
    - If you were to rotate so the globally smallest processed value is at index 0, the processed chunks appear in
      strictly ascending chunk order. Each processed chunk is individually contiguous & ascending; between two processed
      chunks, there may exist only unprocessed values (future chunks) that happened to lie there originally.
    - Valid (C0=1..4, C1=5..8): A = [7,8,20,1,2,3,4, X, 5,6] where X is an unprocessed value; rotation to 1
      yields [1,2,3,4,X,5,6,7,8,20] — chunk order preserved, X sits between processed C0 and C1.
    - Invalid: A = [7,5,6,8,1,2,3,4] → rotate to 1 gives [1,2,3,4,7,5,6,8] (C1 block split / out of order).

4. Allowed values between processed chunks
    - Unprocessed (future) chunk values may appear before, after, or BETWEEN processed chunk blocks ONLY if they were
      never inserted there after processing (i.e. they remained from the original arrangement at those relative
      positions). The algorithm must NOT actively inject new unprocessed values between two already processed chunks.
    - Valid (natural gap): Original A led to processed layout [1,2,3,4, 15,16,17,18, 5,6,7,8] where 15..18 are future
      values not yet processed; C0 and C1 each intact & contiguous.
    - Invalid (injected gap): After C0 and C1 processed, PA inserts a future value 50 between
      them → [1,2,3,4,50,5,6,7,8]. This is forbidden.
    - Invalid (fragmentation): A = [1,2, 9,10, 3,4] (future 9,10 split C0 block).

5. Chunk boundaries by min/max (dense slices)
    - Chunks are always allocated as contiguous ascending slices of the globally sorted list with fixed maximum size (
      e.g., up to 7). Therefore, each chunk’s value set is dense: S = [min, min+1, ..., max] with no intentional gaps.
    - There is no scenario where an interior integer in that closed range is “missing” unless it does not exist in the
      original input (which cannot happen for a permutation). Sparse examples do NOT apply.
    - Valid (chunk slice 11..17): Block = [11,12,13,14,15,16,17] contiguous ascending.
    - Invalid: [11,12,13,14,16,17] (missing 15).
    - Invalid: [10,11,12,13,14,15,16,17] (includes 10 from a previous chunk).

6. Processed chunks are protected
    - Definition:
        * No PA may insert an unprocessed value inside a processed chunk OR between two processed chunks (only fully
          before all processed blocks or fully after all processed blocks).
        * Future chunk operations may rotate the entire structure (RA/RRA etc.) as long as each processed block stays
          contiguous and relative chunk order (after a normalization rotation to the smallest processed) remains
          ascending.
    - Valid examples:
        * Rotation causing wrap: [7,8,1,2,3,4,5,6].
        * Natural gap (context: ONLY chunks up to C1 have been processed): [1,2,3,4, 15,16,17,18, 5,6,7,8]
            * Here 1..4 (C0) and 5..8 (C1) are processed; 15..18 (C2) are still unprocessed future values left in their
              original positions.
            * This layout is TEMPORARILY valid because 15..18 have yet to be processed.
            * Once chunk C2 is marked as processed, this arrangement becomes **INVALID**. A valid final layout after
              processing that chunk would be [1,2,3,4,5,6,7,8,15,16,17,18] (or any relative position).
    - Invalid examples:
        * Inserting future value (PA) between processed chunks: [1,2,3,4,50,5,6,7,8] (50 inserted between C0 and C1).
        * Swapping processed values (SA/SB/SS): [1,2,4,3,5,6,7,8] (3 and 4 swapped inside C0).
