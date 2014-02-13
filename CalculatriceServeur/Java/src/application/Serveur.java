/**
 * @author jobustos
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
 * @author jobustos
 *
 * Le serveur.
 */
public class Serveur {


	/**
	 * Le nombre d'opperation du serveur.
	 */
	private static int nbOpperations = 0;
	
	/**
	 * Retourne le nombre d'opperation du serveur.
	 * @return
	 */
	public static int getNbOpperations()
	{
		return nbOpperations;
	}
	
	/**
	 * Incremente le nombre d'opperations du serveur de 1.
	 */
	public static void incNbOpperation()
	{
		nbOpperations++;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		int port;
		Socket soc = null;
		Date timeOfStart;

		if(args.length != 2) 
		{		    
			messageErreur("usage : java serveur port");
			System.exit(0);
		}
		port = new Integer(args[1]).intValue();

		ServerSocket s = null;
		try 
		{
			s = new ServerSocket (port);
		} 
		catch (IOException e)
		{
			messageErreur(e.getMessage());
		}
		System.out.println("La socket serveur est cree");

		timeOfStart = new Date(System.currentTimeMillis());

		while (true)
		{
			try 
			{
				soc = s.accept();
			} 
			catch (IOException e) 
			{
				messageErreur(e.getMessage());
			}
			System.out.println("Connexion realise a " + soc.toString());

			// ON lance le traitement
			Traitement traitement = new Traitement(s, soc, timeOfStart);
		}
	}


	/**
	 * Permet d'ecrire sur la sortie d'erreur.
	 * @param message Le message d'erreur a afficher.
	 */
	static void messageErreur(String message)
	{
		System.err.println(message); 
	}


}


