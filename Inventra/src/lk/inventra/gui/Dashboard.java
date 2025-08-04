/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package lk.inventra.gui;

import com.formdev.flatlaf.FlatLightLaf;
import java.awt.Component;
import java.awt.FlowLayout;
import java.io.InputStream;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.sql.Connection;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import lk.inventra.connection.MySQL;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author kithu
 */
public class Dashboard extends javax.swing.JFrame {

    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Dashboard.class.getName());

    /**
     * Creates new form Dashboard
     */
    public Dashboard() {
        initComponents();
        this.setVisible(true);
        dashboardData();
        userData();
        loadTotProducts();
        loadTotSales();
        productData();

        productTable.getSelectionModel().addListSelectionListener(e -> {
            int selectedRow = productTable.getSelectedRow();
            if (selectedRow != -1) {
                int id = Integer.parseInt(productTable.getValueAt(selectedRow, 0).toString());
                System.out.println("Selected ID: " + id);
            }
        });

        class ButtonRenderer extends JPanel implements TableCellRenderer {

            public ButtonRenderer() {
                setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
                add(new JButton("Edit"));
                add(new JButton("Delete"));
            }

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                return this;
            }
        }
    }

    public void dashboardData() {
        try {
            ResultSet rs = MySQL.executeSearch("SELECT * FROM `sale`");
            ResultSet rs1 = MySQL.executeSearch("SELECT * FROM `product`");

            DefaultTableModel dtm1 = new DefaultTableModel(
                    new String[]{"Status ID", "Product ID", "Total Amount", "Sale Date"}, 0
            );
            DefaultTableModel dtm2 = new DefaultTableModel(
                    new String[]{"Product ID", "Product Name", "Unit Price", "Added Date"}, 0
            );

            jTable1.setModel(dtm1);
            jTable2.setModel(dtm2);
            jTable3.setModel(dtm2);

            dtm1.setRowCount(0);
            dtm2.setRowCount(0);

            while (rs.next()) {
                Vector<String> v1 = new Vector<>();
                v1.add(rs.getString("id"));
                v1.add(rs.getString("product_id"));
                v1.add(rs.getString("total_amount"));
                v1.add(rs.getString("sale_datetime"));
                dtm1.addRow(v1);
            }

            while (rs1.next()) {
                Vector<String> v2 = new Vector<>();
                v2.add(rs1.getString("id"));
                v2.add(rs1.getString("name"));
                v2.add(rs1.getString("quantity_on_hand"));
                v2.add(rs1.getString("price"));
                v2.add(rs1.getString("added_date"));
                dtm2.addRow(v2);
            }
            
            logger.info("Dashboard Data Loaded");

        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            logger.severe("Dashboard Data Couldn't Be Loaded");
        }
    }

    public void userData() {
        try {
            ResultSet r3 = MySQL.executeSearch("SELECT * FROM `customer`");

            DefaultTableModel userModel = new DefaultTableModel(new String[]{"Customer ID", "Customer Name", "Customer Email", "Date Added", "Status ID"}, 0);
            jTable4.setModel(userModel);

            while (r3.next()) {
                Vector<String> v3 = new Vector<>();
                v3.add(r3.getString("id"));
                v3.add(r3.getString("name"));
                v3.add(r3.getString("email"));
                v3.add(r3.getString("date_added"));
                v3.add(r3.getString("status_id"));
                userModel.addRow(v3);
            }
            
            logger.info("User Data Loaded");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            logger.severe("User Data Couldn't Be Loaded");
        }
    }

    public void productData() {
        try {
            ResultSet r4 = MySQL.executeSearch("SELECT * FROM `product`");

            DefaultTableModel productModel = new DefaultTableModel(new String[]{"ID", "Name", "Quantity", "Price", "Date Added", "Status ID"}, 0);
            productTable.setModel(productModel);

            while (r4.next()) {
                Vector<String> v4 = new Vector<>();
                v4.add(r4.getString("id"));
                v4.add(r4.getString("name"));
                v4.add(r4.getString("quantity_on_hand"));
                v4.add(r4.getString("price"));
                v4.add(r4.getString("added_date"));
                v4.add(r4.getString("status_id"));
                productModel.addRow(v4);
            }
            logger.info("Product Data Loaded");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            logger.severe("Product Data Couldn't Be Loaded");
        }
    }

    public void loadTotProducts() {
        try {
            ResultSet totProducts = MySQL.executeSearch("SELECT COUNT(*) AS total FROM product");
            if (totProducts.next()) {
                int totalProducts = totProducts.getInt("total");
                jLabel4.setText(String.format("%,d", totalProducts));
            }
            logger.info("Total Product Data Couldn't Be Loaded");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            logger.severe("Total Product Data Couldn't Be Loaded");
        }
    }

    public void loadTotSales() {
        try {
            ResultSet totSales = MySQL.executeSearch("SELECT COUNT(*) AS total FROM sale");
            if (totSales.next()) {
                int totalSales = totSales.getInt("total");
                jLabel7.setText(String.format("%,d", totalSales));
            }
            logger.info("Total Product Data Loaded");
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            logger.severe("Total Sales Data Couldn't Be Loaded");
        }
    }

    public void exportStockReport() {
        try {
            InputStream fileStream = getClass().getResourceAsStream("/lk/inventra/reports/stocksinventra.jasper");

            if (fileStream == null) {
                JOptionPane.showMessageDialog(this, "Report file not found! Please check path and file name.");
                return;
            }

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(fileStream);

            Map<String, Object> parameters = new HashMap<>();

            Connection conn = lk.inventra.connection.MySQL.getConnection();

            JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, conn);
            JasperViewer.viewReport(print, false);
            
            logger.info("Stock inventory report successful");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage());
            logger.warning("Stock inventory report wasn't successful");
        }
    }

    public void exportSalesReport() {
        try {
            InputStream fileStream = getClass().getResourceAsStream("/lk/inventra/reports/salesinventra.jasper");

            if (fileStream == null) {
                JOptionPane.showMessageDialog(this, "Report file not found! Please check path and file name.");
                return;
            }

            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(fileStream);

            Map<String, Object> parameters = new HashMap<>();

            Connection conn = lk.inventra.connection.MySQL.getConnection();

            JasperPrint print = JasperFillManager.fillReport(jasperReport, parameters, conn);
            JasperViewer.viewReport(print, false);
            
            logger.info("Sale report successfully exported");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage());
            logger.info("Sales report successfully exported");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPopupMenu1 = new javax.swing.JPopupMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jPanel2 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel11 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton6 = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel5 = new javax.swing.JPanel();
        jPanel14 = new javax.swing.JPanel();
        jButton11 = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jButton9 = new javax.swing.JButton();
        jButton10 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jTextField1 = new javax.swing.JTextField();
        jButton12 = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        productTable = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jButton7 = new javax.swing.JButton();
        jPanel12 = new javax.swing.JPanel();
        jPanel13 = new javax.swing.JPanel();
        jTextField2 = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jButton13 = new javax.swing.JButton();
        jScrollPane4 = new javax.swing.JScrollPane();
        jTable4 = new javax.swing.JTable();

        jMenuItem1.setText("jMenuItem1");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jPopupMenu1.add(jMenuItem1);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("DASHBOARD | INVENTRA");

        jButton2.setBackground(new java.awt.Color(255, 0, 0));
        jButton2.setFont(new java.awt.Font("Segoe UI Variable", 1, 14)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("EXIT");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton1.setBackground(new java.awt.Color(255, 204, 0));
        jButton1.setFont(new java.awt.Font("Segoe UI Variable", 1, 12)); // NOI18N
        jButton1.setText("LOGOUT");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, 35, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.setForeground(new java.awt.Color(0, 0, 153));

        jPanel4.setLayout(new java.awt.GridLayout(1, 0, 2, 0));

        jPanel7.setBackground(new java.awt.Color(51, 204, 255));
        jPanel7.setForeground(new java.awt.Color(255, 255, 255));

        jLabel1.setFont(new java.awt.Font("Segoe UI Variable", 1, 18)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setText("Total Products:");

        jLabel4.setFont(new java.awt.Font("SimSun-ExtG", 1, 48)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addContainerGap(339, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 218, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(87, 87, 87))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(33, 33, 33)
                .addComponent(jLabel4)
                .addContainerGap(87, Short.MAX_VALUE))
        );

        jPanel4.add(jPanel7);

        jPanel9.setBackground(new java.awt.Color(51, 204, 255));
        jPanel9.setForeground(new java.awt.Color(255, 255, 255));
        jPanel9.setToolTipText("");

        jLabel3.setFont(new java.awt.Font("Segoe UI Variable", 1, 18)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Total Sales:");

        jLabel7.setFont(new java.awt.Font("SimSun-ExtB", 1, 48)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel9Layout.createSequentialGroup()
                .addContainerGap(165, Short.MAX_VALUE)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(137, 137, 137))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                .addGap(16, 16, 16))
        );

        jPanel4.add(jPanel9);

        jPanel11.setLayout(new java.awt.GridLayout(1, 0, 10, 0));

        jTable1.setFont(new java.awt.Font("Segoe UI Variable", 1, 12)); // NOI18N
        jTable1.setForeground(new java.awt.Color(0, 102, 204));
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jPanel11.add(jScrollPane1);

        jTable2.setFont(new java.awt.Font("Segoe UI Variable", 1, 12)); // NOI18N
        jTable2.setForeground(new java.awt.Color(0, 102, 204));
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane2.setViewportView(jTable2);

        jPanel11.add(jScrollPane2);

        jButton4.setBackground(new java.awt.Color(0, 153, 255));
        jButton4.setFont(new java.awt.Font("Segoe UI Variable", 1, 14)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Add Product");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(255, 153, 51));
        jButton5.setFont(new java.awt.Font("Segoe UI Variable", 1, 14)); // NOI18N
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("EXPORT SALES REPORT");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton6.setBackground(new java.awt.Color(255, 153, 51));
        jButton6.setFont(new java.awt.Font("Segoe UI Variable", 1, 14)); // NOI18N
        jButton6.setForeground(new java.awt.Color(255, 255, 255));
        jButton6.setText("EXPORT STOCK INFORMATION REPORT");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI Variable", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(0, 153, 255));
        jLabel8.setText("Sales Table :");

        jLabel11.setFont(new java.awt.Font("Segoe UI Variable", 1, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(0, 153, 255));
        jLabel11.setText("Stock Table :");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 318, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(185, 185, 185)
                                .addComponent(jButton4)))
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel11)
                        .addGap(390, 390, 390))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jLabel11))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, 415, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton6, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Dashboard", jPanel1);

        jTabbedPane2.setForeground(new java.awt.Color(0, 0, 153));
        jTabbedPane2.setTabPlacement(javax.swing.JTabbedPane.LEFT);

        jButton11.setBackground(new java.awt.Color(255, 51, 51));
        jButton11.setFont(new java.awt.Font("Segoe UI Variable", 1, 14)); // NOI18N
        jButton11.setForeground(new java.awt.Color(255, 255, 255));
        jButton11.setText("Delete Product");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jLabel9.setBackground(new java.awt.Color(255, 255, 255));
        jLabel9.setFont(new java.awt.Font("Segoe UI Variable", 1, 18)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(0, 102, 204));
        jLabel9.setText("PRODUCT INFORMATION");

        jButton9.setBackground(new java.awt.Color(0, 153, 255));
        jButton9.setFont(new java.awt.Font("Segoe UI Variable", 1, 14)); // NOI18N
        jButton9.setForeground(new java.awt.Color(255, 255, 255));
        jButton9.setText("Add Product");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        jButton10.setBackground(new java.awt.Color(0, 153, 255));
        jButton10.setFont(new java.awt.Font("Segoe UI Variable", 1, 14)); // NOI18N
        jButton10.setForeground(new java.awt.Color(255, 255, 255));
        jButton10.setText("Edit Product");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton8.setBackground(new java.awt.Color(255, 153, 51));
        jButton8.setFont(new java.awt.Font("Segoe UI Variable", 1, 14)); // NOI18N
        jButton8.setForeground(new java.awt.Color(255, 255, 255));
        jButton8.setText("EXPORT STOCK REPORT");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton12.setBackground(new java.awt.Color(0, 153, 255));
        jButton12.setFont(new java.awt.Font("Segoe UI Variable", 1, 14)); // NOI18N
        jButton12.setForeground(new java.awt.Color(255, 255, 255));
        jButton12.setText("SEARCH PRODUCT");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        productTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        scrollPane.setViewportView(productTable);

        javax.swing.GroupLayout jPanel14Layout = new javax.swing.GroupLayout(jPanel14);
        jPanel14.setLayout(jPanel14Layout);
        jPanel14Layout.setHorizontalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addComponent(jTextField1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jPanel14Layout.createSequentialGroup()
                        .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jLabel9)
                        .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 818, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(0, 13, Short.MAX_VALUE))
        );
        jPanel14Layout.setVerticalGroup(
            jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel14Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(jPanel14Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton8, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton10, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton11, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(67, 67, 67))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Product Panel", jPanel5);

        jLabel5.setBackground(new java.awt.Color(255, 255, 255));
        jLabel5.setFont(new java.awt.Font("Segoe UI Variable", 1, 18)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(0, 102, 204));
        jLabel5.setText("SALES INFORMATION");

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane3.setViewportView(jTable3);

        jButton7.setBackground(new java.awt.Color(255, 153, 51));
        jButton7.setFont(new java.awt.Font("Segoe UI Variable", 1, 14)); // NOI18N
        jButton7.setForeground(new java.awt.Color(255, 255, 255));
        jButton7.setText("EXPORT SALES REPORT");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 806, Short.MAX_VALUE)
                    .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(16, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 580, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton7, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jTabbedPane2.addTab("Sales Panel", jPanel6);

        jTextField2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField2ActionPerformed(evt);
            }
        });

        jLabel10.setBackground(new java.awt.Color(255, 255, 255));
        jLabel10.setFont(new java.awt.Font("Segoe UI Variable", 1, 18)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(0, 102, 204));
        jLabel10.setText("CUSTOMER INFORMATION");

        jButton13.setBackground(new java.awt.Color(0, 153, 255));
        jButton13.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jButton13.setForeground(new java.awt.Color(255, 255, 255));
        jButton13.setText("SEARCH USER");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        jTable4.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {},
                {},
                {},
                {}
            },
            new String [] {

            }
        ));
        jScrollPane4.setViewportView(jTable4);

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel13Layout.createSequentialGroup()
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 664, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jLabel10)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 817, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(138, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton13, javax.swing.GroupLayout.PREFERRED_SIZE, 44, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 593, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel12Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(24, 24, 24))
        );

        jTabbedPane2.addTab("Customer Panel", jPanel12);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 941, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 676, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Inventory Management", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 950, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        var exitdialog = JOptionPane.showConfirmDialog(this, "Are you sure you want to exit?",
                "Exit Confirmation",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (exitdialog == JOptionPane.OK_OPTION) {
            System.exit(0);
            logger.info("User cancelled exit");
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        try {
            String name = jTextField2.getText().trim();
            String query = "SELECT * FROM customer WHERE name LIKE '%" + name + "%'";

            ResultSet rs = MySQL.executeSearch(query);

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"Customer ID", "Customer Name", "Customer Email", "Date Added", "Status ID"}, 0
            );

            jTable4.setModel(model);
            model.setRowCount(0);

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(String.valueOf(rs.getInt("id")));
                row.add(rs.getString("name"));
                row.add(rs.getString("email"));
                row.add(rs.getString("date_added"));
                row.add(rs.getString("status_id"));
                model.addRow(row);
            }
            
            logger.info("User searched for: " + name);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        exportStockReport();
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1) {
            int productId = Integer.parseInt(productTable.getValueAt(selectedRow, 0).toString());
            editProduct productedit = new editProduct(new javax.swing.JFrame(), true, productId);
            productedit.setVisible(true);
        }

    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        addProduct productadd = new addProduct(this, true);
        productadd.setVisible(true);
        logger.info("User adding new product");
    }//GEN-LAST:event_jButton9ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        addProduct productadd = new addProduct(this, true);
        productadd.setVisible(true);
        logger.info("User adding new product");
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jTextField2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField2ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        exportStockReport();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        exportSalesReport();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        exportSalesReport();
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        try {
            String name = jTextField1.getText().trim();
            String query = "SELECT * FROM product WHERE name LIKE '%" + name + "%'";

            ResultSet rs = MySQL.executeSearch(query);

            DefaultTableModel model = new DefaultTableModel(
                    new String[]{"ID", "Name", "Quantity", "Price", "Date Added", "Status ID"}, 0
            );

            productTable.setModel(model);
            model.setRowCount(0);

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(String.valueOf(rs.getInt("id")));
                row.add(rs.getString("name"));
                row.add(rs.getString("quantity_on_hand"));
                row.add(rs.getString("price"));
                row.add(rs.getString("added_date"));
                row.add(rs.getString("status_id"));
                model.addRow(row);
            }
            logger.info("User searched for: " + name);
        } catch (java.sql.SQLException e) {
            e.printStackTrace();
            logger.warning("Searched query failed");
        }

    }//GEN-LAST:event_jButton12ActionPerformed

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        int selectedRow = productTable.getSelectedRow();
        if (selectedRow != -1) {
            try {
                int id = Integer.parseInt(productTable.getValueAt(selectedRow, 0).toString());

                // Show confirmation
                int choice = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete ID " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    // Delete from DB
                    lk.inventra.connection.MySQL.executeIUD("DELETE FROM product WHERE id = " + id);

                    // Remove row from table (optional but makes it feel instant)
                    DefaultTableModel model = (DefaultTableModel) productTable.getModel();
                    model.removeRow(selectedRow);

                    JOptionPane.showMessageDialog(this, "Product deleted successfully!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid ID format.", "Error", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "Something went wrong!", "Error", JOptionPane.ERROR_MESSAGE);
            }
            
            logger.info("User successfully deleted a product");
        } else {
            JOptionPane.showMessageDialog(this, "Please select a row first.");
            logger.info("User tried to delete a product");
        }
    }//GEN-LAST:event_jButton11ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to log out?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        logger.info("User logged out");
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();


            Login login = new Login();
            login.setVisible(true);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        FlatLightLaf.setup();
        java.awt.EventQueue.invokeLater(() -> new Dashboard().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JPopupMenu jPopupMenu1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTable jTable4;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTable productTable;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
