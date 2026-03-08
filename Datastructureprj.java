package com.mycompany.datastructureprj;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

// Simple Task class
class Task {
    String description;
    boolean done;

    Task(String description) {
        this.description = description;
        this.done = false;
    }
}

// Common interface for both implementations
interface TaskList {
    void addTask(String description);
    void removeTask(int index);
    void markDone(int index);
    void displayTasks();
    void sortTasks(int sortType); // 1: Bubble, 2: Selection, 3: Insertion
}

// ArrayList implementation
class ArrayListTaskList implements TaskList {
    private List<Task> tasks = new ArrayList<>();

    public void addTask(String description) {
        tasks.add(new Task(description));
    }

    public void removeTask(int index) {
        if (index < 0 || index >= tasks.size()) {
            System.out.println("Invalid task number.");
            return;
        }
        tasks.remove(index);
        System.out.println("Task deleted.");
    }

    public void markDone(int index) {
        if (index < 0 || index >= tasks.size()) {
            System.out.println("Invalid task number.");
            return;
        }
        tasks.get(index).done = true;
        System.out.println("Task marked as completed.");
    }

    public void displayTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks.");
            return;
        }
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            String status = t.done ? "[X]" : "[ ]";
            System.out.println((i + 1) + ". " + status + " " + t.description);
        }
    }

    public void sortTasks(int sortType) {
        switch (sortType) {
            case 1 -> bubbleSort();
            case 2 -> selectionSort();
            case 3 -> insertionSort();
            default -> System.out.println("Invalid sort type.");
        }
    }

    private void bubbleSort() {
        int n = tasks.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (tasks.get(j).description.compareToIgnoreCase(tasks.get(j + 1).description) > 0) {
                    Task temp = tasks.get(j);
                    tasks.set(j, tasks.get(j + 1));
                    tasks.set(j + 1, temp);
                }
            }
        }
        System.out.println("Tasks sorted using Bubble Sort.");
    }

    private void selectionSort() {
        int n = tasks.size();
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (tasks.get(j).description.compareToIgnoreCase(tasks.get(minIndex).description) < 0) {
                    minIndex = j;
                }
            }
            Task temp = tasks.get(minIndex);
            tasks.set(minIndex, tasks.get(i));
            tasks.set(i, temp);
        }
        System.out.println("Tasks sorted using Selection Sort.");
    }

    private void insertionSort() {
        int n = tasks.size();
        for (int i = 1; i < n; i++) {
            Task key = tasks.get(i);
            int j = i - 1;
            while (j >= 0 && tasks.get(j).description.compareToIgnoreCase(key.description) > 0) {
                tasks.set(j + 1, tasks.get(j));
                j--;
            }
            tasks.set(j + 1, key);
        }
        System.out.println("Tasks sorted using Insertion Sort.");
    }
}

// LinkedList implementation
class LinkedListTaskList implements TaskList {
    private List<Task> tasks = new LinkedList<>();

    public void addTask(String description) {
        tasks.add(new Task(description));
    }

    public void removeTask(int index) {
        if (index < 0 || index >= tasks.size()) {
            System.out.println("Invalid task number.");
            return;
        }
        tasks.remove(index);
        System.out.println("Task deleted.");
    }

    public void markDone(int index) {
        if (index < 0 || index >= tasks.size()) {
            System.out.println("Invalid task number.");
            return;
        }
        tasks.get(index).done = true;
        System.out.println("Task marked as completed.");
    }

    public void displayTasks() {
        if (tasks.isEmpty()) {
            System.out.println("No tasks.");
            return;
        }
        for (int i = 0; i < tasks.size(); i++) {
            Task t = tasks.get(i);
            String status = t.done ? "[X]" : "[ ]";
            System.out.println((i + 1) + ". " + status + " " + t.description);
        }
    }

    public void sortTasks(int sortType) {
        switch (sortType) {
            case 1 -> bubbleSort();
            case 2 -> selectionSort();
            case 3 -> insertionSort();
            default -> System.out.println("Invalid sort type.");
        }
    }

    private void bubbleSort() {
        int n = tasks.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (tasks.get(j).description.compareToIgnoreCase(tasks.get(j + 1).description) > 0) {
                    Task temp = tasks.get(j);
                    tasks.set(j, tasks.get(j + 1));
                    tasks.set(j + 1, temp);
                }
            }
        }
        System.out.println("Tasks sorted using Bubble Sort.");
    }

    private void selectionSort() {
        int n = tasks.size();
        for (int i = 0; i < n - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (tasks.get(j).description.compareToIgnoreCase(tasks.get(minIndex).description) < 0) {
                    minIndex = j;
                }
            }
            Task temp = tasks.get(minIndex);
            tasks.set(minIndex, tasks.get(i));
            tasks.set(i, temp);
        }
        System.out.println("Tasks sorted using Selection Sort.");
    }

    private void insertionSort() {
        int n = tasks.size();
        for (int i = 1; i < n; i++) {
            Task key = tasks.get(i);
            int j = i - 1;
            while (j >= 0 && tasks.get(j).description.compareToIgnoreCase(key.description) > 0) {
                tasks.set(j + 1, tasks.get(j));
                j--;
            }
            tasks.set(j + 1, key);
        }
        System.out.println("Tasks sorted using Insertion Sort.");
    }
}

// Main public class matches file name
public class Datastructureprj {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        System.out.println("Choose implementation:");
        System.out.println("1. ArrayList");
        System.out.println("2. LinkedList");
        System.out.print("Your choice: ");
        int choice = in.nextInt();
        in.nextLine(); // clear newline

        TaskList tasks = (choice == 2) ? new LinkedListTaskList() : new ArrayListTaskList();
        System.out.println((choice == 2 ? "Using LinkedList" : "Using ArrayList") + " implementation.\n");

        int option;
        do {
            System.out.println("\n--- To-Do List Menu ---");
            System.out.println("1. Add new task");
            System.out.println("2. View all tasks");
            System.out.println("3. Mark task as completed");
            System.out.println("4. Delete task");
            System.out.println("5. Sort tasks");
            System.out.println("0. Exit");
            System.out.print("Choose: ");
            option = in.nextInt();
            in.nextLine(); // clear newline

            switch (option) {
                case 1 -> {
                    System.out.print("Enter task description: ");
                    String desc = in.nextLine();
                    tasks.addTask(desc);
                }
                case 2 -> tasks.displayTasks();
                case 3 -> {
                    System.out.print("Enter task number to mark as done: ");
                    int doneIndex = in.nextInt() - 1;
                    tasks.markDone(doneIndex);
                }
                case 4 -> {
                    System.out.print("Enter task number to delete: ");
                    int delIndex = in.nextInt() - 1;
                    tasks.removeTask(delIndex);
                }
                case 5 -> {
                    System.out.println("Choose sorting method:");
                    System.out.println("1. Bubble Sort");
                    System.out.println("2. Selection Sort");
                    System.out.println("3. Insertion Sort");
                    int sortChoice = in.nextInt();
                    tasks.sortTasks(sortChoice);
                }
                case 0 -> System.out.println("Goodbye!");
                default -> System.out.println("Invalid option.");
            }

        } while (option != 0);

        in.close();
    }
}
