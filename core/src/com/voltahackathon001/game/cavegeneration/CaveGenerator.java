package com.voltahackathon001.game.cavegeneration;

/*
 * Volta Hackathon
 * Amelia Stead
 * ---------------
 * Procedurally generated a gave using cellular automata.
 */

import java.util.Random;

public class CaveGenerator {
    private Cell[][] cave;
    private Cell[][] nextCave;
    private int width;
    private int height;
    private Random random;

    //private variables defining cave generation
    private int numSteps = 5; //number of times to step through generation algorithm
    private float probStartAlive = 0.30f; //probability of a cell starting alive
    private int numNeighborsForGrowth = 4; //number of neighbors needed for cell to turn on
    private int underPopLimit = 2; //<= this number of neighbors cells will start dying
    private int overPopLimit = 10; //>= this number of neighbors cells will start dying

    public CaveGenerator(int width, int height) {
        this.width = width;
        this.height = height;
        cave = new Cell[width][height];
        nextCave = new Cell[width][height];
        random = new Random();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                nextCave[i][j] = new Cell();
                cave[i][j] = new Cell();
            }
        }
    }

    //getters and setters
    //width and height
    public int getWidth() {return width;}
    public void setWidth(int width) {this.width = width;}
    public int getHeight() {return height;}
    public void setHeight(int height) {this.height = height;}
    //cave generation variables
    public int getNumSteps() {return numSteps;}
    public void setNumSteps(int numSteps) {this.numSteps = numSteps;}
    public float getProbStartAlive() {return probStartAlive;}
    public void setProbStartAlive(float startAlive) {this.probStartAlive = probStartAlive;}
    public int getNumNeighborsForGrowth() {return numNeighborsForGrowth;}
    public void setNumNeighborsForGrowth(int num) {numNeighborsForGrowth = num;}
    public int getUnderPopLimit() {return underPopLimit;}
    public void setUnderPopLimit(int underPopLimit) {this.underPopLimit = underPopLimit;}
    public int getOverPopLimit() {return overPopLimit;}
    public void getOverPopLimit(int overPopLimit) {this.overPopLimit = overPopLimit;}

    /**
     * Generates a 2D Cell array.
     * @return A completed Cell[][] representing a cave structure.
     */
    public Cell[][] getCave() {
        populate(cave);
        blank(cave);
        for (int i = 0; i < numSteps; i++) {
            step(cave);
        }
        fillSides(cave);
        fillBottom(cave);
        clearHorizLayer(cave, height-2);
        return cave;
    }

    // gets the first cave and returns a 2D int array
    public int[][] getCaveInt(){
        int[][] returnMe = new int[width][height];
        getCave();
        for(int x = 0 ; x < width ; x++){
            for(int y = 0 ; y < height ; y++){
                returnMe[x][y] = cave[x][y].getState();
            }
        }
        return returnMe;
    }
    // converts getNext cave into a 2D int array and returns it
    public int[][] getNextInt(){
        int[][] returnMe = new int[width][height];
        getNext();
        for(int x = 0 ; x < width ; x++){
            for(int y = 0 ; y < width ; y++){
                returnMe[x][y] = cave[x][y].getState();
            }
        }
        return returnMe;
    }
    /**
     * Generates and returns the next 2D Cell array. Updates the current array to the new one.
     * @return A 2D Cell array representing the cave to load on top of the current one.
     */
    public Cell[][] getNext() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                nextCave[i][j].setState(0);
            }
        }
        populate(nextCave);
        fillNextBottom();
        blank(nextCave);
        for (int i = 0; i < numSteps; i++) {
            step(nextCave);
        }
        fillSides(nextCave);
        cave = nextCave;
        return cave;
    }

    /**
     * Populates the 2D Cell array randomly with blocks,
     * i.e. randomly turns a section of the cells on.
     * Clears current array.
     */
    private void populate(Cell[][] cellArray) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (random.nextDouble() < probStartAlive) {
                    cellArray[i][j].setState(1);
                    cellArray[i][j].setNextState(1);
                } else {
                    cellArray[i][j].setState(0);
                    cellArray[i][j].setNextState(0);
                }
            }
        }
    }

    /**
     * Fills the bottom row of nextCave with the top row of the current cave.
     */
    private void fillNextBottom() {
        for (int i = 0; i < width; i++) {
            nextCave[i][height-1] = cave[i][0];
        }
    }

    /**
     * Creates non-wall sections in the cave to make a path
     * to the top more likely.
     */
    private void blank(Cell[][] cellArray) {
        //variety of options for blanks to diversify levels
        double r = random.nextDouble();
        if (r < 0.2) {
            //vertical blanks
            for (int j = 0; j < height/3; j++) {
                for (int k = -2; k < 2; k++) {
                    cellArray[width/4+k][j].setState(0);
                    cellArray[width/4+k][j].setNextState(0);
                }
            }
            for (int j = height/3; j < height*2/3; j++) {
                for (int k = -2; k < 2; k++) {
                    cellArray[width/2+k][j].setState(0);
                    cellArray[width/2+k][j].setNextState(0);
                }
            }
            for (int j = height*2/3; j < height; j++) {
                for (int k = -2; k < 2; k++) {
                    cellArray[width*3/4+k][j].setState(0);
                    cellArray[width*3/4+k][j].setNextState(0);
                }
            }
            //horizontal blanks
            for (int i = 0; i < width; i++) {
                for (int k = -2; k < 1; k++) {
                    cellArray[i][height*2/7+k].setState(0);
                    cellArray[i][height*2/7+k].setNextState(0);
                }
            }
            for (int i = 0; i < width; i++) {
                for (int k = -2; k < 1; k++) {
                    cellArray[i][height*6/7+k].setState(0);
                    cellArray[i][height*6/7+k].setNextState(0);
                }
            }
        } else if (r < 0.4) {
            double heightStep = (double)height / width;
            double j = 0;
            for (int i = 0; i < width; i++) {
                for (int k = -2; k < 2; k++) {
                    int y = (int)j+k;
                    if (y < 0 || y > height-2) {
                        continue;
                    }
                    cellArray[i][y].setState(0);
                    cellArray[i][y].setNextState(0);
                }
                j += heightStep;
            }
        } else if (r < 0.6) {
            double heightStep = (double)height / width;
            double j = 0;
            for (int i = width-1; i > 0; i--) {
                for (int k = -2; k < 2; k++) {
                    int y = (int)j+k;
                    if (y < 0 || y > height-2) {
                        continue;
                    }
                    cellArray[i][y].setState(0);
                    cellArray[i][y].setNextState(0);
                }
                j += heightStep;
            }
        } else if (r < 0.8) {
            boolean iMoveRight = true;
            int iStart = width/3;
            int iFinish = width/2;
            int iCurr = iStart;
            int iWidth = width/6;
            for (int j = 0; j < height-1; j++) {
                for (int i = iCurr; i < iCurr+iWidth; i++) {
                    cellArray[i][j].setState(0);
                    cellArray[i][j].setNextState(0);
                }
                if (iMoveRight && iCurr < iFinish) {
                    iCurr++;
                } else if (iMoveRight) {
                    iMoveRight = false;
                    iCurr--;
                } else if (iCurr > iStart) {
                    iCurr--;
                } else {
                    iMoveRight = true;
                    iCurr++;
                }
            }
        } else {
            double heightStep = (double)height / (2*width);
            double j = 0;

            for (int i = 0; i < width; i++) {
                for (int k = -2; k < 2; k++) {
                    //horizontal blank
                    cellArray[i][height/2+k].setState(0);
                    cellArray[i][height/2+k].setNextState(0);

                    //upper diagonal
                    int y = (int)j+k;
                    if (y < 0) {
                        continue;
                    }
                    cellArray[i][y].setState(0);
                    cellArray[i][y].setNextState(0);

                    //lower diagonal
                    y += (height/2);
                    if (y > height-2) {
                        continue;
                    }
                    cellArray[i][y].setState(0);
                    cellArray[i][y].setNextState(0);
                }
                j += heightStep;
            }
        }
    }

    /**
     * Uses rules defining cell life to update the cave once.
     */
    private void step(Cell[][] cellArray) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height-1; j++) { //don't consider floor
                int numNeighbors = countNeighbors(cellArray, i, j);
                if (numNeighbors <= underPopLimit || numNeighbors >= overPopLimit) {
                    cellArray[i][j].setNextState(0);
                } else if (numNeighbors >= numNeighborsForGrowth) {
                    cellArray[i][j].setNextState(1);
                }
            }
        }
        update(cellArray);
    }

    /**
     * Calls update on each cell, moving cave to the next state.
     */
    private void update(Cell[][] cellArray) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                cellArray[i][j].update();
            }
        }
    }

    /**
     * Fills both vertical edges of the array with walls.
     * @param cellArray The 2D Cell array to be updated
     */
    private void fillSides(Cell[][] cellArray) {
        for (int j = 0; j < height; j++) {
            cellArray[0][j].setState(1);
            cellArray[0][j].setNextState(1);
            cellArray[width-1][j].setState(1);
            cellArray[width-1][j].setNextState(1);
        }
    }

    /**
     * Fills the bottom horizontal edge of the array with walls.
     * @param cellArray The 2D Cell array to be updated
     */
    private void fillBottom(Cell[][] cellArray) {
        for (int i = 0; i < width; i++) {
            cellArray[i][height-1].setState(1);
            cellArray[i][height-1].setNextState(1);
        }
    }

    /**
     * Clears a layer of walls (excluding right and left edges)
     * @param cellArray Cell[][] to clear from
     * @param j vertical index to clear
     */
    private void clearHorizLayer(Cell[][] cellArray, int j) {
        for (int i = 1; i < width-1; i++) {
            cellArray[i][j].setState(0);
            cellArray[i][j].setNextState(0);
        }
    }

    /**
     * Calculates the number of on cells (0-8) surrounding cell in current cave at location (i, j)
     * @return The number of cells (0-8) around cell at (i, j) in current 2D Cell array cave.
     */
    private int countNeighbors(Cell[][] cellArray, int i, int j) {
        int result = 0;
        int xMin = Math.max(i-1, 0);
        int xMax = Math.min(i+1, width-1);
        int yMin = Math.max(j-1, 0);
        int yMax = Math.min(j+1, height-2); //don't count floor as neighbor
        for (int x = xMin; x <= xMax; x++) {
            for (int y = yMin; y <= yMax; y++) {
                if (cellArray[x][y].getState() == 1) {
                    result++;
                }
            }
        }

        return result;
    }
}