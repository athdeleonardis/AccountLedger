package Test.CSV;

import CSV.CSV;
import CSV.CSVFileReader;
import CSV.CSVFileWriter;

import java.util.ArrayList;
import java.util.List;

public class CSVTest {
    public static void main(String[] args) {
        CSV csv = new CSV("Test");
        CSVFileReader csvFileReader = new CSVFileReader();

        csvFileReader.setCSV(csv);
        csvFileReader.read("sample/input/test.csv");

        System.out.println("Contents of CSV file '" + csv.getName() + "':");
        System.out.println(csv.toString());

        int rowLength = csv.getColumnNames().size();
        String baseString = "item" + csv.numRows();
        List<String> newRow = new ArrayList<>(rowLength);
        for (int i = 0; i < rowLength; i++) {
            newRow.add(baseString + i);
        }
        csv.addRow(newRow);

        System.out.println("New contents of CSV file '" + csv.getName() + "':");
        System.out.println(csv.toString());

        CSVFileWriter csvFileWriter = new CSVFileWriter();
        csvFileWriter.setCSV(csv);
        csvFileWriter.compile("sample/output/test.csv");
    }
}
