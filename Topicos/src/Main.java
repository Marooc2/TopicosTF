
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import Funciones.*;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
        long startTime = System.nanoTime();
        //INPUT
        //Matriz de adyacencia de g
        int[][] g = {
                {0, 1, 0, 0, 0},
                {0, 0, 0, 1, 0},
                {0, 0, 0, 1, 0},
                {0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0},
        };
        //Ingresar las cardinalidades
        int[] card = {3,3,4,2,3};
        // Ingresar Alpha
        int alpha = 1;
        //Lectura del dataset // INGRESAR NOMBRE DE ARCHIVO
        int[][] dataset = F_herramientas.ReadDs("ds/dataset.txt");
        int columnas = F_herramientas.ColumnasDs(dataset);
        //Transformar la vista de las distribuciones de numeros a letras
        char[] varnames = new char[columnas];
        varnames = F_herramientas.GeneraVariables(varnames);
        int[] vars = {0,1,2,3,4};
        //Realiza las distribuciones del grafo e imprime

        F_visualizacion.VisualizarDistribuciones(g, vars,card, alpha, dataset, varnames);

        /////////////////////////////////////////////////////////////////////////////////////////////////////

        //INGRESAR VARIABLES PARA HALLAR LA DISTRIBUCION CONJUNTA DEL DATASET
        //int[] variablesConjunta = {0,1,2,3,4};
        //System.out.println("Distribucion Conjunta: ");
            //F_distribuciones.DistribucionPConjunta(variablesConjunta, card, alpha, dataset);
            //int[] valsEvidencia = {1,2,3,1};
            //double[] inferencia = ConsultaInf(vars,valsEvidencia,card,alpha,g,dataset);
            //System.out.println();
            //System.out.println("Inferencia de clase cuando " + Arrays.toString(valsEvidencia)+": "
            //        + (int) inferencia[0] + " con la probabilidad de: " + String.format("%.5f",inferencia[1]));

        //int pliegues = 10;
        //CrossValidation(dataset, pliegues);

        int[][] data = {{0,2},
                        {1,1},
                        {0,0},
                        {1,2},
                        {0,2},
                        {1,0},
                        {0,0},
                        {1,1},
                        {0,1},
                        {1,2},
                        {0,2},
                        {0,2},
                        {1,2}};

        //CrossValidation(data,true,vars);

        long endTime   = System.nanoTime();
        long totalTime = endTime - startTime;
        System.out.println("Execution time in milliseconds : " + totalTime / 1000000);
    }

    public static void CrossValidation (int[][] ds, boolean ordenado, int[] vars) {
        int m = ds.length;
        int k = 10;

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

        /////Impresion///////
        System.out.println();
        for (int i=0; i<folds.size();i++){
            for (int j=0; j<folds.get(i).size();j++){
                //System.out.println(Arrays.toString(folds.get(i).get(j)));
            }
            //System.out.println();
        }

        int cols = vars.length;
        List<Integer> filas_train = new ArrayList<>();
        List<Integer> filas_test = new ArrayList<>();
/*
        /////Inicializando Listas con ceross//////
        for (int i = 0; i < k; i++){
            filas_train.add(0);
            filas_test.add(0);
        }

        /////Tamano de los k_folds de training y test//////
        for (int test = 0; test < k; test++){
            for (int train = 0; train < k; train++){
                List<int[]> fold_k = folds.get(train);
                int tam = fold_k.size();

                if (test != train){
                    tam = tam + filas_train.get(test);
                    filas_train.set(test,tam);
                }
                else {
                    tam = tam + filas_test.get(test);
                    filas_test.set(test,tam);
                }
            }
        }
 */
        /////Llenado de los ds de training y test
        List<List<List<int[]>>> dstrain = new ArrayList<> ();
        List<List<List<int[]>>> dstest = new ArrayList<> ();

        for(int test = 0 ; test<k ; test++){
            List<List<int[]>>aux = new ArrayList<> ();
            List<List<int[]>>aux2 = new ArrayList<> ();
            for(int train = 0 ; train <k ; train++){
                if(test == train){
                    aux.add (folds.get (test));
                    dstest.add (aux);
                }else{
                    aux2.add (folds.get (train));
                }
            }
            dstrain.add (aux2);
        }
        System.out.println();
    }
}