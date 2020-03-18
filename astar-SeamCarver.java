package seamcarving;

import edu.princeton.cs.algs4.Picture;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

import astar.AStarGraph;
import astar.AStarSolver;
import astar.WeightedEdge;
import java.awt.Point;



public class AStarSeamCarver implements SeamCarver {
    private Picture picture;
    private double[][] energyMatrix;

    public AStarSeamCarver(Picture picture) {
        if (picture == null) {
            throw new NullPointerException("Picture cannot be null.");
        }
        this.picture = new Picture(picture);
        this.energyMatrix = toEnergyMatrix();
    }

    public Picture picture() {
        return new Picture(picture);
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public int width() {
        return picture.width();
    }

    public int height() {
        return picture.height();
    }

    public Color get(int x, int y) {
        return picture.get(x, y);
    }

    public double energy(int x, int y) {
        if (!inBounds(x, y)) {
            throw new IndexOutOfBoundsException();
        }
        int yBefore = y+1;
        int yAfter = y-1;
        if (y == height()-1) {
            yBefore = 0;
        }
        if (y == 0) {
            yAfter = height()-1;
        }

        int rY = get(x, yBefore).getRed() - get(x, yAfter).getRed();
        int gY = get(x, yBefore).getGreen() - get(x, yAfter).getGreen();
        int bY = get(x, yBefore).getBlue() - get(x, yAfter).getBlue();
        int ySqr = rY*rY + gY*gY +bY*bY;

        int xBefore = x+1;
        int xAfter = x-1;
        if (x == width()-1) {
            xBefore = 0;
        }
        if (x == 0) {
            xAfter = width()-1;
        }
        int rX = get(xBefore, y).getRed() - get(xAfter, y).getRed();
        int gX = get(xBefore, y).getGreen() - get(xAfter, y).getGreen();
        int bX = get(xBefore, y).getBlue() - get(xAfter, y).getBlue();
        int xSqr = rX*rX + gX*gX + bX*bX;

        double energy = Math.sqrt(ySqr+xSqr);
        return energy;
    }

    private double[][] toEnergyMatrix(){
        double[][] matrix = new double[width()][height()];
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                matrix[col][row] = energy(col, row);
            }
        }
        return matrix;
    }

    public int[] findHorizontalSeam() {
        AStarSeamCarver newPic = new AStarSeamCarver(flip());
        int[] result = newPic.findVerticalSeam();
        return result;
    }

    public int[] findVerticalSeam() {
        int[] result = new int[height()];
        Point start = new Point(-1, -1);
        Point end = new Point(width(), height());
        EnergyGraph graph = new EnergyGraph();
        AStarSolver<Point> path = new AStarSolver(graph, start,
                end, 30);
        for (int i = 0; i < path.solution().size()-2; i++) {
            result[i] = path.solution().get(i+1).x;
        }
        return result;
    }


    private Picture flip() {
        Picture flip = new Picture(height(), width());
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                flip.set(row, col, picture.get(col, row));
            }
        }
        return flip;
    }



    private class EnergyGraph implements AStarGraph<Point> {

        @Override
        public List<WeightedEdge<Point>> neighbors(Point v) {
            List<Point> neighbors = new ArrayList<>();
            List<WeightedEdge<Point>> neighborEdges = new ArrayList<>();
            if (v.y == height()-1) {
                Point n = new Point(width(), height());
                neighborEdges.add(new WeightedEdge<>(v, n,
                        0));
            } else {
                if (v.y == -1) {
                    for (int i = 0; i < width(); i++) {
                        neighbors.add(new Point(i, 0));
                    }
                } else {
                    if (v.x > 0) {
                        neighbors.add(new Point(v.x - 1, v.y + 1));
                    }
                    neighbors.add(new Point(v.x, v.y + 1));
                    if (v.x < width()-1) {
                        neighbors.add(new Point(v.x + 1, v.y + 1));
                    }
                }
                for (Point n : neighbors) {
                    neighborEdges.add(new WeightedEdge<>(v, n,
                            energyMatrix[n.x][n.y]));
                }
            }
            return neighborEdges;
        }

        @Override
        public double estimatedDistanceToGoal(Point s, Point goal) {
            return 0;
        }


    }
}
