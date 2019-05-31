
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
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
            //double[] inferencia = Inferencia(vars,valsEvidencia,card,alpha,g,dataset);
            //System.out.println();
            //System.out.println("Inferencia de clase cuando " + Arrays.toString(valsEvidencia)+": "
            //        + (int) inferencia[0] + " con la probabilidad de: " + String.format("%.5f",inferencia[1]));

        //int pliegues = 10;
        //CrossValidation(dataset, pliegues);
    }

    public static void CrossValidation (int[][] ds, int k) {

    }
}