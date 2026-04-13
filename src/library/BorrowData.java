package library;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.Date;
import javax.swing.JTable;

public class BorrowData extends javax.swing.JFrame implements Manageable {
    
//Reference variable "connect" yang memuat objek Connection digunakan untuk
//membuat koneksi ke database. Variable ini ada di semua class yang membutuhkan
//koneksi database
Connection connect = Connector.koneksiDb();

    //Instance variable nameIdMap dibuat dari java.util.Map
    //digunakan untuk menyimpan data dengan format key-value.
    private Map<String, String> nameIdMap;
    
    public BorrowData(JTable jTable1, Connection connect) {
        this.jTable1 = jTable1;
        this.connect = connect;
//        initComponents();
//        jButton2.setEnabled(false);
//        jButton4.setEnabled(false);
//        showData();
//        nameIdMap = new HashMap<>();
//        dataComboBox1();
//        dataComboBox2();
    }
    
    //Menambah data judul buku/nama member ke jComboBox. Data yang ditampilkan
    //adalah judul buku/nama member tetapi data yang disimpan ke database adalah
    //"value" dari nameIdMap yaitu ID dari buku/member tersebut
    public void dataComboBox1() {
        try {
            Statement stat = connect.createStatement();
            ResultSet data = stat.executeQuery("SELECT id_book, title FROM books");
            while (data.next()) {
                String id_book = data.getString("id_book");
                String title = data.getString("title");
                jComboBox1.addItem(title);
                nameIdMap.put(title, id_book);
            }
        } catch (SQLException e) {
            System.out.println("There's an error: " + e);
        }
    }
    
    public void dataComboBox2() {
        try {
            Statement stat = connect.createStatement();
            ResultSet data = stat.executeQuery("SELECT id_member, name FROM member");
            while (data.next()) {
                String id_member = data.getString("id_member");
                String name = data.getString("name");
                jComboBox2.addItem(name);
                nameIdMap.put(name, id_member);
            }
        } catch (SQLException e) {
            System.out.println("There's an error: " + e);
        }
    }
    
    @Override
    public void clear() {
        jTextField1.setText("");
        jComboBox1.setSelectedItem(0);
        jComboBox2.setSelectedItem(0);
        jDateChooser1.setDate(null);
        jDateChooser2.setDate(null);
        jButton2.setEnabled(false);
        jButton4.setEnabled(false);
        jButton1.setEnabled(true);
        jButton1.requestFocus();
        jButton2.requestFocus();
        jButton4.requestFocus();
        jTextField1.setEnabled(true);
        jTextField1.requestFocus();
        jTextField4.setText("");
        jCheckBox1.setSelected(false);
        showData();
    }
    
    //Menampilkan data Borrow dari database. Jika checkbox onlyOverdue true,
    //maka akan menampilkan data yang melewati tanggal pengembalian buku.
    @Override
    public void showData() {
        showData(false);
    }
    
