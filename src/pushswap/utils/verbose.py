from heapq import heappush, heappop

def heuristic(state):
    sorted_state = sorted(state)
    return sum(1 for i, x in enumerate(state) if x != sorted_state[i])

def swap_first_two(state):
    if len(state) < 2:
        return state
    new_state = state[:]
    new_state[0], new_state[1] = new_state[1], new_state[0]
    return new_state

def rotate_front_to_back(state):
    if len(state) < 2:
        return state
    return state[1:] + [state[0]]

def rotate_back_to_front(state):
    if len(state) < 2:
        return state
    return [state[-1]] + state[:-1]

def a_star_sort_verbose(start):
    start_tuple = tuple(start)
    goal_tuple = tuple(sorted(start))
    open_set = []
    heappush(open_set, (heuristic(start), 0, start_tuple, []))
    visited = set()
    visited_order = {}  # track when a node was first visited
    iteration = 0

    while open_set:
        f, g, state, path = heappop(open_set)
        iteration += 1

        print("-" * 50)
        print(f"Iteration {iteration}")
        print(f"Expanding: {list(state)} | g={g}, h={heuristic(list(state))}, f={f}\n")

        if state == goal_tuple:
            print(f"Goal reached in {g} moves!")
            print("Sequence of moves:", path)
            return path

        if state in visited:
            first_seen = visited_order.get(state, "unknown")
            print(f"{list(state)} is already explored (First seen in iteration {first_seen})\n")
            continue

        visited.add(state)
        visited_order[state] = iteration

        children = [
            (swap_first_two(list(state)), "swap"),
            (rotate_front_to_back(list(state)), "rotate front→back"),
            (rotate_back_to_front(list(state)), "rotate back→front")
        ]

        for child_state, move in children:
            child_tuple = tuple(child_state)
            g_new = g + 1
            h_new = heuristic(child_state)
            f_new = g_new + h_new
            new_path = path + [move]

            if child_tuple not in visited:
                heappush(open_set, (f_new, g_new, child_tuple, new_path))
                print(f"Instruction: {move}")
                print(f"Resulting state: {child_state} | g={g_new}, h={h_new}, f={f_new}")
                print(f"Path so far: {new_path}\n")
            else:
                first_seen = visited_order.get(child_tuple, "unknown")
                print(f"{child_state} is already explored (First seen in iteration {first_seen})\n")

        print("Open set after this expansion:")
        for idx, elem in enumerate(open_set, 1):
            s = list(elem[2])
            print(f"#{idx}: {s} | g={elem[1]}, h={heuristic(s)}, f={elem[0]}, path={elem[3]}")
        print("-" * 50 + "\n")

# Example usage
start = list(range(6, 0, -1))  # Reverse sorted list
a_star_sort_verbose(start)
