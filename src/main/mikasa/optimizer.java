import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class optimizer {
    private static final double ZERO = 0.00001;

    public optimizer(){}

    /**
     * @requires arr.length>0
     * @param arr
     * @return
     */
    public static double min(double[] arr){
        double m = arr[0];
        for(int i=1;i<arr.length;++i){
            if(arr[i]<m){m=arr[i];}
        }
        return m;
    }

    /**
     * @requires arr.length>0
     * @param arr
     * @return
     */
    public static double max(double[] arr){
        double m = arr[0];
        for(int i=1;i<arr.length;++i){
            if(arr[i]>m){m=arr[i];}
        }
        return m;
    }

    public static int argmax(double[] array) {
        double max = array[0];
        int re = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] > max) {
                max = array[i];
                re = i;
            }
        }
        return re;
    }

    public static int argmin(double[] array) {
        double min = array[0];
        int re = 0;
        for (int i = 1; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
                re = i;
            }
        }
        return re;
    }

    /**
     * @requires degree_vector!=null
     * @param degree_vector
     * @return
     */
    public static double[] discretize_vector(double[] degree_vector){
        int n = degree_vector.length;
        double[] delta_vector = new double[n];
        for(int i=0;i<n;++i){ // construct delta vector
            delta_vector[i]=(Math.round(degree_vector[i])-degree_vector[i]);
        }
        for(int i=0;i<n;++i){
            if(delta_vector[i]==0){
                continue;
            }
            for(int j=i+1;j<n;++j){
                if(delta_vector[j]==0){
                    continue;
                }
                // both non-zero values
                double value = delta_vector[i];
                degree_vector[i] += value;
                degree_vector[j] -= value;
                delta_vector[j] += value;
                break;
            }
        }
        return degree_vector;
    }

    /**
     * @requires vec.length>0
     * @param vec
     * @return
     */
    public static double[] mm_normalize(double[] vec){
        double lb = min(vec);
        double ub = max(vec);
        for(int i=0;i<vec.length;++i){
            vec[i]=(vec[i]-lb)/(ub-lb);
        }
        return vec;
    }

    /**
     * @requires capacity_vector.length>0 && capacity_vector.length == degree_vector.length
     * @param degree_vector
     * @param capacity_vector
     * @return
     */
    public static double sse(double[] degree_vector, double[] capacity_vector){
        capacity_vector = mm_normalize(capacity_vector);
        int n = degree_vector.length;
        double degree_max = max(degree_vector);
        double sum = 0;
        for(int i=1;i<n;++i){
            sum += Math.pow(((degree_vector[i]-degree_vector[i-1])/degree_max-(capacity_vector[i]-capacity_vector[i-1])),2);
        }
        return sum/(n-1);
    }

    /**
     * @requires capacity_vector.length>1 && capacity_vector.length == degree_vector.length
     * @param degree_vector
     * @param capacity_vector
     * @return
     */
    public static double[] gradient(double[] degree_vector, double[] capacity_vector){
        capacity_vector = mm_normalize(capacity_vector);
        int n = degree_vector.length;
        double degree_max = max(degree_vector);

        double[] gradient_vector = new double[n];
        // first term
        gradient_vector[0]=-(2*(capacity_vector[1] - capacity_vector[0] + (degree_vector[0] - degree_vector[1])/degree_max))/(degree_max*(n - 1));
        // middle terms
        for(int i=1;i<n-1;++i){
            gradient_vector[i]=((2*(capacity_vector[i] - capacity_vector[i-1] + (degree_vector[i-1] - degree_vector[i])/degree_max))/degree_max - (2*(capacity_vector[i+1] - capacity_vector[i] + (degree_vector[i] - degree_vector[i+1])/degree_max))/degree_max)/(n - 1);
        }
        // final term
        gradient_vector[n-1]=(2*(capacity_vector[n-1] - capacity_vector[n-2] + (degree_vector[n-2] - degree_vector[n-1])/degree_max))/(degree_max*(n - 1));
        // get rid of floating point inaccuracy for zero
        for(int i=0;i<n;++i){
            if(Math.abs(gradient_vector[i])<ZERO){gradient_vector[i]=0;}
        }

        return gradient_vector;
    }

    /**
     * @requires capacity_vector.length>1
     * @param capacity_vector
     * @return
     */
    public static double[] solve_degree(double[] capacity_vector){
        int n = capacity_vector.length;
        capacity_vector = mm_normalize(capacity_vector);
        int sum_deg = 2*n-2;

        double[] degree_vector = new double[n];
        degree_vector[0]=1;degree_vector[1]=1;
        for(int i=2;i<n;++i){degree_vector[i]=2;} // fill init degree vector

        double error_previous = sse(degree_vector, capacity_vector);
        double[] degree_previous = new double[n];
        for(int i=0;i<n;++i){degree_previous[i]=degree_vector[i];} // fill previous vector

        while(true){
            double[] gradient_vector = gradient(degree_vector, capacity_vector);
            if(gradient_vector[0]>0){
                for(int i=1;i<n;++i){
                    if(gradient_vector[i]<0){break;}
                    if(i==n-1){return discretize_vector(degree_vector);}
                }
            }else if(gradient_vector[0]<0){
                for(int i=1;i<n;++i){
                    if(gradient_vector[i]>0){break;}
                    if(i==n-1){return discretize_vector(degree_vector);}
                }
            }else{
                for(int i=1;i<n;++i){
                    if(gradient_vector[i]!=0){break;}
                    if(i==n-1){return discretize_vector(degree_vector);}
                }
            } // gradient consistency checks

            int amax = argmax(gradient_vector);
            int amin = argmin(gradient_vector);

            if(degree_vector[amin]==1 || degree_vector[amax]==n-1){discretize_vector(degree_vector);}
            // make changes
            degree_vector[amax] += 0.03125;
            degree_vector[amin] -= 0.03125; // ensures no absolute change

            double error = sse(degree_vector, capacity_vector);
            if(error>=error_previous){return discretize_vector(degree_previous);} // no improvement, return previous

            error_previous = error;
            for(int i=0;i<n;++i){degree_previous[i]=degree_vector[i];} // set previous degree vector
        }
    }

    /**
     * @requires capacity_vector.length>0
     * @param capacity_vector
     * @return
     */
    public static HashMap<Integer, ArrayList<Integer>> solve(double[] capacity_vector) {
        int n = capacity_vector.length;
        HashMap<Integer, ArrayList<Integer>> solution = new HashMap<>();

        double[] degree_vector = solve_degree(capacity_vector);
        boolean[] in_graph = new boolean[n];
        double[] ZERO = new double[n];
        for (int i = 0; i < n; ++i) { // init empty edges and in_graph array
            solution.put(i, new ArrayList<>());
            in_graph[i] = false; // non
            ZERO[i] = 0; // define zero vector
        }

        while(!Arrays.equals(degree_vector, ZERO)){
            int parent = argmax(degree_vector);
            while(degree_vector[parent]!=0){
                double[] selection_vector = new double[n];
                int child;
                if(in_graph[parent]){
                    for(int i=0;i<n;++i){
                        if(in_graph[i]){selection_vector[i]=0;}
                        else{selection_vector[i]=degree_vector[i];}
                    }
                    selection_vector[parent]=0;
                    child = argmax(selection_vector);
                    in_graph[child]=true;
                }else{
                    for(int i=0;i<n;++i){
                        if(!in_graph[i]){selection_vector[i]=0;}
                        else{selection_vector[i]=degree_vector[i];}
                    }
                    selection_vector[parent]=0;
                    child = argmax(selection_vector);
                    in_graph[child]=true;
                    in_graph[parent]=true;
                }
                solution.get(parent).add(child);
                solution.get(child).add(parent); // add to solution

                degree_vector[parent] -= 1;
                degree_vector[child] -= 1;
            }
        }
        return solution;
    }
}
