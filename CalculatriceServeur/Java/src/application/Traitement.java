/**
 * 
 */
package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * @author jordanbustos
 *
 */
public class Traitement implements Runnable {

	/**
	 * La socket.
	 */
	private Socket soc;
	
	/**
	 * La date de lancement du serveur.
	 */
	Date timeOfStart;
	
	/**
	 * La socket du serveur.
	 */
	ServerSocket s;
	/**
	 * Constructeur du traitement.
	 * @param s 
	 * @param soc La socket.
	 * @param timeOfStart La date de lancement du serveur
	 */
	public Traitement(ServerSocket s, Socket soc, Date timeOfStart) 
	{
		this.s = s;
		this.soc = soc;
		this.timeOfStart = timeOfStart;
		Thread threadCourant = new Thread(this);
		threadCourant.setDaemon(true);
		threadCourant.start();
	}

	@Override
	public void run() 
	{
		Reader reader = null;
		PrintStream sortie=null;
		boolean boucle = true;
		String line = null;
		StringBuffer lineToSend = null;

		try 
		{
			reader = new InputStreamReader(soc.getInputStream());
		} 
		catch (IOException e)
		{
			Serveur.messageErreur(e.getMessage());
		}
		try 
		{
			sortie = new PrintStream(soc.getOutputStream());
		} 
		catch (IOException e) 
		{
			Serveur.messageErreur(e.getMessage());
		}
		BufferedReader keyboard = new BufferedReader (reader);

		while (boucle)	
		{
			try 
			{
				line = keyboard.readLine();
			} 
			catch (IOException e) 
			{
				Serveur.messageErreur(e.getMessage());
			}

			System.out.println("Je fais : " + line);

			if(line.contains("FIN"))
			{
				boucle = false;
				line = null;
				try 
				{
					soc.close();
					return;
				} 
				catch (IOException e) 
				{
					Serveur.messageErreur(e.getMessage());
				} 
			}
			else if (line.contains("STATS"))
			{
				long currentDateGetTime = (new Date(System.currentTimeMillis())).getTime();
				long timeOfStartGetTime = timeOfStart.getTime();

				long timeOfServMin = ((currentDateGetTime - timeOfStartGetTime)
						/1000)/60;

				lineToSend = new StringBuffer();
				lineToSend.append("Je tourne depuis : ")
				.append(String.valueOf(timeOfServMin)).append(" minutes. ")
				.append("J'ai fait ").append(String.valueOf(Serveur.getNbOpperations())).append(" opperation(s).");
			}
			else           
			{
				String calcul[] = line.split(" ");
				String operation = calcul[1];
				double firstOperand=Double.parseDouble(calcul[0]);
				double secondOperand = Double.parseDouble(calcul[2]);
				double result = 0d;

				if (secondOperand != 0)
				{
					switch (operation) 
					{
					case "+":
						result = firstOperand + secondOperand;
						break;
					case "-":
						result = firstOperand - secondOperand;
						break;
					case "*":
						result = firstOperand * secondOperand;
						break;
					case "/":
						result = firstOperand / secondOperand;
						break;

					default:
						break;
					}
					Serveur.incNbOpperation();
					lineToSend = new StringBuffer(String.valueOf(result));
				}
				else
				{
					lineToSend = new StringBuffer("Div0");
				}
			}
			System.out.println("J'envoie : " + lineToSend);
			sortie.println(lineToSend);    
		}

		try
		{
			s.close();
		} 
		catch (IOException e) 
		{
			Serveur.messageErreur(e.getMessage());
		}
	}

	
	
}
