package com.voltahackathon001.game.cavegeneration;

/**
 * Created by Amelia on 2016-10-05.
 */
public class GenVerification {

    /**
     * Checks if the given array has a clear path from the bottom to top.
     * @param caveArray The 2D integer array to check
     * @return True if array has clear path to top.
     */
    public static boolean validCave(int[][] caveArray) {
        int width = caveArray.length;
        int height = caveArray[0].length;

        //don't check side walls
        for (int i = 1; i < width-1; i++) {
            if (validate(caveArray, i, 0))
                return true;
        }

        return false;
    }

    private static boolean validate(int[][] caveArray, int i, int y) {
        if (y == caveArray[0].length-1) {
            return true;
        }

        int curr = caveArray[i][y];
        if (curr == 1) {
            return false;
        }

        caveArray[i][y] = 1;

        //try to move a level down, or right/left in hopes of moving down
        return validate(caveArray, i, y+1) || validate(caveArray, i-1, y) || validate(caveArray, i+1, y);
    }
}
