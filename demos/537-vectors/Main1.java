import java.util.List; //!!  
import java.util.Date;
import java.util.ArrayList;
import java.util.stream.*;
import jdk.incubator.vector.*;

//a[i]+b[i] (or a[i]+[ai]) on collections, autoboxed arrays, primitive arrays, and vectors
public class Main1 {

  // the optimal hardware vector shape for your current CPU
  private static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_PREFERRED;

  private static final int max=100000;
  private static final int iter=1000;
  private static final int warms = 8;
  private static final List<Double> primalValues = new ArrayList<>();
  private static final Float[] array1 = new Float[max];
  private static final Float[] array2 = new Float[max];
  private static final Float[] array3 = new Float[max];
  private static final Float[] array4 = new Float[max];
  private static final Float[] array5 = new Float[max];
  private static final float[] parray1 = new float[max];
  private static final float[] parray2 = new float[max];
  private static final float[] parray3 = new float[max];
  private static final float[] parray4 = new float[max];
  private static final float[] parray5 = new float[max];
  private static final float[] parray6 = new float[max];
  private static final float[] parray7 = new float[max];
  private static final float[] parray8 = new float[max];
  private static final float[] parray9 = new float[max];
  private static final List<Float> list1 = new ArrayList<>(max);
  private static final List<Float> list2 = new ArrayList<>(max);
  private static final List<Float> list3 = new ArrayList<>(max);
  private static final List<Float> list4 = new ArrayList<>(max);
  private static final List<Float> list5 = new ArrayList<>(max);
  private static final List<Float> list6 = new ArrayList<>(max);
  private static final List<Float> list7 = new ArrayList<>(max);
  private static final java.util.Vector<Float> vector1 = new java.util.Vector<>(max);
  private static final java.util.Vector<Float> vector2 = new java.util.Vector<>(max);
  private static final java.util.Vector<Float> vector3 = new java.util.Vector<>(max);

  @SafeVarargs 
  public static void fillArrays(Float[]... arr) {
     for(int x = 0; x< arr.length; x++) {
       for(int i = 0; i< arr[x].length; i++) {
         arr[x][i] = Float.valueOf(i);
       }
     }
   }

  @SafeVarargs 
  public static void fillPrimitiveArrays(float[]... arr) {
     for(int x = 0; x< arr.length; x++) {
       for(int i = 0; i< arr[x].length; i++) {
         arr[x][i] = (float)(i);
       }
     }
   }

  @SafeVarargs 
  public static void fillLists(List<Float>... arr) {
     for(int x = 0; x< arr.length; x++) {
       for(int i = 0; i< max; i++) {
         arr[x].add(Float.valueOf(i));
       }
     }
   }

  @SafeVarargs 
  public static void fillVectors(java.util.Vector<Float>... arr) {
     for(int x = 0; x< arr.length; x++) {
       for(int i = 0; i< max; i++) {
         arr[x].add(Float.valueOf(i));
       }
     }
   }


  public static Float[] addTwoArrays(Float[] arr1, Float[] arr2) {
    Float[] result = new Float[max];
    for(int i = 0; i< max; i++) {
        result[i] = arr1[i] + arr2[i];
    }
    return result;
  }

  public static Float[] addArray(Float[] arr1) {
    Float[] result = new Float[max];
    for(int i = 0; i< max; i++) {
        result[i] = arr1[i] + arr1[i];
    }
    return result;
  }

  public static float[] addTwoPrimitiveArrays(float[] arr1, float[] arr2) {
    float[] result = new float[max];
    for(int i = 0; i< max; i++) {
        result[i] = arr1[i] + arr2[i];
    }
    return result;
  }

  public static float[] addPrimitiveArrayInVector(float[]a) {
    float[] c = new float[max];
    int i = 0;
    //process array in bulk blocks matching CPU SIMD registers
    int upperBound = SPECIES.loopBound(a.length);
    for (; i < upperBound; i += SPECIES.length()) {
        FloatVector va = FloatVector.fromArray(SPECIES, a, i);
        FloatVector vc = va.add(va);
        vc.intoArray(c, i);
    }
    //for (; i < a.length; i++) {
    //    c[i] = a[i] + a[i]
    //}
    return c;
  }


