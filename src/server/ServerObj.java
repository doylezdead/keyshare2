package server;

import java.applet.Applet;
import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;

public class ServerObj extends Applet {

	protected Dimension clientSS;
	protected Dimension serverSS = Toolkit.getDefaultToolkit().getScreenSize();
	private ObjectInputStream ois = null;
	private int wRatio = serverSS.width / clientSS.width;
	private int hRatio = serverSS.height / clientSS.height;
	WorkerRobot robot = null;

	public void init() {
		new ServerGUI();
	}

	public class ServerGUI {

		protected JButton end;

		public ServerGUI() {
			// Create a new JFrame.
			JFrame gui = new JFrame();

			// set up grid layout with 1 row and column.
			gui.setLayout(new GridLayout(1, 1));

			// make the program terminate when the window is closed
			gui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			// give the window a title
			gui.setTitle("Server Control");

			// set the size of the window
			gui.setSize(250, 200);
			System.out.println("run");
			end = new JButton("End");
			end.addActionListener(new ServerListener(gui));
			gui.add(end);
			gui.setVisible(true);
			new Server().start();
		}
	}

	public class ServerListener implements ActionListener {
		public JFrame Gui;

		public ServerListener(JFrame g) {
			Gui = g;
		}

		public void actionPerformed(ActionEvent e) {
			System.exit(1);

		}
	}

	public class Server extends Thread {
		public Server() {
			this.run();
		}

		public void run() {
			robot = new WorkerRobot();
			try {

				System.out.println("Waiting for connections ...");
				ServerSocket srvr = new ServerSocket(13337);
				Socket skt = srvr.accept();
				System.out.print("Client has connected to the server!!!\n");

				ois = new ObjectInputStream(skt.getInputStream()); // skt's
				// input
				// stream

				clientSS = (Dimension) ois.readObject();
				clientSS.setSize(clientSS.width / 3, clientSS.height / 3);

				PrintWriter out = new PrintWriter(skt.getOutputStream(), true); // skt's
				// output
				// stream

				int[] readArray = null;

				while (true) {
					readArray = (int[]) ois.readObject();
					if (readArray[0] == 10)
						break;

					for (int i = 0; i < readArray.length; i++) {
						System.out.print(readArray[i] + ", ");
					}
					System.out.println();
					robot.work(readArray);
				}

				out.close(); //
				skt.close(); // close all resources
				srvr.close(); //
			} catch (Exception e) {
				e.printStackTrace();// "No connection found\n");
			}
		}
	}

	public class WorkerRobot {
		private Robot walle;

		// constructor
		public WorkerRobot() {
			try {
				walle = new Robot();
			} catch (AWTException e) {
				System.out.println("Wall-e has died");
			}
		}

		/**
		 * recieves an array of integers [0]: (0,1,2,3) 0: mouse click [1]:
		 * (0,1) 0: mouse press 1: mouse release [2]: (0,1,2) 0: left button 1:
		 * middle button 2: right button 1: mouse wheel [1]: number of notches
		 * 2: mouse motion [1]: x coordinates [2]: y coordinates 3: keyboard
		 * [1]: (0,1) 0: key press 1: key release [2]: key number
		 */
		public void work(int[] task) {
			// mouse click
			if (task[0] == 0) {
				// mouse press
				if (task[1] == 0) {
					// left
					if (task[2] == 0) {
						walle.mousePress(InputEvent.BUTTON1_MASK);
					}
					// middle
					else if (task[2] == 1) {
						walle.mousePress(InputEvent.BUTTON2_MASK);
					}
					// right
					else if (task[2] == 2) {
						walle.mousePress(InputEvent.BUTTON3_MASK);
					}
				}
				// mouse release
				else if (task[1] == 1) {
					if (task[2] == 0) {
						walle.mouseRelease(InputEvent.BUTTON1_MASK);
					}
					// middle
					else if (task[2] == 1) {
						walle.mouseRelease(InputEvent.BUTTON2_MASK);
					}
					// right
					else if (task[2] == 2) {
						walle.mouseRelease(InputEvent.BUTTON3_MASK);
					}
				}
			}
			// mouse wheel
			else if (task[0] == 1) {
				walle.mouseWheel(task[1]);
			}
			// mouse motion
			else if (task[0] == 2) {
				walle.mouseMove(task[1] * wRatio, task[2] * hRatio);

			}
			// keyboard
			else if (task[0] == 3) {
				// key press
				if (task[1] == 0) {
					walle.keyPress(task[2]);
				}
				// key release
				else if (task[1] == 1) {
					walle.keyRelease(task[2]);
				}
			}
		}
	}
}