    public void showData(boolean onlyOverdue) {

        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Book");
        model.addColumn("Borrowed By");
        model.addColumn("Borrow Date");
        model.addColumn("Return Date");
        model.addColumn("Overdue");

        try {
            //Reference variable "stat" dari java.sql.Statement yang digunakan
            //untuk memanggil method createStatement untuk membuat query SQL
            Statement stat = connect.createStatement();
            ResultSet data = stat.executeQuery("SELECT borrow.id_borrow, books.title, member.name,"
                    + " borrow.borrow_date, borrow.return_date"
                    + " FROM borrow"
                    + " INNER JOIN books ON borrow.id_book = books.id_book"
                    + " INNER JOIN member ON borrow.id_member = member.id_member");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDate = new Date();

            while (data.next()) {
                Date borrowDate = data.getDate("borrow.borrow_date");
                Date returnDate = data.getDate("borrow.return_date");
                boolean isOverdue = returnDate.before(currentDate);

                if (!onlyOverdue || isOverdue) {
                    model.addRow(new Object[]{
                        data.getString("borrow.id_borrow"),
                        data.getString("books.title"),
                        data.getString("member.name"),
                        sdf.format(borrowDate),
                        sdf.format(returnDate),
                        isOverdue ? "Yes" : "No"
                    });
                }

                jTable1.setModel(model);
            }
        } catch (SQLException e) {
            System.out.println("There's an error: " + e);
        }
    }
    
    
    @Override
    public void add() {
        try {
            Statement stat = connect.createStatement();
            ResultSet data = stat.executeQuery("SELECT * FROM borrow WHERE id_borrow='" + jTextField1.getText() + "'");
            if (data.next()) {
                jTextField1.requestFocus();
                JOptionPane.showMessageDialog(null, "Borrow ID already exists.", "WARNING", JOptionPane.WARNING_MESSAGE);
                jTextField1.requestFocus();
            } else if (jTextField1.getText().equals("") || jComboBox1.getSelectedItem() == null
                    || jComboBox2.getSelectedItem() == null || jDateChooser1.getDate() == null || jDateChooser2.getDate() == null)
            {
                JOptionPane.showMessageDialog(null, "Data can't be empty!", "WARNING", JOptionPane.WARNING_MESSAGE);
            } else {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String borrowDate = format.format(jDateChooser1.getDate());
                String returnDate = format.format(jDateChooser2.getDate());
                String bookId = nameIdMap.get(jComboBox1.getSelectedItem().toString());
                String memberId = nameIdMap.get(jComboBox2.getSelectedItem().toString());
                String sql = "INSERT INTO borrow VALUES ('" + jTextField1.getText() + "'"
                        + ",'" + bookId + "'"
                        + ",'" + memberId + "'"
                        + ",'" + borrowDate + "'"
                        + ",'" + returnDate + "')";
                stat.executeUpdate(sql);
                
                clear();
                
                JOptionPane.showMessageDialog(null, "Borrowed successfully.", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                showData();
            }
            stat.close();
        } catch (SQLException exc) {
            System.err.println("There's an error: " + exc);
        }
        
    }
    
    @Override
    public void select() {
        int row = jTable1.getSelectedRow();
        if (row != -1) {
            jTextField1.setText(jTable1.getValueAt(row, 0).toString());
            jComboBox1.setSelectedItem(jTable1.getValueAt(row, 1).toString());
            jComboBox2.setSelectedItem(jTable1.getValueAt(row, 2).toString());
            //jDateChooser1.setDate(jTable1.getValueAt(row, 3).toString());
            //jDateChooser2.setDate(jTable1.getValueAt(row, 4).toString());
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            try{
            java.util.Date borrowDate = format.parse(jTable1.getValueAt(row, 3).toString());
            java.util.Date returnDate = format.parse(jTable1.getValueAt(row, 4).toString());
            
            jDateChooser1.setDate(borrowDate);
            jDateChooser2.setDate(returnDate);
            }catch(ParseException e){
                System.err.println("Error parsing date: " + e.getMessage());
            }
        }
        jTextField1.setEnabled(false);
        jButton2.setEnabled(true);
        jButton4.setEnabled(true);
        jButton1.setEnabled(false);
    }
    
    @Override
    public void update() {
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to update this data?", "NOTICE", JOptionPane.YES_NO_OPTION);
        if (confirm == 0) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                String borrowDate = format.format(jDateChooser1.getDate());
                String returnDate = format.format(jDateChooser2.getDate());
                String bookId = nameIdMap.get(jComboBox1.getSelectedItem().toString());
                String memberId = nameIdMap.get(jComboBox2.getSelectedItem().toString());
                String sql = "Update borrow SET id_book = '" + bookId + "', id_member = '" + memberId
                        + "', borrow_date = '" + borrowDate + "', return_date = '" + returnDate
                        + "' WHERE id_borrow='" + jTextField1.getText().trim() + "'";

                Statement stat = connect.createStatement();
                stat.executeUpdate(sql);
                JOptionPane.showMessageDialog(null, "Data successfully updated!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                stat.close();

                clear();
                showData();

            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "There's an error: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void delete() {
        int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this book?", "NOTICE", JOptionPane.YES_NO_OPTION);
        if (confirm == 0) {
            //query sql untuk delete data
            String sql = "Delete FROM borrow WHERE id_borrow = '" + jTextField1.getText().trim() + "'";
            try {
                Statement stat = connect.createStatement();
                stat.executeUpdate(sql);
                JOptionPane.showMessageDialog(null, "Data successfully deleted!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
                stat.close();

                clear();
                showData();

            } catch (SQLException exc) {
                System.err.println(sql);
                System.err.println("Error :" + exc);
            }
        }
    }
    
    @Override
    public void search() {
        //Reference variable "model" dari javax.swing.table.defaulttablemodel
        //yang digunakan untuk membuat model tabel untuk jTable1
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Book");
        model.addColumn("Borrowed By");
        model.addColumn("Borrow Date");
        model.addColumn("Return Date");

        try {
            //Reference variable "search" dari class Searcher untuk memanggil
            //getter dari variable "searchinput" di class Searcher
            Searcher search = new Searcher(jTextField4.getText()) {};
            //Reference variable "stat" dari java.sql.Statement yang digunakan
            //untuk memanggil method createStatement untuk membuat query SQL
            Statement stat = connect.createStatement();
            //Reference variable "data" dari java.sql.ResultSet untuk memanggil
            //method "executeQuery" digunakan untuk menghasilkan hasil query
            //yang dibuat oleh "stat"
            ResultSet data = stat.executeQuery("SELECT borrow.id_borrow, books.title, member.name, borrow.borrow_date, borrow.return_date FROM borrow"
                    + " INNER JOIN books where borrow.id_book = books.id_book"
                    + " INNER JOIN member where borrow.id_member = member.id_member"
                    + " WHERE borrow.id_borrow LIKE '%" + jTextField4.getText() + "%' OR books.title LIKE '%" + jTextField4.getText() + "%' OR member.name LIKE '%" + jTextField4.getText() + "%'");
            while (data.next()) {
                model.addRow(new Object[]{
                    data.getString("borrow.id_borrow"),
                    data.getString("books.title"),
                    data.getString("member.name"),
                    data.getString("borrow.borrow_date"),
                    data.getString("borrow.return_date"),});
                jTable1.setModel(model);
            }
        } catch (SQLException e) {
            System.err.println("There's an error: " + e);
        }
        
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jComboBox2 = new javax.swing.JComboBox<>();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel5 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jTextField4 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jCheckBox1 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));

        jButton1.setText("Add");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Update");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Go Back");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Delete");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setText("Clear");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                    .addComponent(jButton1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton5)
                    .addComponent(jButton4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addContainerGap())
        );