  public static float[] addTwoPrimitiveArraysInVector(float[] a, float[] b) {
    float[] c = new float[max];
    int i = 0;
    int upperBound = SPECIES.loopBound(a.length);
    for (; i < upperBound; i += SPECIES.length()) {
        FloatVector va = FloatVector.fromArray(SPECIES, a, i);
        FloatVector vb = FloatVector.fromArray(SPECIES, b, i);
        FloatVector vc = va.add(vb);
        vc.intoArray(c, i);
    }
    //for (; i < a.length; i++) {
    //    c[i] = a[i] + b[i]
    //}
    return c;
  }


  public static float[] addPrimitiveArray(float[] arr1) {
    float[] result = new float[max];
    for(int i = 0; i< max; i++) {
        result[i] = arr1[i] + arr1[i];
    }
    return result;
  }

  public static List<Float> addTwoLists(List<Float> arr1, List<Float> arr2) {
    List<Float> result = new ArrayList<>(max);
    for(int i = 0; i< max; i++) {
        result.add(arr1.get(i) + arr2.get(i));
    }
    return result;
  }

  public static List<Float> addTwoListsHalfiter(List<Float> arr1, List<Float> arr2) {
    List<Float> result = new ArrayList<>(max);
    int i=-1;
    for(Float f: arr1) {
        i++;
        result.add(f + arr2.get(i));
    }
    return result;
  }

  public static List<Float> addListsIter(List<Float> arr1) {
    List<Float> result = new ArrayList<>(max);
    for(Float f: arr1) {
        result.add(f + f);
    }
    return result;
  }

  public static java.util.Vector<Float> addTwoVectors(java.util.Vector<Float> arr1, java.util.Vector<Float> arr2) {
    java.util.Vector<Float> result = new java.util.Vector<>(max);
    for(int i = 0; i< max; i++) {
        result.add(arr1.get(i) + arr2.get(i));
    }
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
    fillArrays(array1,array2,array3,array4,array5);
    fillPrimitiveArrays(parray1,parray2,parray3,parray4,parray5,parray6,parray7,parray8,parray9);
    fillLists(list1,list2,list3,list4,list5, list6, list7);
    fillVectors(vector1,vector2, vector3);

    new Runner("Lists", ()->{             addTwoLists(list1, list2);} ).run();
    new Runner("Lists(1/2iter)", ()->{    addTwoListsHalfiter(list3, list4);} ).run();
    new Runner("Float Arrays", ()->{      addTwoArrays(array1, array2);} ).run();
    new Runner("Primitive Arrays", ()->{  addTwoPrimitiveArrays(parray1, parray2);} ).run();
    System.out.println("-------------");
    new Runner("List", ()->{              addTwoLists(list5, list5);} ).run();
    new Runner("List(1/2iter)", ()->{     addTwoListsHalfiter(list6, list6);} ).run();
    new Runner("List(iter)", ()->{        addListsIter(list7);} ).run();
    new Runner("Float Array2", ()->{      addTwoArrays(array3, array3);} ).run();
    new Runner("Float Array1", ()->{      addArray(array4);} ).run();
    new Runner("Primitive Array2", ()->{  addTwoPrimitiveArrays(parray3, parray3);} ).run();
    new Runner("Primitive Array1", ()->{  addPrimitiveArray(parray5);} ).run();
    System.out.println("vectors!");
    new Runner("Vectors on floats", ()->{  addTwoPrimitiveArraysInVector(parray6,parray7);} ).run();
    new Runner("Vectors on float", ()->{   addTwoPrimitiveArraysInVector(parray8,parray8);} ).run();
    new Runner("Vector on float", ()->{    addPrimitiveArrayInVector(parray9);} ).run();
    System.out.println("This are not the vectors you are looing for");
    new Runner("ArchaicVector", ()->{     addTwoVectors(vector3, vector3);} ).run();
    new Runner("ArchaicVectors", ()->{    addTwoVectors(vector1, vector2);} ).run();
  }
}
