import numpy as np
import math
import time

ZERO = 0.000001

def rnd(x): # round func without float inaccuracy
    if x%1 > 0.5:
        return math.ceil(x)
    else:
        return math.floor(x)

def discretize_vector(degree_vector):
    print("discretizing")
    diff_vector = [round(x)-x for x in degree_vector]
    print(diff_vector)
    for i in range(len(degree_vector)):
        if diff_vector[i]==0:
            continue
        for j in range(i+1, len(degree_vector)):
            if diff_vector[j]==0:
                continue
            # both non-zero values
            value = diff_vector[i]
            degree_vector[i] += value
            degree_vector[j] -= value # move responsibility
            diff_vector[j] += value
            print(degree_vector)
            break
    return degree_vector

def sse(degree_vector, capacity_vector):
    capacity_vector = minmaxnorm(capacity_vector)
    n = len(degree_vector)
    degree_max = max(degree_vector)
    return sum([((degree_vector[i]-degree_vector[i-1])/degree_max-(capacity_vector[i]-capacity_vector[i-1]))**2 for i in range(1,n)])/(n-1)

def descent_vector(degree_vector, capacity_vector):
    n=len(degree_vector)
    degree_max = max(degree_vector)

    # basically does calculus here

    descent_vector = []
    descent_vector.append(-(2*(capacity_vector[1] - capacity_vector[0] + (degree_vector[0] - degree_vector[1])/degree_max))/(degree_max*(n - 1))) # add first term, which is always constant

    for i in range(1, n-1): # all terms inside
        term = ((2*(capacity_vector[i] - capacity_vector[i-1] + (degree_vector[i-1] - degree_vector[i])/degree_max))/degree_max - (2*(capacity_vector[i+1] - capacity_vector[i] + (degree_vector[i] - degree_vector[i+1])/degree_max))/degree_max)/(n - 1)
        descent_vector.append(term)

    # add last term
    descent_vector.append((2*(capacity_vector[-1] - capacity_vector[-2] + (degree_vector[-2] - degree_vector[-1])/degree_max))/(degree_max*(n - 1)))

    for i in range(len(descent_vector)): # get rid of floating point inaccuracy
        if abs(descent_vector[i])<ZERO:
            descent_vector[i]=0
    return descent_vector

def minmaxnorm(vec, l=0, u=1):
    ret = [0]*len(vec)
    a = min(vec)
    b = max(vec)
    for i in range(len(vec)):
        ret[i]=(u-l)*(vec[i]-a)/(b-a)+l
    return ret

# assumes capacity_vector is already sorted
def solve_degree(capacity_vector, slowmode=False):

    n=len(capacity_vector) # get number of nodes
    capacity_vector = minmaxnorm(capacity_vector)

    sum_deg = 2*n-2 # degree sum is characterstic of tree graph

    degree_vector = [1,1]
    degree_vector.extend([2]*(n-2)) # init degree vector as even graph

    error_previous = sse(degree_vector, capacity_vector)
    print(degree_vector, "error:",error_previous)
    degree_previous = [x for x in degree_vector]

    while True: # train
        
        if slowmode:
            time.sleep(0.25)
        
        # calculate descent vector
        gradient = descent_vector(degree_vector, capacity_vector)

        # check if gradient vector is all positive or negative, or all 0
        if gradient[0]>0:
            for i in range(1,n):
                if gradient[i]<0: # inconsistent
                    break
                if i==n-1: # reached end of gradient vector with no flagged values
                    return discretize_vector(degree_vector)
        elif gradient[0]<0:
            for i in range(1,n):
                if gradient[i]>0: # inconsistent
                    break
                if i==n-1: # reached end of gradient vector with no flagged values
                    return discretize_vector(degree_vector)
        else:
            for i in range(1,n):
                if gradient[i]!=0: # inconsistent
                    break
                if i==n-1: # reached end of gradient vector with no flagged values
                    return discretize_vector(degree_vector)
        print("potential change")

        # find largest deltas in gradient vector
        argmax = np.argmax(gradient)
        argmin = np.argmin(gradient)
        
        if degree_vector[argmin]==1 or degree_vector[argmax]==n-1: # already at extreme solution
            return discretize_vector(degree_vector)

        degree_vector[argmax] += 0.0625
        degree_vector[argmin] -= 0.0625 # this ensures change to sum of degree vector is 0, still valid graph

        error = sse(degree_vector, capacity_vector)
        print(degree_vector, "error:",error)

        if error>=error_previous: # moving backwards, or not moving anymore
            return discretize_vector(degree_previous)

        error_previous = error
        degree_previous = [x for x in degree_vector]


def solve_network(capacity_vector):

    n = len(capacity_vector)
    solution = dict()

    normalized_capacity = minmaxnorm(capacity_vector, u=0.9) # created normalized capacity vector that never equals or exceeds

    # get optimal degree vector for network
    degree_vector = solve_degree(capacity_vector)
    print("Optimal solution:", degree_vector)

    for i in range(n):
        solution[i] = [] # initialize graph with empty edges

    # creates tree based on degree vector

    return solution



            






