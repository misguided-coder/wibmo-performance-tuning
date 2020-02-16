import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/** Network simulation GUI console */

public class Network extends Frame implements WindowListener {

    NetworkControls controls;
    NetworkCanvas canvas;
    Image image_db, image_cl, image_se;

    Object state_lock = new Object();
    boolean[] client_server_states = { false, false, false, false, false };
    boolean server_db_state = false;

    Sim sim = null;
    static int cnt_cs = 0;
    static int cnt_db = 0;

    public static final String START_STRING = "Start";
    public static final String STOP_STRING = "Stop";
    public static final String TITLE_STRING = "Network Simulation";

    public Network() {
        super(TITLE_STRING);
        setSize(300, 300);
        addWindowListener(this);
    }

    public void init(boolean fixLogin, boolean fixConnection) {
        ClassLoader cl = getClass().getClassLoader();

        image_db = Toolkit.getDefaultToolkit().getImage(
                cl.getResource("pic_db.gif"));
        image_cl = Toolkit.getDefaultToolkit().getImage(
                cl.getResource("pic_cl.gif"));
        image_se = Toolkit.getDefaultToolkit().getImage(
                cl.getResource("pic_se.gif"));

        setLayout(new BorderLayout());
        canvas = new NetworkCanvas();
        add("Center", canvas);
        add("South", controls = new NetworkControls(canvas));

        sim = new Sim(this, fixLogin, fixConnection);
        sim.init();
    }

    public void start() {
        controls.setEnabled(true);
    }

    public void stop() {
        controls.setEnabled(false);
    }

    /* Windows Interface */
    public void windowClosed(WindowEvent event) {
    }

    public void windowDeiconified(WindowEvent event) {
    }

    public void windowIconified(WindowEvent event) {
    }

    public void windowActivated(WindowEvent event) {
    }

    public void windowDeactivated(WindowEvent event) {
    }

    public void windowOpened(WindowEvent event) {
    }

    public void windowClosing(WindowEvent event) {
        System.exit(0);
    }

    /* Current state of the model */

    public void setConnectionClientServerState(int client_id, boolean is_active) {
        client_server_states[client_id] = is_active;
        try {
            Thread.sleep(10);
        } catch (Exception e) {
        }

    }

    public void setConnectionDataBaseState(boolean is_active) {
        server_db_state = is_active;
        try {
            Thread.sleep(10);
        } catch (Exception e) {
        }

    }

    public void hasStopped() {
        controls.notifyStop();
    }

    public static void main(String argv[]) {
        Network network = new Network();
        network.init(isLoginFixed(argv), isConnectionFixed(argv));
        network.start();
        network.show();
    }

    private static boolean isLoginFixed(String[] argv) {
        boolean isFlagSpecified = false;

        String flag = "-fl";
        for (int i = 0; i < argv.length; i++) {
            if (argv[i].equals(flag)) {
                isFlagSpecified = true;
                break;
            }
        }
        return isFlagSpecified;
    }

    private static boolean isConnectionFixed(String[] argv) {
        boolean isFlagSpecified = false;

        String flag = "-fc";
        for (int i = 0; i < argv.length; i++) {
            if (argv[i].equals(flag)) {
                isFlagSpecified = true;
                break;
            }
        }
        return isFlagSpecified;
    }

    /** Inner class responsible for the graphic representation of the model */
    class NetworkCanvas extends Canvas implements Runnable {
        double[] server = { 0.5, 0.5 };
        double[][] clients = { { 0.1, 0.5 }, { 0.1, 0.7 }, { 0.1, 0.9 },
                { 0.3, 0.9 }, { 0.5, 0.9 } };
        double[] database = { 0.8, 0.1 };
        double[][] client_server_connections = { { 0.1, 0.5, 0.5, 0.5 },
                { 0.1, 0.7, 0.5, 0.5 }, { 0.1, 0.9, 0.5, 0.5 },
                { 0.3, 0.9, 0.5, 0.5 }, { 0.5, 0.9, 0.5, 0.5 } };
        double[] server_db_connection = { 0.5, 0.5, 0.8, 0.1 };
        int image_size = 20; // nbr of pixels of one image
        Thread canvas_thread = null;
        boolean is_running = true;
        Graphics gr = null;
        Dimension d = new Dimension(300,300);
        
