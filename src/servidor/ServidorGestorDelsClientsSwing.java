package servidor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * GESTOR DE CLIENT PER LA VERSIÓ SWING
 * ======================================
 * Gestiona cada client i integra amb la finestra del servidor.
 */
public class ServidorGestorDelsClientsSwing implements Runnable {

    private Socket socket;
    private BufferedReader entrada;
    private PrintWriter sortida;
    private String nomUsuari;
    private ServidorXatSwing servidor;

    public ServidorGestorDelsClientsSwing(Socket socket, ServidorXatSwing servidor) {
        this.socket = socket;
        this.servidor = servidor;
        try {
            entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            sortida = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            servidor.afegirLog("[ERROR] No s'han pogut crear els fluxos: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            // Demanem el nom d'usuari
            sortida.println("Benvingut/da al chat! Escriu el teu nom:");
            nomUsuari = entrada.readLine();

            servidor.afegirLog("[+] L'usuari '" + nomUsuari + "' s'ha unit al chat.");
            ServidorXatSwing.broadcast("*** " + nomUsuari + " s'ha unit al chat ***", this);
            sortida.println("Connectat/da! Ja pots escriure missatges.");

            // Bucle principal
            String missatgeRebut;
            while ((missatgeRebut = entrada.readLine()) != null) {
                if (missatgeRebut.equalsIgnoreCase("sortir")) {
                    break;
                }

                String missatgeFormatat = "[" + nomUsuari + "]: " + missatgeRebut;
                servidor.afegirLog(missatgeFormatat);
                ServidorXatSwing.broadcast(missatgeFormatat, this);
            }

        } catch (IOException e) {
            servidor.afegirLog("[-] Connexió perduda amb: " + nomUsuari);
        } finally {
            desconnectar();
        }
    }

    public void enviarMissatge(String missatge) {
        sortida.println(missatge);
    }

    private void desconnectar() {
        try {
            ServidorXatSwing.eliminarClient(this);
            if (nomUsuari != null) {
                ServidorXatSwing.broadcast("*** " + nomUsuari + " ha sortit del chat ***", this);
                servidor.afegirLog("[-] L'usuari '" + nomUsuari + "' s'ha desconnectat.");
                servidor.actualitzarNumClients();
            }
            socket.close();
        } catch (IOException e) {
            servidor.afegirLog("[ERROR] Tancant socket: " + e.getMessage());
        }
    }
}
