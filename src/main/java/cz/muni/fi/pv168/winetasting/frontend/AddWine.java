/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.muni.fi.pv168.winetasting.frontend;

import cz.muni.fi.pv168.winetasting.backend.WineCharacter;
import cz.muni.fi.pv168.winetasting.backend.WineColor;
import cz.muni.fi.pv168.winetasting.backend.WineSample;
import cz.muni.fi.pv168.winetasting.backend.WineSampleDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class AddWine extends javax.swing.JFrame {

    final static Logger log = LoggerFactory.getLogger(AddWine.class);


    private static WineSampleDAO wineSampleDAO = CommonResources.getWineSampleDAO();
    private DefaultComboBoxModel wineSampleCharacterComboBoxModel = new DefaultComboBoxModel<>(WineCharacter.values());
    private DefaultComboBoxModel wineSampleColorComboBoxModel = new DefaultComboBoxModel<>(WineColor.values());
    private DefaultComboBoxModel wineSampleYearComboBoxModel = new DefaultComboBoxModel<>(years());
    private MainWindow context;
    private WineSampleTableModel wineSampleModel;
    private WineSample wineSample;
    private String action;
    private int rowIndex;
    
    /**
     * Creates new form AddWine
     */
    public AddWine(MainWindow context, WineSample wineSample, int rowIndex, String action) {
        initComponents();
       
        jComboBox1.setModel(wineSampleCharacterComboBoxModel);
        jComboBox2.setModel(wineSampleColorComboBoxModel);
        jComboBox3.setModel(wineSampleYearComboBoxModel);
        
        this.context = context;
        this.wineSample = wineSample;
        this.rowIndex = rowIndex;
        this.action = action;
        this.wineSampleModel = context.getWineSampleModel();
        jButton1.setText(action);
        
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        if (wineSample != null) {
            jTextField1.setText(wineSample.getVintnerFirstName());
            jTextField2.setText(wineSample.getVintnerLastName());
            jTextField3.setText(wineSample.getVariety());
            jComboBox1.setSelectedItem(wineSample.getCharacter());
            jComboBox2.setSelectedItem(wineSample.getColor());
            jComboBox3.setSelectedItem(wineSample.getYear());
        }
        this.setVisible(true);
    }
    
    private class AddWineWorker extends SwingWorker<WineSample, Integer> {

        @Override
        protected WineSample doInBackground() throws Exception {
            log.debug("Creating new sample in background");
            WineSample wine = getWineSampleFromForm();
            if (wine == null) {
                log.error("wrong data enterd to new wine sample");
                throw new IllegalArgumentException("wrong-enter-data");
            }
            wineSampleDAO.createWineSample(wine);
            return wine;
        }

        @Override
        protected void done() {
            try {
                WineSample wine = get();
                wineSampleModel.addWineSample(wine);
                log.debug("Modifing wineSampleModel - adding new wine:" + wine);
                AddWine.this.dispose();
            } catch (IllegalArgumentException ex) {
                warningMessageBox(ex.getMessage());
                return;
            } catch (ExecutionException ex) {
                log.error("Exception thrown during adding new wine: "+ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground in AddWineWorker was interrupted:" +ex.getCause());
                throw new RuntimeException("Operation interrupted in creating new wine sample");
            }
        }
        
    }
    
    private class UpdateWineWorker extends SwingWorker<WineSample, Integer> {

        @Override
        protected WineSample doInBackground() throws Exception {
            log.debug("Updating wine sample in background");
            WineSample wine = getWineSampleFromForm();
            if (wine == null) {
                log.error("Wrong data entered to updated wine sample");
                throw new IllegalArgumentException("wrong-enter-data");
            }
            wineSampleDAO.updateWineSample(wine);
            return wine;
        }
        
        @Override
        protected void done() {
            try {
                WineSample wine = get();
                wineSampleModel.updateWineSample(wine, rowIndex);
                log.debug("Modifing wineSampleModel - updating wine: " + wine);
                context.getjTableWineSamples().getSelectionModel().clearSelection();
                context.getWineSampleUpdateButton().setEnabled(false);
                context.getWineSampleDeleteButton().setEnabled(false);
                AddWine.this.dispose();
            } catch (IllegalArgumentException ex) {
                log.error("Wrong data to be updated: " + ex.getCause());
            } catch (ExecutionException ex){
                log.error("Exception thrown while updating wine: " + ex.getCause());
            } catch (InterruptedException ex) {
                log.error("Method doInBackground in UpdateWineWorker was interrupted:" + ex.getCause());
                throw new RuntimeException("Operation interrupted in updating wine sample");
            }
        }
        
    }
    
    private WineSample getWineSampleFromForm() {
        
        String firstName = jTextField1.getText();
        if (firstName == null || firstName.length() == 0) {
            warningMessageBox("fill first name");
            return null;
        }
        
        String lastName = jTextField2.getText();
        if (lastName == null || lastName.length() == 0) {
            warningMessageBox("fill last name");
            return null;
        }
        
        String variety = jTextField3.getText();
        if (variety == null || variety.length() == 0) {
            warningMessageBox("fill variety");
            return null;
        }
        WineCharacter character = (WineCharacter)jComboBox1.getSelectedItem();
        WineColor color = (WineColor)jComboBox2.getSelectedItem();
        int year = (Integer)jComboBox3.getSelectedItem();
        
        if (wineSample == null) {
            wineSample = new WineSample();
        }
        
        wineSample.setVintnerFirstName(firstName);
        wineSample.setVintnerLastName(lastName);
        wineSample.setVariety(variety);
        wineSample.setCharacter(character);
        wineSample.setColor(color);
        wineSample.setYear(year);
        return wineSample;        
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
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jTextField2 = new javax.swing.JTextField();
        jTextField3 = new javax.swing.JTextField();
        jComboBox1 = new javax.swing.JComboBox<String>();
        jComboBox2 = new javax.swing.JComboBox<String>();
        jComboBox3 = new javax.swing.JComboBox<String>();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("texts"); // NOI18N
        jLabel1.setText(bundle.getString("Name")); // NOI18N

        jLabel2.setText(bundle.getString("Surname")); // NOI18N

        jLabel3.setText(bundle.getString("Variety")); // NOI18N

        jLabel4.setText(bundle.getString("Color")); // NOI18N

        jLabel5.setText(bundle.getString("Charakter")); // NOI18N

        jLabel6.setText(bundle.getString("Year")); // NOI18N

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jComboBox3.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6))
                        .addGap(32, 32, 32)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextField3, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                            .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBox3, javax.swing.GroupLayout.Alignment.TRAILING, 0, 142, Short.MAX_VALUE)
                                    .addComponent(jComboBox2, javax.swing.GroupLayout.Alignment.TRAILING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(30, 30, 30)
                        .addComponent(jTextField2))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(54, 54, 54)
                        .addComponent(jTextField1)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(jComboBox3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if (action.equals("add")) {
            AddWineWorker worker = new AddWineWorker();
            worker.execute();
        } else if (action.equals("update")) {
            UpdateWineWorker worker = new UpdateWineWorker();
            worker.execute();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBox1ActionPerformed

   

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
    private javax.swing.JLabel jLabel6;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    // End of variables declaration//GEN-END:variables
}
