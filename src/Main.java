import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class Main extends JFrame implements ActionListener,FocusListener {

	private JTextField noSeats,noAgents,maxWait;
	private JButton createSeats,btnBook;	
	public JFrame frame;
	public JPanel seatsPanel;
	public List<JTextField> _seats; // List for new seats.
	private ArrayList<Thread> agents = new ArrayList<Thread>();  //ArrayList for Threads
	private ReentrantLock lock = new ReentrantLock();  //For Safe-Thread
	
	public Main() {
		_seats = new ArrayList<JTextField>(); 
		
		frame = new JFrame("Bus Book Application");
		frame.setLayout(null);
		frame.setSize(1000,1000);
		
		seatsPanel = new JPanel();// This panel will contain new text fields which will be created 
		seatsPanel.setLocation(0, 100); // location on the panel
		seatsPanel.setSize(1000,800); // size of the panel
		seatsPanel.setLayout(new GridLayout(10, 0, 10, 10)); // 10 row 0 column which means when every 10 row get full a new column will be created.
		
		//Initialize variables.
		noSeats = new JTextField("Number of Seats");
		noSeats.setSize(100,20);
		noSeats.setLocation(100,50);
		noSeats.addFocusListener(this);// After a number write it will clear inside of the textfield
		
		noAgents = new JTextField("Number of Agents");
		noAgents.setSize(100,20);
		noAgents.setLocation(240, 50);
		noAgents.addFocusListener(this);// After a number write it will clear inside of the textfield
		
		maxWait = new JTextField("Maximum Waiting Time");
		maxWait.setSize(150,20);
		maxWait.setLocation(380, 50);
		maxWait.addFocusListener(this);// After a number write it will clear inside of the textfield
		
		createSeats = new JButton("Create Seats");
		createSeats.setSize(150,20);
		createSeats.setLocation(570, 50);
		createSeats.addActionListener(this);
			
		btnBook = new JButton("Book");
		btnBook.setSize(150,20);
		btnBook.setLocation(760, 50);
		btnBook.addActionListener(this);
		
		frame.add(noSeats);
		frame.add(noAgents);
		frame.add(maxWait);
		frame.add(createSeats);
		frame.add(btnBook);
		frame.add(seatsPanel);
		
		frame.setVisible(true);
		
	}
	// This method will generate textfields on the screen for the number of seats entered and it add a List

	public void generateSeats(int count) {
		
		for(int i=0; i < count; i++) {
			
			JTextField seat = new JTextField("Not booked");//Text fields are produced
			seat.setEditable(false);
			seat.setSize(200, 70);// I couldn't enlarge the size of the textfield to show the text inside.This problem arises when there are more seats
			
			_seats.add(seat);//Adding new seat to list
			seatsPanel.add(seat);// adding new seat to panel
		}	
	}
	//This method works number of seats and threads will make the background color of the seats red
	public  void paintRelatedSeat(){
	
		String name = Thread.currentThread().getName(); //Get the name of the Thread
		String[] nameList = name.split("-");   //Used split for acquiring its number
		int refIndex = Integer.parseInt(nameList[1]) - 1;   //Converted String into an int and substracted 1 because Main Thread is the Thread 1
		for (int i =0; i<_seats.size(); i++) { 
			
			if(!(_seats.get(i).getBackground().equals(Color.red))) { // if seat background isnot red thread will work.
				lock.lock();
				int waitTime = Integer.parseInt(maxWait.getText());// Thread will sleep max. maxWait number.

				Random rd = new Random();
				int n = rd.nextInt(waitTime)+1;//+1 avoid 0 Sleep.
				
				_seats.get(i).setBackground(Color.red);	//background will be red.		
				_seats.get(i).setText("Booked by Agent "+ refIndex);
		
				lock.unlock();
				try{
					Thread.currentThread().sleep(n); // Thread sleep between 1 and entered number(waitTime).
					
				}catch(Exception e){
						e.printStackTrace();
				}
			}
		}
		if(Thread.currentThread().equals(agents.get(agents.size() - 1))) {   //Check if all the seats are taken
			showPane();   
		}
	}
	
	public void showPane() {   //Method for show Result 
		String message = "";
		ArrayList<Integer> messageList = new ArrayList<Integer>();   //ArrayList for pane
		int order = 1;   //The initial Agent(Thread) is 1
		
		for(int i=0; i<Integer.parseInt(noAgents.getText()); i++) {  //Adding elements until they reach the given agent number
			messageList.add(0);  //Their initial values are 0, they will be increased by the "howMany" method
		}
		
		while(order<=agents.size()) {  //This works until order reaches the size of the agents(threads)
			message = message +  "Agent " + order + " booked " + howMany(order) + " seats.\n"; //message keeps updating 
			order++;
		}

		JOptionPane.showMessageDialog(null, message);  //JOptionPane will show message on the screen.
		System.exit(0);  //To Exit Program
	}
	
	public int howMany(int order) {   //This method gives us how many times that the agent with the given number "order" has taken a seat as "count"
		int count = 0;
		for(int i=0; i<_seats.size(); i++) {    //checking every seat
			if(_seats.get(i).getText().equals("Booked by Agent " + order)) {  
				count++;
			}
		}
		return count;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		if(e.getSource().equals(btnBook)) { // actionType book seats >>> book button is clicked
			
			int agentNo = Integer.parseInt(noAgents.getText());
			
			for(int i = 0; i < agentNo; i++) { // This loop works agentNo times and it produce our threads.

					agents.add(new Thread(new Runnable() {
				
					@Override
					public void run() {
						paintRelatedSeat();   //All the Threads will go the same method
					}
				}));
				agents.get(i).start();   //Starting the Thread
			
			}
		}
		
		
		else if (e.getSource().equals(createSeats)) { // actionType createSeats >>> Create button is clicked
			int number = Integer.parseInt(noSeats.getText());

			generateSeats(number);
			//frame.add(seatsPanel);
			frame.setVisible(true);
		}		
	}

	public static void main(String[] args) {	
		new Main();
	}
	 
	@Override
	public void focusGained(FocusEvent e) {
		noSeats.selectAll();
		noAgents.selectAll();
		maxWait.selectAll();
		
	}
	@Override
	public void focusLost(FocusEvent e) {
	
		
	}
}

