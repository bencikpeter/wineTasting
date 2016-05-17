/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.winetasting.frontend;

import cz.muni.fi.pv168.winetasting.backend.WineTastingDAO;
import cz.muni.fi.pv168.winetasting.backend.WineTastingSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author lukas
 */
public class AddSession extends javax.swing.JFrame {
    
    final static Logger log = LoggerFactory.getLogger(AddSession.class);
    
    private static WineTastingDAO wineTastingDAO = CommonResources.getWineTastingDAO();
    private DefaultComboBoxModel wineSessionYearComboBoxModel = new DefaultComboBoxModel<>(years());
    private DefaultComboBoxModel wineSessionMonthComboBoxModel = new DefaultComboBoxModel<>(months());
    private DefaultComboBoxModel wineSessionDayComboBoxModel;
    private MainWindow context;
    private WineSessionTableModel wineSessionModel;
    private WineTastingSession wineTastingSession;
    private String action;
    private int rowIndex;
    /**
     * Creates new form AddSession
     */
    public AddSession(MainWindow context, WineTastingSession wineTastingSession, int rowIndex, String action) {
        initComponents();
        
        jComboBox1.setModel(wineSessionYearComboBoxModel);
        jComboBox2.setModel(wineSessionMonthComboBoxModel);
        
        wineSessionDayComboBoxModel = new DefaultComboBoxModel<>(days());
        jComboBox3.setModel(wineSessionDayComboBoxModel);
        
        this.context = context;
        this.wineTastingSession = wineTastingSession;
        this.rowIndex = rowIndex;
        this.action = action;
        this.wineSessionModel = context.getWineSessionModel();
        jButton1.setText(action);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        if (wineTastingSession != null) {
            jTextField1.setText(wineTastingSession.getPlace());
            jComboBox1.setSelectedItem(wineTastingSession.getDate().getYear());
            jComboBox2.setSelectedItem(wineTastingSession.getDate().getMonthValue());
            jComboBox3.setSelectedItem(wineTastingSession.getDate().getDayOfWeek());
        }
    }
    
    private class AddSessionWorker extends SwingWorker<WineTastingSession, Integer> {

        @Override
        protected WineTastingSession doInBackground() throws Exception {
            log.debug("creating new session in doInBackgroud");
            WineTastingSession session = getWineTastingSessionFromForm();
            if(session == null){
                log.error("Session to add is null (wrong enter data)");
                throw new IllegalArgumentException("wrong-enter-data");
            }
            wineTastingDAO.createSession(session);
            return session;
        }

        @Override
        protected void done() {
            try {
                WineTastingSession session = get();
                wineSessionModel.addWineSession(session);
                log.debug("Modifing wineSessionModel - Adding new session: "+session);
                AddSession.this.dispose();
            } catch (IllegalArgumentException ex) {
                warningMessageBox(ex.getMessage());
                return;
            } catch (ExecutionException ex) {
                log.error("Exception thrown while adding session: " +ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground in AddSessionWorker was interrupted:" +ex.getCause());
                throw new RuntimeException("Operation interrupted in creating new wine session");
            }
        } 
    }
    
    private class UpdateSessionWorker extends SwingWorker<WineTastingSession, Integer> {

        @Override
        protected WineTastingSession doInBackground() throws Exception {
            log.debug("Updating session in doInBackground");
            WineTastingSession session = getWineTastingSessionFromForm();
            if (session == null){
                log.error("Session to add is null (wrong enter data)");
                throw new IllegalArgumentException("wrong-enter-data");
            }
            wineTastingDAO.updateSession(session);
            return session;
        }

        @Override
        protected void done() {
            try {
                WineTastingSession session = get();
                wineSessionModel.updateWineSession(session, rowIndex);
                log.debug("Modifing wineSessionModel - Upadting session: "+session);
                context.getjTableWineSessions().getSelectionModel().clearSelection();
                context.getWineSessionUpdateButton().setEnabled(false);
                context.getWineSessionDeleteButtion().setEnabled(false);
                context.getShowWinesInSessionButton().setEnabled(false);
                context.getLayoutButton().setEnabled(false);
                AddSession.this.dispose();
            } catch (IllegalArgumentException ex) {
                log.error("Illegal argument exception thrown while updating session: " + ex.getCause());
            } catch (ExecutionException ex) {
                log.error("Exception thrown while updating session: "+ ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground in UpdateSessionWorker was interrupted:" +ex.getCause());
                throw new RuntimeException("Operation interrupted in updating wine session");
            } 
        }
    }
    
    
    private WineTastingSession getWineTastingSessionFromForm() {
        String place = jTextField1.getText();
        if (place == null || place.length() == 0) {
            warningMessageBox("fill place");
            return null;
        }
        LocalDate date = LocalDate.of((Integer)jComboBox1.getSelectedItem(), 
                                      (Integer)jComboBox2.getSelectedItem(),
                                      (Integer)jComboBox3.getSelectedItem());
        if (wineTastingSession == null) {
            wineTastingSession = new WineTastingSession();
        }
        
        wineTastingSession.setPlace(place);
        wineTastingSession.setDate(date);
        return wineTastingSession;        
    }
    
    private void warningMessageBox(String message) {
        log.debug("Showing warning message box with message: " + message);
        JOptionPane.showMessageDialog(rootPane, message, null, JOptionPane.INFORMATION_MESSAGE);
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

        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<String>();
        jComboBox2 = new javax.swing.JComboBox<String>();
        jComboBox3 = new javax.swing.JComboBox<String>();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("texts"); // NOI18N
        jLabel1.setText(bundle.getString("Place")); // NOI18N

        jLabel2.setText(bundle.getString("Date")); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox1ItemStateChanged(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox2ItemStateChanged(evt);
            }
        });

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText(bundle.getString("Year")); // NOI18N

        jLabel4.setText(bundle.getString("Month")); // NOI18N

        jLabel5.setText(bundle.getString("Day")); // NOI18N

        jButton1.setText(bundle.getString("Add")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(42, 42, 42)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField1)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel3)
                                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(0, 69, Short.MAX_VALUE))
                                    .addComponent(jComboBox2, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jComboBox1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox1ItemStateChanged
        wineSessionDayComboBoxModel = new DefaultComboBoxModel<>(days());
        jComboBox3.setModel(wineSessionDayComboBoxModel);
    }//GEN-LAST:event_jComboBox1ItemStateChanged

    private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox2ItemStateChanged
        wineSessionDayComboBoxModel = new DefaultComboBoxModel<>(days());
        jComboBox3.setModel(wineSessionDayComboBoxModel);
    }//GEN-LAST:event_jComboBox2ItemStateChanged

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (action.equals("add")) {
            AddSessionWorker worker = new AddSessionWorker();
            worker.execute();
        } else if (action.equals("update")) {
            UpdateSessionWorker worker = new UpdateSessionWorker();
            worker.execute();
        }
    }//GEN-LAST:event_jButton1ActionPerformed



    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JComboBox<String> jComboBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables
}
