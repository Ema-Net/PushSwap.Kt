import argparse

INT_MIN = -2_147_483_648
INT_MAX = 2_147_483_647

def parse_ints(args_list):
    """
    Parse a list of strings into integers. A valid integer is defined as
    being in the 32-bit signed integer range and not found more than once.
    """
    numbers = []
    for item in args_list:
        for x in item.split():
            try:
                num = int(x)
            except ValueError:
                raise argparse.ArgumentTypeError()
            if not (INT_MIN <= num <= INT_MAX):
                raise argparse.ArgumentTypeError()
            numbers.append(num)
    if len(numbers) != len(set(numbers)):
        raise argparse.ArgumentTypeError()
    return numbers
