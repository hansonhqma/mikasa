import java.util.ArrayList;
import java.util.HashMap;
import java.math.*;

public class optimizer {

    public optimizer(){

    }

    private double min(double[] arr){
        double m = arr[0];
        for(int i=1;i<arr.length;++i){
            if(arr[i]<m){m=arr[i];}
        }
        return m;
    }

    private double max(double[] arr){
        double m = arr[0];
        for(int i=1;i<arr.length;++i){
            if(arr[i]>m){m=arr[i];}
        }
        return m;
    }

    public double[] discretive_vector(double[] degree_vector){
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

    public double[] mm_normalize(double[] vec){
        double lb = min(vec);
        double ub = max(vec);
        for(int i=0;i<vec.length;++i){
            vec[i]=(vec[i]-lb)/(ub-lb);
        }
        return vec;
    }

    public double sse(double[] degree_vector, double[] capacity_vector){
        capacity_vector = mm_normalize(capacity_vector);
        int n = degree_vector.length;
        double degree_max = max(degree_vector);
        double sum = 0;
        for(int i=1;i<n;++i){
            sum += Math.pow(((degree_vector[i]-degree_vector[i-1])/degree_max-(capacity_vector[i]-capacity_vector[i-1])),2);
        }
        return sum/(n-1);
    }

    public int[] gradient(int[] degree_vector, int[] capacity_vector){

        throw new RuntimeException("unfinished");
    }

    public HashMap<Integer, ArrayList<Integer>> solve(int [] capacity_vector){

        throw new RuntimeException("unfinished");
    }
}
