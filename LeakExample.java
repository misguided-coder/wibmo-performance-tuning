import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class LeakExample extends JFrame {

    JPanel panel = new JPanel();
    JButton buttons[] = new JButton[100];
    int removeButtonIndex[] = new int[100];
    int numRemoveButtons = 0;
    int numButtons = 0;
    static boolean fixed = false;

    public LeakExample() {

        super("Leak Example");

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

    public void addButtonToPanel() {
        if (numButtons < buttons.length) {

            JButton btn = new JButton(String.valueOf(numButtons));
            buttons[numButtons++] = btn;
            removeButtonIndex[numRemoveButtons++] = numButtons - 1;
            panel.add(btn);
            panel.validate();
            panel.repaint();
        }
    }

    public void removeButtonFromPanel() {
        if (numRemoveButtons > 0) {
            panel.remove(buttons[removeButtonIndex[--numRemoveButtons]]);
            buttons[removeButtonIndex[numRemoveButtons]] = null;
           
            panel.validate();
            panel.repaint();
        }
    }

    static public void main(String args[]) {

        if (args.length > 0) {
            if (args[0].equals("fix")) {
                fixed = false;
            }
        }

        JFrame frame = new LeakExample();
        frame.setSize(300, 200);
        frame.setVisible(true);
    }

} // End of class

