import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.rmi.Naming;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author ABDULLAH
 */

class ImplementionThread implements Runnable{
    String clientSentence;          
    String OutputCode; 
    ServerSocket welcomeSocket;
    DefaultListModel dlm; 
    MFServer ui;
    boolean isGrafieldEnabled=false;
    ImplementionThread(ServerSocket ws,DefaultListModel dm, MFServer sui) {
        welcomeSocket=ws;
        dlm=dm;
        ui=sui;
    }
    
    boolean noError=true;
    
   @Override
    public void run(){             
        while(true){                  
                    try{
			Socket connectionSocket = welcomeSocket.accept();             
			dlm.addElement("Client Connected ");
			BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream())); 
			dlm.addElement("Obtained a handle on Client Input Stream");
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());         
			dlm.addElement("Obtained a handle on Client Output Stream");    					
			clientSentence = inFromClient.readLine();             
			dlm.addElement("Received: " + clientSentence);             
			OutputCode = calculateTAC(clientSentence) + '\n';  
                        //OutputCode = clientSentence + '\n';  
                        System.out.println(OutputCode);
			outToClient.writeBytes(OutputCode);
			dlm.addElement("Message Sent"); 
                        if(!ui.getGarfieldVisible()){
                            new Thread(){
                                @Override
                                public void run(){
                                    ui.setGarfieldVisible(true);
                                    try {
                                        sleep(3000);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(ImplementionThread.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    ui.setGarfieldVisible(false);
                                }
                            }.start();
                        }
                        
                        /*
                        
                        if(!getvisible()){
                        thread {
                            make visible
                            sleep 3000
                            make invisible
                            }
                        }
                        **/
                    }catch(Exception e)
                    {
                        noError=false;
                        dlm.addElement(e);
                    }
                    ui.SetList3(dlm);
        }        
           
    }  
    String calculateTAC(String expr) throws IOException{
        int i, j, opc=0;
        String output="";
		char token;
		boolean processed[];
		String[][] operators = new String[10][2];
		
                String temp;		
		processed = new boolean[expr.length()];
		for (i=0; i < processed.length; i++)
		{
			processed[i] = false;
		}
		for (i=0; i < expr.length(); i++)
		{
			token = expr.charAt(i);
			for (j=0; j < precedence.length; j++)
			{
				if (token==precedence[j][0])
				{
					operators[opc][0] = token+"";
					operators[opc][1] = i+"";
					opc++;
					break;
				}
			}
		}
                
		output="\nOperators:\nOperator\tLocation\n";
		for (i=0; i < opc; i++)
		{
			output=output+(operators[i][0] + "\t\t" + operators[i][1])+"\n";
		}
		//sort
		for (i=opc-1; i >= 0; i--)
		{
			for (j=0; j < i; j++)
			{
				if (precedenceOf(operators[j][0]) > precedenceOf(operators[j+1][0]))
				{
					temp = operators[j][0];
					operators[j][0] = operators[j+1][0];
					operators[j+1][0] = temp;
					temp = operators[j][1];
					operators[j][1] = operators[j+1][1];
					operators[j+1][1] = temp;
				}				
			}
		}
		output=output+"\nOperators sorted in their precedence:\nOperator\tLocation"+"\n";
		for (i=0; i < opc; i++)
		{
			output=output+(operators[i][0] + "\t\t" + operators[i][1])+"\n";
		}
		output=output+"\n";
		for (i=0; i < opc; i++)
		{
			j = Integer.parseInt(operators[i][1]+"");
			String op1="", op2="";
			if (processed[j-1]==true)
			{
				if (precedenceOf(operators[i-1][0]) == precedenceOf(operators[i][0]))
				{
					op1 = "t"+i;
				}
				else
				{
					for (int x=0; x < opc; x++)
					{
						if ((j-2) == Integer.parseInt(operators[x][1]))
						{
							op1 = "t"+(x+1)+"";
						}
					}
				}
			}
			else
			{
				op1 = expr.charAt(j-1)+"";
			}
			if (processed[j+1]==true)
			{
				for (int x=0; x < opc; x++)
				{
					if ((j+2) == Integer.parseInt(operators[x][1]))
					{
						op2 = "t"+(x+1)+"";
					}
				}
			}
			else
			{
				op2 = expr.charAt(j+1)+"";
			}
			output=output+("t"+(i+1)+" = "+op1+operators[i][0]+op2)+"\n";
			processed[j] = processed[j-1] = processed[j+1] = true;
		}
                
	return formatOutput(output);
    }
    String formatOutput(String output){
        output=output.replace('\n', '@');
        output=output.replace('\t', '#');
        return output;
    }
    private static final char[][] precedence = {
		{'/', '1'},
		{'*', '1'},
		{'+', '2'},
		{'-', '2'}
	};
    private static int precedenceOf(String t)
	{
		char token = t.charAt(0);
		for (int i=0; i < precedence.length; i++)
		{
			if (token == precedence[i][0])
			{
				return Integer.parseInt(precedence[i][1]+"");
			}
		}
		return -1;
	}
    
}

