import static org.junit.Assert.*;
import org.junit.Test;

public final class optimizerTests {

    @Test
    public void discretizer_test(){
        optimizer demo  = new optimizer();
        double[] out = demo.discretive_vector(new double[]{1.25,1.125,1.875,2.125,1.625});
        assertTrue(out[0]==1);
        assertTrue(out[1]==1);
        assertTrue(out[2]==2);
        assertTrue(out[3]==2);
        assertTrue(out[4]==2);
    }

    @Test
    public void normalizerTest(){
        optimizer demo  = new optimizer();
        double[] actual = new double[]{0.0, 0.25, 0.5, 0.75, 1.0};
        double[] normalized = demo.mm_normalize(new double[]{1,2,3,4,5});
        for(int i=0;i<normalized.length;++i){
            assertTrue(normalized[i]==actual[i]);
        }
    }

    @Test
    public void sseTest(){
        optimizer demo  = new optimizer();
        double[] d = new double[]{1,1,2,2,2};
        double[] c = new double[]{1,2,3,4,5};
        double e = demo.sse(d, c);
        assertTrue(e==0.0625);
    }
}
