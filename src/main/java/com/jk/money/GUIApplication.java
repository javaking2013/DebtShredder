package com.jk.money;

import javax.swing.*;
import java.awt.*;

public class GUIApplication {

    public static void main(String args[]){
        showit();
        btnAction();
    }

    public static JFrame f;
    public static JScrollPane certsListPane, certDetailsPane;
    public static JList<String> list;
    public static JTextArea certDetailsArea;
    public static JMenuBar menuBar;
    public static JMenu file, tools, help;
    public static JTextField fileField, pwField;
    public static DefaultListModel<String> listModel;

    private static void showit(){
        f = new JFrame();
        JPanel upperPanel = new JPanel();

        menuBar = new JMenuBar();

        file = new JMenu("File");
        tools = new JMenu("Tools");
        help = new JMenu("Help");

        menuBar.add(file);
        menuBar.add(tools);
        menuBar.add(help);

        tools.add(getMenuItem("Get Remote Certificate"));

        file.add(getMenuItem("Populate Default Keystore"));
        file.add(getMenuItem("Choose File"));
        file.addSeparator();
        file.add(getMenuItem("Exit"));

        //tools.add(Items.getMenuItem("List Cert"));

        help.add(getMenuItem("About"));

        fileField = new JTextField(40);
        fileField.setText("bla");
        pwField = new JTextField(10);
        pwField.setText("bla");

        listModel = new DefaultListModel<>();
        list = new JList<>(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        certsListPane = new JScrollPane(list);
        certsListPane.setPreferredSize(new Dimension(250,80));

        certDetailsArea = new JTextArea(20,10);
        certDetailsArea.setEditable(false);
        certDetailsArea.setWrapStyleWord(true);
        certDetailsArea.setLineWrap(true);
        certDetailsPane = new JScrollPane(certDetailsArea);
        certDetailsPane.setPreferredSize(new Dimension(250,80));
        certDetailsPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        upperPanel.add(new JLabel("Input: "));
        upperPanel.add(fileField);
        upperPanel.add(getButton("Choose File"));
        upperPanel.add(new JLabel("Password:"));
        upperPanel.add(pwField);

        Font font = new Font("Courier New", Font.PLAIN, 12);
        certDetailsArea.setFont(font);

        f.add(certsListPane,BorderLayout.LINE_START);
        f.add(certDetailsPane,BorderLayout.CENTER);
        f.add(upperPanel,BorderLayout.PAGE_START);
        f.setJMenuBar(menuBar);
        f.setTitle("Money");
        f.setResizable(true);
        f.setSize(900,500);
        f.setVisible(true);
        f.setLocationRelativeTo(null);
        f.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private static void btnAction(){
        list.addListSelectionListener((ListSelectionEvent) -> {

        });
    }

    private static JMenuItem getMenuItem(final String value){
        JMenuItem item = new JMenuItem(value);
        item.addActionListener(e -> doit(value));
        return item;
    }

    private static JButton getButton(final String value){
        JButton but = new JButton(value);
        but.addActionListener(e -> doit(value));
        return but;
    }

    static void doit(String value) {

        switch(value){
            case "Exit": System.exit(0);
                break;
            default: JOptionPane.showMessageDialog(null, "This feature is not implemented yet.");
        }
    }

/*    private static void chooseFile(){
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.showOpenDialog(null);
        File file = fileChooser.getSelectedFile();
        if(!(file == null)){
            certmon.fileField.setText(file.toString());
            certmon.listModel.removeAllElements();
            GetCert.getCertList();
        }
    }*/
}
