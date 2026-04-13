package library;

import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;

public class Form extends javax.swing.JFrame {
Connection connect = Connector.koneksiDb();
    private boolean currentlyMember;
    
    public Form() {
        initComponents();
        jButton2.setEnabled(false);
        jButton4.setEnabled(false);
        if(currentlyMember=true){
            Member member = new Member();
            member.clear();
            member.showData();
            jTextField5.setVisible(true);
            jComboBox1.setVisible(false);
        }else{
            Book book = new Book();
            book.clear();
            book.showData();
            jTextField5.setVisible(false);
            jComboBox1.setVisible(true);
        }
    }
    
    public class Book implements Manageable {
    @Override
    public void clear() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jComboBox1.setSelectedItem("");
        jTextField5.setText("");
        jTextField1.setEnabled(true);
        jButton2.setEnabled(false);
        jButton4.setEnabled(false);
        jButton1.setEnabled(true);
        jComboBox1.setSelectedIndex(0);
        jButton1.requestFocus();
        jButton2.requestFocus();
        jButton4.requestFocus();
        jTextField1.requestFocus();
        jTextField4.setText("");
        showData();
        jLabel2.setText("Title");
        jLabel3.setText("Author");
        jLabel4.setText("Genre");
        if (currentlyMember = true) {
            Member member = new Member();
            member.showData();
            jTextField5.setVisible(true);
            jComboBox1.setVisible(false);
        } else {
            Book book = new Book();
            book.showData();
            jTextField5.setVisible(false);
            jComboBox1.setVisible(true);
        }
    }
    
    //private void clearData() {

    //}
    
    @Override
    public void showData() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Title");
        model.addColumn("Author");
        model.addColumn("Genre");
        
