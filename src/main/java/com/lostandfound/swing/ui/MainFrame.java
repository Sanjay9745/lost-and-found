package com.lostandfound.swing.ui;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lostandfound.dto.LostItemRequest;
import com.lostandfound.dto.MarkFoundRequest;
import com.lostandfound.model.LostItem;
import com.lostandfound.swing.api.ApiClient;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainFrame extends JFrame {
    private JTable itemsTable;
    private DefaultTableModel tableModel;
    private JButton reportButton;
    private JButton markFoundButton;
    private JButton refreshButton;
    private JButton deleteButton;
    private JComboBox<String> filterComboBox;
    private JTextField searchField;
    private JButton searchButton;
    
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    public MainFrame() {
        setTitle("Lost and Found - Management System");
        setSize(1200, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
        loadItems();
    }

    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Top Panel - Title and Search
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        JLabel titleLabel = new JLabel("Lost and Found Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(titleLabel, BorderLayout.NORTH);

        // Search Panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Filter:"));
        filterComboBox = new JComboBox<>(new String[]{"All Items", "Lost Items", "Found Items"});
        filterComboBox.addActionListener(e -> filterItems());
        searchPanel.add(filterComboBox);
        
        searchPanel.add(Box.createHorizontalStrut(20));
        searchPanel.add(new JLabel("Search:"));
        searchField = new JTextField(20);
        searchPanel.add(searchField);
        
        searchButton = new JButton("Search by Name");
        searchButton.addActionListener(e -> searchItems());
        searchPanel.add(searchButton);
        
        topPanel.add(searchPanel, BorderLayout.SOUTH);
        
        mainPanel.add(topPanel, BorderLayout.NORTH);

        // Table
        String[] columns = {"ID", "Item Name", "Description", "Location", "Student Name", 
                           "Contact", "Reported Date", "Status", "Found By", "Found Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        itemsTable = new JTable(tableModel);
        itemsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemsTable.getColumnModel().getColumn(0).setPreferredWidth(40);
        itemsTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(2).setPreferredWidth(200);
        itemsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(6).setPreferredWidth(130);
        itemsTable.getColumnModel().getColumn(7).setPreferredWidth(70);
        itemsTable.getColumnModel().getColumn(8).setPreferredWidth(100);
        itemsTable.getColumnModel().getColumn(9).setPreferredWidth(130);
        
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        
        reportButton = new JButton("Report Lost Item");
        reportButton.addActionListener(e -> showReportDialog());
        buttonPanel.add(reportButton);
        
        markFoundButton = new JButton("Mark as Found");
        markFoundButton.addActionListener(e -> markItemAsFound());
        buttonPanel.add(markFoundButton);
        
        deleteButton = new JButton("Delete Item");
        deleteButton.addActionListener(e -> deleteItem());
        buttonPanel.add(deleteButton);
        
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadItems());
        buttonPanel.add(refreshButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void loadItems() {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private List<LostItem> items;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    ApiClient.ApiResponse response = ApiClient.get("/items");
                    if (response.isSuccess()) {
                        items = objectMapper.readValue(response.getBody(), 
                                new TypeReference<List<LostItem>>() {});
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                if (items != null) {
                    updateTable(items);
                }
            }
        };
        worker.execute();
    }

    private void filterItems() {
        String filter = (String) filterComboBox.getSelectedItem();
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private List<LostItem> items;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    String endpoint = "/items";
                    if ("Lost Items".equals(filter)) {
                        endpoint = "/items/lost";
                    } else if ("Found Items".equals(filter)) {
                        endpoint = "/items/found";
                    }
                    
                    ApiClient.ApiResponse response = ApiClient.get(endpoint);
                    if (response.isSuccess()) {
                        items = objectMapper.readValue(response.getBody(), 
                                new TypeReference<List<LostItem>>() {});
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                if (items != null) {
                    updateTable(items);
                }
            }
        };
        worker.execute();
    }

    private void searchItems() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            loadItems();
            return;
        }

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            private List<LostItem> items;

            @Override
            protected Void doInBackground() throws Exception {
                try {
                    ApiClient.ApiResponse response = ApiClient.get("/items/search/item?name=" + searchTerm);
                    if (response.isSuccess()) {
                        items = objectMapper.readValue(response.getBody(), 
                                new TypeReference<List<LostItem>>() {});
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void done() {
                if (items != null) {
                    updateTable(items);
                }
            }
        };
        worker.execute();
    }

    private void updateTable(List<LostItem> items) {
        tableModel.setRowCount(0);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        
        for (LostItem item : items) {
            Object[] row = {
                item.getId(),
                item.getItemName(),
                item.getDescription(),
                item.getLocation(),
                item.getStudentName(),
                item.getContactInfo(),
                item.getReportedDate().format(formatter),
                item.getFound() ? "Found" : "Lost",
                item.getFoundBy() != null ? item.getFoundBy() : "-",
                item.getFoundDate() != null ? item.getFoundDate().format(formatter) : "-"
            };
            tableModel.addRow(row);
        }
    }

    private void showReportDialog() {
        JDialog dialog = new JDialog(this, "Report Lost Item", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JTextField itemNameField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        JTextField locationField = new JTextField(20);
        JTextField studentNameField = new JTextField(20);
        JTextField contactField = new JTextField(20);

        // Item Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        panel.add(new JLabel("Item Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(itemNameField, gbc);

        // Description
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(new JScrollPane(descriptionArea), gbc);

        // Location
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        panel.add(new JLabel("Location:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(locationField, gbc);

        // Student Name
        gbc.gridx = 0; gbc.gridy = 3; gbc.weightx = 0.3;
        panel.add(new JLabel("Student Name:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(studentNameField, gbc);

        // Contact
        gbc.gridx = 0; gbc.gridy = 4; gbc.weightx = 0.3;
        panel.add(new JLabel("Contact Info:"), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        panel.add(contactField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton submitButton = new JButton("Submit");
        JButton cancelButton = new JButton("Cancel");

        submitButton.addActionListener(e -> {
            String itemName = itemNameField.getText().trim();
            String description = descriptionArea.getText().trim();
            String location = locationField.getText().trim();
            String studentName = studentNameField.getText().trim();
            String contact = contactField.getText().trim();

            if (itemName.isEmpty() || description.isEmpty() || location.isEmpty() || 
                studentName.isEmpty() || contact.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            LostItemRequest request = new LostItemRequest(itemName, description, location, 
                                                          studentName, contact);
            
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                private boolean success = false;

                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        ApiClient.ApiResponse response = ApiClient.post("/items/report", request);
                        success = response.isSuccess();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, "Item reported successfully!", 
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        loadItems();
                    } else {
                        JOptionPane.showMessageDialog(dialog, "Failed to report item!", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        panel.add(buttonPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void markItemAsFound() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to mark as found!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long itemId = (Long) tableModel.getValueAt(selectedRow, 0);
        String status = (String) tableModel.getValueAt(selectedRow, 7);

        if ("Found".equals(status)) {
            JOptionPane.showMessageDialog(this, "This item is already marked as found!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JTextField foundByField = new JTextField();
        JTextArea remarksArea = new JTextArea(3, 20);
        remarksArea.setLineWrap(true);

        panel.add(new JLabel("Found By:"));
        panel.add(foundByField);
        panel.add(new JLabel("Remarks:"));
        panel.add(new JScrollPane(remarksArea));

        int result = JOptionPane.showConfirmDialog(this, panel, "Mark Item as Found", 
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String foundBy = foundByField.getText().trim();
            String remarks = remarksArea.getText().trim();

            if (foundBy.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter who found the item!", 
                        "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            MarkFoundRequest request = new MarkFoundRequest(foundBy, remarks);
            
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                private boolean success = false;

                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        ApiClient.ApiResponse response = ApiClient.put("/items/" + itemId + "/found", request);
                        success = response.isSuccess();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    if (success) {
                        JOptionPane.showMessageDialog(MainFrame.this, 
                                "Item marked as found successfully!", 
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadItems();
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, 
                                "Failed to mark item as found!", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }

    private void deleteItem() {
        int selectedRow = itemsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete!", 
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Long itemId = (Long) tableModel.getValueAt(selectedRow, 0);
        
        int result = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete this item?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION);

        if (result == JOptionPane.YES_OPTION) {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                private boolean success = false;

                @Override
                protected Void doInBackground() throws Exception {
                    try {
                        ApiClient.ApiResponse response = ApiClient.delete("/items/" + itemId);
                        success = response.isSuccess();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void done() {
                    if (success) {
                        JOptionPane.showMessageDialog(MainFrame.this, 
                                "Item deleted successfully!", 
                                "Success", JOptionPane.INFORMATION_MESSAGE);
                        loadItems();
                    } else {
                        JOptionPane.showMessageDialog(MainFrame.this, 
                                "Failed to delete item!", 
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            worker.execute();
        }
    }
}
