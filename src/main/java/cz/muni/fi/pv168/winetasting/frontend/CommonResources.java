/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.winetasting.frontend;

import org.apache.derby.jdbc.EmbeddedDataSource;
import cz.muni.fi.pv168.winetasting.backend.WineSampleDAO;
import cz.muni.fi.pv168.winetasting.backend.WineSampleDAOImpl;
import cz.muni.fi.pv168.winetasting.backend.WineTastingDAO;
import cz.muni.fi.pv168.winetasting.backend.WineTastingDAOImpl;
import cz.muni.fi.pv168.winetasting.backend.WineTastingManager;
import cz.muni.fi.pv168.winetasting.backend.WineTastingManagerImpl;
import java.util.Arrays;
import java.util.Collections;
import org.apache.derby.jdbc.ClientDataSource;

/**
 *
 * @author lukas
 */
public class CommonResources {
    protected static ClientDataSource dataSource = new ClientDataSource();
    protected static WineSampleDAO wineSampleDAO = new WineSampleDAOImpl(dataSource);
    protected static WineTastingDAO wineTastingDAO = new WineTastingDAOImpl(dataSource);
    protected static WineTastingManager wineTastingManager = new WineTastingManagerImpl(dataSource);

    public static ClientDataSource getDataSource() {
        return dataSource;
    }

    public static WineSampleDAO getWineSampleDAO() {
        return wineSampleDAO;
    }

    public static WineTastingDAO getWineTastingDAO() {
        return wineTastingDAO;
    }

    public static WineTastingManager getWineTastingManager() {
        return wineTastingManager;
    }
    
    public static Integer[] getSortedDesc(int[] a) {
        if (a == null) {
            return null;
        }
        Integer[] result = new Integer[a.length];
        for (int i = 0; i < a.length; i++) {
            result[i] = Integer.valueOf(a[i]);
            
        }
        Arrays.sort(result, Collections.reverseOrder());
        return result;
    }
    
    public static void init(){
        dataSource.setServerName("localhost");
        dataSource.setPortNumber(1527);
        dataSource.setDatabaseName("winesDB");
    }
    
    public CommonResources() {
        dataSource.setServerName("localhost");
        dataSource.setPortNumber(1527);
        dataSource.setDatabaseName("winesDB");
    }  
}
