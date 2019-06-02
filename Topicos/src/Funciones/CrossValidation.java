package Funciones;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class CrossValidation {
    public static List<List<int[]>> GetFolds (int[][] ds, boolean ordenado, int k){
        int m = ds.length;

        int tam_fold = m/k;
        List<List<int[]>> folds = new ArrayList<>();

        for (int i=0; i < k; i++){
            List<int[]> fold_k = new ArrayList<>();
            folds.add(fold_k);
        }

        int[] indices = new int[m];
        Random aleatorio = new Random(System.currentTimeMillis());

        /////Cross ordenado o aleatorio//////

        if (ordenado==false){
            for(int i=0;i < m; i++){
                int x = aleatorio.nextInt(50);
                aleatorio.setSeed(System.currentTimeMillis());
                x = x % m;
                if (!Arrays.asList(indices).contains(x)){
                    indices[i] = x;
                }
            }
        }
        else {
            for (int i = 0; i < m; i++){
                indices[i]=i;
            }
        }

        /////Llenado de los k_folds//////
        int f=0;
        for (Integer i:indices){
            int[] ins = ds[i];
            List<int[]> fold_k = folds.get(f);
            fold_k.add(ins);
            f = (f+1) % k;
        }
        return folds;
    }

    public static List<List<int[]>> GetTrain (List<List<int[]>> folds, int k){

        List<List<int[]>> dstrain = new ArrayList<> ();

        for(int test = 0 ; test<k ; test++){
            List<int[]> aux = new ArrayList<> ();
            List<int[]> aux1 = new ArrayList<> ();
            for(int train = 0 ; train <k ; train++){
                if(test != train){
                    aux1 = folds.get(train);
                }
                aux1.addAll(aux);
            }
            dstrain.add(aux1);
        }
        return dstrain;
    }

    public static List<List<int[]>> GetTest (List<List<int[]>> folds, int k){

        List<List<int[]>> dstest = new ArrayList<> ();

        for(int test = 0 ; test<k ; test++){
            List<int[]>aux = new ArrayList<> ();
            for(int train = 0 ; train <k ; train++){
                if(test == train){
                    aux = (folds.get (test));
                    dstest.add (aux);
                }
            }
        }
        return dstest;
    }

    public static List<int[][]> Train (List<List<int[]>> folds, int k){

        List<List<int[]>> train = GetTrain(folds,k);
        List<int[][]> train1 = new ArrayList<>();

        for (int i=0; i < train.size();i++){
            train1.add(train.get(i).stream().toArray(int[][]::new));
        }

        return train1;
    }

    public static List<int[][]> Test (List<List<int[]>> folds, int k){
        List<List<int[]>> test = GetTest(folds,k);
        List<int[][]> test1 = new ArrayList<>();

        for (int i=0; i < test.size();i++){
            test1.add(test.get(i).stream().toArray(int[][]::new));
        }

        return test1;
    }
}
