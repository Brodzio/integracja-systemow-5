package com.company;

import javax.jws.WebService;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

@WebService(endpointInterface = "com.company.LaptopsInterface")
public class LaptopBean implements LaptopsInterface {

    String[][] laptopsData = readDataFromDatabaseToTable();

    @Override
    public int numberOfLaptopsByProducer(String producer) {
        int sum = 0;

        for(int i=0;i<laptopsData.length;i++) {
            if(laptopsData[i][0].equals(producer)) {
                sum++;
            }
        }
        return sum;
    }

    @Override
    public String[] listOfProducers() {
        String[] listOfProducers;
        TreeSet<String> tree = new TreeSet<>();

        for(int i=0;i<laptopsData.length;i++) {
            tree.add(laptopsData[i][0]);
        }

        listOfProducers = new String[tree.size()];
        tree.toArray(listOfProducers);

        return listOfProducers;
    }

    @Override
    public String[][] listOfLaptopsByMatrix(String matrix) {
        int index =0, arrayLength = 0;

        for(int i=0;i<laptopsData.length;i++) {
            if(String.valueOf(laptopsData[i][3]).equals(matrix)) {
                arrayLength++;
            }
        }

        String[][] arrayOfLaptopsSortByMatrix = new String[arrayLength][15];

        for(int i=0;i<laptopsData.length;i++) {
            if(String.valueOf(laptopsData[i][3]).equals(matrix)) {
                arrayOfLaptopsSortByMatrix[index] = laptopsData[i];
                index++;
            }
        }

        return arrayOfLaptopsSortByMatrix;
    }

    @Override
    public int numberOfLaptopsByMatrixSize(String proportion) {
        int sum=0;
        double a, b, c, d, x1, x2;

        String[] proportionNumbers = proportion.split(":");
        a = Double.parseDouble(proportionNumbers[0]);
        b = Double.parseDouble(proportionNumbers[1]);
        x1 = a/b;
        x1 = Math.round(x1 * 100.0) / 100.0;

        for(int i=0;i<laptopsData.length;i++) {
            if(!(laptopsData[i][2].equals("brak"))) {
                String[] proportionsNumbers2 = laptopsData[i][2].split("x");
                c = Double.parseDouble(proportionsNumbers2[0]);
                d = Double.parseDouble(proportionsNumbers2[1]);
                x2 = c/d;
                x2 = Math.round(x2 * 100.0) / 100.0;

                if(x1 == x2) {
                    sum++;
                }
            }
        }
        return sum;
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

        String[][] table = new String[data.size()][15];

        for(int j=0;j<data.size();j++) {
            for(int k=0;k<15;k++) {
                table[j][k] = data.get(j).get(k);
            }
        }

        return table;
    }
}
