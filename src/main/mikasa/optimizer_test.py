import numpy as np
import time

ZERO = 0.000001


def sse(degree_vector, capacity_vector):
    n = len(degree_vector)
    return sum([((degree_vector[i]-degree_vector[i-1])/degree_vector[-1]-(capacity_vector[i]-capacity_vector[i-1]))**2 for i in range(1,n)])/(n-1)

def descent_vector(degree_vector, capacity_vector):
    n=len(degree_vector)

    descent_vector = []
    descent_vector.append(-(2*(capacity_vector[1] - capacity_vector[0] + (degree_vector[0] - degree_vector[1])/degree_vector[-1]))/(degree_vector[-1]*(n - 1))) # add first term, which is always constant

    for i in range(1, n-1): # all terms inside
        term = ((2*(capacity_vector[i] - capacity_vector[i-1] + (degree_vector[i-1] - degree_vector[i])/degree_vector[-1]))/degree_vector[-1] - (2*(capacity_vector[i+1] - capacity_vector[i] + (degree_vector[i] - degree_vector[i+1])/degree_vector[-1]))/degree_vector[-1])/(n - 1)
        descent_vector.append(term)

    # add last term
    final_term = 0
    for i in range(1, n-1):
        final_term += (2*(degree_vector[i-1] - degree_vector[i])*(capacity_vector[i] - capacity_vector[i-1] + (degree_vector[i-1] - degree_vector[i])/degree_vector[-1]))/(degree_vector[-1]**2*(n - 1))

    final_term += (2*((degree_vector[-2] - degree_vector[-1])/degree_vector[-1]**2 + 1/degree_vector[-1])*(capacity_vector[-1] - capacity_vector[-2] + (degree_vector[-2] - degree_vector[-1])/degree_vector[-1]))/(n - 1)

    descent_vector.append(final_term)

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
def solve_degree(capacity_vector):

    n=len(capacity_vector) # get number of nodes
    capacity_vector = minmaxnorm(capacity_vector)

    sum_deg = 2*n-2 # degree sum is characterstic of tree graph

    degree_vector = [1,1]
    degree_vector.extend([2]*(n-2)) # init degree vector as even graph

    error_previous = sse(degree_vector, capacity_vector)

    while True: # train
        
        # calculate descent vector
        gradient = descent_vector(degree_vector, capacity_vector)

        # check if gradient vector is all positive or negative, or all 0
        if gradient[0]>0:
            for i in range(1,n):
                if gradient[i]<0: # inconsistent
                    break
                if i==n-1: # reached end of gradient vector with no flagged values
                    return degree_vector
        elif gradient[0]<0:
            for i in range(1,n):
                if gradient[i]>0: # inconsistent
                    break
                if i==n-1: # reached end of gradient vector with no flagged values
                    return degree_vector
        else:
            for i in range(1,n):
                if gradient[i]!=0: # inconsistent
                    break
                if i==n-1: # reached end of gradient vector with no flagged values
                    return degree_vector

        # apply adjusted delta to degree vector
        argmax = np.argmax(gradient)
        argmin = np.argmin(gradient)
        
        if degree_vector[argmin]==1 or degree_vector[argmax]==n-1: # already at extreme solution
            return degree_vector

        degree_vector[argmax] += 1
        degree_vector[argmin] -= 1 # this ensures change to sum of degree vector is 0, still valid graph

        error = sse(degree_vector, capacity_vector)
        print("Iteration error:", error)

        if error>=error_previous: # moving backwards, or not moving anymore
            return degree_vector


def solve_network(capacity_vector): # TODO: Fix, creates loops

    n = len(capacity_vector)
    solution = dict()

    normalized_capacity = minmaxnorm(capacity_vector, u=0.9) # created normalized capacity vector that never equals or exceeds

    # get optimal degree vector for network
    degree_vector = solve_degree(capacity_vector)
    print("Optimal solution:", degree_vector)

    for i in range(n):
        solution[i] = [] # initialize graph with empty edges

    while degree_vector!=[0]*n: # while there are still edges we can assign

        # build a vector to rank nodes
        selection_vector = [degree_vector[i]+normalized_capacity[i] for i in range(n)]
        
        print(selection_vector)
        parent = np.argmax(selection_vector) # find first node to start with
        print("---------------Selecting parent", parent)

        selection_vector[parent] = 0 # ensures parent cant select itself to build edge

        while degree_vector[parent]!=0: # while still has edges to give
            child = np.argmax(selection_vector) # find child with highest rank
            print(selection_vector)
            print("Selecting child", child)

            solution[parent].append(child) # make connection
            solution[child].append(parent)
            degree_vector[parent] -= 1
            degree_vector[child] -= 1
            selection_vector[child] = 0 # parent cant make duplicate edges

    return solution



            








    
