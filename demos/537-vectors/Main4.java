import java.util.List;  
import java.util.Date;
import java.util.ArrayList;
import java.util.stream.*;
import jdk.incubator.vector.*;

//similar to `3` but using functions with no chaining
//result[i] = sin((a[i]*b[i])/(a[i]+b[i]+1)).
public class Main4 {

  private static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

  private static final int max=50000;
  private static final int iter=2000;
  private static final int warms = 16;
  private static final List<Double> primalValues = new ArrayList<>();
  private static final float[] parray1 = new float[max];
  private static final float[] parray2 = new float[max];
  private static final float[] parray6 = new float[max];
  private static final float[] parray7 = new float[max];


  @SafeVarargs 
  public static void fillPrimitiveArrays(float[]... arr) {
     for(int x = 0; x< arr.length; x++) {
       for(int i = 0; i< arr[x].length; i++) {
         arr[x][i] = (float)(i);
       }
     }
   }
  

  public static float[] computeTwoPrimitiveArrays(float[] a, float[] b) {
    float[] result = new float[max];
    for(int i = 0; i< max; i++) {
         float num = a[i] * b[i];
         float den = a[i] + b[i] + 1.0f;
         result[i] = (float) Math.sin(num / den);
    }
    return result;
  }

  public static float[] computeTwoPrimitiveArraysInVector(float[] a, float[] b) {
        float[] result = new float[max];
        int i = 0;
        int upperBound = SPECIES.loopBound(a.length);
        for (; i < upperBound; i += SPECIES.length()) {
            FloatVector va = FloatVector.fromArray(SPECIES, a, i);
            FloatVector vb = FloatVector.fromArray(SPECIES, b, i);
            FloatVector numerator = va.mul(vb);
            FloatVector denominator = va.add(vb).add(va.broadcast(1.0f));
            FloatVector fraction = numerator.div(denominator);
            FloatVector finalResult = fraction.lanewise(VectorOperators.SIN);
            finalResult.intoArray(result, i);
        }
        //for (; i < a.length; i++) {
        //    float num = a[i] * b[i];
        //    float den = a[i] + b[i] + 1.0f;
        //    result[i] = (float) Math.sin(num / den);
        // }
    return result;
  }

			

public static class Runner implements Runnable {
   private final Runnable main;
   private final String title;
   public Runner(String title, Runnable main) {
     this.title=title;
     this.main=main;
   }
   public void run() {
     List<Long> avgs = new ArrayList<>(warms/2+1);
     long start, stop;
     for(int w = 0; w<= warms; w++) {
      start=new Date().getTime();
      for(int x = 0; x< iter; x++) {
        main.run();
      }
      stop=new Date().getTime();
      if (w>warms/2){
        avgs.add(stop-start);
      }
    }
      double avg = avgs.stream().collect(Collectors.averagingLong(Long::longValue));
      primalValues.add(avg);
      System.out.println(title + ": " + avg + "ms " + printPrimals());
   }
}

private static String printPrimals() {
    StringBuilder sb = new StringBuilder();
    for(int i = 0; i<primalValues.size()-1; i++) {
      sb.append("("+(int)(primalValues.get(primalValues.size()-1)/primalValues.get(i)*100f)+"%)");
    }
    return sb.toString();
}

  public static void main(final String... args) throws Exception {
    fillPrimitiveArrays(parray1,parray2,parray6,parray7);

    new Runner("Primitive Arrays", ()->{  computeTwoPrimitiveArrays(parray1, parray2);} ).run();
    new Runner("Vectors on floats", ()->{  computeTwoPrimitiveArraysInVector(parray6,parray7);} ).run();


  }
}
