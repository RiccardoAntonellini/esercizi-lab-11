package it.unibo.oop.reactivegui02;

/**
 * Exercise on a reactive GUI.
 */
public final class Test {

    private Test() {
    }

    /**
     * Main method to start the GUI.
     *
     * @param args
     *            possible args to pass (not used)
     */
    public static void main(final String... args) {
        new ConcurrentGUI();
    }
}
