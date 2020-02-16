import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;

public class LeakExample2 extends JFrame {

    JPanel panel = new JPanel();
    ListeningButton buttons[] = new ListeningButton[100];
    int numButtons = 0;
    protected EventListenerList listenerList = new EventListenerList();
    private static boolean fixed = false;

    public LeakExample2() {
        super("Leak Example 2");

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        panel.setLayout(new FlowLayout());
        getContentPane().add("Center", panel);

        JPanel addRemovePanel = new JPanel();

        JButton addBtn = new JButton("Add");
        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addButtonToPanel();
            }
        });
        addRemovePanel.add(addBtn);
        JButton removeBtn = new JButton("Remove");
        removeBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                removeButtonFromPanel();
            }
        });
        addRemovePanel.add(removeBtn);
        getContentPane().add("North", addRemovePanel);

    }

    class ListeningButton extends JButton implements ChangeListener {
        public ListeningButton(String text) {
            super(text);
        }

        public void stateChanged(ChangeEvent e) {
            System.out.println("stateChanged received by " + getText());

        }

    }

    public void addButtonToPanel() {
        if (numButtons < buttons.length) {

            ListeningButton btn = new ListeningButton(String
                    .valueOf(numButtons));
            buttons[numButtons++] = btn;
            fireStateChanged();

            addChangeListener(btn);
            panel.add(btn);
            panel.validate();
            panel.repaint();
        }

    }

    public void removeButtonFromPanel() {
        if (numButtons > 0) {
            panel.remove(buttons[--numButtons]);

            //if (fixed) {
                removeChangeListener(buttons[numButtons]);
            //}

            fireStateChanged();

            buttons[numButtons] = null;
            panel.validate();
            panel.repaint();
            System.gc();
        }

    }

    public void addChangeListener(ChangeListener l) {
        listenerList.add(ChangeListener.class, l);
    }

    public void removeChangeListener(ChangeListener l) {
        listenerList.remove(ChangeListener.class, l);
    }

    protected void fireStateChanged() {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ChangeListener.class) {
                // Lazily create the event:
                ChangeEvent changeEvent = new ChangeEvent(this);
                ((ChangeListener) listeners[i + 1]).stateChanged(changeEvent);
            }
        }
    }

    static public void main(String args[]) {
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("fix")) {
                fixed = true;
                break;
            }
        }
        JFrame frame = new LeakExample2();
        frame.setSize(300, 200);
        frame.setVisible(true);
    }

} // End of class

