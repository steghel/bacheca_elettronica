package server;

import java.io.*;
import java.net.*;

public class ServitoreThread extends Thread {
  private Socket s;
  private DataInputStream disIn;
  private DataOutputStream dosOut;
  private BufferedReader brIn;
  private PrintWriter pwOut;
  private Lista l;
  
  public ServitoreThread(Socket so,Lista lis){
      s = so;
      l = lis;
  }
   
  public void run(){      
      try{
          Console.scriviStringa("Si e' connesso un cliente");
          disIn = new DataInputStream(s.getInputStream());
          dosOut = new DataOutputStream(s.getOutputStream());
          brIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
          pwOut = new PrintWriter(s.getOutputStream(),true);
          boolean continua = true;
          while (continua){
              Menu menu;
              menu = Menu.fromIndice(disIn.readInt());
              switch (menu){
                  case MOSTRA_MESSAGGI:
                       mostraMessaggi();
                       break;
                  case INSERISCI_MESSAGGIO:
                       inserisciMessaggio();
                       break;
                  case CANCELLA_MESSAGGIO:
                       cancellaMessaggio(l);
                       break;
                  case ESCI:
                       
              }
          }
     }
     catch (IOException ioe){
         Console.scriviStringa("Problemi durante la comunicazione "
                 + "con il cliente " + ioe.getMessage());
     }
   }

   public void mostraMessaggi()throws IOException{
       String st = null;
       String st1 = "";
       // la stringa st contiene tutti i messaggi contenuti nella lista l
       st = l.stampa();
       //invia la stringa con tutti i messaggi al client
       pwOut.println(st);
       //invia al client la stringa STOP per indicargli che le linee
       //da cui è formata la stringa st sono terminate
       pwOut.println("STOP");
   }

   public void inserisciMessaggio()throws IOException{
       String st;
       // ricevi il testo del messaggio dal client
       st = brIn.readLine();
       // crea il messaggio sul server e inseriscilo in testa alla lista 
       l.insTesta(st);
       //mostra messaggio sul server
       l.mostraMessaggio();
       // invia l'ID e il codice canc del messaggio al client
       dosOut.writeInt(l.mostraId());
       dosOut.writeInt(l.mostraCodiceCanc());
   }

   public void cancellaMessaggio(Lista lis)throws IOException {
       int id,cc;
       String st;
       //ricevi id e codice cancellazione del messaggio da cancellare dal client
       id = disIn.readInt();
       cc = disIn.readInt();
       st=lis.estElem(id,cc);
       //invia al client il risultato dell'operazione di eliminazione
       pwOut.println(st);
   }
}


