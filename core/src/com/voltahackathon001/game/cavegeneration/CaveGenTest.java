package com.voltahackathon001.game.cavegeneration;

/**
 * Created by Amelia on 2016-10-06.
 */
public class CaveGenTest {
    public static void main(String[] args) {
        CaveGenerator cg = new CaveGenerator(40,100);

        int[][] c1 = cg.getCaveInt();
        int[][] c2 = cg.getNextInt();
        int[][] c3 = cg.getNextInt();

        printArr(c1);
        printArr(c2);
        printArr(c3);
    }

    private static void printArr(int[][] a) {
        System.out.println("\n\n\nCAVE\n\n");
        for (int j = 0; j < a[0].length; j++) {
            for (int i = 0; i < a.length; i++) {
                System.out.print(a[i][j] + "   ");
            }
            System.out.println();
        }
    }
}
