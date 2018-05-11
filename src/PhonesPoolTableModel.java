/*
 * Copyright © 2008 Ron de Jong (ronuitzaandam@gmail.com).
 *
 * This is free software; you can redistribute it 
 * under the terms of the Creative Commons License
 * Creative Commons License: (CC BY-NC-ND 4.0) as published by
 * https://creativecommons.org/licenses/by-nc-nd/4.0/ ; either
 * version 4.0 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the 
 * Creative Commons Attribution-NonCommercial-NoDerivatives 4.0
 * International Public License for more details.
 *
 * You should have received a copy of the Creative Commons 
 * Public License License along with this software;
 */

import javax.swing.ImageIcon;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author ron
 */
public class PhonesPoolTableModel extends AbstractTableModel
{
    
    private int phonesTableRowsNeeded = 20;
    private int phonesPoolTablePreferredColumns = 25;
    private String[] columnNames = new String[phonesPoolTablePreferredColumns];

    PhonesPoolTableModel()
    {
//        for ( i = 0; i < phonesPoolTablePreferredColumns; i++) { columnNames[i] = Integer.toString(i); }
    }

    private Object[][] data = new Object[phonesTableRowsNeeded][phonesPoolTablePreferredColumns];
    private Object[] header = new Object[phonesPoolTablePreferredColumns];

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data[row][col];
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
//        return Object.class;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
        if (col < 1) {
            return false;
        } else {
            return false;
        }
    }

    @Override
    public void setValueAt(Object value, int row, int col) {

        if (value instanceof String)
        {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }

        if (value instanceof ImageIcon)
        {
            data[row][col] = value;
            fireTableCellUpdated(row, col);
        }
    }
}
