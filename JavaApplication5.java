package javaapplication5;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.ArrayList;
import java.util.List;

public class JavaApplication5 {

    // ---------- GUI COMPONENTS ----------
    private JFrame frame;                          // Main window
    private DefaultListModel<Task> taskListModel;  // Stores tasks displayed in the list
    private JList<Task> taskList;                  // UI component to display tasks
    private JTextField taskField;                  // Input field for new tasks
    private JButton addButton, removeButton;       // Task management buttons
    private JButton startButton, pauseButton, resetButton; // Timer control buttons
    private TomatoTimerPanel timerPanel;           // Custom drawing panel for tomato timer
    private JLabel phaseLabel;                     // Shows "Work Phase" or "Break Phase"
    private JSlider workSlider, breakSlider;       // Duration sliders
    private Timer timer;                           // Swing timer that updates every second

    // ---------- TIMER LOGIC ----------
    private int workDuration = 25 * 60;   // Work duration (default 25 min)
    private int breakDuration = 5 * 60;   // Break duration (default 5 min)
    private int remainingTime = workDuration; // Current countdown
    private boolean isWorkPhase = true;        // True = work, false = break
    private boolean isRunning = false;         // Timer running or paused

    // List to store tasks logically
    private List<Task> tasks = new ArrayList<>();

    // -----------------------------------------------------------
    //                     MAIN CONSTRUCTOR
    // -----------------------------------------------------------
    public JavaApplication5() {

        // Create main application window
        frame = new JFrame("Pomodoro + To-Do App");
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());
        frame.getContentPane().setBackground(Color.WHITE);

        // Custom panel that loads background image
        GradientPanel mainPanel = new GradientPanel();
        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // ---------- TIMER PANEL ----------
        timerPanel = new TomatoTimerPanel();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        mainPanel.add(timerPanel, gbc);

