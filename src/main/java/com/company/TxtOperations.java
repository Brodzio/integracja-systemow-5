package com.company;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TxtOperations {

    static String[] column = {"Producent", "Przekątna", "Rozdzielczość", "Powierzchnia ekranu", "Dotykowy ekran", "CPU",
            "Liczba rdzeni", "Taktowanie [MHz]", "Ilość RAM", "Pojemność dysku", "Rodzaj dysku", "GPU", "Pamięć GPU",
            "OS", "Napęd ODD"};

    public static void saveDataToTxtFile(JTable jTable) {
        try {
            File file = new File("src\\main\\java\\com\\company\\katalog.txt");
            if(!file.exists()) {
                file.createNewFile();
            } else {
                FileWriter out = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(out);

                String line = "";

                for(int row = 0; row < jTable.getRowCount(); row++) {
                    for(int col = 0; col < jTable.getColumnCount(); col++) {
                        String value = (String) jTable.getValueAt(row, col);
                        line += value + ";";
                    }
                    bw.write(line.toCharArray());
                    bw.newLine();
                    line = "";
                }
                bw.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String[][] loadData() {
        List<List<String>> table = new ArrayList<>();

        try {
            File file = new File("src\\main\\java\\com\\company\\katalog.txt");
            if(!file.exists()) {
                file.createNewFile();
            } else {
                FileReader plik = new FileReader("src\\main\\java\\com\\company\\katalog.txt");
                BufferedReader bufor = new BufferedReader(plik);
                boolean koniecPliku = false;
                while (!koniecPliku) {
                    String wiersz = bufor.readLine();
                    if(wiersz == null) {
                        koniecPliku = true;
                    } else {
                        String[] rozdzielonyWiersz = wiersz.split(";", -1);
                        List<String> line = new ArrayList<>();
                        for(int i=0; i<column.length; i++) {
                            if(rozdzielonyWiersz[i].isEmpty()) {
                                line.add("brak");
                            } else {
                                line.add(rozdzielonyWiersz[i]);
                            }
                        }
                        table.add(line);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        String[][] text = new String[table.size()][column.length];

        int j =0;
        int k =0;

        for(j=0;j<table.size();j++) {
            for(k=0;k<column.length;k++) {
                text[j][k] = table.get(j).get(k);
            }
        }

        return text;
    }
}