        try{
            Statement stat = connect.createStatement();
            ResultSet data = stat.executeQuery("SELECT * FROM books");
            while(data.next()){
                model.addRow(new Object[]{
                    data.getString("id_book"),
                    data.getString("title"),
                    data.getString("author"),
                    data.getString("genre"),
                });
                jTable1.setModel(model);
            }
        } catch (SQLException e){
            System.out.println("There's an error: " + e);
        }
    }
    
    @Override
    public void add() {
        try {
            Statement stat = connect.createStatement();
            ResultSet data = stat.executeQuery("SELECT * FROM books WHERE id_book='" + jTextField1.getText() + "'");
            if (data.next()) {
                jTextField1.requestFocus();
                JOptionPane.showMessageDialog(null, "Book ID already exists.", "WARNING", JOptionPane.WARNING_MESSAGE);
                jTextField1.requestFocus();
            } else if (jTextField1.getText().equals("") || jTextField2.getText().equals("")
                    || jTextField3.getText().equals("") || jComboBox1.getSelectedItem().equals(""))
            {
                JOptionPane.showMessageDialog(null, "Data can't be empty!", "WARNING", JOptionPane.WARNING_MESSAGE);
            } else {
                String sql = "INSERT INTO books VALUES ('" + jTextField1.getText() + "'"
                        + ",'" + jTextField2.getText() + "'"
                        + ",'" + jTextField3.getText() + "'"
                        + ",'" + jComboBox1.getSelectedItem() + "')";
                stat.executeUpdate(sql);
                
                clear();
                
                JOptionPane.showMessageDialog(null, "Book successfully added.", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
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
            jTextField2.setText(jTable1.getValueAt(row, 1).toString());
            jTextField3.setText(jTable1.getValueAt(row, 2).toString());
            jComboBox1.setSelectedItem(jTable1.getValueAt(row, 3).toString());
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
                //query sql untuk update data
                String sql = "Update books SET title = '" + jTextField2.getText() + "', author = '" + jTextField3.getText()
                        + "', genre = '" + jComboBox1.getSelectedItem() + "' WHERE id_book='" + jTextField1.getText().trim() + "'";

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
            String sql = "Delete FROM books WHERE id_book = '" + jTextField1.getText().trim() + "'";
            try {
                Statement stat = connect.createStatement();
                stat.executeUpdate(sql);
                JOptionPane.showMessageDialog(null, "Book successfully deleted!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
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
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Title");
        model.addColumn("Author");
        model.addColumn("Genre");

        try {
            Statement stat = connect.createStatement();
            ResultSet data = stat.executeQuery("SELECT * FROM books WHERE id_book LIKE '%" + jTextField4.getText() + "%' OR title LIKE '%" + jTextField4.getText() + "%' OR author LIKE '%" + jTextField4.getText() + "%' OR genre LIKE '%" + jTextField4.getText() + "%'");
            while (data.next()) {
                model.addRow(new Object[]{
                    data.getString("id_book"),
                    data.getString("title"),
                    data.getString("author"),
                    data.getString("genre"),});
                jTable1.setModel(model);
            }
        } catch (SQLException e) {
            System.err.println("Terjadi Kesalahan :" + e);
        }
        
    }
    }
    
    public class Member implements Manageable {
    @Override
    public void clear() {
        jTextField1.setText("");
        jTextField2.setText("");
        jTextField3.setText("");
        jComboBox1.setSelectedItem("");
        jTextField5.setText("");
        jTextField1.setEnabled(true);
        jButton2.setEnabled(false);
        jButton4.setEnabled(false);
        jButton1.setEnabled(true);
        jComboBox1.setSelectedIndex(0);
        jButton1.requestFocus();
        jButton2.requestFocus();
        jButton4.requestFocus();
        jTextField1.requestFocus();
        jTextField4.setText("");
        showData();
        jLabel2.setText("Name");
        jLabel3.setText("Phone Number");
        jLabel4.setText("Address");
        if (currentlyMember = true) {
            Member member = new Member();
            member.showData();
            jTextField5.setVisible(true);
            jComboBox1.setVisible(false);
        } else {
            Book book = new Book();
            book.showData();
            jTextField5.setVisible(false);
            jComboBox1.setVisible(true);
        }
    }
    
    //private void clearData() {

    //}
    
    @Override
    public void showData() {
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Phone Number");
        model.addColumn("Address");
        
        try{
            Statement stat = connect.createStatement();
            ResultSet data = stat.executeQuery("SELECT * FROM member");
            while(data.next()){
                model.addRow(new Object[]{
                    data.getString("id_member"),
                    data.getString("name"),
                    data.getString("phone_number"),
                    data.getString("address"),
                });
                jTable1.setModel(model);
            }
        } catch (SQLException e){
            System.out.println("There's an error: " + e);
        }
    }
    
    @Override
    public void add() {
        try {
            Statement stat = connect.createStatement();
            ResultSet data = stat.executeQuery("SELECT * FROM member WHERE id_member='" + jTextField1.getText() + "'");
            if (data.next()) {
                jTextField1.requestFocus();
                JOptionPane.showMessageDialog(null, "Member ID already exists.", "WARNING", JOptionPane.WARNING_MESSAGE);
                jTextField1.requestFocus();
            } else if (jTextField1.getText().equals("") || jTextField2.getText().equals("")
                    || jTextField3.getText().equals("") || jTextField5.getText().equals(""))
            {
                JOptionPane.showMessageDialog(null, "Data can't be empty!", "WARNING", JOptionPane.WARNING_MESSAGE);
            } else {
                String sql = "INSERT INTO member VALUES ('" + jTextField1.getText() + "'"
                        + ",'" + jTextField2.getText() + "'"
                        + ",'" + jTextField3.getText() + "'"
                        + ",'" + jTextField5.getText() + "')";
                stat.executeUpdate(sql);
                
                clear();
                
                JOptionPane.showMessageDialog(null, "Member successfully added.", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
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
            jTextField2.setText(jTable1.getValueAt(row, 1).toString());
            jTextField3.setText(jTable1.getValueAt(row, 2).toString());
            jTextField5.setText(jTable1.getValueAt(row, 3).toString());
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
                //query sql untuk update data
                String sql = "Update member SET name = '" + jTextField2.getText() + "', phone_number = '" + jTextField3.getText()
                        + "', address = '" + jTextField5.getText() + "' WHERE id_member='" + jTextField1.getText().trim() + "'";

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
            String sql = "Delete FROM member WHERE id_member = '" + jTextField1.getText().trim() + "'";
            try {
                Statement stat = connect.createStatement();
                stat.executeUpdate(sql);
                JOptionPane.showMessageDialog(null, "Member successfully deleted!", "SUCCESS", JOptionPane.INFORMATION_MESSAGE);
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
        DefaultTableModel model = new DefaultTableModel();
        model.addColumn("ID");
        model.addColumn("Name");
        model.addColumn("Phone Number");
        model.addColumn("Address");

        try {
            Statement stat = connect.createStatement();
            ResultSet data = stat.executeQuery("SELECT * FROM member WHERE id_member LIKE '%" + jTextField4.getText()
                    + "%'OR name LIKE '%" + jTextField4.getText() + "%' OR phone_number LIKE '%" + jTextField4.getText()
                    + "%' OR address LIKE '%" + jTextField4.getText() + "%'");
            while (data.next()) {
                model.addRow(new Object[]{
                    data.getString("id_member"),
                    data.getString("name"),
                    data.getString("phone_number"),
                    data.getString("address"),});
                jTable1.setModel(model);
            }
        } catch (SQLException e) {
            System.err.println("There's an error: " + e);
        }
        
    }
    }
    //private void showData(){
       
    //}
    
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
        jTextField2 = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jTextField5 = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel3 = new javax.swing.JPanel();
        jTextField4 = new javax.swing.JTextField();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();

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

        jButton3.setText("Exit");
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 46, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addContainerGap())
        );

        jLabel1.setText("ID");

        jLabel2.setText("Title");

        jLabel3.setText("Author");

        jLabel4.setText("Genre");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Fiction", "Non-Fiction", "Poetry", "Drama", "Graphic Novels", "Graphic Comics", "Classics", "Short Stories" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jTextField1)
                    .addComponent(jTextField2)
                    .addComponent(jTextField3)
                    .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(jTextField5))
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
                .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jPanel2.setBackground(new java.awt.Color(255, 255, 255));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "ID", "Title", "Author", "Genre"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

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

        jButton7.setText("Book");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("Member");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("Borrow");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField4)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jButton7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton9)
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
                    .addComponent(jButton7)
                    .addComponent(jButton8)
                    .addComponent(jButton9))
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
        this.dispose();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        if(currentlyMember==true){
        Member member = new Member();
        member.select();
        }else{
        Book book = new Book();
        book.select();
        }
    }//GEN-LAST:event_jTable1MouseClicked

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(currentlyMember==true){
        Member member = new Member();
        member.add();
        }else{
        Book book = new Book();
        book.add();
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        if(currentlyMember==true){
        Member member = new Member();
        member.clear();
        }else{
        Book book = new Book();
        book.clear();
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(currentlyMember==true){
        Member member = new Member();
        member.update();
        }else{
        Book book = new Book();
        book.update();
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        if(currentlyMember==true){
        Member member = new Member();
        member.delete();
        }else{
        Book book = new Book();
        book.delete();
        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        if(currentlyMember==true){
        Member member = new Member();
        member.search();
        }else{
        Book book = new Book();
        book.search();
        }
    }//GEN-LAST:event_jButton6ActionPerformed

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        currentlyMember = false;
        if(currentlyMember==true) {
        Member member = new Member();
        member.showData();
        jLabel2.setText("Name");
        jLabel3.setText("Phone Number");
        jLabel4.setText("Address");
        jTextField5.setVisible(true);
        jComboBox1.setVisible(false);
        }else{
        Book book = new Book();
        book.clear();
        book.showData();
        jLabel2.setText("Title");
        jLabel3.setText("Author");
        jLabel4.setText("Genre");
        jTextField5.setVisible(false);
        jComboBox1.setVisible(true);
        }
    }//GEN-LAST:event_jButton7ActionPerformed

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        currentlyMember = true;
        if(currentlyMember==true) {
        Member member = new Member();
        member.clear();
        member.showData();
        jLabel2.setText("Name");
        jLabel3.setText("Phone Number");
        jLabel4.setText("Address");
        jTextField5.setVisible(true);
        jComboBox1.setVisible(false);
        }else{
        Book book = new Book();
        book.showData();
        jLabel2.setText("Title");
        jLabel3.setText("Author");
        jLabel4.setText("Genre");
        jTextField5.setVisible(false);
        jComboBox1.setVisible(true);
        }
    }//GEN-LAST:event_jButton8ActionPerformed

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
            java.util.logging.Logger.getLogger(Form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Form.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
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
                new Form().setVisible(true);
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
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JTextField jTextField4;
    private javax.swing.JTextField jTextField5;
    // End of variables declaration//GEN-END:variables
}
