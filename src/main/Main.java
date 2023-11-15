package main;

import java.util.Scanner;
import java.util.Vector;

public class Main {
    private enum SIGNALS {
        DONE, PRINT, KILL
    }

    // Vector is a syncronized list. Used for Thread safety.
    // If you use an ArrayList, you will never print the value due to locking
    // problems.
    private static Vector<SIGNALS> signalQueue = new Vector<>();

    public static void main(String[] args) throws InterruptedException {
        Thread counterThread = new Thread(Main::counterFunction);
        Thread cliThread = new Thread(Main::cliFunction);

        counterThread.start();
        cliThread.start();

        counterThread.join();
        cliThread.join();

        System.out.println("Main thread ended");
    }

    private static void counterFunction() {
        int counter = 0;
        while (!signalQueue.contains(SIGNALS.KILL)) {
            counter++;

            if (signalQueue.contains(SIGNALS.PRINT)) {
                signalQueue.remove(SIGNALS.PRINT);
                System.out.println(counter);
                signalQueue.add(SIGNALS.DONE);
            }
        }

        System.out.println("Thread 1 ended");
    }

    private static void cliFunction() {
        Scanner sc = new Scanner(System.in);
        String line;

        do {
            System.out.println("Write Y for printing value");
            System.out.println("Write N for exiting");

            line = sc.nextLine();

            if (line.equals("Y")) {
                signalQueue.add(SIGNALS.PRINT);

                while (!signalQueue.contains(SIGNALS.DONE)) {
                    // Wait until the counter thread sends the print.
                }

                signalQueue.remove(SIGNALS.DONE);
                continue;
            }

            System.out.println("Invalid option");

        } while (!line.equals("N"));

        signalQueue.add(SIGNALS.KILL);
        sc.close();

        System.out.println("Thread 2 ended");
    }
}