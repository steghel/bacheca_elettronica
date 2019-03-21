package client;

import java.io.*;
import java.net.*;

public class Cliente{
  private String indirizzoIpServer;
  private int portaServer;
  private String nome ;
  private Socket s;
  private DataInputStream disIn;
  private DataOutputStream dosOut;
  private BufferedReader brIn;
  private PrintWriter pwOut;

  
  public Cliente() throws IOException{
     Console.scriviStringa("scrivi l'indirizzo Ip del server");
     indirizzoIpServer = Console.leggiStringa();
     Console.scriviStringa("scrivi il numero della porta del  server");
     portaServer = Console.leggiIntero();
     Console.scriviStringa("scrivi il tuo nome");
     nome = Console.leggiStringa();
     s = new Socket(indirizzoIpServer, portaServer);
     disIn = new DataInputStream(s.getInputStream());
     dosOut = new DataOutputStream(s.getOutputStream());
     brIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
     pwOut = new PrintWriter(s.getOutputStream(),true);
     selezionaServizio();
  }

  
  public void selezionaServizio(){
     boolean continua = true;  
     while(continua){
        Console.scriviStringa("\n SELEZIONA UN SERVIZIO");
        Console.scriviStringa("1) mostra messaggi presenti in bacheca \n2) "
             + "inserisci nuovo messaggio \n3) cancella messaggio \n4) esci\n");
        int n = Console.leggiIntero();
        if((n == 0) ||( n >4 )  ){
            Console.scriviStringa("Selezione errata");
            continue;
        }
        Menu menu;
        menu = Menu.fromIndice(n);
            try{
              switch (menu){
                  case MOSTRA_MESSAGGI:
                       dosOut.writeInt(Menu.MOSTRA_MESSAGGI.getIndice());
                       mostraMessaggi();
                       break;
                  case INSERISCI_MESSAGGIO:
                       //invio della scelta selezionata al server
                       dosOut.writeInt(Menu.INSERISCI_MESSAGGIO.getIndice());
                       inserisciMessaggio();
                       break;
                  case CANCELLA_MESSAGGIO:
                       dosOut.writeInt(Menu.CANCELLA_MESSAGGIO.getIndice());
                       cancellaMessaggio();
                       break;
                  case ESCI:
                       System.exit(0);
              }
           }
           catch (IOException e){
               Console.scriviStringa("Problemi di comunicazione ...");
          }
      }
  }

  
  public void mostraMessaggi()throws IOException  {
      String st = null;
      // crea una stringa STOP
      String st1 = new String("STOP");
      //ciclo infinito per ricevere i vari messaggi dal server
      while(true){
          st = brIn.readLine();
          //  esci dal ciclo se ricevi il messaggio STOP
          if(st.equals(st1)){
              break;
          }
          Console.scriviStringa(st);
      }
 } 

  public void inserisciMessaggio() throws IOException {
      String st,st1;
      int id,cc;
      Console.scriviStringa("Inserisci il testo del messaggio ");
      while(true){
         /* senza il while ,il metodo leggiLinea()invia sempre qualcosa
         in uscita e st1 diventa "", questo crea problemi al server.
         Per evitare questo fatto, faccio uscire il programma dal ciclo
         solo quando st1 diverso da "" cioè dal messaggio vuoto*/
         st = Console.leggiLinea();  
         if (!st.equals("")){  //esci dal while se la stringa st è vuota (st="")
            break;
         }
      }
      st1 = nome + ": " + "\"" + st + "\"";
      pwOut.println(st1);
      Console.scriviStringa("Messaggio inviato");
      id = disIn.readInt();
      cc = disIn.readInt();
      Console.scriviStringa("Il messaggio è il numero " + id + " e il codice"
              + " di cancellazione è " + cc);
  }

  
  public void cancellaMessaggio()throws IOException  {
      String st;
      int id,cc;
      Console.scriviStringa("sono in cancellaMessaggio()");
      Console.scriviStringa("Quale messaggio vuoi eliminare?");
      id = Console.leggiInt();
      dosOut.writeInt(id);
      Console.scriviStringa("Codice di cancellazione");
      cc = Console.leggiInt();
      dosOut.writeInt(cc);
      st = brIn.readLine();
      Console.scriviStringa(st);
  }

}