class recievePacketThread implements Runnable{
    DatagramSocket sock = null;
    DatagramPacket incoming;
    MFServer ageServer;
    DefaultListModel dlm;

    recievePacketThread(DatagramSocket sock, DatagramPacket incoming,MFServer ageServer,DefaultListModel dlm) {
        this.sock=sock;
        this.incoming=incoming;
        this.ageServer=ageServer;
        this.dlm=dlm;
    }
    @Override
    public void run() {
        try{
            while(true)
                {
                    String calData="";//Calculated Data
                    sock.receive(incoming);
                    byte[] data = incoming.getData();
                    String s = new String(data, 0, incoming.getLength());
                    //echo the details of incoming data - client ip : client port - client message
                    dlm.addElement(incoming.getAddress().getHostAddress() + " : " + incoming.getPort() + " - " + s);
                    dlm.addElement("Calculating Please wait");
                    ageServer.SetList(dlm);
                    calData=Arrays.toString(calculateAge(s));
                    s = "OK : " + s;
                    DatagramPacket dp = new DatagramPacket(calData.getBytes() , calData.getBytes().length , incoming.getAddress() , incoming.getPort());
                    sock.send(dp);
                    dlm.addElement("Values send to Client...");
                    ageServer.SetList(dlm);
                }
        }
        catch(Exception e){
            dlm.addElement(e);
        }
        ageServer.SetList(dlm);
       
    }
   int[] calculateAge(String str)
   {
       
      int years = 0;
      int months = 0;
      int days = 0;
      int totalDays=0;
      int totalHours=0;
      int totalMinutes=0;
      int daysleftAndDay[]=null;
      int dayOftheWeek=0;
      int currentAge=0;
      SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
      Date birthDate = null;
        try {                
            birthDate =  sdf.parse(str);
        } catch (ParseException ex) {
            Logger.getLogger(MFServer.class.getName()).log(Level.SEVERE, null, ex);
        }
       
      //create calendar object for birth day
      Calendar birthDay = Calendar.getInstance();
      birthDay.setTimeInMillis(birthDate.getTime());
      //create calendar object for current day
      long currentTime = System.currentTimeMillis();
      
      Calendar now = Calendar.getInstance();
      now.setTimeInMillis(currentTime);
      //Get difference between years
      years = now.get(Calendar.YEAR) - birthDay.get(Calendar.YEAR);
      int currMonth = now.get(Calendar.MONTH) + 1;
      int birthMonth = birthDay.get(Calendar.MONTH) + 1;
      //Get difference between months
      months = currMonth - birthMonth;
      //if month difference is in negative then reduce years by one and calculate the number of months.
      if (months < 0)
      {
         years--;
         months = 12 - birthMonth + currMonth;
         if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
            months--;
      } else if (months == 0 && now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
      {
         years--;
         months = 11;
      }
      //Calculate the days
      if (now.get(Calendar.DATE) > birthDay.get(Calendar.DATE))
         days = now.get(Calendar.DATE) - birthDay.get(Calendar.DATE);
      else if (now.get(Calendar.DATE) < birthDay.get(Calendar.DATE))
      {
         int today = now.get(Calendar.DAY_OF_MONTH);
         now.add(Calendar.MONTH, -1);
         days = now.getActualMaximum(Calendar.DAY_OF_MONTH) - birthDay.get(Calendar.DAY_OF_MONTH) + today;
      } else
      {
         days = 0;
         if (months == 12)
         {
            years++;
            months = 0;
         }
      }

      long tempMS=now.getTimeInMillis()-birthDay.getTimeInMillis();
      totalDays=(int) (tempMS/86400000);
      totalHours=(int) (tempMS/3600000);
      totalMinutes=(int) (tempMS/60000);
      daysleftAndDay=dayCal(str);
      
      int Age[]={days, months, years,totalDays,totalHours,totalMinutes,daysleftAndDay[0],daysleftAndDay[1]};
      return Age;
   }
   
    int[] dayCal(String str){
        SimpleDateFormat sdf= new SimpleDateFormat("dd/MM/yyyy");
      Date date=new Date();

      Date birthDate = null;
      long daysLeft=0;
      int daysleftAndDay[]=new int[2];
        try {                
            birthDate =  sdf.parse(str);
        } catch (ParseException ex) {
            System.out.println(ex);
        } 
       
        if(date.getMonth()<=birthDate.getMonth()){
            //birthday to come            
           birthDate.setYear(date.getYear());
        }else{
            //birthday already went
            birthDate.setYear(date.getYear()+1);
        }
      Calendar birthDay = Calendar.getInstance();
      birthDay.setTimeInMillis(birthDate.getTime());
    
      //create calendar object for current day      
      
      Calendar now = Calendar.getInstance();
      now.setTimeInMillis(date.getTime());
      daysLeft=(birthDay.getTimeInMillis()-now.getTimeInMillis());      
      daysLeft= (daysLeft/86400000);  
      daysleftAndDay[0]=(int)daysLeft;
      daysleftAndDay[1]=birthDate.getDay();
      return daysleftAndDay;
    }

}

public class MFServer extends javax.swing.JFrame {

