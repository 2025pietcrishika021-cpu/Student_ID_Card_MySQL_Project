import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class StudentIDCardApp extends JFrame {
    private final Color BLUE = new Color(37, 99, 235);
    private final Color NAVY = new Color(15, 23, 42);
    private final Color BG = new Color(241, 245, 249);
    private final Color GREEN = new Color(22, 163, 74);
    private final Color RED = new Color(220, 38, 38);
    private final Color MUTED = new Color(100, 116, 139);

    private CardLayout cardLayout;
    private JPanel content;
    private DefaultTableModel studentModel, requestModel;
    private JTable studentTable, requestTable;
    private JLabel totalStudents, pendingRequests, totalRequests;

    public StudentIDCardApp() {
        setTitle("PIET - Student ID Card Request & Generation System");
        setSize(1150, 700);
        setMinimumSize(new Dimension(950, 600));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        home();
    }

    private void home() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG);

        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(35, 45, 35, 45));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel logo = new JLabel("PIET");
        logo.setFont(new Font("Arial", Font.BOLD, 42));
        logo.setForeground(BLUE);
        logo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel title = new JLabel("Student ID Card System");
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Student Registration, ID Card Requests & Admin Management");
        subtitle.setForeground(MUTED);
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton studentButton = button("Student Portal", BLUE, Color.WHITE);
        JButton adminButton = button("Admin Login", NAVY, Color.WHITE);
        studentButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        adminButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        studentButton.setMaximumSize(new Dimension(300, 44));
        adminButton.setMaximumSize(new Dimension(300, 44));

        JLabel hint = new JLabel("Demo admin login: admin / admin123");
        hint.setForeground(MUTED);
        hint.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(logo);
        panel.add(Box.createVerticalStrut(10));
        panel.add(title);
        panel.add(Box.createVerticalStrut(8));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(28));
        panel.add(studentButton);
        panel.add(Box.createVerticalStrut(12));
        panel.add(adminButton);
        panel.add(Box.createVerticalStrut(20));
        panel.add(hint);
        root.add(panel);

        studentButton.addActionListener(e -> studentPortal());
        adminButton.addActionListener(e -> adminLogin());
        setContentPane(root);
        revalidate();
        repaint();
    }

    private void adminLogin() {
        JTextField username = new JTextField("admin");
        JPasswordField password = new JPasswordField();
        JPanel panel = new JPanel(new GridLayout(2, 2, 8, 8));
        panel.add(new JLabel("Username:"));
        panel.add(username);
        panel.add(new JLabel("Password:"));
        panel.add(password);

        if (JOptionPane.showConfirmDialog(this, panel, "Admin Login",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            if ("admin".equals(username.getText().trim())
                    && "admin123".equals(new String(password.getPassword()))) {
                adminPage();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void studentPortal() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(new EmptyBorder(16, 22, 16, 22));

        JLabel title = new JLabel("Student Portal");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 24));

        JButton back = button("Back to Home", new Color(51, 65, 85), Color.WHITE);
        header.add(title, BorderLayout.WEST);
        header.add(back, BorderLayout.EAST);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Student Registration", registrationPanel());
        tabs.addTab("Apply for ID Card", applyPanel());

        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(BG);
        body.setBorder(new EmptyBorder(20, 20, 20, 20));
        body.add(tabs);

        root.add(header, BorderLayout.NORTH);
        root.add(body, BorderLayout.CENTER);
        back.addActionListener(e -> home());

        setContentPane(root);
        revalidate();
        repaint();
    }

    private JPanel registrationPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(22, 22, 22, 22));

        JLabel title = new JLabel("Register Student Details");
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JTextField studentId = new JTextField();
        JTextField studentName = new JTextField();
        JTextField course = new JTextField("B.Tech");
        JTextField branch = new JTextField("Computer Science & Engineering");
        JTextField semester = new JTextField();
        JTextField phone = new JTextField();
        JTextField email = new JTextField();

        JPanel fields = new JPanel(new GridLayout(0, 2, 12, 12));
        fields.setBackground(Color.WHITE);
        fields.add(field("Student ID *", studentId));
        fields.add(field("Full Name *", studentName));
        fields.add(field("Course", course));
        fields.add(field("Branch", branch));
        fields.add(field("Semester", semester));
        fields.add(field("Phone", phone));
        fields.add(field("Email", email));

        JButton register = button("Register Student", BLUE, Color.WHITE);
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottom.setBackground(Color.WHITE);
        bottom.add(register);

        panel.add(title, BorderLayout.NORTH);
        panel.add(fields, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        register.addActionListener(e -> {
            String id = studentId.getText().trim();
            String name = studentName.getText().trim();

            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Student ID and Full Name are required.");
                return;
            }

            String sql = "INSERT INTO students "
                    + "(student_id, student_name, course, branch, semester, phone, email) "
                    + "VALUES (?, ?, ?, ?, ?, ?, ?)";

            try (Connection con = DBConnection.getConnection();
                 PreparedStatement ps = con.prepareStatement(sql)) {
                ps.setString(1, id);
                ps.setString(2, name);
                ps.setString(3, course.getText().trim());
                ps.setString(4, branch.getText().trim());
                ps.setString(5, semester.getText().trim());
                ps.setString(6, phone.getText().trim());
                ps.setString(7, email.getText().trim());
                ps.executeUpdate();

                JOptionPane.showMessageDialog(this, "Student registered successfully in MySQL!");
                studentId.setText("");
                studentName.setText("");
                semester.setText("");
                phone.setText("");
                email.setText("");
            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(this, "This Student ID is already registered.");
            } catch (SQLException ex) {
                showDatabaseError(ex);
            }
        });
        return panel;
    }

    private JPanel applyPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(Color.WHITE);
        outer.setBorder(new EmptyBorder(30, 30, 30, 30));

        JPanel form = new JPanel();
        form.setBackground(Color.WHITE);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Apply for Student ID Card");
        title.setFont(new Font("Arial", Font.BOLD, 22));

        JLabel help = new JLabel("<html>Enter the Student ID used during registration.<br>"
                + "Your request will be saved in the database.</html>");
        help.setForeground(MUTED);

        JTextField requestStudentId = new JTextField(25);
        requestStudentId.setMaximumSize(new Dimension(400, 38));
        JButton submit = button("Submit ID Card Request", GREEN, Color.WHITE);
        submit.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(title);
        form.add(Box.createVerticalStrut(12));
        form.add(help);
        form.add(Box.createVerticalStrut(22));
        form.add(new JLabel("Student ID"));
        form.add(Box.createVerticalStrut(6));
        form.add(requestStudentId);
        form.add(Box.createVerticalStrut(18));
        form.add(submit);
        outer.add(form);

        submit.addActionListener(e -> {
            String id = requestStudentId.getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Enter your Student ID.");
                return;
            }

            String checkSql = "SELECT student_id FROM students WHERE student_id = ?";
            String duplicateSql = "SELECT request_id FROM id_card_requests "
                    + "WHERE student_id = ? AND status = 'Pending'";
            String insertSql = "INSERT INTO id_card_requests "
                    + "(student_id, request_date, request_type, status) VALUES (?, ?, ?, ?)";

            try (Connection con = DBConnection.getConnection()) {
                try (PreparedStatement check = con.prepareStatement(checkSql)) {
                    check.setString(1, id);
                    try (ResultSet rs = check.executeQuery()) {
                        if (!rs.next()) {
                            JOptionPane.showMessageDialog(this, "Student not found. Please register first.");
                            return;
                        }
                    }
                }

                try (PreparedStatement duplicate = con.prepareStatement(duplicateSql)) {
                    duplicate.setString(1, id);
                    try (ResultSet rs = duplicate.executeQuery()) {
                        if (rs.next()) {
                            JOptionPane.showMessageDialog(this, "A pending request already exists.");
                            return;
                        }
                    }
                }

                try (PreparedStatement insert = con.prepareStatement(insertSql)) {
                    insert.setString(1, id);
                    insert.setDate(2, Date.valueOf(LocalDate.now()));
                    insert.setString(3, "New ID Card");
                    insert.setString(4, "Pending");
                    insert.executeUpdate();
                }

                requestStudentId.setText("");
                JOptionPane.showMessageDialog(this, "ID Card request submitted successfully! Status: Pending.");
            } catch (SQLException ex) {
                showDatabaseError(ex);
            }
        });
        return outer;
    }

    private void adminPage() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG);

        JPanel side = new JPanel();
        side.setPreferredSize(new Dimension(220, 0));
        side.setBackground(NAVY);
        side.setBorder(new EmptyBorder(22, 14, 22, 14));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));

        JLabel brand = new JLabel("PIET ADMIN");
        brand.setForeground(Color.WHITE);
        brand.setFont(new Font("Arial", Font.BOLD, 23));
        brand.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton dashboard = menu("Dashboard");
        JButton requests = menu("ID Card Requests");
        JButton students = menu("Registered Students");
        JButton portal = menu("Student Portal");
        JButton logout = menu("Logout");

        side.add(brand);
        side.add(Box.createVerticalStrut(28));
        side.add(dashboard);
        side.add(Box.createVerticalStrut(8));
        side.add(requests);
        side.add(Box.createVerticalStrut(8));
        side.add(students);
        side.add(Box.createVerticalStrut(8));
        side.add(portal);
        side.add(Box.createVerticalGlue());
        side.add(logout);

        cardLayout = new CardLayout();
        content = new JPanel(cardLayout);
        content.setBackground(BG);
        content.add(dashboardPanel(), "dashboard");
        content.add(requestsPanel(), "requests");
        content.add(studentsPanel(), "students");

        root.add(side, BorderLayout.WEST);
        root.add(content, BorderLayout.CENTER);

        dashboard.addActionListener(e -> { refresh(); cardLayout.show(content, "dashboard"); });
        requests.addActionListener(e -> { refresh(); cardLayout.show(content, "requests"); });
        students.addActionListener(e -> { refresh(); cardLayout.show(content, "students"); });
        portal.addActionListener(e -> studentPortal());
        logout.addActionListener(e -> home());

        setContentPane(root);
        refresh();
        revalidate();
        repaint();
    }

    private JPanel dashboardPanel() {
        JPanel panel = page();
        JPanel heading = new JPanel();
        heading.setOpaque(false);
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Admin Dashboard");
        title.setFont(new Font("Arial", Font.BOLD, 30));
        JLabel subtitle = new JLabel("Monitor registrations and incoming ID card requests");
        subtitle.setForeground(MUTED);
        heading.add(title);
        heading.add(Box.createVerticalStrut(5));
        heading.add(subtitle);
        panel.add(heading, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(0, 20));
        center.setOpaque(false);
        JPanel stats = new JPanel(new GridLayout(1, 3, 15, 0));
        stats.setOpaque(false);

        totalStudents = new JLabel("0");
        pendingRequests = new JLabel("0");
        totalRequests = new JLabel("0");
        stats.add(stat("REGISTERED STUDENTS", totalStudents, BLUE));
        stats.add(stat("PENDING REQUESTS", pendingRequests, RED));
        stats.add(stat("TOTAL ID REQUESTS", totalRequests, GREEN));
        center.add(stats, BorderLayout.NORTH);

        JPanel note = new JPanel();
        note.setBackground(Color.WHITE);
        note.setBorder(new EmptyBorder(24, 24, 24, 24));
        note.setLayout(new BoxLayout(note, BoxLayout.Y_AXIS));
        JLabel noteTitle = new JLabel("Incoming Request Notifications");
        noteTitle.setFont(new Font("Arial", Font.BOLD, 20));
        JLabel message = new JLabel("<html>Open ID Card Requests to approve, reject or generate a card.<br>"
                + "Use Refresh Dashboard to load the latest database records.</html>");
        message.setForeground(MUTED);
        JButton refresh = button("Refresh Dashboard", BLUE, Color.WHITE);
        refresh.setAlignmentX(Component.LEFT_ALIGNMENT);
        note.add(noteTitle);
        note.add(Box.createVerticalStrut(12));
        note.add(message);
        note.add(Box.createVerticalStrut(20));
        note.add(refresh);
        refresh.addActionListener(e -> refresh());
        center.add(note, BorderLayout.CENTER);
        panel.add(center, BorderLayout.CENTER);
        return panel;
    }

    private JPanel requestsPanel() {
        JPanel panel = page();
        JLabel title = new JLabel("ID Card Requests");
        title.setFont(new Font("Arial", Font.BOLD, 28));

        JButton refresh = button("Refresh", BLUE, Color.WHITE);
        JButton approve = button("Approve Selected", GREEN, Color.WHITE);
        JButton reject = button("Reject Selected", RED, Color.WHITE);
        JButton generate = button("Generate ID Card", NAVY, Color.WHITE);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        actions.add(refresh);
        actions.add(approve);
        actions.add(reject);
        actions.add(generate);
        top.add(actions, BorderLayout.EAST);

        requestModel = new DefaultTableModel(
                new Object[]{"Request ID", "Student ID", "Student Name", "Request Date", "Request Type", "Status"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        requestTable = new JTable(requestModel);
        requestTable.setRowHeight(30);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(requestTable), BorderLayout.CENTER);

        refresh.addActionListener(e -> refresh());
        approve.addActionListener(e -> updateRequestStatus("Approved"));
        reject.addActionListener(e -> updateRequestStatus("Rejected"));
        generate.addActionListener(e -> generateSelectedCard());
        return panel;
    }

    private JPanel studentsPanel() {
        JPanel panel = page();
        JLabel title = new JLabel("Registered Students");
        title.setFont(new Font("Arial", Font.BOLD, 28));
        JButton refresh = button("Refresh", BLUE, Color.WHITE);

        JPanel top = new JPanel(new BorderLayout());
        top.setOpaque(false);
        top.add(title, BorderLayout.WEST);
        top.add(refresh, BorderLayout.EAST);

        studentModel = new DefaultTableModel(
                new Object[]{"Student ID", "Name", "Course", "Branch", "Semester", "Phone", "Email"}, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };
        studentTable = new JTable(studentModel);
        studentTable.setRowHeight(28);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);
        refresh.addActionListener(e -> refresh());
        return panel;
    }

    private void updateRequestStatus(String status) {
        int row = requestTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a request first.");
            return;
        }
        int modelRow = requestTable.convertRowIndexToModel(row);
        int requestId = Integer.parseInt(requestModel.getValueAt(modelRow, 0).toString());

        String sql = "UPDATE id_card_requests SET status = ? WHERE request_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, requestId);
            int changed = ps.executeUpdate();
            if (changed > 0) {
                JOptionPane.showMessageDialog(this, "Request status updated to " + status + ".");
                refresh();
            } else {
                JOptionPane.showMessageDialog(this, "Request could not be updated.");
            }
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void generateSelectedCard() {
        int row = requestTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an approved request first.");
            return;
        }
        int modelRow = requestTable.convertRowIndexToModel(row);
        String status = requestModel.getValueAt(modelRow, 5).toString();
        if (!"Approved".equalsIgnoreCase(status)) {
            JOptionPane.showMessageDialog(this, "Approve this request before generating the ID card.");
            return;
        }

        String studentId = requestModel.getValueAt(modelRow, 1).toString();
        String sql = "SELECT student_id, student_name, course, branch, semester, phone, email "
                + "FROM students WHERE student_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String card = "PIET - STUDENT ID CARD\n\n"
                            + "Student ID : " + rs.getString("student_id") + "\n"
                            + "Name       : " + rs.getString("student_name") + "\n"
                            + "Course     : " + rs.getString("course") + "\n"
                            + "Branch     : " + rs.getString("branch") + "\n"
                            + "Semester   : " + rs.getString("semester") + "\n"
                            + "Phone      : " + rs.getString("phone") + "\n"
                            + "Email      : " + rs.getString("email") + "\n\n"
                            + "Authorized Student Identity Card";
                    JTextArea area = new JTextArea(card);
                    area.setEditable(false);
                    area.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
                    JOptionPane.showMessageDialog(this, new JScrollPane(area),
                            "Generated Student ID Card", JOptionPane.INFORMATION_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void refresh() {
        loadStudents();
        loadRequests();
        loadDashboardCounts();
    }

    private void loadStudents() {
        if (studentModel == null) return;
        studentModel.setRowCount(0);
        String sql = "SELECT student_id, student_name, course, branch, semester, phone, email "
                + "FROM students ORDER BY student_id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                studentModel.addRow(new Object[]{
                        rs.getString("student_id"), rs.getString("student_name"),
                        rs.getString("course"), rs.getString("branch"),
                        rs.getString("semester"), rs.getString("phone"), rs.getString("email")
                });
            }
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void loadRequests() {
        if (requestModel == null) return;
        requestModel.setRowCount(0);
        String sql = "SELECT r.request_id, r.student_id, s.student_name, r.request_date, "
                + "r.request_type, r.status FROM id_card_requests r "
                + "LEFT JOIN students s ON r.student_id = s.student_id "
                + "ORDER BY r.request_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                requestModel.addRow(new Object[]{
                        rs.getInt("request_id"), rs.getString("student_id"),
                        rs.getString("student_name"), rs.getDate("request_date"),
                        rs.getString("request_type"), rs.getString("status")
                });
            }
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private void loadDashboardCounts() {
        if (totalStudents == null || pendingRequests == null || totalRequests == null) return;
        String sql = "SELECT "
                + "(SELECT COUNT(*) FROM students) AS student_count, "
                + "(SELECT COUNT(*) FROM id_card_requests WHERE status='Pending') AS pending_count, "
                + "(SELECT COUNT(*) FROM id_card_requests) AS request_count";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                totalStudents.setText(String.valueOf(rs.getInt("student_count")));
                pendingRequests.setText(String.valueOf(rs.getInt("pending_count")));
                totalRequests.setText(String.valueOf(rs.getInt("request_count")));
            }
        } catch (SQLException ex) {
            showDatabaseError(ex);
        }
    }

    private JPanel page() {
        JPanel panel = new JPanel(new BorderLayout(0, 22));
        panel.setBackground(BG);
        panel.setBorder(new EmptyBorder(28, 28, 28, 28));
        return panel;
    }

    private JPanel field(String label, JTextField input) {
        JPanel panel = new JPanel(new BorderLayout(0, 6));
        panel.setBackground(Color.WHITE);
        JLabel caption = new JLabel(label);
        caption.setFont(new Font("Arial", Font.BOLD, 13));
        input.setPreferredSize(new Dimension(250, 36));
        panel.add(caption, BorderLayout.NORTH);
        panel.add(input, BorderLayout.CENTER);
        return panel;
    }

    private JPanel stat(String title, JLabel value, Color color) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(18, 20, 18, 20));
        JLabel caption = new JLabel(title);
        caption.setForeground(MUTED);
        caption.setFont(new Font("Arial", Font.BOLD, 12));
        value.setFont(new Font("Arial", Font.BOLD, 34));
        value.setForeground(color);
        panel.add(caption, BorderLayout.NORTH);
        panel.add(value, BorderLayout.CENTER);
        return panel;
    }

    private JButton button(String text, Color background, Color foreground) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 13));
        button.setBackground(background);
        button.setForeground(foreground);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setBorder(new EmptyBorder(10, 16, 10, 16));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton menu(String text) {
        JButton button = button(text, new Color(30, 41, 59), Color.WHITE);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        return button;
    }

    private void showDatabaseError(SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Database error: " + ex.getMessage()
                        + "\nCheck DBConnection.java and MySQL table structure.",
                "MySQL Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) { }
            new StudentIDCardApp().setVisible(true);
        });
    }
}
