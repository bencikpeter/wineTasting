/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.winetasting.frontend;

import cz.muni.fi.pv168.winetasting.backend.DBUtils;
import cz.muni.fi.pv168.winetasting.backend.Exceptions.ServiceFailureException;
import cz.muni.fi.pv168.winetasting.backend.WineSample;
import cz.muni.fi.pv168.winetasting.frontend.CommonResources;
import cz.muni.fi.pv168.winetasting.backend.WineSampleDAO;
import cz.muni.fi.pv168.winetasting.backend.WineTastingDAO;
import cz.muni.fi.pv168.winetasting.backend.WineTastingManager;
import cz.muni.fi.pv168.winetasting.backend.WineTastingSession;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import org.apache.derby.jdbc.ClientDataSource;
import org.apache.derby.jdbc.EmbeddedDataSource;

/**
 *
 * @author lukas
 */
public class MainWindow extends javax.swing.JFrame {
    
    private static CommonResources resources;
    private static ClientDataSource dataSource = resources.getDataSource();
    private static WineSampleDAO wineSampleDAO = resources.getWineSampleDAO();
    private static WineTastingDAO wineTastingDAO = resources.getWineTastingDAO();
    private static WineTastingManager wineTastingManager = resources.getWineTastingManager();
    private WineSampleTableModel wineSampleModel;
    private WineSessionTableModel wineSessionModel;
    private DefaultComboBoxModel wineSessionYearComboBoxModel;
    private DefaultComboBoxModel wineSessionMonthComboBoxModel;
    private DefaultComboBoxModel wineSessionDayComboBoxModel; 
    private FindAllWineSamplesWorker findAllWineSamplesWorker;
    private FindAllWineSessionsWorker findAllWineSessionsWorker;

    public WineSampleTableModel getWineSampleModel() {
        return wineSampleModel;
    }

    public WineSessionTableModel getWineSessionModel() {
        return wineSessionModel;
    }

    public JTable getjTableWineSamples() {
        return jTableWineSamples;
    }

    public JButton getWineSampleUpdateButton() {
        return jButton12;
    }

    public JButton getWineSampleDeleteButton() {
        return jButton13;
    }

    public JTable getjTableWineSessions() {
        return jTableWineSessions;
    }

    public JButton getShowWinesInSessionButton() {
        return jButton15;
    }

    public JButton getWineSessionDeleteButtion() {
        return jButton8;
    }

    public JButton getWineSessionUpdateButton() {
        return jButton9;
    }

    public JButton getLayoutButton() {
        return jButton7;
    }
    
    
    
    
    
    
    private class FindAllWineSamplesWorker extends SwingWorker<List<WineSample>, Integer> {

        @Override
        protected List<WineSample> doInBackground() throws Exception {
            return wineSampleDAO.findAllWineSamples();
        }

        @Override
        protected void done() {
            try{
         //TODO   log.debug("Changing wineSample model - all wineSamples are loaded from database."
                wineSampleModel.setWineSamples(get());
            }catch(ExecutionException ex) {
         //TODO   log.error("Exception was thrown in FindAllWineSamplesWorker in method doInBackGround " + ex.getCause());
            } catch (InterruptedException ex) {
         //TODO   log.error("Method doInBackground has been interrupted in FindAllWineSamplesWorker " + ex.getCause());
                throw new RuntimeException("Operation interrupted in FindAllWineSamplesWorker");
            }
        }
        
    }
    
    private class FindWineSamplesByVarietyWorker extends SwingWorker<List<WineSample>, Integer> {
            
        private String variety;
        
        public FindWineSamplesByVarietyWorker(String variety) {
            this.variety = variety;
        }
        
        @Override
        protected List<WineSample> doInBackground() throws Exception {
            return wineSampleDAO.findWineSamplesByVariety(variety);
        }

        @Override
        protected void done() {
            try {
                // TODO log
                wineSampleModel.setWineSamples(get());
            } catch (ExecutionException ex) {
                //TODO log
            } catch (InterruptedException ex) {
                //TODO log
                throw new RuntimeException("Operation interrupted in FindWineSamplesByVarietyWorker");
            }
        }
    }
    
    private int[] convert(List<Integer> o) {
        int[] result = new int[o.size()];
        for (int i = 0; i < o.size(); i++) {
            result[i] = o.get(i);
        }
        return result;
    }
    
    private class DeleteWineSampleWorker extends SwingWorker<int[], Void> {

