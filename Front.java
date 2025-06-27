import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

class Stack
{
	int top=0;
	String[] arr=new String[5];
    public Stack() {
    try {
        File Obj = new File("stack.txt");
        Scanner Reader = new Scanner(Obj);
		arr[0]="recent searches:";
		arr[4]="---------------";
        int i = 1;
        while (Reader.hasNextLine()) {
			if(i<4)
			{
            arr[i] = Reader.nextLine();
            i++;
			}
        }
		top=i-1;
        Reader.close();
    } catch (Exception e) {
        System.out.print("error");
    }
	}
	public void push(String data)
	{
		try{
		String temp;
		int p=1;
		if(top>=3)
		{
			temp=arr[p+1];
			arr[p+1]=arr[p];
			arr[p+2]=temp;
			arr[p]=data;
		}
		else
		{
			top++;
			arr[top]=data;
		}
		BufferedWriter bw=new BufferedWriter(new FileWriter("stack.txt"));
		for(int i=arr.length-2;i>=1;i--)
		{
			 bw.write(arr[i]);
             bw.newLine();
		}
		bw.close();
		}
		catch(IOException e)
		{
			System.out.println("Error while writing to the file: " + e.getMessage());
		}
	}
	
	public void pop(String data)
	{
			top--;
		
	}
	public String[] getStack()
	{
		return arr;
	}
	public String[] joinArray(String[] x,String[] y)
	{
		String[] z=new String[x.length+y.length];
		int j=0;
		int k=0;
		for(int i=0;i<x.length+y.length;i++)
		{
			if(i<5)
			{
				z[i]=x[j];
				j++;
			}
			else
			{
				z[i]=y[k];
				k++;
			}
		}
		return z;
	}
	
}
class Front extends JFrame implements ActionListener
{
	
	JLabel l;
	JTextField tf;
	JButton b;
	Stack st=new Stack();
	String[] stk=st.getStack();
	String[] textdata={"rahul","vikrant","abhishek","mayank","navneet"};
	String[] items=st.joinArray(stk,textdata);
    JComboBox<String> cb = new JComboBox<>(items);
	JFrame f=new JFrame();
	Front()
	{
		
		f.setSize(600,350);
		f.setLocation(450,250);
		f.setTitle("Predictive text example");
		f.setLayout(null);
		l=new JLabel("please enter the text");
		tf=new JTextField();
		b=new JButton("search");
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		l.setBounds(75,100,200,30);
		tf.setBounds(75,135,350,30);
		b.setBounds(428,135,80,30);
		cb.setBounds(75,135,350,30);
		cb.setVisible(false);
		tf.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateSuggestions();
            }
            public void removeUpdate(DocumentEvent e) {
                updateSuggestions();
            }
            public void changedUpdate(DocumentEvent e) {
                updateSuggestions();
            }
            private void updateSuggestions() {
                String input = tf.getText();
                if (input.isEmpty()) {
                    cb.setVisible(false);
                    return;
                }
				cb.setVisible(true);
                cb.setPopupVisible(true);
            }
        });
		f.add(l);
		f.add(tf);
		f.add(b);
		f.add(cb);
		
		InputMap inputMap = tf.getInputMap(JComponent.WHEN_FOCUSED);
		ActionMap actionMap = tf.getActionMap();

		inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "enterKey");
		actionMap.put("enterKey", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				String t,temp;
				t=tf.getText();
				if(!tf.getText().trim().isEmpty())
				{
					l.setText(t);
					st.push(t);
				}
				String[] recent=new String[3];
				recent=st.getStack();
				String[] textdata={"rahul","vikrant","abhishek","mayank","navneet"};
				String[] newitems=st.joinArray(recent,textdata);
				cb.setVisible(false);
				cb.setModel(new DefaultComboBoxModel<>(newitems));
			}
		});
	
		b.addActionListener(this);
		cb.addActionListener(this);
		f.setVisible(true);
	}
	public void actionPerformed(ActionEvent ae)
	{
		if(ae.getSource()==cb)
		{
			if(cb.getSelectedItem() != null)
			{
				if(cb.getSelectedItem().equals("recent searches:"))
				{
					cb.setVisible(true);
					cb.setPopupVisible(true);
				}
				else if(cb.getSelectedItem().equals("---------------"))
				{
					cb.setVisible(true);
					cb.setPopupVisible(true);
				}
				else
				{
					tf.setText(cb.getSelectedItem().toString());
				}
			}
		}
		if(ae.getSource()==b)
		{
		String t;
		t=tf.getText();
		if(!tf.getText().trim().isEmpty() || !tf.getText().equals("recent searches:"))
		{
			l.setText(t);
			st.push(t);
		}
		String[] recent=new String[4];
		recent=st.getStack();
		String[] textdata={"rahul","vikrant","abhishek","mayank","navneet"};
		String[] newitems=st.joinArray(recent,textdata);
		cb.setVisible(false);
		cb.setModel(new DefaultComboBoxModel<>(newitems));
		}
		
	}
	public static void main(String[] args)
	{
		Front obj=new Front();
		String url = "jdbc:mysql://localhost:3306/yourDatabaseName"; // Change this to your MySQL database URL
        String username = "yourUsername";                          // Your MySQL username
        String password = "yourPassword";                          // Your MySQL password

        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected successfully without third-party software!");
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
	}
	
}
