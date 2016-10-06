package com.voltahackathon001.game.cavegeneration;

/**
 * Created by Amelia on 2016-10-05.
 */
public class VerificationTester {
    public static void main(String[] args) {
//        int[][] caveArray =
//                       {{1,1,1,1,1,1},
//                        {1,0,0,1,0,1},
//                        {1,0,1,0,0,1},
//                        {0,0,0,0,1,1},
//                        {1,0,0,1,1,1},
//                        {1,0,0,0,1,1},
//                        {1,1,1,1,1,1}};
//
        CaveGenerator cg = new CaveGenerator(40, 100);
        cg.getCave();
        int[][] caveArray = cg.getCaveInt();

        for (int j = 0; j < caveArray[0].length; j++) {
            for (int i = 0; i < caveArray.length; i++) {
                if (caveArray[i][j] == 0) {
                    System.out.print("    ");
                } else {
                    System.out.print("X   ");
                }
            }
            System.out.println();
        }
        System.out.println(GenVerification.validCave(caveArray));
    }
}