        @Override
        protected int[] doInBackground() throws Exception {
            int[] selectedRows = jTableWineSamples.getSelectedRows();
            List<Integer> toDeleteRows = new ArrayList<>();
            if (selectedRows.length >= 0) {
                for (int selectedRow : selectedRows) {
                    WineSample wineSample = wineSampleModel.getWineSample(selectedRow);
                    try {
                        wineSampleDAO.deleteWineSample(wineSample);
                        toDeleteRows.add(selectedRow);
                    } catch (Exception ex) {
                        throw new ServiceFailureException(wineSample.toString());
                    }
                }
                getjTableWineSamples().getSelectionModel().clearSelection();
                getWineSampleUpdateButton().setEnabled(false);
                getWineSampleDeleteButton().setEnabled(false);
                return convert(toDeleteRows);
            }
            getjTableWineSamples().getSelectionModel().clearSelection();
            getWineSampleUpdateButton().setEnabled(false);
            getWineSampleDeleteButton().setEnabled(false);
            return null;
        }

        @Override
        protected void done() {
            try {
                int [] indexes = get();
                // log debug
                if (indexes != null && indexes.length != 0) {
                    wineSampleModel.deleteWineSamples(indexes);
                }
            } catch (ExecutionException ex) {
                JOptionPane.showMessageDialog(rootPane, "cannot-delete-wine-sample");
                // log error
            } catch (InterruptedException ex) {
                //log error
                throw new RuntimeException("Operation interrupted.. DeleteWineSampleWorker");
            }
        }
    }
    
    private class FindAllWineSessionsWorker extends SwingWorker<List<WineTastingSession>, Integer> {

        @Override
        protected List<WineTastingSession> doInBackground() throws Exception {
            return wineTastingDAO.findAllSessions();
        }
        
        @Override
        protected void done() {
            try {
                // log debug
                wineSessionModel.setWineSessions(get());
            } catch (ExecutionException ex) {
                //TODO log error
            } catch (InterruptedException ex) {
                //TODO log error
                throw new RuntimeException("Operation interrupted in FindAllWineTastingSessions");
            }
        }
    }
    
    private class FindWineSessionsByDate extends SwingWorker<List<WineTastingSession>, Integer> {

        private LocalDate date;
        
        public FindWineSessionsByDate(LocalDate date) {
            this.date = date;
        }
        
        @Override
        protected List<WineTastingSession> doInBackground() throws Exception {
            return wineTastingDAO.findSessionByDate(date);
        }

        @Override
        protected void done() {
            try {
                //TODO log
                wineSessionModel.setWineSessions(get());
            } catch (ExecutionException ex) {
                //TODO log
            } catch (InterruptedException ex) {
                //TODO log
                throw new RuntimeException("Operation interrupted in FindWineTastingSessionsByDate");
            }
        }
    }
    
    private class DeleteWineSessionWorker extends SwingWorker<int[], Void> {

        @Override
        protected int[] doInBackground() throws Exception {
            int[] selectedRows = jTableWineSessions.getSelectedRows();
            List<Integer> toDeleteRows = new ArrayList<>();
            if (selectedRows.length >= 0) {
                for (int selectedRow: selectedRows) {
                    WineTastingSession wineSession = wineSessionModel.getWineTastingSession(selectedRow);
                    try {
                        wineTastingDAO.deleteSession(wineSession);
                        toDeleteRows.add(selectedRow);
                    } catch (Exception ex) {
                        throw new ServiceFailureException(wineSession.toString());
                    }
                }
                getjTableWineSessions().getSelectionModel().clearSelection();
                getShowWinesInSessionButton().setEnabled(false);
                getWineSessionDeleteButtion().setEnabled(false);
                getWineSessionUpdateButton().setEnabled(false);
                getLayoutButton().setEnabled(false);
                return convert(toDeleteRows);
            }
            getjTableWineSessions().getSelectionModel().clearSelection();
            getShowWinesInSessionButton().setEnabled(false);
            getWineSessionDeleteButtion().setEnabled(false);
            getWineSessionUpdateButton().setEnabled(false);
            return null;
        }

        @Override
        protected void done() {
            try {
                int[] indexes = get();
                //TODO log debug
                if(indexes != null && indexes.length != 0) {
                    wineSessionModel.deleteWineSessions(indexes);
                }
            } catch (ExecutionException ex) {
                JOptionPane.showMessageDialog(rootPane, "cannot-delete-wine-session");
                //TOTO log error
            } catch (InterruptedException ex) {
                //log error
                throw new RuntimeException("Operation interrupted.. DeleteWineSessionWorker");
            }
        }
    }
    
    
    private void createDB() {
        try {
            DBUtils.executeSqlScript(dataSource, WineTastingManager.class.getResource("/createTables.sql"));
        } catch (SQLException ex) {
            // logging and show JOptionPane.showMessageDialog some error
            System.out.println(ex.getMessage());
        }
    }
    
