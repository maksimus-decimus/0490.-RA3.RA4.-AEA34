package servidor;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * SERVIDOR DE CHAT MULTI-CLIENT AMB INTERFÍCIE SWING
 * ====================================================
 * Aquest servidor escolta connexions entrants al port 12345.
 * Mostra una finestra amb l'activitat del servidor.
 */
public class ServidorXatSwing extends JFrame {

    static final int PORT = 12345;
    static List<ServidorGestorDelsClientsSwing> clientsConnectats = new ArrayList<>();
    
    private JTextArea areaLog;
    private JLabel lblClients;
    private int numClients = 0;

    public ServidorXatSwing() {
        configurarFinestra();
        iniciarServidor();
    }

    private void configurarFinestra() {
        setTitle("Servidor de Chat - Port " + PORT);
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel superior amb informació
        JPanel panelSuperior = new JPanel();
        panelSuperior.setLayout(new BorderLayout());
        panelSuperior.setBackground(new Color(70, 130, 180));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblTitol = new JLabel("🖥️ SERVIDOR ACTIU", SwingConstants.CENTER);
        lblTitol.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitol.setForeground(Color.WHITE);
        
        lblClients = new JLabel("Clients connectats: 0", SwingConstants.CENTER);
        lblClients.setFont(new Font("Arial", Font.PLAIN, 14));
        lblClients.setForeground(Color.WHITE);

        panelSuperior.add(lblTitol, BorderLayout.NORTH);
        panelSuperior.add(lblClients, BorderLayout.SOUTH);

        // Àrea de log
        areaLog = new JTextArea();
        areaLog.setEditable(false);
        areaLog.setFont(new Font("Courier New", Font.PLAIN, 12));
        areaLog.setBackground(new Color(250, 250, 250));
        JScrollPane scrollPane = new JScrollPane(areaLog);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Activitat del Servidor"));

        // Layout principal
        setLayout(new BorderLayout());
        add(panelSuperior, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        setVisible(true);
        
        afegirLog("=== SERVIDOR DE CHAT INICIANT ===");
        afegirLog("Escoltant al port: " + PORT);
        afegirLog("Esperant clients...");
        afegirLog("---------------------------------");
    }

    private void iniciarServidor() {
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PORT)) {
                while (true) {
                    Socket socketClient = serverSocket.accept();
                    
                    String adrecaClient = socketClient.getInetAddress().getHostAddress();
                    afegirLog("[+] Nou client connectat des de: " + adrecaClient);
                    
                    ServidorGestorDelsClientsSwing gestor = 
                        new ServidorGestorDelsClientsSwing(socketClient, this);
                    clientsConnectats.add(gestor);
                    new Thread(gestor).start();
                    
                    actualitzarNumClients();
                }
            } catch (IOException e) {
                afegirLog("[ERROR] No s'ha pogut iniciar el servidor: " + e.getMessage());
            }
        }).start();
    }

    public void afegirLog(String missatge) {
        SwingUtilities.invokeLater(() -> {
            areaLog.append(missatge + "\n");
            areaLog.setCaretPosition(areaLog.getDocument().getLength());
        });
    }

    public synchronized void actualitzarNumClients() {
        numClients = clientsConnectats.size();
        SwingUtilities.invokeLater(() -> 
            lblClients.setText("Clients connectats: " + numClients)
        );
    }

    static synchronized void broadcast(String missatge, ServidorGestorDelsClientsSwing origen) {
        for (ServidorGestorDelsClientsSwing client : clientsConnectats) {
            if (client != origen) {
                client.enviarMissatge(missatge);
            }
        }
    }

    static synchronized void eliminarClient(ServidorGestorDelsClientsSwing gestor) {
        clientsConnectats.remove(gestor);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ServidorXatSwing());
    }
}
