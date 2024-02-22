package application;

import java.util.InputMismatchException;
import java.util.Scanner;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class SJFJavaFXforFinals extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		Scanner scanner = new Scanner(System.in);

		while (true) {
			System.out.println("-------------------------------");
			System.out.println("      Shortest Job First       ");
			System.out.println("-------------------------------");

			int numProcesses = 0;
			boolean validInput = false;

			while (!validInput) {
				try {
					System.out.print("Enter the number of processes: ");
					numProcesses = scanner.nextInt();
					validInput = true;
				} catch (InputMismatchException e) {
					scanner.nextLine();
					System.out.println("Invalid input. Please enter a valid integer.");
				}
			}

			int[] arrivalTime = new int[numProcesses];
			int[] burstTime = new int[numProcesses];

			for (int i = 0; i < numProcesses; i++) {
				validInput = false;
				while (!validInput) {
					try {
						System.out.print("Enter Arrival Time for Process " + (i + 1) + ": ");
						arrivalTime[i] = scanner.nextInt();
						validInput = true;
					} catch (InputMismatchException e) {
						scanner.nextLine();
						System.out.println("Invalid input. Please enter a valid integer for Arrival Time.");
					}
				}

				validInput = false;
				while (!validInput) {
					try {
						System.out.print("Enter Burst Time for Process " + (i + 1) + ": ");
						burstTime[i] = scanner.nextInt();
						validInput = true;
					} catch (InputMismatchException e) {
						scanner.nextLine();
						System.out.println("Invalid input. Please enter a valid integer for Burst Time.");
					}
				}
			}

			int[] processOrder = new int[numProcesses];
			for (int i = 0; i < numProcesses; i++) {
				processOrder[i] = i;
			}
			for (int i = 0; i < numProcesses - 1; i++) {
				for (int j = 0; j < numProcesses - i - 1; j++) {
					if (burstTime[j] > burstTime[j + 1]) {
						int temp = burstTime[j];
						burstTime[j] = burstTime[j + 1];
						burstTime[j + 1] = temp;
						temp = processOrder[j];
						processOrder[j] = processOrder[j + 1];
						processOrder[j + 1] = temp;
					}
				}
			}

			int[] completedTime = new int[numProcesses];
			int[] turnaroundTime = new int[numProcesses];
			int[] waitingTime = new int[numProcesses];

			int totalTime = 0;
			double avgWaitingTime = 0.0;
			double avgTurnaroundTime = 0.0;

			System.out.println("\nProcess Table:");
			System.out.println();
			System.out.println("-------------------------------------------------------------------------------------");
			System.out.println("| PID | Arrival Time | Burst Time | Completed Time | Turnaround Time | Waiting Time |");
			System.out.println("-------------------------------------------------------------------------------------");

			for (int i = 0; i < numProcesses; i++) {
				int processIndex = processOrder[i];
				completedTime[processIndex] = totalTime + burstTime[processIndex];
				turnaroundTime[processIndex] = completedTime[processIndex] - arrivalTime[processIndex];
				waitingTime[processIndex] = turnaroundTime[processIndex] - burstTime[processIndex];
				totalTime = completedTime[processIndex];

				System.out.printf("| %-3d | %-12d | %-10d | %-14d | %-15d | %-12d |\n", processIndex + 1,
						arrivalTime[processIndex], burstTime[processIndex], completedTime[processIndex],
						turnaroundTime[processIndex], waitingTime[processIndex]);
			}

			System.out.println("-------------------------------------------------------------------------------------");
			System.out.println("\nThe Total Time is: " + totalTime);

			for (int i = 0; i < numProcesses; i++) {
				avgWaitingTime += Math.max(0, waitingTime[i]);
				avgTurnaroundTime += turnaroundTime[i];
			}

			avgWaitingTime /= numProcesses;
			avgTurnaroundTime /= numProcesses;

			System.out.printf("\nThe Average WT is: %.2fms\n", avgWaitingTime);
			System.out.printf("The Average TA is: %.2fms\n", avgTurnaroundTime);

			launchGanttChartWindow(primaryStage, completedTime, totalTime, avgWaitingTime, avgTurnaroundTime);

			System.out.print("\nProcess completed. Do you want to Try again? (Y/N): ");
			String restartOption = scanner.next();
			if (!restartOption.equalsIgnoreCase("Y")) {
				break;
			}
		}
		System.out.println();
		System.out.println("Submitted by: Iseiah O. Escaño");
		System.out.println("BSCPE-2A Operating Systems Finals");
		scanner.close();
	}

	private void launchGanttChartWindow(Stage primaryStage, int[] completionTimes, int totalTime,
			double averageWaitingTime, double averageTurnaroundTime) {
		Pane root = new Pane();
		double scale = 50.0;

		Text title = new Text("Gantt Chart Table");
		title.setFont(new Font("Comic Sans MS", 20));
		title.setX(30);
		title.setY(30);

		Text totalTimeText = new Text("The Total Time is: " + totalTime);
		totalTimeText.setFont(new Font("Comic Sans MS", 12));
		totalTimeText.setX(30);
		totalTimeText.setY(120);

		Text avgWaitingTimeText = new Text("The Average WT is: " + String.format("%.2fms", averageWaitingTime));
		avgWaitingTimeText.setFont(new Font("Comic Sans MS", 12));
		avgWaitingTimeText.setX(30);
		avgWaitingTimeText.setY(140);

		Text avgTurnaroundTimeText = new Text("The Average TA is: " + String.format("%.2fms", averageTurnaroundTime));
		avgTurnaroundTimeText.setFont(new Font("Comic Sans MS", 12));
		avgTurnaroundTimeText.setX(30);
		avgTurnaroundTimeText.setY(160);

		root.getChildren().addAll(title, totalTimeText, avgWaitingTimeText, avgTurnaroundTimeText);

		double xPos = 0;
		for (int i = 0; i < completionTimes.length; i++) {
			double width = (i == 0 ? completionTimes[i] : completionTimes[i] - completionTimes[i - 1]) * scale;

			Rectangle rect = new Rectangle(xPos, 50, width, 20);
			rect.setStroke(Color.BLACK);
			rect.setFill(Color.TRANSPARENT);

			Text text = new Text(xPos + 5, 65, "P" + (i + 1));

			root.getChildren().addAll(rect, text);
			xPos += width;
		}

		for (int i = 0; i <= totalTime; i++) {
			Text timeText = new Text(scale * i - (i < 10 ? 3 : 7), 85, String.valueOf(i));
			root.getChildren().add(timeText);
		}

		ScrollPane scrollPane = new ScrollPane(root);
		scrollPane.setPrefViewportWidth(600);
		scrollPane.setPrefViewportHeight(300);
		scrollPane.setPannable(true);

		primaryStage.setTitle("Gantt Chart | Shortest Job First");
		primaryStage.setScene(new Scene(scrollPane));
		primaryStage.show();
	}
}