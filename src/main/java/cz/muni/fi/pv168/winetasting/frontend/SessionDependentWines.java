/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.winetasting.frontend;

import cz.muni.fi.pv168.winetasting.backend.Exceptions.ServiceFailureException;
import cz.muni.fi.pv168.winetasting.backend.WineSample;
import cz.muni.fi.pv168.winetasting.backend.WineSampleDAO;
import cz.muni.fi.pv168.winetasting.backend.WineTastingManager;
import cz.muni.fi.pv168.winetasting.backend.WineTastingSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 *
 * @author lukas
 */
public class SessionDependentWines extends javax.swing.JFrame {

    final static Logger log = LoggerFactory.getLogger(SessionDependentWines.class);

    private static WineSampleDAO wineSampleDAO = CommonResources.getWineSampleDAO();
    private static WineTastingManager wineTastingManager = CommonResources.getWineTastingManager();
    private MainWindow context;
    private WinesInSessionTableModel wineSampleModel;
    private WineTastingSession wineSession;
    private int rowIndex;
    private FindWineSamplesBySessionWorker findWineSamplesBySession;

    public WinesInSessionTableModel getWineSampleModel() {
        return wineSampleModel;
    }
    
    
    private class FindWineSamplesBySessionWorker extends SwingWorker<List<WineSample>, Integer> {

        private WineTastingSession session;

        public FindWineSamplesBySessionWorker(WineTastingSession session) {
            this.session = session;
        }
        
        @Override
        protected List<WineSample> doInBackground() throws Exception {
            return wineTastingManager.findAllWinesInSession(session);
        }

        @Override
        protected void done() {
            try {
                log.debug("Finding winesamples in session:" + session);
                wineSampleModel.setWineSamples(get());
            } catch (ExecutionException ex) {
                log.error("Exception thrown while finding wines in session: " + session);
            } catch (InterruptedException ex) {
                log.error("Method doInBackground in FindWineSamplesBySessionWorker was interrupted"+ex.getCause());
                throw new RuntimeException("Operation interrupted in FindWineSamplesBySession");
            }
        }   
    }
    
    private class AssignRatingToWineWorker extends SwingWorker<WineSample, Integer> {

        @Override
        protected WineSample doInBackground() throws Exception {
            log.debug("Assigning rating to wine in background");
            rowIndex = jTableWineSamples.getSelectedRow();
            WineSample wineSample = wineSampleModel.getWineSample(rowIndex);
            wineSample.setRating(jSlider1.getValue());
            wineTastingManager.assignRatingToWine(wineSample, wineSession, wineSample.getRating());
            return wineSample;
        }

        @Override
        protected void done() {
            try {
                WineSample wine = get();
                wineSampleModel.updateWineSample(wine, rowIndex);
                log.debug("Assigning rating to wine");
                jTableWineSamples.getSelectionModel().clearSelection();
                jSlider1.setEnabled(false);
                jButton2.setEnabled(false);
                jButton1.setEnabled(false);
            } catch (IllegalArgumentException ex) {
                log.error("Wrong arguments assinged: " + ex.getCause());
            } catch (ExecutionException ex){
                log.error("Exception thrown while assigning rating to wine: "+ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground in AssignRatingToWineWorker was interrupted"+ex.getCause());
                throw new RuntimeException("Operation interrupted in AssignRatingToWineWorker");
            }
        }
        
    }
    
    private class RemoveWinesFromSessionWorker extends SwingWorker <int [], Void> {

        @Override
        protected int[] doInBackground() throws Exception {
            log.debug("Removing wines from session in background");
            int[] selectedRows = jTableWineSamples.getSelectedRows();
            List<Integer> toDeleteRows = new ArrayList<>();
            if (selectedRows.length >= 0) {
                for (int selectedRow : selectedRows) {
                    WineSample wineSample = wineSampleModel.getWineSample(selectedRow);
                    try {
                        wineTastingManager.removeWineFromSession(wineSession, wineSample);
                        toDeleteRows.add(selectedRow);
                    } catch (Exception ex) {
                        log.error("Error removing wine from session in background: " + ex.getCause());
                        throw new ServiceFailureException("error removing wine from session");
                    }
                }
                jTableWineSamples.getSelectionModel().clearSelection();
                jSlider1.setEnabled(false);
                jButton1.setEnabled(false);
                jButton2.setEnabled(false);
                return convert(toDeleteRows);
            }
            jTableWineSamples.getSelectionModel().clearSelection();
            jSlider1.setEnabled(false);
            jButton1.setEnabled(false);
            jButton2.setEnabled(false);
            return null;
        }

        @Override
        protected void done() {
            try {
                int[] indexes = get();
                log.debug("Removing wines from session");
                if (indexes != null && indexes.length != 0) {
                    wineSampleModel.deleteWineSamples(indexes);
                }
            } catch (ExecutionException ex) {
                log.error("Exception thrown while attempting to remove wines from session: "+ex.getCause());
                JOptionPane.showMessageDialog(rootPane, "cannot-remove-wine-sample-from-session");
            } catch (InterruptedException ex) {
                log.error("Method doInBackground in RemoveWinesFromSessionWorker was interrupted"+ex.getCause());
                throw new RuntimeException("Operation interrupted.. RemoveWinesFromSessionWorker");
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
    
    /**
     * Creates new form SessionDependentWines
     */
    public SessionDependentWines(MainWindow context, WineTastingSession wineTastingSession, int rowIndex) {
        initComponents();
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        wineSession = wineTastingSession;
        wineSampleModel = (WinesInSessionTableModel) jTableWineSamples.getModel();  
        
        findWineSamplesBySession = new FindWineSamplesBySessionWorker(wineSession);
        findWineSamplesBySession.execute();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane2 = new javax.swing.JScrollPane();
        jTableWineSamples = new javax.swing.JTable();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jSlider1 = new javax.swing.JSlider();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTableWineSamples.setModel(new WinesInSessionTableModel());
        jTableWineSamples.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableWineSamplesMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(jTableWineSamples);

        jButton1.setText("Remove selected");
        jButton1.setEnabled(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Rate selected wine");
        jButton2.setEnabled(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Add Wines");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jSlider1.setMajorTickSpacing(10);
        jSlider1.setMinorTickSpacing(1);
        jSlider1.setPaintLabels(true);
        jSlider1.setPaintTicks(true);
        jSlider1.setEnabled(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE))
                .addGap(38, 38, 38)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, 499, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 565, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jButton2)
                            .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3))
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jSlider1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jTableWineSamplesMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTableWineSamplesMouseReleased
        if (jTableWineSamples.getSelectedRowCount() != 1) {
            jButton2.setEnabled(false);
            jSlider1.setEnabled(false);
        } else {
            jButton2.setEnabled(true);
            jSlider1.setEnabled(true);
        }
        jButton1.setEnabled(true);
    }//GEN-LAST:event_jTableWineSamplesMouseReleased

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        AssignRatingToWineWorker worker = new AssignRatingToWineWorker();
        worker.execute();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        RemoveWinesFromSessionWorker worker = new RemoveWinesFromSessionWorker();
        worker.execute();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new SessionLessWines(SessionDependentWines.this, wineSession).setVisible(true);
            }
        });
    }//GEN-LAST:event_jButton3ActionPerformed

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSlider jSlider1;
    private javax.swing.JTable jTableWineSamples;
    // End of variables declaration//GEN-END:variables
}
