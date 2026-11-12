import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;
import javax.sound.sampled.*;
import java.time.*;

class Task implements Serializable {

    String name, priority, date, time;
    boolean isCompleted;
    boolean notified;

    Task(String name, String priority, String date, String time) {

        this.name = name;
        this.priority = priority;
        this.date = date;
        this.time = time;

        this.isCompleted = false;
        this.notified = false;
    }

    public String toString() {

        return name + " [" + priority + "] on "
                + date + " at " + time + " - "
                + (isCompleted ? "Done" : "Pending");
    }
}

public class DailyLifeOptimizerAdvanced {

    static ArrayList<Task> taskList = new ArrayList<>();

    static DefaultListModel<String> listModel =
            new DefaultListModel<>();

    static JList<String> taskJList =
            new JList<>(listModel);

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> createGUI());
    }

    static void createGUI() {

        JFrame frame =
                new JFrame("Daily Life Optimizer Advanced");

        frame.setSize(700, 500);

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new BorderLayout());

        // TOP PANEL
        JPanel top = new JPanel();

        JTextField taskField = new JTextField(10);

        JTextField dateField = new JTextField(10);
        // yyyy-MM-dd

        JTextField timeField = new JTextField(5);
        // HH:mm

        String[] priorities = {"High", "Medium", "Low"};

        JComboBox<String> priorityBox =
                new JComboBox<>(priorities);

        JButton addBtn = new JButton("Add Task");

        top.add(new JLabel("Task"));
        top.add(taskField);

        top.add(new JLabel("Priority"));
        top.add(priorityBox);

        top.add(new JLabel("Date"));
        top.add(dateField);

        top.add(new JLabel("Time"));
        top.add(timeField);

        top.add(addBtn);

        // CENTER PANEL
        JScrollPane scroll =
                new JScrollPane(taskJList);

        // BOTTOM PANEL
        JPanel bottom = new JPanel();

        JButton completeBtn =
                new JButton("Complete");

        JButton deleteBtn =
                new JButton("Delete");

        JButton saveBtn =
                new JButton("Save");

        JButton loadBtn =
                new JButton("Load");

        bottom.add(completeBtn);
        bottom.add(deleteBtn);
        bottom.add(saveBtn);
        bottom.add(loadBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        frame.add(panel);

        frame.setVisible(true);

        // ADD TASK
        addBtn.addActionListener(e -> {

            String name = taskField.getText().trim();

            String priority =
                    (String) priorityBox.getSelectedItem();

            String date =
                    dateField.getText().trim();

            String time =
                    timeField.getText().trim();

            if (name.isEmpty()
                    || date.isEmpty()
                    || time.isEmpty()) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Please fill all fields!"
                );

                return;
            }

            // DATE/TIME VALIDATION
            try {

                LocalDate.parse(date);

                LocalTime.parse(time);

            } catch (Exception ex) {

                JOptionPane.showMessageDialog(
                        frame,
                        "Wrong Format!\n\n"
                                + "Date = yyyy-MM-dd\n"
                                + "Time = HH:mm"
                );

                return;
            }

            Task t = new Task(
                    name,
                    priority,
                    date,
                    time
            );

            taskList.add(t);

            listModel.addElement(t.toString());

            taskField.setText("");
            dateField.setText("");
            timeField.setText("");
        });

        // COMPLETE TASK
        completeBtn.addActionListener(e -> {

            int i = taskJList.getSelectedIndex();

            if (i != -1) {

                taskList.get(i).isCompleted = true;

                listModel.set(
                        i,
                        taskList.get(i).toString()
                );
            }
        });

        // DELETE TASK
        deleteBtn.addActionListener(e -> {

            int i = taskJList.getSelectedIndex();

            if (i != -1) {

                taskList.remove(i);

                listModel.remove(i);
            }
        });

        // SAVE TASKS
        saveBtn.addActionListener(e -> saveTasks());

        // LOAD TASKS
        loadBtn.addActionListener(e -> loadTasks());

        // REMINDER TIMER
        new javax.swing.Timer(
                1000,
                e -> checkReminder(frame)
        ).start();
    }

    // REMINDER CHECK
    static void checkReminder(JFrame frame) {

        LocalDate today = LocalDate.now();

        LocalTime now =
                LocalTime.now()
                        .withSecond(0)
                        .withNano(0);

        for (Task t : taskList) {

            if (!t.isCompleted && !t.notified) {

                try {

                    LocalDate taskDate =
                            LocalDate.parse(t.date);

                    LocalTime taskTime =
                            LocalTime.parse(t.time);

                    // REMINDER CONDITION
                    if (today.equals(taskDate)
                            && !now.isBefore(taskTime)) {

                        playSound();

                        JOptionPane.showMessageDialog(
                                frame,
                                "Reminder: " + t.name
                        );

                        t.notified = true;
                    }

                } catch (Exception ex) {

                    System.out.println(
                            "Date/Time Error"
                    );
                }
            }
        }
    }

    // SOUND
    static void playSound() {

        try {

            File file =
                    new File("alert.wav");

            if (file.exists()) {

                AudioInputStream audio =
                        AudioSystem.getAudioInputStream(file);

                Clip clip =
                        AudioSystem.getClip();

                clip.open(audio);

                clip.start();

            } else {

                Toolkit.getDefaultToolkit().beep();
            }

        } catch (Exception e) {

            Toolkit.getDefaultToolkit().beep();
        }
    }

    // SAVE TASKS
    static void saveTasks() {

        try {

            ObjectOutputStream out =
                    new ObjectOutputStream(
                            new FileOutputStream("tasks.dat")
                    );

            out.writeObject(taskList);

            out.close();

            JOptionPane.showMessageDialog(
                    null,
                    "Tasks Saved Successfully!"
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Save Error!"
            );
        }
    }

    // LOAD TASKS
    @SuppressWarnings("unchecked")

    static void loadTasks() {

        try {

            ObjectInputStream in =
                    new ObjectInputStream(
                            new FileInputStream("tasks.dat")
                    );

            taskList =
                    (ArrayList<Task>) in.readObject();

            in.close();

            listModel.clear();

            for (Task t : taskList) {

                listModel.addElement(
                        t.toString()
                );
            }

            JOptionPane.showMessageDialog(
                    null,
                    "Tasks Loaded Successfully!"
            );

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    null,
                    "Load Error!"
            );
        }
    }
}