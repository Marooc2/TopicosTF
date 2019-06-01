
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import weka.core.converters.ConverterUtils.DataSource;
import Funciones.*;

public class Main {
    public static void main(String[] args) throws FileNotFoundException {
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
        int[] vars = {0,1,2,3,4};
        int alpha = 1;
        //Lectura del dataset // INGRESAR NOMBRE DE ARCHIVO
        int[][] dataset = F_herramientas.ReadDs("ds/dataset.txt");
        int columnas = F_herramientas.ColumnasDs(dataset);
        //Transformar la vista de las distribuciones de numeros a letras
        char[] varnames = new char[columnas];
        varnames = F_herramientas.GeneraVariables(varnames);
        
        //Impresion
        Impresion(g, vars,card, alpha, dataset, varnames);

        int[][] data = {{0,2}, {1,1}, {0,0}, {1,2}, {0,2}, {1,0}, {0,0},
                        {1,1}, {0,1}, {1,2}, {0,2}, {0,2}, {1,2}};
    }

    public static List<List<int[]>> GetFolds (int[][] ds, boolean ordenado, int[] vars, int k){
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

        /////Impresion///////
        System.out.println();
        for (int i=0; i<folds.size();i++){
            for (int j=0; j<folds.get(i).size();j++){
                //System.out.println(Arrays.toString(folds.get(i).get(j)));
            }
            //System.out.println();
        }
        return folds;
    }

    public static List<List<List<int[]>>> GetTrain (int[][] ds, boolean ordenado, int[] vars, int k){

        List<List<int[]>> folds = GetFolds(ds,ordenado,vars,k);
        List<List<List<int[]>>> dstrain = new ArrayList<> ();


        for(int test = 0 ; test<k ; test++){
            List<List<int[]>>aux2 = new ArrayList<> ();
            for(int train = 0 ; train <k ; train++){
                if(test != train){
                    aux2.add (folds.get (train));
                }
            }
            dstrain.add (aux2);
        }
        return dstrain;
    }

    public static List<List<List<int[]>>> GetTest (int[][] ds, boolean ordenado, int[] vars, int k){

        List<List<int[]>> folds = GetFolds(ds,ordenado,vars,k);
        List<List<List<int[]>>> dstest = new ArrayList<> ();

        for(int test = 0 ; test<k ; test++){
            List<List<int[]>>aux = new ArrayList<> ();
            for(int train = 0 ; train <k ; train++){
                if(test == train){
                    aux.add (folds.get (test));
                    dstest.add (aux);
                }
            }
        }
        return dstest;
    }

