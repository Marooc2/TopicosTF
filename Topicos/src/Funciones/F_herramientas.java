package Funciones;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.DoubleStream;
import weka.core.Instances;
import weka.core.converters.ConverterUtils;

public class F_herramientas {

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

    public static int[] toInArray(List<Integer> list){
        int [] ret = new int [list.size()];
        for (int i = 0; i <ret.length;i++){
            ret[i] = list.get(i);
        }
        return ret;
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

    public static double[] Inferencia (int[] vars, int [] valsE, int[] card, int alpha, int[][]g,int[][] dataset){

        List<Double> ConjuntaG = F_distribuciones.ConjuntaGrafo(vars,card,alpha,g,dataset);

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

    public static double[] ConsultaInf (int[] vars, int [] valsE, int[] card, int alpha, int[][]g,int[][] dataset){

        List<Double> ConjuntaG = F_distribuciones.ConjuntaGrafo(vars,card,alpha,g,dataset);

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

    public static int[] TablaInferencia(int[] vars, int[] card, int alpha, int[][]g,int[][] dataset){
        int[] arrval = new int[vars.length - 1];
        int[] arrinf = new int[dataset.length];
        double[] infaux;

        for (int i = 0; i < dataset.length; i++){
            for (int j = 0; j < dataset[i].length - 1; j++){
                arrval[j] = dataset[i][j];
            }
            infaux = Inferencia(vars,arrval,card,alpha,g,dataset);
            arrinf[i] = (int) infaux[0];
        }
        return arrinf;
    }

    public static int[][] MatrizDeConfusion (int[] vars, int[] card, int alpha, int[][]g,int[][] dataset){

        int[] arrinf = TablaInferencia(vars,card,alpha,g,dataset);
        int[] arrclase = new int[dataset.length];

        for (int i = 0; i < dataset.length; i++){
            arrclase[i]= dataset[i][dataset[i].length-1];
        }

        int[][] matriz = new int[dataset.length][2];

        for(int i = 0; i <arrclase.length;i++){
            matriz[i][0] = arrclase[i];
        }

        for(int i = 0;i<arrinf.length;i++){
            matriz[i][1] = arrinf[i];
        }

        int[][]matrixConfusion = new int[card[card.length-1]][card[card.length-1]];

        int[] a;
        for(int i = 0;i< matriz.length;i++){
            a = matriz[i];
            matrixConfusion[a[0]][a[1]]++;
        }

        return matrixConfusion;
    }

    public static List<double[]> Medidas (int[] vars, int[] card, int alpha, int[][]g,int[][] dataset){

        int[][] matrixConfusion = MatrizDeConfusion(vars,card,alpha,g,dataset);

        double[] recall = new double[card[vars.length-1]];
        double[] f1 = new double[card[vars.length-1]];
        double[] precision = new double[card[vars.length-1]];
        double[] accuaracy = new double[1];


        List<double[]> medidas = new ArrayList<>();


        double[]TP = new double[matrixConfusion.length];
        double[]FP = new double[matrixConfusion.length];
        double[]FN = new double[matrixConfusion.length];

        for(int i = 0;i<matrixConfusion.length;i++){
            for(int j = 0;j<matrixConfusion.length;j++){
                if(i == j){
                    TP[i]=matrixConfusion[i][j];
                }
                if(j > i){
                    FP[j] += matrixConfusion[i][j];
                    FN[i] += matrixConfusion[i][j];
                }
                if(j < i){
                    FP[j] += matrixConfusion[i][j];
                    FN[i] += matrixConfusion[i][j];
                }
            }
        }
        for (int i = 0; i < matrixConfusion.length; i++){
            precision[i] = TP[i]/(TP[i]+FP[i]);
            recall[i] = TP[i]/(TP[i]+FN[i]);
            f1[i] = (2*precision[i]*recall[i]/precision[i]+recall[i]);

            if (Double.isNaN(precision[i])||Double.isNaN(recall[i])||Double.isNaN(f1[i])){
                precision[i] = 0;
                recall[i] = 0;
                f1[i] = 0;
            }
        }
        accuaracy[0] = DoubleStream.of(TP).sum()/dataset.length;

        medidas.add(precision);
        medidas.add(recall);
        medidas.add(f1);
        medidas.add(accuaracy);

    return medidas;
}

    public static int[][] LeerDataArff(String file) throws Exception {

        ConverterUtils.DataSource source = new ConverterUtils.DataSource (file);
        Instances newData = source.getDataSet ();
        int numVariables = newData.numAttributes ();

        int[][] data = new int[newData.size()][numVariables];

        for ( int i = 0; i < newData.numInstances (); i++ ) {
            int[] aux = new int[numVariables];
            for ( int j = 0; j < newData.numAttributes (); j++ ) {
                aux[j] = (int) newData.instance (i).value (j);
            }
            data[i] = aux;
        }
        return data;
    }

    public static int[] LeerCardinalidad(String file) throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource (file);
        Instances newData = source.getDataSet ();
        int numVariables = newData.numAttributes ();
        int[] card = new int[numVariables];
        for ( int i = 0; i < card.length; i++ ) {
            card[i] = newData.attribute (i).numValues ();
        }
        return card;
    }

    public static int[] LeerVariables(String file) throws Exception {
        ConverterUtils.DataSource source = new ConverterUtils.DataSource (file);
        Instances newData = source.getDataSet ();

        int [] vars = new int[newData.numAttributes()];

        for (int i = 0; i < vars.length; i++ ) {
            vars[i] = i;
        }
        return vars;
    }
}
