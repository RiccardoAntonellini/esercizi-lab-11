package it.unibo.oop.reactivegui03;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.unibo.oop.JFrameUtil;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private static final Logger LOGGER = LoggerFactory.getLogger(AnotherConcurrentGUI.class);
    private static final long TIMELIMIT = 10_000;
    private final JLabel display = new JLabel();
    private final JButton up = new JButton("up");
    private final JButton down = new JButton("down");
    private final JButton stop = new JButton("stop");

    /**
     * this builds the AnotherConcurrenGUI.
     */
    public AnotherConcurrentGUI() {
        super();
        JFrameUtil.dimensionJFrame(this);
        final JPanel panel = new JPanel(new FlowLayout());
        panel.add(display);
        panel.add(up);
        panel.add(down);
        panel.add(stop);
        this.getContentPane().add(panel);
        this.setVisible(true);
        final Agent agent = new Agent();
        final StopAgent agent2 = new StopAgent();
        new Thread(agent2).start();
        up.addActionListener(e -> {
            agent.up = true;
            new Thread(agent).start();
        });
        down.addActionListener(e -> {
            agent.up = false;
            new Thread(agent).start();
        });
        stop.addActionListener(e -> agent.stopCounting());
    }

    private final class Agent implements Runnable {
        private volatile boolean stop;
        private volatile boolean up;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                try {
                    final var nextText = Integer.toString(this.counter);
                    SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(nextText));
                    if (up) {
                        this.counter++;
                    } else {
                        this.counter--;
                    }
                    Thread.sleep(100);
                } catch (InvocationTargetException | InterruptedException ex) {
                    LOGGER.error(ex.getMessage(), ex);
                }
            }
        }

        public void stopCounting() {
            this.stop = true;
            AnotherConcurrentGUI.this.stop.setEnabled(false);
            AnotherConcurrentGUI.this.up.setEnabled(false);
            AnotherConcurrentGUI.this.down.setEnabled(false);
        }
    }

    private final class StopAgent implements Runnable {
        private final long counter = System.currentTimeMillis();

        @Override
        public void run() {
            while (true) {
                if ((System.currentTimeMillis() - counter) >= TIMELIMIT) {
                    stopCounting();
                    break;
                }
            }
        }

        public void stopCounting() {
            for (final ActionListener stopper : AnotherConcurrentGUI.this.stop.getActionListeners()) {
                stopper.actionPerformed(new ActionEvent(AnotherConcurrentGUI.this.stop, ActionEvent.ACTION_PERFORMED, null));
            }
        }
    }
}
