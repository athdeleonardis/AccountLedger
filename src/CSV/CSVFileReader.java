package CSV;

import java.io.*;
import java.util.Arrays;

public class CSVFileReader {
    private CSV csv;
    private boolean readSuccess;

    public CSVFileReader setCSV(CSV csv) {
        this.csv = csv;
        readSuccess = false;
        return this;
    }

    public CSVFileReader read(String filename) {
        try {
            FileReader fileReader = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String firstLine = bufferedReader.readLine();
            String[] firstLineArray = firstLine.split(",");
            csv.setColumnNames(Arrays.asList(firstLineArray));

            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                String[] elements = line.split(",");
                csv.addRow(Arrays.asList(elements));
            }
            fileReader.close();
            readSuccess = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }

    public boolean success() {
        return readSuccess;
    }
}
