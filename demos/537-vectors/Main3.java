import java.util.List;  
import java.util.Date;
import java.util.ArrayList;
import java.util.stream.*;
import jdk.incubator.vector.*;

//similar to `2` but with more complex equation, but still simple chaining
//a[i] * a[i] + b[i] * b[i]) * -1.0f;
public class Main3 {

  private static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

  private static final int max=50000;
  private static final int iter=2000;
  private static final int warms = 16;
  private static final List<Double> primalValues = new ArrayList<>();
  private static final float[] parray1 = new float[max];
  private static final float[] parray2 = new float[max];
  private static final float[] parray3 = new float[max];
  private static final float[] parray4 = new float[max];
  private static final float[] parray5 = new float[max];
  private static final float[] parray6 = new float[max];
  private static final float[] parray7 = new float[max];
  private static final float[] parray8 = new float[max];
  private static final float[] parray9 = new float[max];


  @SafeVarargs 
  public static void fillPrimitiveArrays(float[]... arr) {
     for(int x = 0; x< arr.length; x++) {
       for(int i = 0; i< arr[x].length; i++) {
         arr[x][i] = (float)(i);
       }
     }
   }

  public static float[] addPrimitiveArray(float[] arr1) {
    float[] result = new float[max];
    for(int i = 0; i< max; i++) {
        result[i] = (arr1[i]*arr1[i] + arr1[i]*arr1[i])*-1f;
    }
    return result;
  }

  public static float[] addPrimitiveArrayInVector(float[]a) {
    float[] c = new float[max];
    int i = 0;
    int upperBound = SPECIES.loopBound(a.length);
    for (; i < upperBound; i += SPECIES.length()) {
        var va = FloatVector.fromArray(SPECIES, a, i);
        var vc = va.mul(va)
                   .add(va.mul(va))
                   .neg();
        vc.intoArray(c, i);
    }
    //for (; i < a.length; i++) {
    //    c[i] = (a[i] * a[i] + b[i] * b[i]) * -1.0f;
    //}
    return c;
  }


  public static float[] addTwoPrimitiveArrays(float[] arr1, float[] arr2) {
    float[] result = new float[max];
    for(int i = 0; i< max; i++) {
        result[i] = (arr1[i]*arr1[i] + arr2[i]*arr2[i])*-1f;
    }
    return result;
  }

  public static float[] addTwoPrimitiveArraysInVector(float[] a, float[] b) {
    float[] c = new float[max];
    int i = 0;
    int upperBound = SPECIES.loopBound(a.length);
    for (; i < upperBound; i += SPECIES.length()) {
        var va = FloatVector.fromArray(SPECIES, a, i);
        var vb = FloatVector.fromArray(SPECIES, b, i);
        var vc = va.mul(va)
                   .add(vb.mul(vb))
                   .neg();
        vc.intoArray(c, i);
    }
    //for (; i < a.length; i++) {
    //    c[i] = (a[i] * a[i] + b[i] * b[i]) * -1.0f;
    //}
    return c;
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
    fillPrimitiveArrays(parray1,parray2,parray3,parray4,parray5,parray6,parray7,parray8,parray9);

    new Runner("Primitive Arrays", ()->{  addTwoPrimitiveArrays(parray1, parray2);} ).run();
    System.out.println("-------------");
    new Runner("Primitive Array2", ()->{  addTwoPrimitiveArrays(parray3, parray3);} ).run();
    new Runner("Primitive Array1", ()->{  addPrimitiveArray(parray5);} ).run();
    System.out.println("vectors!");
    new Runner("Vectors on floats", ()->{  addTwoPrimitiveArraysInVector(parray6,parray7);} ).run();
    new Runner("Vectors on float", ()->{   addTwoPrimitiveArraysInVector(parray8,parray8);} ).run();
    new Runner("Vector on float", ()->{    addPrimitiveArrayInVector(parray9);} ).run();

  }
}
