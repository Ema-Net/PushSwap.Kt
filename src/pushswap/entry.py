import argparse
import sys

from .chunk.chunk_sort import chunk_sort_hybrid
from .utils.parser import parse_ints

class PushSwap:
    @staticmethod
    def pushswap(numbers: list[int] = None) -> None:
        """
        PushSwap entry point. You can provide a list of integers directly, or
        if there are no arguments, it will prompt for input on the standard input.
        You can either use this or the CLI to run PushSwap.
        """
        try:
            if numbers is None or len(numbers) == 0:
                numbers = parse_ints(input("Enter numbers: ").split())
        except argparse.ArgumentTypeError:
            print("Error\n", file=sys.stderr)
            sys.exit(1)
        chunk_sort_hybrid(numbers)

def cli() -> None:
    """Command-line interface for PushSwap. You can either use this or the PushSwap function to run PushSwap."""
    if len(sys.argv) <= 1:
        print("Error", file=sys.stderr)
        sys.exit(1)
    parser = argparse.ArgumentParser(description="PushSwap CLI")

    parser.add_argument(
        "numbers",
        nargs="+",
        help="List of integers (space-separated or multiple arguments)"
    )
    args = parser.parse_args()
    try:
        numbers = parse_ints(args.numbers)
    except argparse.ArgumentTypeError:
        print("Error", file=sys.stderr)
        sys.exit(1)

    chunk_sort_hybrid(numbers)

def test_cli():
    chunk_sort_hybrid(list(range(16, 0, -1)))
