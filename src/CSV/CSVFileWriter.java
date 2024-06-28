package CSV;

import java.io.FileWriter;
import java.io.IOException;

public class CSVFileWriter {
    private CSV csv;

    public CSVFileWriter setCSV(CSV csv) {
        this.csv = csv;
        return this;
    }

    public CSVFileWriter compile(String filename) {
        try {
            FileWriter fileWriter = new FileWriter(filename, false);
            fileWriter.append(csv.toString());
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return this;
    }
}