        jLabel1.setText("ID");

        jLabel2.setText("Book");

        jLabel3.setText("Member");

        jLabel4.setText("Borrow Date");

        jDateChooser1.setDateFormatString("yyyy-MM-dd");

        jLabel5.setText("Return Date");

        jDateChooser2.setDateFormatString("yyyy-MM-dd");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField1)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Book", "Member", "Borrow Date", "Return Date", "Overdue"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel3.setBackground(new java.awt.Color(255, 255, 255));

        jButton6.setText("Search");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jCheckBox1.setText("Overdue");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField4)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jCheckBox1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton6)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTextField4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton6)
                    .addComponent(jCheckBox1))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        MainTable go = new MainTable();
        go.show();
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        select();
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        add();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        clear();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        update();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        delete();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        search();
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
        boolean isChecked = jCheckBox1.isSelected();
        showData(isChecked);
    }//GEN-LAST:event_jCheckBox1ActionPerformed

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
            java.util.logging.Logger.getLogger(BorrowData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(BorrowData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(BorrowData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(BorrowData.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                Connection connect = Connector.koneksiDb();
                javax.swing.JTable jTable1 = new javax.swing.JTable();
                BorrowData borrowData = new BorrowData(jTable1, connect);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField4;
    // End of variables declaration//GEN-END:variables
}
