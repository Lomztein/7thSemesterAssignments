import math
import numpy

# This is a horrible solution to some uni homework. Given a list of positions, the goal is to find any
# combination of four or more positions, which are colinear, meaning you can draw a line through them. I have no idea
# if this works for everything, there is a terrifying amount of jank, but it seems to be pumping out the right results.
# The requirements of the homework was to use sorting and have it run in O(N^2 log N) time, which I believe this does.
# If it does not, well then I've wasted like four hours.

# That said, it was a pretty fun one to solve regardless. And most of those four hours was spent tweaking shitty code.
# It wasn't even my homework, I just did it for fun.

vectors = [
    [2, 2],
    [3, 3],
    [4, 4],
    [7, 1],
    [14, 6],
    [9, 4],
    [1, 1],
    [1, 4],
    [1, 5],
    [1, 2],
    [2, 4],
]

_v = None
def compare_slope_with_current(vec):
    slope = compare_slope(vec, _v)
    return slope


def compare_slope(vec1, vec2):
    transformed = [vec1[1] - vec2[1], vec1[0] - vec2[0]]
    slope = 0
    if transformed[0] != 0 and transformed[1] != 0:
        slope = transformed[1] / transformed[0]
    if transformed[1] == 0:
        slope = math.inf

    return slope


arrs = []
origins = []

# Create a list of lists where each original point acts as an origin, and the rest are sorted according to the slope
# of a line which would pass through both points.
for v in vectors: # N^2 log N
    _v = v
    arr = vectors.copy()
    arr.sort(key=compare_slope_with_current)
    origins.append(v)
    arrs.append(arr)


colinears = []
epsilon = 0.01

# Traverse each list previously created, and pick out all rows of more than 3 with the same slope. This works by
# tracking whether or not the slope changes between steps in a traversal. A change indicates that the current and
# previous points are not both colinear with the origin point.
for n in range(0, len(origins)):  # N^2
    arr = arrs[n]
    origin = origins[n]

    cur = [origin, arr[0]]
    cur_angle = compare_slope(origin, cur[1])

    for vec in arr[1:len(arr)]:
        if numpy.array_equiv(vec, origin):  # Origin will always be in the list, so ignore it to avoid duplicates.
            continue

        angle = compare_slope(origin, vec)
        delta = cur_angle - angle
        if angle == math.inf and cur_angle == math.inf:  # Annoying edge case support.
            delta = 0

        if abs(delta) < epsilon:  # If no change is detected, append current point to colinear list.
            cur.append(vec)
        else:  # If change is detected..
            if len(cur) > 3:  # If above three, then add to a list of found colinear points.
                colinears.append(cur)

            cur = [origin, vec]  # Reset tracking to current point.
            cur_angle = compare_slope(origin, vec)

    if len(cur) > 3:
        colinears.append(cur)


# Horrible bit of code to remove duplicates, of which there are a lot of.
results = []
for colinear in colinears: # ~M^2 log M
    colinear.sort()
    contains = False
    for r in results:
        if numpy.array_equiv(r, colinear):
            contains = True
    if contains is False:
        results.append(colinear)


print(results)
