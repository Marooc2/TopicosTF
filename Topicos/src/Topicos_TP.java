
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
        int[][] g = {{0, 0, 1},
                     {0, 0, 0},
                     {0, 0, 0}};
        //Ingresar las cardinalidades
        int[] card = {2,3,2};
        // Ingresar Alpha
        int alpha = 1;
        //Lectura del dataset // INGRESAR NOMBRE DE ARCHIVO
        int[][] dataset = ReadDs("data4.txt");
        int columnas = ColumnasDs(dataset);
        //Transformar la vista de las distribuciones de numeros a letras
        char[] varnames = new char[columnas];
        varnames = GeneraVariables(varnames);
        //Realiza las distribuciones del grafo e imprime
        VisualizarDistribuciones(g, card, alpha, dataset, varnames);

        //INGRESAR VARIABLES PARA HALLAR LA DISTRIBUCION CONJUNTA DEL DATASET
        int[] variablesConjunta = {0,1,2};
        int[] vars = {0,1,2};
        int[] vals = {1,1,1};
        System.out.println("Distribucion Conjunta: ");
        List<Double> Listadistribucion = DistribucionPConjunta(variablesConjunta, card, alpha, dataset);
        double[] distribucion = new double[Listadistribucion.size()];
        for (int i = 0; i<Listadistribucion.size();i++)
            distribucion[i] = Listadistribucion.get(i);
        GetProbabilidad(vars,vals,card,distribucion);
        //ConjuntaGrafo(vars,vals,card,alpha,g,dataset);
        int[] valsEvidencia = {1,2};
        int inferencia = Inferencia(vars,vals,valsEvidencia,card,alpha,g,dataset);
        System.out.println(inferencia);
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //CLASIFICAR
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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

    public static List<Double> ConjuntaGrafo(int[] vars, int[] vals, int[] card, int alpha, int[][]grafo, int[][]ds) {
        System.out.println();
        System.out.println("Distribucion Conjunta del grafo:");
        List<Double> ConjuntaG = new ArrayList<>();
        List<int[]> Listdist = GeneraDistribuciones(grafo);
        List<double[]> Listprobdist = RealizaDistribuciones(grafo, card, alpha, ds);
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
                        int indice = GetIndex(aux,card);
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

    public static int Inferencia (int[] vars,int[]vals, int [] valsE, int[] card, int alpha, int[][]g,int[][] dataset){

        List<Double> ConjuntaG = ConjuntaGrafo(vars,vals,card,alpha,g,dataset);

        int[] varevidencia = Arrays.copyOfRange(vars,0,vars.length-1);
        int varconsulta = vars[vars.length-1];
        int []auxval = new int[vars.length];
        System.arraycopy(valsE,0,auxval,0,varevidencia.length);
        int indice;
        double[] prob = new double[card[varconsulta]];

        for (int i=0; i<card[varconsulta]; i++){
            auxval[card[varconsulta]] = i;
            indice = GetIndex(auxval,card);

            for (int j = 0; j < ConjuntaG.size(); j++){
                if (j == indice){
                    prob[i] = ConjuntaG.get(j);
                }
            }
        }
        double max = Arrays.stream(prob).max().getAsDouble();
        int pos = 0;

        for (int k = 0; k < prob.length; k++){
            if (max == prob[k])
                pos = k;
        }
        return pos;
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

    public static int[] toInArray(List<Integer>list){
        int [] ret = new int [list.size()];
        for (int i = 0; i <ret.length;i++){
            ret[i] = list.get(i);
        }
        return ret;
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

    public static double[] ProbabilidadCondicionadaDiricht(int[] var, int[][] vals, int[] card, int alpha, int[][] ds) {
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

        return Distribucion;
    }

    public static List<Double> DistribucionPConjunta(int[] var, int[] card, int alpha, int[][] ds) {
        int[] val = new int[var.length];
        int ivar;
        int cardAnterior;
        int tamfactor = 1;
        double suma = 0.0;
        List<Double> Distribuciones = new ArrayList<Double>();

        //  Halla el tamaño del factor
        for (int p = 0; p < var.length; p++) {
            tamfactor *= card[p];
        }

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

        return Distribuciones;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //DISTRIBUCIONES
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public static List<int[]> GeneraDistribuciones(int[][] grafo) {

        List<int[]> listPosicionesDis = new ArrayList<int[]>();

        for (int i = 0; i < grafo.length; i++) {
            List<Integer> listAux = new ArrayList<Integer>();
            int [] aux = new int [grafo.length];
            listAux.add(i);
            for (int j = 0; j < grafo[i].length; j++) {
                if (grafo[j][i] == 1)
                    listAux.add(j);
            }
            aux = toInArray(listAux);
            listPosicionesDis.add(aux);
        }
        return listPosicionesDis;
    }

    public static List<double[]> RealizaDistribuciones(int[][] grafo, int[] card, int alpha, int[][] ds) {

        List<int[]> listDis = GeneraDistribuciones(grafo);
        List<double[]> listDistribuciones = new ArrayList<>();

        for (int i = 0; i < listDis.size(); i++) {

            //Si el arreglo de distribuciones en la posicion i es mayor que 1
            if (listDis.get(i).length > 1) {

                int[] val = new int[listDis.get(i).length];
                int ivar;
                int cardAnterior;
                int tamfactor = 1;
                int[][] auxval = new int[card[listDis.get(i)[0]]][listDis.get(i).length];

                for (int p = 0; p < listDis.get(i).length; p++) {
                    tamfactor *= card[listDis.get(i)[p]];
                }

                double[] auxarr = new double[tamfactor];
                int pos = 0;
                for (int k = 0; k < tamfactor; k += card[listDis.get(i)[0]]) {
                    for (int x = 0; x < card[listDis.get(i)[0]]; x++) {
                        cardAnterior = 1;
                        for (int j = 0; j < listDis.get(i).length; j++) {
                            ivar = listDis.get(i)[j];
                            val[j] = (int) (Math.floor((k + x) / cardAnterior) % card[ivar]);
                            cardAnterior *= card[ivar];
                        }
                        auxval[x] = val.clone();
                    }

                    double[] pc = ProbabilidadCondicionadaDiricht(listDis.get(i), auxval, card, alpha, ds);
                    System.arraycopy(pc,0,auxarr,pos,pc.length);
                    pos = pc.length;
                }
                listDistribuciones.add(auxarr);
            }
            //De lo contrario hace marginal de los Padres
            else {
                int[] val = new int[listDis.get(i).length];
                int ivar;
                int cardAnterior;
                int tamfactor = 1;

                //  Halla el tamaño del factor
                for (int p = 0; p < listDis.get(i).length; p++) {
                    tamfactor *= card[listDis.get(i)[p]];
                }
                double[] auxarr = new double[tamfactor];
                for (int k = 0; k < tamfactor; k++) {
                    cardAnterior = 1;
                    for (int j = 0; j < listDis.get(i).length; j++) {
                        ivar = listDis.get(i)[j];
                        val[j] = (int) (Math.floor(k / cardAnterior) % card[ivar]);
                        cardAnterior *= card[ivar];
                    }

                    double pc;
                    pc = ProbabilidadConjuntaDiricht(listDis.get(i), val, card, alpha, ds);
                    auxarr[k] = pc;
                }
                listDistribuciones.add(auxarr);
            }
        }
        return listDistribuciones;
    }

    public static void VisualizarDistribuciones(int[][] grafo, int[] card, int alpha, int[][] ds, char[] vn) {
        List<int[]> listDis = GeneraDistribuciones(grafo);
        List<double[]> listDistribucionesGrafo = RealizaDistribuciones(grafo, card, alpha, ds);
        int aux;
        System.out.println("Distribuciones de la matriz de Adyacencia:");
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
                if (j<listDis.get(i).length - 1)
                    System.out.print("|");
            }
            System.out.println();
        }
        System.out.println();

        for (int i = 0; i < listDis.size(); i++){
            if(listDis.get(i).length <= 1){
                for (int j = 0; j < listDistribucionesGrafo.get(i).length; j++){
                    System.out.println("Valor de la variable: " + j);
                    System.out.println(listDistribucionesGrafo.get(i)[j]);
                }
            }
            else {
                for (int j=0;j<listDistribucionesGrafo.get(i).length;j++){
                    System.out.println("Valor de las variables uwu");
                    System.out.println(listDistribucionesGrafo.get(i)[j]);
                }
            }
            System.out.println();
        }
    }
}