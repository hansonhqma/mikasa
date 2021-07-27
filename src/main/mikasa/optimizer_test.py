
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
    return descent_vector

def minmaxnorm(vec):
    a = min(vec)
    b = max(vec)
    for i in range(len(vec)):
        vec[i]=(vec[i]-a)/(b-a)
    return vec

# assumes capacity_vector is already sorted
def degree_vector(capacity_vector):
    n=len(capacity_vector) # get number of nodes
    sum_deg = 2*n-2 # degree sum is characterstic of tree graph

    degree_vector = [1,1]
    degree_vector.extend([2]*(n-2)) # init degree vector as even graph

    while True:
        # calculate current error and print
        error = sse(degree_vector, capacity_vector)
        print("Iteration error:", error)

        # calculate descent vector
        descent_vector = []
        

