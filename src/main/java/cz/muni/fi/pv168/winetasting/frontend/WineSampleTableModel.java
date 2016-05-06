/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.winetasting.frontend;

import cz.muni.fi.pv168.winetasting.backend.WineCharacter;
import cz.muni.fi.pv168.winetasting.backend.WineColor;
import cz.muni.fi.pv168.winetasting.backend.WineSample;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author lukas
 */
public class WineSampleTableModel extends AbstractTableModel{
    
    //TODO here should be logger
    
    private List<WineSample> wineSamples = new ArrayList<>();

    @Override
    public int getRowCount() {
        return wineSamples.size();
    }

    @Override
    public int getColumnCount() {
        return 6;
    }
    
    public WineSample getWineSample(int index) {
        return wineSamples.get(index);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        WineSample wineSample = wineSamples.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return wineSample.getVintnerFirstName();
            case 1:
                return wineSample.getVintnerLastName();
            case 2:
                return wineSample.getVariety();
            case 3:
                return wineSample.getColor().toString();
            case 4:
                return wineSample.getCharacter().toString();
            case 5:
                return wineSample.getYear();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                return String.class;
            case 5:
                return Integer.class;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public String getColumnName(int column) {
        switch ( column ) {
            case 0:
                return "first name";
            case 1:
                return "last name";
            case 2:
                return "variety";
            case 3:
                return "color";
            case 4:
                return "character";
            case 5:
                return "year";
            default:
                throw new IllegalArgumentException("column incorrect index");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        WineSample wineSample = wineSamples.get(rowIndex);
        
        switch (columnIndex) {
            case 0:
                wineSample.setVintnerFirstName((String) aValue);
                break;
            case 1:
                wineSample.setVintnerLastName((String) aValue);
                break;
            case 2:
                wineSample.setVariety((String) aValue);
                break;
            case 3:
                wineSample.setColor(WineColor.valueOf((String) aValue));
                break;
            case 4:
                wineSample.setCharacter(WineCharacter.valueOf((String) aValue));
                break;
            case 5:
                wineSample.setYear((int) aValue);
                break;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch(columnIndex) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
                return false;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    public void addWineSample(WineSample wineSample) {
        wineSamples.add(wineSample);
        int lastRow = wineSamples.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }
    
    public void updateWineSample(WineSample wineSample, int rowIndex) {
        wineSamples.set(rowIndex, wineSample);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public void deleteWineSample(int rowIndex) {
        wineSamples.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
    
    public void deleteWineSamples(int [] selectedRows) {
        Integer[] rowsToDelete = CommonResources.getSortedDesc(selectedRows);
        for(int i : rowsToDelete) {
            deleteWineSample(i);
        }
    }
    
    public void setWineSamples(List<WineSample> wineSamplesToAdd) {
        wineSamples = wineSamplesToAdd;
        fireTableDataChanged();
    }
}
