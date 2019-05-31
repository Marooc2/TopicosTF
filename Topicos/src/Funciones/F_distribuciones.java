package Funciones;

import java.util.ArrayList;
import java.util.List;

public class F_distribuciones {

    public static List<int[]> GeneraDistribuciones(int[][] grafo) {

        List<int[]> listPosicionesDis = new ArrayList<>();
        int[] aux;
        for (int i = 0; i < grafo.length; i++) {
            List<Integer> listAux = new ArrayList<Integer>();

            listAux.add(i);
            for (int j = 0; j < grafo[i].length; j++) {
                if (grafo[j][i] == 1)
                    listAux.add(j);
            }
            aux = F_herramientas.toInArray(listAux);
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

                    double[] pc = F_probabilidades.ProbabilidadCondicionadaDiricht(listDis.get(i), auxval, card, alpha, ds);
                    System.arraycopy(pc,0,auxarr,pos,pc.length);
                    pos += pc.length;
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
                    pc = F_probabilidades.ProbabilidadConjuntaDiricht(listDis.get(i), val, card, alpha, ds);
                    auxarr[k] = pc;
                }
                listDistribuciones.add(auxarr);
            }
        }
        return listDistribuciones;
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
            System.out.print(k + " ");
            for (int j = 0; j < var.length; j++) {
                ivar = var[j];
                val[j] = (int) (Math.floor(k / cardAnterior) % card[ivar]);
                cardAnterior *= card[ivar];
                System.out.print(val[j]);
            }
            double pc = F_probabilidades.ProbabilidadConjuntaDiricht(var, val, card, alpha, ds);
            suma = suma + pc;
            System.out.println(" = " + pc);
            Distribuciones.add(pc);
        }

        return Distribuciones;
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

                    int indice = F_herramientas.GetIndex(aux,auxcard);
                    a = Listprobdist.get(j)[indice];
                }
                else {
                    a = Listprobdist.get(j)[val[Listdist.get(j)[0]]];
                }
                prob *= a;
            }

            ConjuntaG.add(prob);
        }

        return ConjuntaG;
    }

}
