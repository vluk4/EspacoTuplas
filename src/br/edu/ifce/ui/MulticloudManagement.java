package br.edu.ifce.ui;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MulticloudManagement extends JFrame {
    private JPanel mainPanel;
    private JList<String> cloudList;
    private JButton btAddCloud;
    private ArrayList<String> clouds = new ArrayList<>();


    public MulticloudManagement() {
        btAddCloud.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                String cloudName = JOptionPane.showInputDialog("Digite o nome da Nuvem");
                clouds.add(cloudName);
            }
        });

        new UpdateCloudList().start();
    }

    public static void main(String[] args) {
        MulticloudManagement mcm = new MulticloudManagement();
        mcm.setContentPane(mcm.mainPanel);
        mcm.setTitle("Multicloud Management");
        mcm.setSize(1080, 720);

        mcm.setVisible(true);
    }


    public class UpdateCloudList extends Thread {
        public void run() {
            while (true) {
                ArrayList<String> newList = new ArrayList<>();
                newList.addAll(clouds);
                try {
                    sleep(200);
                    if (!newList.equals(clouds)) {
                        DefaultListModel<String> model = new DefaultListModel<>();
                        cloudList.setModel(model);
                        for (String name : clouds) {
                            model.addElement(name);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void logError(Exception e) {
        Logger.getLogger(MulticloudManagement.class.getName()).log(Level.SEVERE, null, e);
    }
}