    private void insertIntoDB() {
        try {
            DBUtils.executeSqlScript(dataSource, WineTastingManager.class.getResource("/insertValues.sql"));
        } catch (SQLException ex) {
             // logging and show JOptionPane.showMessageDialog some error
             System.out.println(ex.getMessage());
        }
    }
    
    private static void deleteDB() {
        try {
            // we need to create dropTables.sql file
            DBUtils.executeSqlScript(dataSource, WineTastingManager.class.getResource("/dropTables.sql"));
        } catch (SQLException ex) {
            // logging
        }
    }
    
    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        
        this.wineSessionYearComboBoxModel = new DefaultComboBoxModel<>(years());
        this.wineSessionMonthComboBoxModel = new DefaultComboBoxModel<>(months());
        
        jComboBox1.setModel(wineSessionYearComboBoxModel);
        jComboBox2.setModel(wineSessionMonthComboBoxModel);
        
        this.wineSessionDayComboBoxModel = new DefaultComboBoxModel<>(days());
        jComboBox3.setModel(wineSessionDayComboBoxModel);
        
        resources = new CommonResources();
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        createDB();
        insertIntoDB();
        wineSampleModel = (WineSampleTableModel) jTableWineSamples.getModel();
        wineSessionModel = (WineSessionTableModel) jTableWineSessions.getModel();
        
        findAllWineSamplesWorker = new FindAllWineSamplesWorker();
        findAllWineSamplesWorker.execute();
        
