import static org.junit.Assert.*;
import org.junit.Test;

import java.util.*;

public final class optimizerTests{

    @Test
    public void discretizer_test(){
        double[] out = optimizer.discretize_vector(new double[]{1.25,1.125,1.875,2.125,1.625});
        assertTrue(out[0]==1);
        assertTrue(out[1]==1);
        assertTrue(out[2]==2);
        assertTrue(out[3]==2);
        assertTrue(out[4]==2);
    }

    @Test
    public void normalizerTest(){
        double[] actual = new double[]{0.0, 0.25, 0.5, 0.75, 1.0};
        double[] normalized = optimizer.mm_normalize(new double[]{1,2,3,4,5});
        for(int i=0;i<normalized.length;++i){
            assertTrue(normalized[i]==actual[i]);
        }
    }

    @Test
    public void sseTest(){
        double[] d = new double[]{1,1,2,2,2};
        double[] c = new double[]{1,2,3,4,5};
        double e = optimizer.sse(d, c);
        assertTrue(e==0.0625);
    }

    @Test
    public void gradientTest(){
        double[] d = new double[]{1,1,2,2,2};
        double[] c = new double[]{1,2,3,4,5};
        double[] g = optimizer.gradient(d,c);
        double[] actual = new double[]{-0.0625, 0.125, -0.125, 0, 0.0625};
        assertTrue(Arrays.equals(g, actual));
    }

    @Test
    public void solveDegreeTest(){
        double[] c = new double[]{1,1,2,3,6};
        double[] actual = new double[]{1,1,1,2,3};
        assertTrue(Arrays.equals(actual, optimizer.solve_degree(c)));
    }

    @Test
    public void solveNetworkTest(){
        double[] c = new double[]{1,3,1,2,5,15};
        HashMap<Integer, ArrayList<Integer>> s = optimizer.solve(c);
        System.out.println(s); //  not really a test but whatever
    }
}
