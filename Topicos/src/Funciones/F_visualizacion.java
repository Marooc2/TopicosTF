package Funciones;

import java.sql.SQLOutput;
import java.util.List;

public class F_visualizacion {

    public static void VisualizarDistribuciones(int[][] grafo,int [] vars, int[] card, int alpha, int[][] ds, char[] vn) {
        List<int[]> listDis = F_distribuciones.GeneraDistribuciones(grafo);
        List<double[]> listDistribucionesGrafo = F_distribuciones.RealizaDistribuciones(grafo, card, alpha, ds);

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
        System.out.println("Distribuciones");
        System.out.println();
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

    }

}
