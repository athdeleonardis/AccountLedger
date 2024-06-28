package CSV;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CSV {
    private String name;
    private HashMap<String,Integer> nameToColumnMap;
    private List<String> columnNames;
    private List<List<String>> rows;

    public CSV(String name) {
        this.name = name;
        nameToColumnMap = new HashMap<>();
        rows = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public CSV setColumnNames(List<String> names) {
        this.columnNames = new ArrayList<String>();
        nameToColumnMap.clear();
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            // Sometimes file will start with this invisible character :(
            if (name.startsWith(""+(char)65279))
                name = name.substring(1);
            nameToColumnMap.put(name, i);
            columnNames.add(name);
        }
        return this;
    }

    public CSV addRow(List<String> row) {
        this.rows.add(row);
        return this;
    }

    public int numRows() {
        return rows.size();
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    public String getElement(int row, String columnName) {
        Integer column = nameToColumnMap.get(columnName);
        return rows.get(row).get(column);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < columnNames.size(); i++) {
            stringBuilder.append(columnNames.get(i));
            if (i != columnNames.size()-1);
            stringBuilder.append(",");
        }
        stringBuilder.append("\n");
        for (int i = 0; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            for (int j = 0; j < row.size(); j++) {
                stringBuilder.append(row.get(j));
                if (j != row.size()-1)
                    stringBuilder.append(",");
            }
            if (i != rows.size()-1)
                stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}

