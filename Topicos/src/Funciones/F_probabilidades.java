package Funciones;

import java.util.Arrays;

public class F_probabilidades {

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

}
