package it.unibo.oop.workers02;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a standard implementation of the calculation.
 *
 */
public final class MultiThreadedMatrixSumClassic implements SumMatrix {
    private final int nthread;

    /**
     * @param nthread
     *            no. of thread performing the sum.
     */
    public MultiThreadedMatrixSumClassic(final int nthread) {
        this.nthread = nthread;
    }

    @Override
    public double sum(final double[][] matrix) {
        final int matrixsize = matrix.length * matrix[0].length;
        final int size = matrixsize % nthread + matrixsize / nthread;

        final List<Worker> workers = new ArrayList<>(nthread);
        int progressiveI = 0;
        int progressiveJ = 0;

        int n = 0;
        while (n < nthread) {
            boolean flag = false;
            int counter = 0;
            final List<Double> list = new ArrayList<>(size);

            final int startI = progressiveI;
            final int startJ = progressiveJ;

            for (int x = startI; x < matrix.length; x++) {
                final int col;
                if (x == startI) {
                    col = startJ;
                } else {
                    col = 0;
                }

                for (int z = col; z < matrix[x].length; z++) {
                    if (counter < size) {
                        list.add(matrix[x][z]);
                        counter++;

                        progressiveI = x;
                        progressiveJ = z + 1;

                        if (progressiveJ >= matrix[x].length) {
                            progressiveI++;
                            progressiveJ = 0;
                        }
                    } else {
                        flag = true;
                        break;
                    }
                }
                if (flag) {
                    break;
                }
            }
            workers.add(new Worker(list));
            n++;
        }

        for (final Worker w: workers) {
            w.start();
        }

        double sum = 0;
        for (final Worker w: workers) {
            try {
                w.join();
                sum += w.getResult();
            } catch (final InterruptedException e) {
                throw new IllegalStateException(e);
            }
        }

        return sum;
    }

    private static class Worker extends Thread {
        private final List<Double> list;
        private double res;

        Worker(final List<Double> list) {
            super();
            this.list = list;
        }

        @Override
        @SuppressWarnings("PMD.SystemPrintln")
        public synchronized void run() {
            System.out.println("Working on the entire partial-list everytime");
            for (final Double d : this.list) {
                this.res += d;
            }
        }

        /**
         * Returns the result of summing up the doubles within the list.
         *
         * @return the sum of every element in the array
         */
        public synchronized double getResult() {
            return this.res;
        }

    }
}
