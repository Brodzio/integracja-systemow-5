package com.company;

import javax.swing.*;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.xml.ws.Endpoint;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TableFrame extends JFrame{

    String[] column = {"Producent", "Przekątna", "Rozdzielczość", "Powierzchnia ekranu", "Dotykowy ekran", "CPU",
            "Liczba rdzeni", "Taktowanie [MHz]", "Ilość RAM", "Pojemność dysku", "Rodzaj dysku", "GPU", "Pamięć GPU",
            "OS", "Napęd ODD"};

    private JTable jTable;
    private JPanel buttonPanel;
    private JScrollPane scrollPane;
    private Object[][] data = new Object[0][15];
    private List<Integer> duplicatedRows = new ArrayList<>();
    public static List<Integer> editedValues = new ArrayList<>();
    public static LaptopBean laptopBean = new LaptopBean();

    public int numberOfduplicates = 0, newRows =0;
    public boolean isDuplicateExist = false;

    private JButton readFromFileButton = new JButton("Wczytaj dane z pliku TXT");
    private JButton saveDataToFile = new JButton("Zapisz dane od pliku TXT");
    private JButton saveDataToXmlFile = new JButton("Zapisz dane do XML");
    private JButton readFromXmlFileButton = new JButton("Wczytaj dane z pliku XML");
    private JButton saveDataToDatabase = new JButton("Zapisz dane do Bazy Danych");
    private JButton readDataFromDatabase = new JButton("Wczytaj dane z Bazy Danych");

    public TableFrame() {
        super("Integracja Systemów - Bartłomiej Brodawka");
        Endpoint.publish("http://localhost:8888/laptops", laptopBean);
        prepareGUI();
    }

    public void prepareGUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocation(100, 100);
        setLayout(new BorderLayout());
        setResizable(true);

        buttonPanel = new JPanel();
        setButtons();
        add(buttonPanel, BorderLayout.PAGE_START);

        loadTable();
        scrollPane = new JScrollPane(jTable);
        add(scrollPane, BorderLayout.CENTER);

        pack();
        setVisible(true);
    }

    public void setButtons() {
        readFromFileButton.addActionListener(actionEvent -> {
            String[][] text = TxtOperations.loadData();
            duplicatedRows.clear();
            editedValues.clear();

            if (jTable.getRowCount() != 0) {
                System.out.println("tak");
                String[][] dataToCompare = loadDataFromJTable();
                checkForDuplicates(text, dataToCompare);
            }

            setTable(text);
            cleanScrollPane();
            if(newRows > 0 || numberOfduplicates > 0) {
                JOptionPane.showMessageDialog(null, "Wczytałem dane z pliku TXT - znalazłem " + newRows + " nowe rekordy, pozostałe " + numberOfduplicates + " to duplikaty.");
            }
        });

        saveDataToFile.addActionListener(actionEvent -> {
            TxtOperations.saveDataToTxtFile(jTable);
        });

        readFromXmlFileButton.addActionListener(actionEvent -> {
            String[][] text = XmlOperations.loadDataFromXmlFile();
            duplicatedRows.clear();
            editedValues.clear();

            if (jTable.getRowCount() != 0) {
                String[][] dataToCompare = loadDataFromJTable();
                checkForDuplicates(text, dataToCompare);
            }

            setTable(text);
            cleanScrollPane();
            if(newRows > 0 || numberOfduplicates > 0) {
                JOptionPane.showMessageDialog(null, "Wczytałem dane z pliku XML - znalazłem " + newRows + " nowe rekordy, pozostałe " + numberOfduplicates + " to duplikaty.");
            }
        });

        saveDataToXmlFile.addActionListener(actionEvent -> {
            XmlOperations.saveDataFromTableToXmlFile(jTable);
        });

        saveDataToDatabase.addActionListener(actionEvent -> {
            saveDataToDatabase();
        });

        readDataFromDatabase.addActionListener(actionEvent -> {
            String[][] table = readDataFromDatabaseToTable();
            duplicatedRows.clear();
            editedValues.clear();

            if (jTable.getRowCount() != 0) {
                String[][] dataToCompare = loadDataFromJTable();
                checkForDuplicates(table, dataToCompare);
            }

            setTable(table);
            cleanScrollPane();
            if(newRows > 0 || numberOfduplicates > 0) {
                JOptionPane.showMessageDialog(null, "Wczytałem dane z Bazy danych - znalazłem " + newRows + " nowe rekordy, pozostałe " + numberOfduplicates + " to duplikaty.");
            }
        });

        buttonPanel.add(readFromFileButton);
        buttonPanel.add(saveDataToFile);
        buttonPanel.add(readFromXmlFileButton);
        buttonPanel.add(saveDataToXmlFile);
        buttonPanel.add(readDataFromDatabase);
        buttonPanel.add(saveDataToDatabase);
    }

    public void loadTable() {
        jTable = new JTable(data, column);
        setPreferredSizeOfColumns();
    }

    public void setPreferredSizeOfColumns() {
        TableColumn columns = null;
        for(int i = 0; i < column.length; i++) {
            columns = jTable.getColumnModel().getColumn(i);
            if(i == 2 || i == 3 || i == 4 || i == 7 || i == 9 ) {
                columns.setPreferredWidth(120);
            } else if (i == 11 || i == 13) {
                columns.setPreferredWidth(200);
            } else if(i == 5) {
                columns.setPreferredWidth(50);
            } else {
                columns.setPreferredWidth(100);
            }
        }
        jTable.setPreferredScrollableViewportSize(new Dimension(jTable.getPreferredSize().width, 500));
    }

    public void setTable(String[][] text) {
        jTable = new JTable(text, column) {
            @Override
            public void setValueAt(Object value, int row, int col) {
                if(value.toString().trim().isEmpty()) {
                    JOptionPane.showMessageDialog(null, "Pole jest puste!");
                } else if ((col == 6 || col == 7) && !isInteger(value.toString().trim())) {
                    JOptionPane.showMessageDialog(null, "Niewłaściwy format danych!");
                } else if (col == 1 && !value.toString().matches("[0-9]+\"")){
                    JOptionPane.showMessageDialog(null, "Wprowadź poprawne dane, np. 15\"");
                } else if (col == 2 && !value.toString().matches("[0-9]+x[0-9]+")) {
                    JOptionPane.showMessageDialog(null, "Wprowadź poprawne dane, np. 1920x1080");
                } else if ((col == 8 || col == 9 || col ==12) && !value.toString().matches("[0-9]+GB")) {
                    JOptionPane.showMessageDialog(null, "Wprowadź poprawne dane, np. 15GB");
                } else if (col == 4 && !isTouchable(value.toString().trim())){
                    JOptionPane.showMessageDialog(null, "Wprowadź poprawne wartość: tak lub nie");
                } else {
                    if(!String.valueOf(jTable.getValueAt(row,col)).equals(String.valueOf(value))) {
                        editedValues.add(row);
                    }

                    super.setValueAt(value, row, col);
                }
            }

            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                Color color;

                if (editedValues.contains(row)) {
                    color = Color.WHITE;
                } else if (duplicatedRows.contains(row)) {
                    color = Color.RED;
                } else {
                    color = Color.GRAY;
                }

                c.setBackground(color);
                return c;
            }
        };
        setPreferredSizeOfColumns();
    }

    public boolean isTouchable(String s) {
        if(s.equals("tak") || s.equals("nie")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        } catch (NullPointerException e) {
            return false;
        }
    }

    public int checkIfEmpty(String s) {
        if(s.trim().equals("brak")) {
            return 0;
        } else {
            return Integer.parseInt(s);
        }
    }

    public void cleanScrollPane() {
        this.remove(scrollPane);
        this.scrollPane = new JScrollPane(jTable);
        this.add(scrollPane, BorderLayout.CENTER);
        this.invalidate();
        this.validate();
        this.repaint();
    }

    public String[][] loadDataFromJTable() {
        String[][] dataFromJTable = new String[jTable.getRowCount()][jTable.getColumnCount()];
        for (int i = 0; i < jTable.getRowCount(); i++) {
            for (int j = 0; j < jTable.getColumnCount(); j++) {
                dataFromJTable[i][j] = jTable.getValueAt(i, j).toString();
            }
        }
        return dataFromJTable;
    }

    public String[][] readDataFromDatabaseToTable() {
        List<List<String>> data = new ArrayList<>();

        try {
            Connection con = ConnectionManager.getConnection();
            String temp;

            Statement statement = con.createStatement();
            String query = "select * from laptop";
            ResultSet rs = statement.executeQuery(query);

            while (rs.next()) {
                List<String> line = new ArrayList<>();

                for(int i = 2;i<17;i++) {
                    if (i==6) {
                        if(String.valueOf(rs.getBoolean(i)).equals("false")) {
                            temp = "nie";
                        } else {
                            temp = "tak";
                        }
                    } else if (i==8 || i==9) {
                        temp = String.valueOf(rs.getInt(i));
                    } else {
                        temp = rs.getString(i);
                    }
                    line.add(temp);
                }
                data.add(line);
            }

            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        String[][] table = new String[data.size()][column.length];

        for(int j=0;j<data.size();j++) {
            for(int k=0;k<column.length;k++) {
                table[j][k] = data.get(j).get(k);
            }
        }

        return table;
    }

    public void checkForDuplicates(String[][] table, String[][] dataToCompare) {
        numberOfduplicates = 0;
        newRows = 0;

        for(int i = 0; i<table.length;i++) {
            isDuplicateExist = false;
            for(int j = 0; j<dataToCompare.length;j++) {
                if(String.valueOf(table[i][0]).equals(String.valueOf(dataToCompare[j][0])) && String.valueOf(table[i][1]).equals(String.valueOf(dataToCompare[j][1])) &&
                        String.valueOf(table[i][2]).equals(String.valueOf(dataToCompare[j][2])) && String.valueOf(table[i][3]).equals(String.valueOf(dataToCompare[j][3])) &&
                        String.valueOf(table[i][4]).equals(String.valueOf(dataToCompare[j][4])) && String.valueOf(table[i][5]).equals(String.valueOf(dataToCompare[j][5])) &&
                        String.valueOf(table[i][6]).equals(String.valueOf(dataToCompare[j][6])) && String.valueOf(table[i][7]).equals(String.valueOf(dataToCompare[j][7])) &&
                        String.valueOf(table[i][8]).equals(String.valueOf(dataToCompare[j][8])) && String.valueOf(table[i][9]).equals(String.valueOf(dataToCompare[j][9])) &&
                        String.valueOf(table[i][10]).equals(String.valueOf(dataToCompare[j][10])) && String.valueOf(table[i][11]).equals(String.valueOf(dataToCompare[j][11])) &&
                        String.valueOf(table[i][12]).equals(String.valueOf(dataToCompare[j][12])) && String.valueOf(table[i][13]).equals(String.valueOf(dataToCompare[j][13])) &&
                        String.valueOf(table[i][14]).equals(String.valueOf(dataToCompare[j][14])) ) {
                    duplicatedRows.add(i);
                    isDuplicateExist = true;
                }
            }
            if(isDuplicateExist) {
                numberOfduplicates++;
            } else {
                newRows++;
            }
        }

    }

    public void saveDataToDatabase() {
        if(jTable.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Tab is Empty");
        } else  {
            try {

                String[][] dataFromJTable = loadDataFromJTable();
                String[][] table = readDataFromDatabaseToTable();
                Connection con = ConnectionManager.getConnection();
                newRows = 0;
                numberOfduplicates = 0;

                for(int i = 0; i<dataFromJTable.length;i++) {
                    isDuplicateExist = false;
                    for(int j = 0; j<table.length;j++) {
                        if(String.valueOf(dataFromJTable[i][0]).equals(String.valueOf(table[j][0])) && String.valueOf(dataFromJTable[i][1]).equals(String.valueOf(table[j][1])) &&
                                String.valueOf(dataFromJTable[i][2]).equals(String.valueOf(table[j][2])) && String.valueOf(dataFromJTable[i][3]).equals(String.valueOf(table[j][3])) &&
                                String.valueOf(dataFromJTable[i][4]).equals(String.valueOf(table[j][4])) && String.valueOf(dataFromJTable[i][5]).equals(String.valueOf(table[j][5])) &&
                                String.valueOf(dataFromJTable[i][6]).equals(String.valueOf(table[j][6])) && String.valueOf(dataFromJTable[i][7]).equals(String.valueOf(table[j][7])) &&
                                String.valueOf(dataFromJTable[i][8]).equals(String.valueOf(table[j][8])) && String.valueOf(dataFromJTable[i][9]).equals(String.valueOf(table[j][9])) &&
                                String.valueOf(dataFromJTable[i][10]).equals(String.valueOf(table[j][10])) && String.valueOf(dataFromJTable[i][11]).equals(String.valueOf(table[j][11])) &&
                                String.valueOf(dataFromJTable[i][12]).equals(String.valueOf(table[j][12])) && String.valueOf(dataFromJTable[i][13]).equals(String.valueOf(table[j][13])) &&
                                String.valueOf(dataFromJTable[i][14]).equals(String.valueOf(table[j][14])) ) {
                            isDuplicateExist = true;
                        }
                    }
                    if(isDuplicateExist) {
                        numberOfduplicates++;
                    } else {
                        newRows++;

                        String query = "INSERT INTO laptop(manufacturer, size, resolution, screen_type, isTouchable, processor, physical_cores, clock_speed, ram, disc_capcity, disc_type, graphic_card, graphic_card_memory, os, disc_reader) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                        PreparedStatement prepStat = con.prepareStatement(query);

                        prepStat.setString(1, jTable.getValueAt(i, 0).toString());
                        prepStat.setString(2, jTable.getValueAt(i, 1).toString());
                        prepStat.setString(3, jTable.getValueAt(i, 2).toString());
                        prepStat.setString(4, jTable.getValueAt(i, 3).toString());
                        prepStat.setBoolean(5, isTouchable(jTable.getValueAt(i, 4).toString().trim()));
                        prepStat.setString(6, jTable.getValueAt(i, 5).toString());
                        prepStat.setInt(7, checkIfEmpty(jTable.getValueAt(i, 6).toString()));
                        prepStat.setInt(8, checkIfEmpty(jTable.getValueAt(i, 7).toString()));
                        prepStat.setString(9, jTable.getValueAt(i, 8).toString());
                        prepStat.setString(10, jTable.getValueAt(i, 9).toString());
                        prepStat.setString(11, jTable.getValueAt(i, 10).toString());
                        prepStat.setString(12, jTable.getValueAt(i, 11).toString());
                        prepStat.setString(13, jTable.getValueAt(i, 12).toString());
                        prepStat.setString(14, jTable.getValueAt(i, 13).toString());
                        prepStat.setString(15, jTable.getValueAt(i, 14).toString());

                        prepStat.execute();
                    }
                }

                JOptionPane.showMessageDialog(null, "Zapisałem dane do Bazy danych - znalazłem " + newRows + " nowych rekordów, pozostałe " + numberOfduplicates + " to duplikaty.");

                con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