        findAllWineSessionsWorker = new FindAllWineSessionsWorker();
        findAllWineSessionsWorker.execute();
        
    }
    
    private static Object[] years() {
        ArrayList<Integer> years_tmp = new ArrayList<Integer>();
        for(int years = 1980 ; years<=Calendar.getInstance().get(Calendar.YEAR);years++){
            years_tmp.add(years);
        }
        return years_tmp.toArray();
    }
    
    private static Object[] months() {
        ArrayList<Integer> months_tmp = new ArrayList<Integer>();
        for(int months = 1 ; months <= 12; months++){
            months_tmp.add(months);
        }
        return months_tmp.toArray();
    }
    
    private Object[] days() {
        int selected = (Integer) jComboBox2.getSelectedItem();
        int days = 0;
        switch (selected) {
            case 1:
            case 3:
            case 5:
            case 7:
            case 8:
            case 10:
            case 12:
                days = 31;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                days = 30;
                break;
            case 2:
                int year = (Integer) jComboBox1.getSelectedItem();
                if (year % 4 == 0 ){
                    days = 29;
                } else {
                    days = 28;
                }
                break;
            default:
                throw new RuntimeException("Non-existing days");
        }
        ArrayList<Integer> days_tmp = new ArrayList<Integer>();
        for(int i = 1; i <= days; i++){
            days_tmp.add(i);
        }
        return days_tmp.toArray();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTableWineSamples = new javax.swing.JTable();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTableWineSessions = new javax.swing.JTable();
        jButton2 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jComboBox3 = new javax.swing.JComboBox<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jButton15 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButton1.setText("List All");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton4.setText("Add Wine");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton10.setText("Search by variety");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("Search by last name");

        jButton12.setText("Update selected");
        jButton12.setEnabled(false);
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setText("Delete selected");
        jButton13.setEnabled(false);
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jTableWineSamples.setModel(new WineSampleTableModel());
        jTableWineSamples.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableWineSamplesMouseReleased(evt);
            }
        });
        jScrollPane3.setViewportView(jTableWineSamples);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton4, javax.swing.GroupLayout.DEFAULT_SIZE, 139, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(96, 96, 96)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jTextField1, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                    .addComponent(jTextField2))
                .addGap(18, 18, 18)
                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39)
                .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(55, Short.MAX_VALUE))
            .addComponent(jScrollPane3)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 485, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(29, 29, 29)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 24, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Wines", jPanel4);

        jTableWineSessions.setModel(new WineSessionTableModel());
        jTableWineSessions.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableWineSessionsMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jTableWineSessions);

        jButton2.setText("List All");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton5.setText("Add Session");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setText("Search by date");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton8.setText("Delete selected");
        jButton8.setEnabled(false);
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Update selected");
        jButton9.setEnabled(false);
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox2ItemStateChanged(evt);
            }
        });

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText("Year");

        jLabel2.setText("Month");

        jLabel3.setText("Day");

        jButton15.setText("Wines in selected");
        jButton15.setEnabled(false);
        jButton15.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton15ActionPerformed(evt);
            }
        });

        jButton7.setText("Layout");
        jButton7.setEnabled(false);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 994, Short.MAX_VALUE)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(32, 32, 32)
                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton15, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 36, Short.MAX_VALUE))
        );

        jTabbedPane2.addTab("Sessions", jPanel5);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1000, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane2)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        jButton7.setEnabled(false);
        jButton8.setEnabled(false);
        jButton9.setEnabled(false);
        jButton15.setEnabled(false);
        findAllWineSessionsWorker = new FindAllWineSessionsWorker();
        findAllWineSessionsWorker.execute();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        jTextField1.setText(null);
        jTextField2.setText(null);
        jButton12.setEnabled(false);
        jButton13.setEnabled(false);
        findAllWineSamplesWorker = new FindAllWineSamplesWorker();
        findAllWineSamplesWorker.execute();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AddWine(MainWindow.this, null, -1, "add").setVisible(true);
            }
        });
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        String variety = jTextField1.getText();
        if (variety == null || variety.length() == 0) {
            findAllWineSamplesWorker = new FindAllWineSamplesWorker();
            findAllWineSamplesWorker.execute();
            return;
        }
        FindWineSamplesByVarietyWorker worker = new FindWineSamplesByVarietyWorker(variety);
        worker.execute();
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                int selectedRow = jTableWineSamples.getSelectedRow();
                new AddWine(MainWindow.this, wineSampleModel.getWineSample(selectedRow), selectedRow, "update");
            }
        });
    }//GEN-LAST:event_jButton12ActionPerformed

    private void jTableWineSamplesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableWineSamplesMouseReleased
        if (jTableWineSamples.getSelectedRowCount() != 1) {
            jButton12.setEnabled(false);
        } else {
            jButton12.setEnabled(true);
        }
        jButton13.setEnabled(true);
    }//GEN-LAST:event_jTableWineSamplesMouseReleased

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        DeleteWineSampleWorker worker = new DeleteWineSampleWorker();
        worker.execute();
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new AddSession(MainWindow.this, null, -1, "add").setVisible(true);
            }
        });
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox2ItemStateChanged
        this.wineSessionDayComboBoxModel = new DefaultComboBoxModel<>(days());
        jComboBox3.setModel(wineSessionDayComboBoxModel);
    }//GEN-LAST:event_jComboBox2ItemStateChanged

    private void jTableWineSessionsMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableWineSessionsMouseReleased
        if (jTableWineSessions.getSelectedRowCount() != 1) {
            jButton7.setEnabled(false);
            jButton9.setEnabled(false);
            jButton15.setEnabled(false);
        } else {
            jButton7.setEnabled(true);
            jButton9.setEnabled(true);
            jButton15.setEnabled(true);
        }
        jButton8.setEnabled(true);
    }//GEN-LAST:event_jTableWineSessionsMouseReleased

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        DeleteWineSessionWorker worker = new DeleteWineSessionWorker();
        worker.execute();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
           public void run() {
               int selectedRow = jTableWineSessions.getSelectedRow();
               new AddSession(MainWindow.this, wineSessionModel.getWineTastingSession(selectedRow), selectedRow, "update").setVisible(true);
           } 
        });
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        this.wineSessionDayComboBoxModel = new DefaultComboBoxModel<>(days());
        jComboBox3.setModel(wineSessionDayComboBoxModel);
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        LocalDate date = LocalDate.of((Integer)jComboBox1.getSelectedItem(), 
                                      (Integer)jComboBox2.getSelectedItem(),
                                      (Integer)jComboBox3.getSelectedItem());
        jButton7.setEnabled(false);
        jButton8.setEnabled(false);
        jButton9.setEnabled(false);
        jButton15.setEnabled(false);
        FindWineSessionsByDate worker = new FindWineSessionsByDate(date);
        worker.execute();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton15ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton15ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                int selectedRow = jTableWineSessions.getSelectedRow();
                new SessionDependentWines(MainWindow.this, wineSessionModel.getWineTastingSession(selectedRow), selectedRow).setVisible(true);
            }
        });
    }//GEN-LAST:event_jButton15ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        CommonResources.init();
        deleteDB();
        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton15;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTableWineSamples;
    private javax.swing.JTable jTableWineSessions;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    // End of variables declaration//GEN-END:variables
}