    public static void Impresion(int[][] grafo,int [] vars, int[] card, int alpha, int[][] ds, char[] vn) {
        List<int[]> listDis = F_distribuciones.GeneraDistribuciones(grafo);
        List<double[]> listDistribucionesGrafo = F_distribuciones.RealizaDistribuciones(grafo, card, alpha, ds);
        int[][] matriz_de_confusion = F_herramientas.MatrizDeConfusion(vars,card,alpha,grafo,ds);
        List<Double> listConjuntaGrafo = F_distribuciones.ConjuntaGrafo(vars,card,alpha,grafo,ds);
        int[] arrinferencia = F_herramientas.TablaInferencia(vars,card,alpha,grafo,ds);
        double[] medidas = F_herramientas.Medidas(vars,card,alpha,grafo,ds);

        int aux;
        /////Impresion de distribuciones MA////
        System.out.println("Distribuciones de la matriz de Adyacencia:");
        System.out.println("------------------");
        for (int i = 0; i < listDis.size(); i++) {
            for (int j = 0; j < listDis.get(i).length; j++) {
                aux = listDis.get(i)[j];
                switch (aux) {
                    case 0: {
                        System.out.print(vn[0]);
                        break;
                    }
                    case 1: {
                        System.out.print(vn[1]);
                        break;
                    }
                    case 2: {
                        System.out.print(vn[2]);
                        break;
                    }
                    case 3: {
                        System.out.print(vn[3]);
                        break;
                    }
                    case 4: {
                        System.out.print(vn[4]);
                        break;
                    }
                    case 5: {
                        System.out.print(vn[5]);
                        break;
                    }
                    default:
                        System.out.println("N");
                }
                if (j<listDis.get(i).length - 1)
                    System.out.print("|");
            }
            System.out.println();
        }
        System.out.println("------------------");
        System.out.println();

        /////Impresion distribuciones/////
        System.out.println("Distribuciones");
        System.out.println("------------------");
        for (int i = 0; i < listDis.size(); i++){
            int[] val = new int[listDis.get(i).length];
            int ivar;
            int cardAnterior;
            int tamfactor = 1;
            double[] auxprob = listDistribucionesGrafo.get(i);

            if(listDis.get(i).length > 1){
                for (int p = 0; p < listDis.get(i).length; p++) {
                    tamfactor *= card[listDis.get(i)[p]];
                }

                System.out.println("Distribucion de " + listDis.get(i)[0]);
                for (int k = 0; k < tamfactor; k++) {
                    cardAnterior = 1;
                    for (int j = 0; j < listDis.get(i).length; j++) {
                        ivar = vars[listDis.get(i)[j]];
                        val[j] = (int) (Math.floor(k / cardAnterior) % card[ivar]);
                        cardAnterior *= card[ivar];
                        System.out.print(val[j]);
                    }
                    System.out.println(" = " +auxprob[k]);
                }
            }
            else {
                tamfactor = card[listDis.get(i)[0]];

                System.out.println("Distribucion de " + listDis.get(i)[0]);
                for (int k = 0; k < tamfactor; k++) {
                    cardAnterior = 1;
                    for (int j = 0; j < listDis.get(i).length; j++) {
                        ivar = vars[listDis.get(i)[j]];
                        val[j] = (int) (Math.floor(k / cardAnterior) % card[ivar]);
                        cardAnterior *= card[ivar];
                        System.out.print(val[j]);
                    }
                    System.out.println(" = " + auxprob[k]);
                }
            }
            System.out.println();
        }
        System.out.println("------------------");
        System.out.println();

        /////Impresion distribucion conjunta dataset//////

        /*
        int[] variablesConjunta = {0,1,2,3,4};
        System.out.println("Distribucion Conjunta del dataset: ");
        System.out.println("------------------");
        F_distribuciones.DistribucionPConjunta(variablesConjunta, card, alpha, ds);
        System.out.println("------------------");
        System.out.println();
         */

        /////Impresion Conjunta grafo/////
        System.out.println("Conjunta del grafo");
        System.out.println("------------------");
        {
            int tamfactor = 1;
            for (int p = 0; p < listDis.size(); p++) {
                tamfactor *= card[p];
            }
            int[] val = new int[card.length];
            int ivar;
            int cardAnterior;
            for (int k = 0; k < tamfactor; k++) {
                cardAnterior = 1;
                for (int j = 0; j < vars.length; j++) {
                    ivar = vars[j];
                    val[j] = (int) (Math.floor(k / cardAnterior) % card[ivar]);
                    cardAnterior *= card[ivar];
                    System.out.print(val[j]);
                }
                System.out.println(" = " + listConjuntaGrafo.get(k));
            }
        }
        System.out.println("------------------");
        System.out.println();

        /////Consulta inferencia//////
        System.out.println("Consulta inferencia");
        System.out.println("------------------");
        int[] valsEvidencia = {1,2,3,1};
        double[] inferencia = F_herramientas.ConsultaInf(vars,valsEvidencia,card,alpha,grafo,ds);
        System.out.println("Inferencia de clase cuando " + Arrays.toString(valsEvidencia)+": "
                + (int) inferencia[0] + " con la probabilidad de: " + String.format("%.5f",inferencia[1]));
        System.out.println("------------------");
        System.out.println();

        /////Impresion Tabla inferencia/////
        System.out.println("Tabla inferencia");
        System.out.println("------------------");
        System.out.println("Evid Cla Inf");
        for (int i = 0; i < ds.length; i++){
            for (int j = 0; j < ds[i].length-1; j++){
                System.out.print(ds[i][j]);
            }
            System.out.print("  " + ds[i][ds[i].length-1]);
            System.out.print("   " + arrinferencia[i]);
            System.out.println();
        }
        System.out.println("------------------");
        System.out.println();

        /////Impresion matriz de confusion/////
        System.out.println("Matriz de Confusion");
        System.out.println("------------------");
        for (int i=0; i < matriz_de_confusion.length; i++){
            System.out.println(Arrays.toString(matriz_de_confusion[i]));
        }
        System.out.println("------------------");
        System.out.println();

        /////Impresion medidas//////
        System.out.println("Medidas");
        System.out.println("------------------");
        System.out.println ("Presición: "+ medidas[0]);
        System.out.println ("Recall: "+ medidas[1]);
        System.out.println ("F1: "+ medidas[2]);
        System.out.println("------------------");
    }
}