
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
        //INGRESAR VARIABLES PARA HALLAR LA DISTRIBUCION CONJUNTA DEL DATASET
        int[] variablesConjunta = {0,1,2,3,4};

        //System.out.println("Distribucion Conjunta: ");
        //F_distribuciones.DistribucionPConjunta(variablesConjunta, card, alpha, dataset);

        ConjuntaGrafo2(vars,card,alpha,g,dataset);

        //int[] valsEvidencia = {1,2,3,1};

        //double[] inferencia = Inferencia(vars,valsEvidencia,card,alpha,g,dataset);
        //System.out.println();
        //System.out.println("Inferencia de clase cuando " + Arrays.toString(valsEvidencia)+": "
        //        + (int) inferencia[0] + " con la probabilidad de: " + String.format("%.5f",inferencia[1]));

        MatrizDeConfusion(vars,card,alpha,g,dataset);
        int pliegues = 10;
        //CrossValidation(dataset, pliegues);
    }

    public static int GetIndex(int[] valor, int[] cardinalidad){
        int index = 0;
        int[] aux = new int[cardinalidad.length];
        aux[0] = 1;
        for (int i=0;i<cardinalidad.length-1;i++){
            aux[i+1] = aux[i]*cardinalidad[i];
        }
        for (int j=0;j<valor.length;j++){
            index += valor[j]* aux[j];
        }
        return index;
    }

    public static int[] GetPosList(int[] variable,int[] cardinalidad, int index){
        int ivar;
        int cardAnterior = 1;
        int[] valores = new int[cardinalidad.length];

        for (int j = 0; j < variable.length; j++) {
            ivar = variable[j];
            valores[j] = (int) (Math.floor(index / cardAnterior) % cardinalidad[ivar]);
            cardAnterior *= cardinalidad[ivar];
            //System.out.print(valores[j]);
        }
        //System.out.print(" =");
        return valores;
    }

    public static double GetProbabilidad(int[] vars, int[] vals, int[] card, double[] dist){
        double prob = 0.0;
        int ind;

        ind = GetIndex(vals,card);
        //GetPosList(vars,card,ind);

        for (int i=0;i<dist.length;i++){
            if(i == ind){
                prob = dist[ind];
                //System.out.print(" " + prob);
            }
        }
        System.out.println();
        return prob;
    }

    public static List<Double> ConjuntaGrafo(int[] vars, int[] card, int alpha, int[][]grafo, int[][]ds) {
        //System.out.println();
        //System.out.println("Distribucion Conjunta del grafo:");

        List<Double> ConjuntaG = new ArrayList<>();
        List<int[]> Listdist = F_distribuciones.GeneraDistribuciones(grafo);
        List<double[]> Listprobdist = F_distribuciones.RealizaDistribuciones(grafo, card, alpha, ds);
        int tamfactor = 1;

        for (int p = 0; p < Listdist.size(); p++) {
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
                //System.out.print(val[j]);
            }
            //System.out.print(" = ");
            double prob = 1.0;
            double a;
            for (int j = 0; j < Listdist.size(); j++){
                if (Listdist.get(j).length > 1){
                    int[] aux = new int[Listdist.get(j).length];
                    for (int l = 0; l < aux.length; l++){
                        aux[l] = val[Listdist.get(j)[l]];
                    }
                    int[] auxcard = new int[Listdist.get(j).length];

                    for (int n = 0; n < Listdist.get(j).length; n++)
                        auxcard[n] = card[Listdist.get(j)[n]];

                    int indice = GetIndex(aux,auxcard);
                    a = Listprobdist.get(j)[indice];
                }
                else {
                    a = Listprobdist.get(j)[val[Listdist.get(j)[0]]];
                }
                prob *= a;
            }
            //System.out.print(prob);
            //System.out.println();
            ConjuntaG.add(prob);
        }
        //System.out.println();
        return ConjuntaG;
    }

    public static List<Double> ConjuntaGrafo2(int[] vars, int[] card, int alpha, int[][]grafo, int[][]ds) {
        System.out.println();
        System.out.println("Distribucion Conjunta del grafo:");

        List<Double> ConjuntaG = new ArrayList<>();
        List<int[]> Listdist = F_distribuciones.GeneraDistribuciones(grafo);
        List<double[]> Listprobdist = F_distribuciones.RealizaDistribuciones(grafo, card, alpha, ds);
        int tamfactor = 1;

        for (int p = 0; p < Listdist.size(); p++) {
            tamfactor *= card[p];
        }
        int[] val = new int[card.length];
        int ivar;
        int cardAnterior;

        for (int k = 0; k < tamfactor; k++) {
            System.out.print(k + " ");
            cardAnterior = 1;
            for (int j = 0; j < vars.length; j++) {
                ivar = vars[j];
                val[j] = (int) (Math.floor(k / cardAnterior) % card[ivar]);
                cardAnterior *= card[ivar];
                System.out.print(val[j]);
            }
            System.out.print(" = ");
            double prob = 1.0;
            double a;
            for (int j = 0; j < Listdist.size(); j++){
                if (Listdist.get(j).length > 1){
                    int[] aux = new int[Listdist.get(j).length];
                    for (int l = 0; l < aux.length; l++){
                        aux[l] = val[Listdist.get(j)[l]];
                    }
                    int[] auxcard = new int[Listdist.get(j).length];

                    for (int n = 0; n < Listdist.get(j).length; n++)
                        auxcard[n] = card[Listdist.get(j)[n]];

                    int indice = GetIndex(aux,auxcard);
                    a = Listprobdist.get(j)[indice];
                }
                else {
                    a = Listprobdist.get(j)[val[Listdist.get(j)[0]]];
                }
                prob *= a;
            }
            System.out.print(prob);
            System.out.println();
            ConjuntaG.add(prob);
        }
        System.out.println();
        return ConjuntaG;
    }
    public static double[] Inferencia (int[] vars, int [] valsE, int[] card, int alpha, int[][]g,int[][] dataset){

        List<Double> ConjuntaG = ConjuntaGrafo(vars,card,alpha,g,dataset);

        int[] varevidencia = Arrays.copyOfRange(vars,0,vars.length-1);
        int varconsulta = vars[vars.length-1];

        int []auxvalds = new int[vars.length];
        System.arraycopy(valsE,0,auxvalds,0,valsE.length);

        int indice;
        double[] prob = new double[card[varconsulta]];
        double[] inferencia = new double[2];

        for (int i=0; i<card[varconsulta]; i++){
            auxvalds[auxvalds.length-1] = i;
            indice = GetIndex(auxvalds,card);

            for (int j = 0; j < ConjuntaG.size(); j++){
                if (j == indice){
                    prob[i] = ConjuntaG.get(j);
                }
            }
        }
        double max = Arrays.stream(prob).max().getAsDouble();
        inferencia[1] = max;

        for (int k = 0; k < prob.length; k++){
            if (max == prob[k])
                inferencia[0] = k;
        }
        return inferencia;
    }

    public static double[] MatrizDeConfusion (int[] vars, int[] card, int alpha, int[][]g,int[][] dataset){
        System.out.println();
        double[] matriz = new double[vars.length];
        int[] arrval = new int[vars.length - 1];
        int[] arrclase = new int[dataset.length];
        int[] inf = new int[dataset.length];
        double[] infaux = new double[2];
        double[] tp = new double[card[vars.length - 1]];
        double[] fp = new double[card[vars.length - 1]];
        double[] precision = new double[tp.length];
        double accuracy;

        for (int i = 0; i < dataset.length; i++){
            for (int j = 0; j < dataset[i].length - 1; j++){
                arrval[j] = dataset[i][j];
            }
            infaux = Inferencia(vars,arrval,card,alpha,g,dataset);
            inf[i] = (int) infaux[0];
        }

        System.out.println("Inferencia ");
        for (int i = 0; i < dataset.length; i++){
            System.out.print(i + "  ");
            for (int j = 0; j < dataset[i].length; j++){
                System.out.print(dataset[i][j]);
            }
            System.out.print(" " + inf[i]);
            System.out.println();
        }
/*
        int[] auxarr = new int[card[vars.length - 1]];

        for (int p = 0; p < card[vars.length - 1]; p++){
            auxarr[p] = p;
        }

        for (int i = 0; i < dataset.length; i++){
            arrclase[i] = dataset[i][dataset.length - 1];
        }

        for (int i = 0; i < auxarr.length;i++){
            int truep = 0;
            int falsep = 0;

            for (int j = 0; j < arrclase.length; j++){
                if (arrclase[j] == i){
                    if (arrclase[j] == inf[j]){
                        truep++;
                    }
                    else {
                        falsep++;
                    }
                }
            }
            tp[i]=truep;
            fp[i]=falsep;
        }

        double auxtp = 0.0;
        double auxtodo = 0.0;

        System.out.println();

        for (int i = 0; i< precision.length; i++){
            double aux = tp[i]/(tp[i]+fp[i]);
            precision[i] = aux;
            System.out.println("Precision de Clase en " + i + " = " + precision[i]);
            auxtp += tp[i];
            auxtodo += tp[i]+fp[i];
        }
        accuracy = auxtp/auxtodo;
        System.out.println("Accuracy de todo = " + accuracy);
        System.arraycopy(precision,0,matriz,0,precision.length);
        matriz[matriz.length - 1] = accuracy;
 */
        return matriz;
    }

    public static void CrossValidation (int[][] ds, int k){

        int[] indexes = new int[ds.length];
        for (int i=0; i<ds.length;i++)
            indexes[i] = i;

        int[][] foldIndexes = new int[k][];

        int step = indexes.length / k;
        int beginIndex = 0;

        for (int i = 0; i < k - 1; i++) {
            foldIndexes[i] = Arrays.copyOfRange(indexes, beginIndex, beginIndex + step);
            beginIndex = beginIndex + step;
        }

        foldIndexes[k - 1] = Arrays.copyOfRange(indexes, beginIndex, indexes.length);

        for (int i=0;i<foldIndexes.length;i++)
            System.out.println(Arrays.toString(foldIndexes[i]));
    }

}