
import java.util.ArrayList;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Topicos_TP {
    public static void main(String[] args) throws FileNotFoundException {
        //INPUT
        //Matriz de adyacencia de g
        int[][] g = {{0, 0},
                     {0, 0}};
        //Ingresar las cardinalidades
        int[] card = {3,2,2};
        // Ingresar Alpha
        int alpha = 1;

        //Lectura del dataset // INGRESAR NOMBRE DE ARCHIVO
        int[][] dataset = ReadDs("data2.txt");
        int columnas = ColumnasDs(dataset);
        //Transformar la vista de las distribuciones de numeros a letras
        char[] varnames = new char[columnas];
        varnames = GeneraVariables(varnames);
        //Realiza las distribuciones del grafo e imprime
        //VisualizarDistribuciones(g, card, alpha, dataset, varnames);

        //INGRESAR VARIABLES PARA HALLAR LA DISTRIBUCION CONJUNTA DEL DATASET
        int[] variablesConjunta = {0,1,2};
        ArrayList<Double> distribuciones = DistribucionPConjunta(variablesConjunta, card, alpha, dataset);

        for (int i=0; i<distribuciones.size();i++){
            distribuciones.get(i);
        }

        int[] vars = {0,1,2};
        int[] vals = {2,1,1};
        // A = 0 B = 1
        // P(A=0,B=1)
        //System.out.println(" Cuando A: " + vals[0] + " y B: "+ vals[1]);
        System.out.println("Indice de A: "+ vals[0] + " B: " + vals[1] + " C: " +vals[2] + " = " + GetIndex(vars,vals,card,distribuciones));

    }
    public static int GetIndex(int[] variable,int[] valor, int[] cardinalidad, ArrayList<Double> distribucion){
        int index = 0;
        int[] aux = new int[cardinalidad.length+1];
        aux[0] = 1;
        for (int i=1;i<aux.length;i++){
            aux[i] = aux[i-1]*cardinalidad[i-1];
        }
        for (int j=0;j<cardinalidad.length;j++){
            index += valor[j]* aux[j];
        }
        /*for (int i=0;i<variable.length;i++){
            index+= valor[i];
        }
        index = index+(cardinalidad[0]-1)*valor[variable.length-1];
         */
        return index;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //LECTURA DEL DATASET
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static int[][] ReadDs(String ds) throws FileNotFoundException {

        ArrayList<int[]> listDataset = new ArrayList<int[]>();

        FileReader fr = new FileReader(ds);
        Scanner s = new Scanner(fr);

        while (s.hasNextLine()) {
            String linea = s.nextLine();
            String[] campos = linea.split("\t");
            int[] camposInt = new int[campos.length];
            for (int i = 0; i < campos.length; i++)
                camposInt[i] = Integer.parseInt(campos[i]);
            listDataset.add(camposInt);
        }

        int[][] dataset = new int[listDataset.size()][listDataset.get(0).length];

        for (int i = 0; i < dataset.length; i++) {
            dataset[i] = listDataset.get(i);
        }
        /////////////////////////////////////////////////////////////////////////

        return dataset;
    }

    public static int ColumnasDs(int[][] dataset) {
        int columnas = 0;
        for (int i = 0; i < dataset.length; i++) {
            for (int j = 0; j < dataset[i].length; j++) {
                columnas += 1;
            }
            break;
        }
        return columnas;
    }

    public static char[] GeneraVariables(char[] vn) {

        for (int i = 0; i < vn.length; i++) {
            vn[i] = (char) ('A' + i);
        }
        return vn;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //PROBABILIDADES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static double ProbabilidadMarginalDiricht(int var, int val, int card, int alpha, int[][] ds) {
        double contador = 0.0;
        double probMarginalD;

        for (int i = 0; i < ds.length; i++) {
            if (ds[i][var] == val)
                contador += 1.0;
        }
        probMarginalD = (contador + alpha) / (ds.length + card * alpha);
        return probMarginalD;
    }

    public static double ProbabilidadConjuntaDiricht(int[] var, int[] val, int[] card, int alpha, int[][] ds) {
        double contador = 0.0;
        double probConjuntaD;
        double prodCard = 1.0;
        boolean flag = false;
        for (int i = 0; i < ds.length; i++) {
            for (int j = 0; j < var.length; j++) {
                if (ds[i][var[j]] != val[j]) {
                    flag = false;
                    break;
                } else
                    flag = true;
            }
            if (flag == true)
                contador += 1.0;
        }

        for (int i = 0; i < var.length; i++) {
            prodCard = prodCard * card[var[i]];
        }

        probConjuntaD = (contador + alpha) / (ds.length + (prodCard * alpha));
        return probConjuntaD;
    }

    public static void ProbabilidadCondicionadaDiricht(int[] var, int[][] vals, int[] card, int alpha, int[][] ds) {
        double ProbA;
        double ProbB;
        double SumProb = 0.0;
        double[] Distribucion = new double[card[var[0]]];
        int[] auxvar;
        int[] auxval;

        auxvar = Arrays.copyOfRange(var, 1, var.length);

        //El arreglo va a recorer hasta la cardinalidad de la variable 0 que es el hijo
        for (int i = 0; i < card[var[0]]; i++) {
            ProbA = ProbabilidadConjuntaDiricht(var, vals[i], card, alpha, ds);

            auxval = Arrays.copyOfRange(vals[i], 1, vals[i].length);

            if (auxvar.length > 1) {
                ProbB = ProbabilidadConjuntaDiricht(auxvar, auxval, card, alpha, ds);
            } else {
                ProbB = ProbabilidadMarginalDiricht(auxvar[0], auxval[0], card[0], alpha, ds);
            }
            Distribucion[i] = ProbA / ProbB;
        }

        // Normalizar

        for (int i = 0; i < Distribucion.length; i++) {
            SumProb += Distribucion[i];
        }

        for (int j = 0; j < Distribucion.length; j++) {
            Distribucion[j] = Distribucion[j] / SumProb;
        }
        // Imprimir
        for (int k = 0; k < Distribucion.length; k++) {
            System.out.print("Valores de las variables: ");
            for (int l = 0; l < vals[k].length; l++) {
                System.out.print(vals[k][l]+ " ");
            }
            System.out.println();
            System.out.print(Distribucion[k]);
            System.out.println();
        }
    }

    public static ArrayList<Double> DistribucionPConjunta(int[] var, int[] card, int alpha, int[][] ds) {
        int[] val = new int[var.length];
        int ivar;
        int cardAnterior = 1;
        int tamfactor = 1;
        double suma = 0.0;
        ArrayList<Double> Distribuciones = new ArrayList<Double>();

        //  Halla el tamaño del factor
        for (int p = 0; p < var.length; p++) {
            tamfactor *= card[p];
        }
        //System.out.println("FACTOR: " + tamfactor);
        //System.out.println("Distribucion conjunta del dataset:");

        for (int k = 0; k < tamfactor; k++) {
            cardAnterior = 1;
            for (int j = 0; j < var.length; j++) {
                ivar = var[j];
                val[j] = (int) (Math.floor(k / cardAnterior) % card[ivar]);
                cardAnterior *= card[ivar];
                System.out.print(val[j]);

            }
            double pc = ProbabilidadConjuntaDiricht(var, val, card, alpha, ds);
            suma = suma + pc;
            System.out.println(" = " + pc);
            Distribuciones.add(pc);
        }
        //System.out.println("Suma: " + suma);

        return Distribuciones;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //DISTRIBUCIONES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static List<List<Integer>> GeneraDistribuciones(int[][] grafo) {

        List<List<Integer>> listPosicionesDis = new ArrayList<List<Integer>>();

        for (int i = 0; i < grafo.length; i++) {
            List<Integer> listAux = new ArrayList<Integer>();
            listAux.add(i);
            for (int j = 0; j < grafo[i].length; j++) {
                if (grafo[j][i] == 1)
                    listAux.add(j);
            }
            listPosicionesDis.add(listAux);

        }
        return listPosicionesDis;
    }

    public static void RealizaDistribuciones(int[][] grafo, int[] card, int alpha, int[][] ds) {

        List<List<Integer>> listDis = GeneraDistribuciones(grafo);

        for (int i = 0; i < listDis.size(); i++) {

            //System.out.println();
            System.out.println(listDis.get(i));

            //Si el arreglo de distribuciones en la posicion i es mayor que 1
            // hace la distribucion condicional del arreglo (A DADO B) A:{0,1} B:{0,1}
            if (listDis.get(i).size() > 1) {

                int[] val = new int[listDis.get(i).size()];
                int ivar;
                int cardAnterior = 1;
                int tamfactor = 1;
                int[][] auxval = new int[card[listDis.get(i).get(0)]][listDis.get(i).size()];

                for (int p = 0; p < listDis.get(i).size(); p++) {
                    tamfactor *= card[listDis.get(i).get(p)];
                }

                for (int k = 0; k < tamfactor; k += card[listDis.get(i).get(0)]) {
                    for (int x = 0; x < card[listDis.get(i).get(0)]; x++) {
                        cardAnterior = 1;
                        for (int j = 0; j < listDis.get(i).size(); j++) {
                            ivar = listDis.get(i).get(j);
                            val[j] = (int) (Math.floor((k + x) / cardAnterior) % card[ivar]);
                            cardAnterior *= card[ivar];
                        }
                        auxval[x] = val.clone();
                    }
                    ProbabilidadCondicionadaDiricht(listDis.get(i).stream().mapToInt(Integer::intValue).toArray(), auxval, card, alpha, ds);
                    System.out.println();
                }
            }
            //De lo contrario hace marginal de los Padres
            else {
                int[] val = new int[listDis.get(i).size()];
                int ivar;
                int cardAnterior = 1;
                int tamfactor = 1;

                //  Halla el tamaño del factor
                for (int p = 0; p < listDis.get(i).size(); p++) {
                    tamfactor *= card[listDis.get(i).get(p)];
                }
                for (int k = 0; k < tamfactor; k++) {
                    cardAnterior = 1;
                    for (int j = 0; j < listDis.get(i).size(); j++) {
                        ivar = listDis.get(i).get(j);
                        val[j] = (int) (Math.floor(k / cardAnterior) % card[ivar]);
                        cardAnterior *= card[ivar];
                    }
                    for (int l = 0; l < val.length; l++) {
                        System.out.println("Valor de la variable: " + val[l]);
                    }
                    System.out.println(ProbabilidadConjuntaDiricht(listDis.get(i).stream().mapToInt(Integer::intValue).toArray(), val, card, alpha, ds));
                }
                System.out.println();
            }

        }
    }

    public static void VisualizarDistribuciones(int[][] grafo, int[] card, int alpha, int[][] ds, char[] vn) {
        List<List<Integer>> listDis = GeneraDistribuciones(grafo);
        int aux;

        System.out.println("Distribuciones de la matriz de Adyacencia:");
        for (int i = 0; i < listDis.size(); i++) {
            for (int j = 0; j < listDis.get(i).size(); j++) {
                aux = listDis.get(i).get(j);
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
                    case 6: {
                        System.out.print(vn[6]);
                        break;
                    }
                    case 7: {
                        System.out.print(vn[7]);
                        break;
                    }
                    case 8: {
                        System.out.print(vn[8]);
                        break;
                    }
                    case 9: {
                        System.out.print(vn[9]);
                        break;
                    }
                    case 10: {
                        System.out.print(vn[10]);
                        break;
                    }
                    default:
                        System.out.println("N");
                }
                if (j<listDis.get(i).size() - 1)
                    System.out.print("|");
            }
            System.out.println();
        }
        System.out.println();

        RealizaDistribuciones(grafo, card, alpha, ds);
    }
}