    /**
     * Creates new form MFServer
     */
    public MFServer() {
        initComponents();
        GarfieldImg.setVisible(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jToggleButton1 = new javax.swing.JToggleButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jToggleButton3 = new javax.swing.JToggleButton();
        jPanel4 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        jToggleButton4 = new javax.swing.JToggleButton();
        jToggleButton5 = new javax.swing.JToggleButton();
        jPanel3 = new javax.swing.JPanel();
        jLayeredPane1 = new javax.swing.JLayeredPane();
        GarfieldImg = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jList3 = new javax.swing.JList();
        jButton3 = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jTabbedPane1.setTabPlacement(javax.swing.JTabbedPane.BOTTOM);

        jToggleButton1.setText("Start");
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("RPC Server");

        jLabel2.setText("rmiregistry");

        jToggleButton3.setText("Quit");
        jToggleButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton3ActionPerformed(evt);
            }
        });

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Messages"));

        jList1.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Server status: OFF", "To run the server open cmd and try rmiregistry" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
        );

        jLabel1.setText("netstat -o -n -a | findstr 0.0:1099");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jToggleButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToggleButton3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING)))
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jToggleButton3, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jToggleButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addGap(1, 1, 1))
        );

        jTabbedPane1.addTab("Calculator server(RPC)", jPanel1);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("Messages"));

        jList2.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Server status: OFF", " " };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        jScrollPane3.setViewportView(jList2);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 283, Short.MAX_VALUE)
        );

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel5.setText("Age Server");

        jToggleButton4.setText("Start");
        jToggleButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton4ActionPerformed(evt);
            }
        });

        jToggleButton5.setText("Quit");
        jToggleButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton5ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jToggleButton4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 261, Short.MAX_VALUE)
                .addComponent(jToggleButton5)
                .addContainerGap())
            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jToggleButton4)
                    .addComponent(jToggleButton5))
                .addContainerGap())
        );

        jTabbedPane1.addTab("Age Server(UDP)", jPanel2);

        GarfieldImg.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        GarfieldImg.setIcon(new javax.swing.ImageIcon(getClass().getResource("/gif_garfield_january09.gif"))); // NOI18N

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel3.setText("TAC Server");

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Messages"));

        jScrollPane2.setViewportView(jList3);

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 379, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 272, Short.MAX_VALUE)
        );

        jButton3.setText("Quit");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton1.setText("Start Server");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jLayeredPane1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton3)))
                .addContainerGap())
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addGap(105, 105, 105)
                    .addComponent(GarfieldImg, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(106, Short.MAX_VALUE)))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 51, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton3))
                .addContainerGap())
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addGap(101, 101, 101)
                    .addComponent(GarfieldImg)
                    .addContainerGap(102, Short.MAX_VALUE)))
        );
        jLayeredPane1.setLayer(GarfieldImg, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jLabel3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jPanel6, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jButton3, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(jButton1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1)
        );

        jTabbedPane1.addTab("TAC Server(TCP)", jPanel3);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    DefaultListModel dlm=new DefaultListModel();
    boolean isconnected=false;
    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        try{
            CalciImplementation li=new CalciImplementation();
            Naming.rebind("CalciImplementation",li);
            dlm.addElement("Server Started");
            dlm.addElement("Server Object Registered");
            isconnected=true;
        }
        catch(Exception e){
            dlm.addElement(""+e);
            dlm.addElement("Please Check that rmirigstry is running..");
        }
        displayfull();
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void jToggleButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton3ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jToggleButton3ActionPerformed

    
    Thread thread;
    DatagramSocket sock = null;
    DefaultListModel dlm2=new DefaultListModel();
    
    private void jToggleButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton4ActionPerformed
        try {
            sock = new DatagramSocket(7777);
            //buffer to receive incoming data
            byte[] buffer = new byte[65536];
            DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
            //2. Wait for an incoming data
            dlm2.addElement("Server socket created. Waiting for incoming data...");
            
            displayfull();
            recievePacketThread recThread=new recievePacketThread(sock,incoming,this,dlm2);
            thread =new Thread(recThread);
            thread.start();

        } catch (SocketException e) {
            dlm2.addElement(""+e);
        }
        displayfull2();
    }//GEN-LAST:event_jToggleButton4ActionPerformed

    private void jToggleButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton5ActionPerformed
        System.exit(0);
    }//GEN-LAST:event_jToggleButton5ActionPerformed

    
    boolean isServerStarted;
    ServerSocket welcomeSocket;
    DefaultListModel dlm3=new DefaultListModel();  
    Thread thread2;
      
    
    public void setGarfieldVisible(boolean visible){
        GarfieldImg.setVisible(visible);
    }
    
    public boolean getGarfieldVisible(){
        return GarfieldImg.isVisible();
    }
    
    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            welcomeSocket = new ServerSocket(6789);            
            dlm3.addElement("server started..");            
            dlm3.addElement("Server Waiting for Connections on Port 6789");
            jList3.setModel(dlm3); 
            displayfull3();
        } catch (Exception e) {
            dlm3.addElement(e);
        } 
        ImplementionThread tt=new ImplementionThread(welcomeSocket,dlm3,this);
        thread2=new Thread(tt);
        thread2.start();  
        jList3.setModel(dlm3); 
        displayfull3();
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        System.exit(0);
    }//GEN-LAST:event_jButton3ActionPerformed

    public void SetList3(DefaultListModel dLM){
        jList3.setModel(dLM);
        jList3.ensureIndexIsVisible(dlm3.getSize()-1);
    }
    
    void displayfull3(){
        jList3.ensureIndexIsVisible(dlm3.getSize()-1);
   }
    
    void displayfull2(){
        jList2.setModel(dlm); 
        jList2.ensureIndexIsVisible(dlm.getSize()-1);
    }
    public void SetList(DefaultListModel dLM){
        jList2.setModel(dLM);
        jList2.ensureIndexIsVisible(dlm.getSize()-1);
    }   
    
    void displayfull(){
        jList1.setModel(dlm); 
        jList1.ensureIndexIsVisible(dlm.getSize()-1);
    }
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MFServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MFServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MFServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MFServer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MFServer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel GarfieldImg;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JList jList1;
    private javax.swing.JList jList2;
    private javax.swing.JList jList3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton3;
    private javax.swing.JToggleButton jToggleButton4;
    private javax.swing.JToggleButton jToggleButton5;
    // End of variables declaration//GEN-END:variables
}
