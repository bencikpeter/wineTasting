/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.winetasting.frontend;

import cz.muni.fi.pv168.winetasting.backend.WineTastingSession;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author lukas
 */
public class WineSessionTableModel extends AbstractTableModel {
    
    //TODO here should be logger
    
    private List<WineTastingSession> wineSessions = new ArrayList<>();
    
    @Override
    public int getRowCount() {
        return wineSessions.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }
    
    public WineTastingSession getWineTastingSession(int index) {
        return wineSessions.get(index);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        WineTastingSession wineSession = wineSessions.get(rowIndex);
        switch (columnIndex) {
            case 0:
                return wineSession.getPlace();
            case 1:
                return wineSession.getDate();
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        WineTastingSession wineSession = wineSessions.get(rowIndex);
        
        switch (columnIndex) {
            case 0:
                wineSession.setPlace((String) aValue);
            case 1:
                wineSession.setDate((LocalDate) aValue);
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        switch (columnIndex) {
            case 0:
            case 1:
                return false;
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        switch (columnIndex) {
            case 0:
                return String.class;
            case 1:
                return LocalDate.class;
            default:
                throw new IllegalArgumentException("column index");
        }
    }

    @Override
    public String getColumnName(int column) {
        switch (column) {
            case 0:
                return "place";
            case 1:
                return "date";
            default:
                throw new IllegalArgumentException("columnIndex");
        }
    }
    
    public void addWineSession(WineTastingSession wineSession) {
        wineSessions.add(wineSession);
        int lastRow = wineSessions.size() - 1;
        fireTableRowsInserted(lastRow, lastRow);
    }
    
    public void updateWineSession(WineTastingSession wineSession, int rowIndex) {
        wineSessions.set(rowIndex, wineSession);
        fireTableRowsUpdated(rowIndex, rowIndex);
    }
    
    public void deleteWineSession(int rowIndex) {
        wineSessions.remove(rowIndex);
        fireTableRowsDeleted(rowIndex, rowIndex);
    }
    
    public void deleteWineSessions(int [] selectedRows) {
        Integer[] rowsToDelete = CommonResources.getSortedDesc(selectedRows);
        for (int i : rowsToDelete) {
            deleteWineSession(i);
        }
    }
    
    public void setWineSessions(List<WineTastingSession> wineSessionsToAdd) {
        wineSessions = wineSessionsToAdd;
        fireTableDataChanged();
    }
}
