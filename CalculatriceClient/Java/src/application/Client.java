/**
 * @author jobustos
 */
package application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * @author jobustos
 *
 * La classe client.
 */
public class Client {

	/** Message erreur de syntaxe. */
	private static String ERREUR_SYNTAXE = "Erreur de syntaxe ...";

	/** Message de fin. */
	private static String FIN = "FIN" ;

	/** Message erreur sur l'utilisation du programme. */
	private static String USAGE = "Usage : java client nom_serveur port";

	/** Message que le client affiche a l'utilisateur. */
	private static String INDICATION_SAISIE = "Entrez une opperation : n {+,-,*,/} n : ";

	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		int port;
		String adresse, line = null, lineReseve = null;
		Reader readSoc = null;
		PrintStream sortie=null;
		boolean envoyer = false;


		if(args.length != 2)	
		{
			messageErreur(USAGE);
			System.exit(0); 
		}

		adresse = args[0];
		port = new Integer(args[1]).intValue();

		Socket socket = null;
		socket = creerSocket(socket,adresse,port);

		Reader reader = new InputStreamReader(System.in);
		BufferedReader keyboard = new BufferedReader(reader);

		try 
		{
			sortie = new PrintStream(socket.getOutputStream());
		} 
		catch (IOException e) 
		{
			messageErreur(e.getMessage());
		}

		try 
		{
			readSoc = new InputStreamReader(socket.getInputStream());
		} 
		catch (IOException e) 
		{
			messageErreur(e.getMessage());
		}

		BufferedReader keyboardSoc = new BufferedReader (readSoc);

		while (true)
		{
			message(INDICATION_SAISIE);
			try 
			{
				line = keyboard.readLine();
			} 
			catch (IOException e)
			{
				messageErreur(e.getMessage());
			}
			if (line.length()>=5)
			{
				if (line.contains("STATS"))
				{
					sortie.println(line);
					try
					{
						lineReseve = keyboardSoc.readLine();
					} 
					catch (IOException e) 
					{
						messageErreur(e.getMessage());
					}

					message("Recu : " + lineReseve);
				}
				else
				{
					if (line.contains(" "))
					{
						String calcul[] = line.split(" ");
						String operation = calcul[1];

						if (operation.equals("+") || operation.equals("-") || 
								operation.equals("*") || operation.equals("/"))
						{
							try 
							{
								Double.parseDouble(calcul[0]); // First opperand
								Double.parseDouble(calcul[2]); // Seconde opperand
								envoyer=true;
							}
							catch (NumberFormatException e)
							{
								messageErreur(ERREUR_SYNTAXE);
								envoyer=false;
							}
							finally
							{
								if (envoyer)
								{
									sortie.println(line);
									try
									{
										lineReseve = keyboardSoc.readLine();
									} 
									catch (IOException e) 
									{
										messageErreur(e.getMessage());
									}
									if (lineReseve.equals("Div0"))
										messageErreur("Recu : " + "... Divison par 0 impossible ...");
									else 
										message("Recu : " + lineReseve);
								}
							}
						}
						else
						{
							messageErreur(ERREUR_SYNTAXE);
						}
					}
					else 
					{
						messageErreur(ERREUR_SYNTAXE);
					}
				}
			}
			else  if (line.equals(FIN)) // si on a tape "FIN" on quitte le client
			{
				sortie.println(line);
				break;
			}
			else
			{
				messageErreur(ERREUR_SYNTAXE);
			}
		}
		// fermeture de la socket
		fermerSocket(socket);
	}

	/**
	 * Permet de creer la socket.
	 * @param socket La socket.
	 * @param adresse L'adresse.
	 * @param port Le port.
	 * @return La socket cree.
	 */
	private static Socket creerSocket(Socket socket, String adresse, int port) 
	{
		try
		{
			socket = new Socket(adresse, port);
		} 
		catch (UnknownHostException e) 
		{
			messageErreur(e.getMessage());
		} 
		catch (IOException e) 
		{
			messageErreur(e.getMessage());
		}
		return socket;
	}

	/**
	 * Permet de fermer la socket.
	 * @param socket La socket a fermer.
	 */
	private static void fermerSocket(Socket socket)
	{
		try
		{
			socket.close();
		} 
		catch (IOException e)
		{
			messageErreur(e.getMessage());
		}
	}

	/**
	 * Permet d'ecrire sur la sortie d'erreur.
	 * @param message Le message d'erreur a afficher.
	 */
	private static void messageErreur(String message)
	{
		System.err.println(message); 
	}

	/**
	 * Permet d'afficher un message sur la sortie standard.
	 * @param message Le message a afficher.
	 */
	private static void message(String message)
	{
		System.out.println(message);
	}
}