        public void paint(Graphics g) {
            //Dimension d = getSize();
            int width = d.width;
            int height = d.height;
            g.drawImage(image_db, (int) (database[0] * width),
                    (int) (database[1] * height), this);
            g.drawImage(image_se, (int) (server[0] * width),
                    (int) (server[1] * height), this);
            g.setColor(Color.red);
            if (server_db_state) {
                g.drawLine((int) (server_db_connection[0] * width),
                        (int) (server_db_connection[1] * height),
                        (int) (server_db_connection[2] * width),
                        (int) (server_db_connection[3] * height));
                g.drawLine((int) (server_db_connection[0] * width + 1),
                        (int) (server_db_connection[1] * height),
                        (int) (server_db_connection[2] * width + 1),
                        (int) (server_db_connection[3] * height));
            }
            for (int i = 0; i < 5; i++) {
                g.drawImage(image_cl, (int) (clients[i][0] * width),
                        (int) (clients[i][1] * height), this);

                if (client_server_states[i]) {
                    g
                            .drawLine(
                                    (int) (client_server_connections[i][0] * width),
                                    (int) (client_server_connections[i][1]
                                            * height - 1),
                                    (int) (client_server_connections[i][2] * width),
                                    (int) (client_server_connections[i][3] * height));
                }
            }
        }

        public void update() {
            if (gr == null) {
				// Cache the Graphics object for performance reasons.
            	gr = this.getGraphics();
            }
            //Dimension d = getSize(); // Do not cache - window may resize!
            int width   = d.width;
            int height  = d.height;
            gr.setColor(server_db_state ? Color.red : Color.white);
            gr.drawLine((int) (server_db_connection[0] * width),
                    (int) (server_db_connection[1] * height),
                    (int) (server_db_connection[2] * width),
                    (int) (server_db_connection[3] * height));
            gr.drawLine((int) (server_db_connection[0] * width + 1),
                    (int) (server_db_connection[1] * height),
                    (int) (server_db_connection[2] * width + 1),
                    (int) (server_db_connection[3] * height));
            for (int i = 0; i < 5; i++) {
                gr.setColor(client_server_states[i] ? Color.red : Color.white);
                gr.drawLine((int) (client_server_connections[i][0] * width),
                        (int) (client_server_connections[i][1] * height - 1),
                        (int) (client_server_connections[i][2] * width),
                        (int) (client_server_connections[i][3] * height));
            }
        }

        public void start() {
            if (canvas_thread == null) {
                is_running = true;
                canvas_thread = new Thread(this);
                canvas_thread.start();
            }
        }

        public void run() {
            try {
                while (is_running) {
                    update();
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        public void stop() {
            is_running = false;
            canvas_thread = null;
            repaint();
        }
    }

    /** Inner class responsible for the GUI controls */
    class NetworkControls extends Panel implements ActionListener {
        NetworkCanvas canvas;
        Button button;

        public NetworkControls(NetworkCanvas canvas) {
            this.canvas = canvas;
            button = new Button(START_STRING);
            button.addActionListener(this);
            add(button);
        }

        public void notifyStop() {
            button.setLabel(START_STRING);
            canvas.stop();
        }

        public void actionPerformed(ActionEvent ev) {
            String label = ev.getActionCommand();
            if (label.equals(START_STRING)) {
                button.setLabel(STOP_STRING);
                canvas.start();
                sim.start();
            } else {
                notifyStop();
                sim.stop();
            }
        }
    }
}