        // Label showing current phase (Work / Break)
        phaseLabel = new JLabel("Work Phase", SwingConstants.CENTER);
        phaseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 20));
        phaseLabel.setForeground(Color.DARK_GRAY);
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.1;
        mainPanel.add(phaseLabel, gbc);

        // ---------- SLIDER PANEL ----------
        JPanel sliderPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        sliderPanel.setOpaque(false);

        // Work duration slider setup
        JPanel workSliderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        workSliderPanel.setOpaque(false);
        JLabel workSliderLabel = new JLabel("Work Duration: ");
        workSliderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        workSlider = new JSlider(1, 60, 25); // min 1 max 60 default 25
        workSlider.setMajorTickSpacing(10);
        workSlider.setMinorTickSpacing(1);
        workSlider.setPaintTicks(true);
        workSlider.setPaintLabels(true);
        workSlider.setOpaque(false);

        // Update timer when work slider changes
        workSlider.addChangeListener(e -> {
            if (!workSlider.getValueIsAdjusting()) {
                workDuration = workSlider.getValue() * 60;
                if (!isRunning) {
                    remainingTime = workDuration;
                    timerPanel.repaint();
                }
            }
        });

        workSliderPanel.add(workSliderLabel);
        workSliderPanel.add(workSlider);
        sliderPanel.add(workSliderPanel);

        // Break duration slider setup
        JPanel breakSliderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        breakSliderPanel.setOpaque(false);
        JLabel breakSliderLabel = new JLabel("Break Duration: ");
        breakSliderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        breakSlider = new JSlider(1, 60, 5); // min 1 max 60 default 5
        breakSlider.setMajorTickSpacing(10);
        breakSlider.setMinorTickSpacing(1);
        breakSlider.setPaintTicks(true);
        breakSlider.setPaintLabels(true);
        breakSlider.setOpaque(false);

        // Update timer only if in break phase
        breakSlider.addChangeListener(e -> {
            if (!breakSlider.getValueIsAdjusting()) {
                breakDuration = breakSlider.getValue() * 60;
                if (!isRunning && !isWorkPhase) {
                    remainingTime = breakDuration;
                    timerPanel.repaint();
                }
            }
        });

        breakSliderPanel.add(breakSliderLabel);
        breakSliderPanel.add(breakSlider);
        sliderPanel.add(breakSliderPanel);

        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.1;
        mainPanel.add(sliderPanel, gbc);

        // ---------- TO-DO LIST ----------
        taskListModel = new DefaultListModel<>();
        taskList = new JList<>(taskListModel);
        taskList.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        taskList.setCellRenderer(new TaskRenderer());

        JScrollPane scrollPane = new JScrollPane(taskList);
        scrollPane.setBorder(BorderFactory.createTitledBorder("To-Do List"));
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weighty = 0.3;
        mainPanel.add(scrollPane, gbc);

        // Input field + Add/Remove task buttons
        JPanel inputPanel = new JPanel(new FlowLayout());
        inputPanel.setOpaque(false);
        taskField = new JTextField(30);
        taskField.setFont(new Font("Segoe UI", Font.PLAIN, 15));

        addButton = new JButton("Add Task");
        removeButton = new JButton("Remove");

        inputPanel.add(taskField);
        inputPanel.add(addButton);
        inputPanel.add(removeButton);

        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.1;
        mainPanel.add(inputPanel, gbc);

        // ---------- TIMER CONTROL BUTTONS ----------
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setOpaque(false);

        startButton = new JButton("Start");
        pauseButton = new JButton("Pause");
        resetButton = new JButton("Reset");

        buttonPanel.add(startButton);
        buttonPanel.add(pauseButton);
        buttonPanel.add(resetButton);

        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weighty = 0.1;
        mainPanel.add(buttonPanel, gbc);

        frame.add(mainPanel, BorderLayout.CENTER);

        // Apply button styling
        styleButton(addButton);
        styleButton(removeButton);
        styleButton(startButton);
        styleButton(pauseButton);
        styleButton(resetButton);

        // ---------- EVENT LISTENERS ----------
        addButton.addActionListener(e -> addTask());
        removeButton.addActionListener(e -> removeTask());
        startButton.addActionListener(e -> startTimer());
        pauseButton.addActionListener(e -> pauseTimer());
        resetButton.addActionListener(e -> resetTimer());

        // Timer updates every second
        timer = new Timer(1000, e -> updateTimer());

        frame.setVisible(true);
    }

    // -----------------------------------------------------------
    //                       BUTTON STYLING
    // -----------------------------------------------------------
    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorderPainted(false);
        button.setOpaque(true);

        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 149, 237));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
    }

    // -----------------------------------------------------------
    //                  TASK MANAGEMENT METHODS
    // -----------------------------------------------------------
    private void addTask() {
        String text = taskField.getText().trim();
        if (!text.isEmpty()) {
            Task t = new Task(text);
            taskListModel.addElement(t);
            tasks.add(t);
            taskField.setText("");
        }
    }

    private void removeTask() {
        Task selected = taskList.getSelectedValue();
        if (selected != null) {
            taskListModel.removeElement(selected);
            tasks.remove(selected);
        }
    }

    // -----------------------------------------------------------
    //                       TIMER METHODS
    // -----------------------------------------------------------
    private void startTimer() {
        if (!isRunning) {
            timer.start();
            isRunning = true;
        }
    }

    private void pauseTimer() {
        timer.stop();
        isRunning = false;
    }

    private void resetTimer() {
        timer.stop();
        isRunning = false;
        remainingTime = isWorkPhase ? workDuration : breakDuration;
        timerPanel.repaint();
    }

    // Called every second by Swing Timer
    private void updateTimer() {

        if (remainingTime > 0) { // Countdown still running
            remainingTime--;
            timerPanel.repaint();
        } 
        else { // Timer phase finished
            timer.stop();
            isRunning = false;

            // Switch between work and break
            isWorkPhase = !isWorkPhase;
            remainingTime = isWorkPhase ? workDuration : breakDuration;

            phaseLabel.setText(isWorkPhase ? "Work Phase" : "Break Phase");

            // Show alert
            JOptionPane.showMessageDialog(frame,
                    isWorkPhase ? "Break Over! Back to Work." : "Work Complete! Take a Break.");

            timerPanel.repaint();

            // Increment Pomodoro count ONLY after work session
            if (isWorkPhase) {
                Task selected = taskList.getSelectedValue();
                if (selected != null) {
                    selected.incrementPomodoros();
                    taskList.repaint();
                }
            }
        }
    }

    // -----------------------------------------------------------
    //       PANEL THAT LOADS IMAGE BACKGROUND (CHRISTMAS)
    // -----------------------------------------------------------
    static class GradientPanel extends JPanel {

        private BufferedImage backgroundImage;

        public GradientPanel() {
            try {
                // Loads background image file (same folder as project)
                backgroundImage = ImageIO.read(new File("m.jpg"));
            } catch (IOException e) {
                System.out.println("Background image not found. Using gradient.");
                backgroundImage = null; // Fallback to gradient
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            if (backgroundImage != null) {
                // Draw image stretched to fill window
                g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            } else {
                // If image fails, draw a soft gradient
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(240, 248, 255),
                        0, getHeight(), new Color(220, 230, 240)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }

    // -----------------------------------------------------------
    //               TIMER VISUAL DRAWING PANEL
    // -----------------------------------------------------------
    class TomatoTimerPanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int size = Math.min(width, height) - 40; // Keep circle inside
            int x = (width - size) / 2;
            int y = (height - size) / 2;

            // Draw tomato body
            g2d.setPaint(new GradientPaint(
                    x, y, new Color(220, 20, 60),
                    x + size, y + size, new Color(139, 0, 0)
            ));
            g2d.fill(new Ellipse2D.Double(x, y, size, size));

            // Draw progress arc
            int totalTime = isWorkPhase ? workDuration : breakDuration;
            double progress = (double) (totalTime - remainingTime) / totalTime;

            g2d.setColor(new Color(34, 139, 34));
            g2d.setStroke(new BasicStroke(10, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            Arc2D arc = new Arc2D.Double(
                x + 5, y + 5, size - 10, size - 10,
                90, -360 * progress, Arc2D.OPEN
            );
            g2d.draw(arc);

            // Draw leaf on top
            g2d.fill(new Ellipse2D.Double(x + size / 2 - 10, y - 10, 20, 20));

            // Draw remaining time text
            g2d.setColor(Color.WHITE);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 36));
            FontMetrics fm = g2d.getFontMetrics();

            String timeText = formatTime(remainingTime);
            int textX = x + (size - fm.stringWidth(timeText)) / 2;
            int textY = y + (size + fm.getAscent()) / 2;

            g2d.drawString(timeText, textX, textY);
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(300, 300);
        }
    }

    // -----------------------------------------------------------
    //                     UTILITY METHOD
    // -----------------------------------------------------------
    private String formatTime(int seconds) {
        int min = seconds / 60;
        int sec = seconds % 60;
        return String.format("%02d:%02d", min, sec);
    }

    // -----------------------------------------------------------
    //               CUSTOM CELL RENDERER FOR TASK LIST
    // -----------------------------------------------------------
    static class TaskRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            Task task = (Task) value;

            if (task.pomodorosCompleted > 0) {
                setText("✓ " + task.toString());
            } else {
                setText(task.toString());
            }

            return this;
        }
    }

    // -----------------------------------------------------------
    //                      TASK CLASS
    // -----------------------------------------------------------
    static class Task {
        String description;
        int pomodorosCompleted;

        Task(String desc) {
            this.description = desc;
            this.pomodorosCompleted = 0;
        }

        void incrementPomodoros() {
            pomodorosCompleted++;
        }

        @Override
        public String toString() {
            return description + " (" + pomodorosCompleted + " pomodoros)";
        }
    }

    // -----------------------------------------------------------
    //                         MAIN METHOD
    // -----------------------------------------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(JavaApplication5::new);
    }
}
