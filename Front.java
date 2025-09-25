
package predictivetext;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

class Stack
{
	int top=0;
	String[] arr=new String[5];
    public Stack() {
    try {
        File obj = new File("stack.txt");
        Scanner reader = new Scanner(obj);
		arr[0]="recent searches:";
		arr[4]="---------------";
        int i = 1;
        while (reader.hasNextLine()) {
			if(i<4)
			{
	            arr[i] = reader.nextLine();
	            i++;
			}
        }
		top=i-1;
        reader.close();
    } 
    catch (Exception e) {
        System.out.print("error:"+e.getMessage());
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
		for(int i=1;i<4;i++)
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
	public String[] joinArray(String[] x,ArrayList<String> y)
	{
		String[] z=new String[x.length+y.size()];
		int j=0;
		int k=0;
		for(int i=0;i<x.length+y.size();i++)
		{
				if(i<5)
				{
					z[i]=x[j];
					j++;
				}
				else
				{
					z[i]=y.get(k);
					k++;
				}
		}
		
		return z;
	}
	
}
class Trie
{
	public Trie[] node;
	public int wordcount;
	boolean endOfWord=false;
	Trie()
	{
		node=new Trie[27];
		wordcount=0;
	}
	static void insert(Trie root, String key)
	{
	    Trie data = root;

	    for (int i = 0; i < key.length(); i++) {
	        int index;
	        if(key.charAt(i)==' ')
	        {
	            index=26;
	        }
	        else
	        {
	            index = key.charAt(i) - 'a';
	        }
	        if (data.node[index] == null) 
	        {

	            data.node[index] = new Trie();
	        }
	        data = data.node[index];
	        if(i==key.length()-1)
	        {
	            data.endOfWord=true;
	        }
	    }
	    data.wordcount++;
	}
    static boolean search(Trie root, String key)
	{
	    Trie data = root;

	    for (int i = 0; i < key.length(); i++) {
	        int index;
	        if(key.charAt(i)==' ')
	        {
	            index=26;
	        }
	        else
	        {
	            index = key.charAt(i) - 'a';
	        }
	        if (data.node[index] == null)
	            return false;
	        data = data.node[index];
	    }
	    return true;
	}
	static void fetchTrie(Trie tr,String prefix,ArrayList<String> al)
    {
        Trie data=tr;
        if(data==null)
        {
            return ;
        }
        if(data.endOfWord==true)
        {
    			 al.add(prefix);
        }
        
        for(int i=0;i<27;i++)
        {
            if(data.node[i]!=null)
            {
                char temp;
                if(i==26)
                {
                    temp=' ';
                }
                else
                {
                    temp=(char)(i+97);
                }
                fetchTrie(data.node[i],prefix+temp,al);
            }
            
        }
       
       
    }
	static Trie getPostPrefix(Trie tr,String str)
	{
		for(int i=0;i<str.length();i++)
		{
		    int val;
		    if(str.charAt(i)==' ')
		    {
		        val=26;
		    }
		    else
		    {
		        val=(int)(str.charAt(i))-97;
		    }
		    if(tr.node[val]!=null)
		    {
		        tr=tr.node[val];
		    }
		}
		return tr;
	}

}
class Front extends JFrame implements ActionListener
{
	
	JLabel l;
	JTextField tf;
	JButton b;
	Connection conn;
	Stack st=new Stack();
	String[] stk=st.getStack();
	ArrayList<String> textdata=new ArrayList<String>();
	String[] items=st.joinArray(stk,textdata);
    JComboBox<String> cb = new JComboBox<>(items);
	JFrame f=new JFrame();
	Front(Connection c)
	{
		conn=c;
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
		cb.setVisible(true);
		
		
         
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
                if (input.isEmpty()) 
                {
                    return;
                }
                else
                {
                		ArrayList<String> textdata=new ArrayList<String>();
                		String[] recent=new String[5];
        				recent=st.getStack();
                		char k=input.charAt(0);
                		String str=input;
                		Trie tr=new Trie();
                		ArrayList<String> list=new ArrayList<String>(); 
                		if(k==' ')
                		{
                			str=input.substring(1,input.length());
                		}
                		else
                		{
                			l.setText("");
	                		String query1="SELECT COUNT(*) "
	                				+ "FROM information_schema.tables "
	                				+ "WHERE table_schema = 'satyam' AND table_name = '"+k+"';";
	                		String query="select data from "+k+";";
	                		try
	                		{
							Statement statement=conn.createStatement();
							ResultSet rs1=statement.executeQuery(query1);
							if(rs1.next())
							{
								int count=rs1.getInt(1);
								if(count>0)
								{
									ResultSet rs=statement.executeQuery(query);
									
									while(rs.next())
									{
										textdata.add(rs.getString("data"));
									}
								}
							}
							for(int i=0;i<textdata.size();i++)
		                		{
								String lower=textdata.get(i).toLowerCase();
		                			tr.insert(tr, lower);
		                		}
							boolean ch=false;
							for(int i=0;i<textdata.size();i++)
	            				{
	            					if(textdata.get(i).contains(str))
	            					{
	            						ch=true;
	            					}
	            				}
	                			if(ch)
	                			{
	                				tr.fetchTrie(tr.getPostPrefix(tr, str),str,list);
	                			}
		                		
						} 
	                		catch (SQLException e) 
	                		{
							e.printStackTrace();
						}
                		}
                		String[] newitems=st.joinArray(recent,list);
                		cb.setModel(new DefaultComboBoxModel<>(newitems));
                		cb.setVisible(true);
                		cb.setPopupVisible(true);
                }
			
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
				String[] recent=st.getStack();
				String a=tf.getText();
				char v=a.charAt(0);
				String query="create table if not exists "+v+"(data varchar(30) primary key,priority int(10));";
				String query1="insert ignore into "+v+"(data,priority) values('"+a+"',0);";
				Statement statement;
				try {
					statement = conn.createStatement();
					statement.executeUpdate(query);
					statement.executeUpdate(query1);
				} catch (SQLException se) {
					se.printStackTrace();
				}
				ArrayList<String> textdata=new ArrayList<String>();
				String[] newitems=st.joinArray(recent,textdata);
				cb.setModel(new DefaultComboBoxModel<>(newitems));
				JFrame f1= new JFrame();
				JLabel ln1;
				f1.setSize(400,250);
				f1.setLayout(null);
				f1.setLocation(550,300);
				ln1=new JLabel("thank you for searching");
				ln1.setBounds(0, 70, 200, 50);
				f1.add(ln1);
				f1.setVisible(true);
				Timer timer = new Timer(50, q -> {
	            int x = ln1.getX();
	            x = (x >= 400) ? -150 : x + 1; 
	            ln1.setBounds(x, 70, 200, 50);
		        });
		        timer.start();
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
					
				}
				else if(cb.getSelectedItem().equals("---------------"))
				{
					
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
			String[] recent=st.getStack();
			String a=tf.getText();
			char v=a.charAt(0);
			String query="create table if not exists "+v+"(data varchar(30) primary key,priority int(10));";
			String query1="insert ignore into "+v+"(data,priority) values('"+a+"',0);";
			Statement statement;
			try {
				statement = conn.createStatement();
				statement.executeUpdate(query);
				statement.executeUpdate(query1);
			} catch (SQLException e) {
				e.printStackTrace();
			}
			ArrayList<String> textdata=new ArrayList<String>();
			String[] newitems=st.joinArray(recent,textdata);
			cb.setModel(new DefaultComboBoxModel<>(newitems));
			cb.setVisible(false);
			JFrame f1= new JFrame();
			JLabel ln1;
			f1.setSize(400,250);
			f1.setLayout(null);
			f1.setLocation(550,300);
			ln1=new JLabel("thank you for searching");
			ln1.setBounds(0, 70, 200, 50);
			f1.add(ln1);
			f1.setVisible(true);
			Timer timer = new Timer(50, e -> {
            int x = ln1.getX();
            x = (x >= 400) ? -150 : x + 1; 
            ln1.setBounds(x, 70, 200, 50);
	        });
	        timer.start();
		}
		
	}
	public static void main(String[] args)
	{
		String url = "jdbc:mysql://localhost:3306/satyam"; 
        String username = "root";                          
        String password = "root";                          

        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            System.out.println("Database connected successfully");
            Front obj=new Front(conn);
        } catch (SQLException e) {
            System.out.println("Error connecting to the database: " + e.getMessage());
        }
	}
	
}
