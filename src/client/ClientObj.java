package client;

import java.applet.Applet;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class ClientObj extends Applet {
	private InputReader ir;
	private Socket skt = null;
	private ObjectOutputStream oos = null;
	private int commands[] = new int[3];
	protected Dimension clientSS = Toolkit.getDefaultToolkit().getScreenSize();

	@Override
	public void init() {
		ir = new InputReader(this);
		addMouseListener(ir);
		addMouseWheelListener(ir);
		addMouseMotionListener(ir);
		addKeyListener(ir);
		new ClientGUI();
	}

	public class ClientGUI {
		protected JButton start;
		protected JTextField IP = new JTextField();
		protected JButton end;

		public ClientGUI() {
			JFrame cGui = new JFrame();
			// set up grid layout with rows and columns equal to size.
			cGui.setLayout(new GridLayout(3, 1));
			// make the program terminate when the window is closed
			cGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			// give the window a title
			cGui.setTitle("Client Initiator");
			// set the size of the window
			cGui.setSize(clientSS.width / 3, clientSS.height / 3);

			cGui.setLocation(clientSS.width - clientSS.width / 3,
					clientSS.height);

			IP = new JTextField("Enter a remote IP address");
			start = new JButton("Start");
			end = new JButton("End");

			start.addActionListener(new ClientListener(this, cGui, false));
			end.addActionListener(new ClientListener(this, cGui, true));

			cGui.add(IP);
			cGui.add(start);
			cGui.add(end);

			cGui.setVisible(true);
		}
	}

	public class ClientListener implements ActionListener {
		private ClientGUI Client;
		private boolean initiated = false;
		protected String address;
		public JFrame Gui;
		private boolean end;

		public ClientListener(ClientGUI b, JFrame g, boolean stop) {
			Client = b;
			Gui = g;
			end = stop;

		}

		public void actionPerformed(ActionEvent e) {
			if (end == true) {
				System.exit(1);

			} else if (initiated == true) {

			} else {
				Client.start.setText("Reconnect");
				address = Client.IP.getText();
				Client.IP.setEditable(false);
				Gui.setVisible(false);
				System.out.println("Client thread worked");
			}
			initiated = true;
			new Client(address).start();
		}
	}

	public class Client extends Thread {
		String address = null;

		public Client(String addr) {
			address = addr;
			this.run(addr);
		}

		public Client getInstance() {
			return this;
		}

		public void run(String addr) {
			System.out.println("RUNNING");
			for (int i = 0; i <= 1; i++) {
				try {
					System.out.println(address);
					skt = new Socket(address, 13337);
					oos = new ObjectOutputStream(skt.getOutputStream());
					oos.writeObject(clientSS);

					break;
				} catch (Exception e) {
					if (i == 0) {
						System.out
								.println("Reattempting connection, wait 10 seconds.");
						try {
							Thread.sleep(10000);
						} catch (InterruptedException e1) {

						}
					} else {
						System.out.println("Could not find connection");
					}
				}
			}
		}
	}

	private class InputReader implements MouseListener, MouseWheelListener,
			MouseMotionListener, KeyListener {

		private Applet mParent;

		private InputReader(Applet aParent) {
			mParent = aParent;
			mParent.setSize(clientSS.width / 3, clientSS.height / 3);
			mParent.setLocation(0, clientSS.height);
		}

		// MouseListener stuff
		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			try {
				oos.reset();
			} catch (IOException e2) {
				e2.printStackTrace();
			}

			commands[0] = 0;
			commands[1] = 0;
			switch (e.getModifiers()) {
			case InputEvent.BUTTON1_MASK: {
				// System.out.println("LEFT PRESS");
				commands[2] = 0;

				try {
					oos.writeObject(commands);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				commands[0] = -1;
				commands[1] = -1;
				commands[2] = -1;
				break;
			}
			case InputEvent.BUTTON2_MASK: {
				// System.out.println("MIDDLE PRESS");
				commands[2] = 1;
				try {
					oos.writeObject(commands);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				commands[0] = -1;
				commands[1] = -1;
				commands[2] = -1;
				break;
			}
			case InputEvent.BUTTON3_MASK: {
				// System.out.println("RIGHT PRESS");
				commands[2] = 2;

				try {
					oos.writeObject(commands);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				commands[0] = -1;
				commands[1] = -1;
				commands[2] = -1;
				break;
			}
			}

		}

		@Override
		public void mouseReleased(MouseEvent e) {
			try {
				oos.reset();
			} catch (IOException e2) {
				e2.printStackTrace();
			}

			commands[0] = 0;
			commands[1] = 1;
			switch (e.getModifiers()) {
			case InputEvent.BUTTON1_MASK: {
				// System.out.println("LEFT RELEASE");
				commands[2] = 0;
				try {
					oos.writeObject(commands);
				} catch (IOException e1) {
					e1.printStackTrace();
				}

				commands[0] = -1;
				commands[1] = -1;
				commands[2] = -1;
				break;
			}
			case InputEvent.BUTTON2_MASK: {
				// System.out.println("MIDDLE RELEASE");
				commands[2] = 1;
				try {
					oos.writeObject(commands);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				commands[0] = -1;
				commands[1] = -1;
				commands[2] = -1;
				break;
			}
			case InputEvent.BUTTON3_MASK: {
				// System.out.println("RIGHT RELEASE");
				commands[2] = 2;
				try {
					oos.writeObject(commands);
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				commands[0] = -1;
				commands[1] = -1;
				commands[2] = -1;
				break;
			}
			}
		}

		// MouseWheelListener stuff
		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			try {
				oos.reset();
			} catch (IOException e2) {
				e2.printStackTrace();
			}

			commands[0] = 1;
			try {
				oos.writeObject(commands);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			commands[0] = -1;
			commands[1] = -1;
			commands[2] = -1;
			int rotation = e.getWheelRotation();
			commands[1] = rotation;
			// System.out.println(rotation);
		}

		// MouseMotionListener stuff
		@Override
		public void mouseDragged(MouseEvent e) {
			try {
				oos.reset();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			commands[0] = 2;

			Point coords = MouseInfo.getPointerInfo().getLocation();
			commands[1] = (int) coords.getX();
			commands[2] = (int) coords.getY();

			try {
				oos.writeObject(commands);

			} catch (IOException e1) {
				e1.printStackTrace();
			}

			commands[0] = -1;
			commands[1] = -1;
			commands[2] = -1;
			// System.out.println("x:" + coords.getX() + " y:" + coords.getY());
		}

		@Override
		public void mouseMoved(MouseEvent e) {
			try {
				oos.reset();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			commands[0] = 2;

			Point coords = MouseInfo.getPointerInfo().getLocation();
			commands[1] = (int) coords.getX();
			commands[2] = (int) coords.getY();

			try {
				oos.writeObject(commands);

			} catch (IOException e1) {
				e1.printStackTrace();
			}

			commands[0] = -1;
			commands[1] = -1;
			commands[2] = -1;
			// System.out.println("x:" + coords.getX() + " y:" + coords.getY());
		}

		// KeyListener stuff
		@Override
		public void keyPressed(KeyEvent e) {
			try {
				oos.reset();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			commands[0] = 3;
			commands[1] = 0;
			int key = e.getKeyCode();
			commands[2] = key;
			try {
				oos.writeObject(commands);

			} catch (IOException e1) {
				e1.printStackTrace();
			}
			commands[0] = -1;
			commands[1] = -1;
			commands[2] = -1;
			// System.out.println(key + " PRESS");
		}

		@Override
		public void keyReleased(KeyEvent e) {
			try {
				oos.reset();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			commands[0] = 3;
			commands[1] = 1;
			int key = e.getKeyCode();
			commands[2] = key;

			try {
				oos.writeObject(commands);
				// for(int i = 0; i<3; i++){
				// System.out.print(commands[i]+", ");
				// }
				// System.out.println();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			commands[0] = -1;
			commands[1] = -1;
			commands[2] = -1;
			// System.out.println(key + " RELEASE");
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}
	}
}
