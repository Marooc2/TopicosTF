
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.InputMismatchException;
import Funciones.*;

public class Main {
    public static void main(String[] args) throws Exception {
        //INPUT

        //Lectura del dataset // INGRESAR NOMBRE DE ARCHIVO
        String data = "ds/WeatherNominal.arff";
        int[][] dataset = F_herramientas.LeerDataArff(data);

        //Cross Validation K-fold orenado y aleatorio
        List<List<int[]>> folds = CrossValidation.GetFolds(dataset,true,10);
        List<int[][]> train = CrossValidation.Train(folds,10);
        List<int[][]> test = CrossValidation.Test(folds,10);

        //Matriz de adyacencia de g
        int[][] g = {
                {0, 1, 0, 0},
                {0, 0, 0, 1},
                {0, 0, 0, 1},
                {0, 0, 0, 0},
        };
        //Cardinalidades, variables y alpha
        int[] card = F_herramientas.LeerCardinalidad(data);
        int[] vars = F_herramientas.LeerVariables(data);
        int alpha = 1;

        //Transformar la vista de las distribuciones de numeros a letras
        int columnas = F_herramientas.ColumnasDs(dataset);
        char[] varnames = new char[columnas];
        varnames = F_herramientas.GeneraVariables(varnames);

        //Impresion
        Impresion(g, vars,card, alpha, dataset, varnames,columnas);


    }

    public static void Impresion(int[][] grafo,int [] vars, int[] card, int alpha, int[][] ds, char[] vn,int columnas) {
        List<int[]> listDis = F_distribuciones.GeneraDistribuciones(grafo);
        List<double[]> listDistribucionesGrafo = F_distribuciones.RealizaDistribuciones(grafo, card, alpha, ds);
        int[][] matriz_de_confusion = F_herramientas.MatrizDeConfusion(vars,card,alpha,grafo,ds);
        List<Double> listConjuntaGrafo = F_distribuciones.ConjuntaGrafo(vars,card,alpha,grafo,ds);
        int[] arrinferencia = F_herramientas.TablaInferencia(vars,card,alpha,grafo,ds);
        List<double[]> medidas = F_herramientas.Medidas(vars,card,alpha,grafo,ds);
        int aux;

        /////Impresion de distribuciones MA////
        System.out.println("Distribuciones de la matriz de Adyacencia:");
        System.out.println("------------------");
        for (int i = 0; i < listDis.size(); i++) {
            boolean boleano = true;
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
                if (boleano) {
                    if (j < listDis.get(i).length - 1) {
                        System.out.print("|");
                        boleano = false;
                    }
                }
            }
            System.out.println();
        }
        System.out.println("------------------");
        System.out.println();


        /*
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
        */


        /*
        /////Impresion distribucion conjunta dataset//////
        int[] variablesConjunta = {0,1,2,3,4};
        System.out.println("Distribucion Conjunta del dataset: ");
        System.out.println("------------------");
        F_distribuciones.ConjuntaDs(variablesConjunta, card, alpha, ds);
        System.out.println("------------------");
        System.out.println();
        */


        /*
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
        */


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


      /*  /////Consulta inferencia//////
        System.out.println("Consulta inferencia");
        System.out.println("------------------");
        int[] valsEvidencia = new int[columnas - 1];
        Scanner reader = new Scanner(System.in);
        int numero;
        int iterador = 0;
        System.out.println("Introduce " + valsEvidencia.length+ " evidencias. Numero enter Numero ...");

        do {
            try {
                numero = reader.nextInt();
                valsEvidencia[iterador] = numero;
                iterador++;
            } catch (InputMismatchException ime){
                System.out.println("¡Cuidado! Solo puedes insertar números. ");
                reader.next();
            }
        } while (iterador != valsEvidencia.length);
        double[] inferencia = F_herramientas.ConsultaInf(vars,valsEvidencia,card,alpha,grafo,ds);
        System.out.println("Inferencia de clase cuando E:" + Arrays.toString(valsEvidencia)+": "
                + (int) inferencia[0] + " con la probabilidad de: " + String.format("%.5f",inferencia[1]));
        System.out.println("------------------");
        System.out.println();
*/

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
        System.out.println("Presicion: " + Arrays.toString(medidas.get(0)));
        System.out.println("Recall: " + Arrays.toString(medidas.get(1)));
        System.out.println("F1: " + Arrays.toString(medidas.get(2)));
        System.out.println("Accuaracy: " + medidas.get(3)[0]);
        System.out.println("------------------");
    }
}