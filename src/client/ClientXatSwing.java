package client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * CLIENT DE CHAT AMB INTERFÍCIE SWING
 * =====================================
 * Cada client té la seva pròpia finestra independent.
 * Permet enviar i rebre missatges amb una interfície gràfica.
 */
public class ClientXatSwing extends JFrame {

    static final String ADRECA_SERVIDOR = "localhost";
    static final int PORT = 12345;

    private JTextArea areaMissatges;
    private JTextField campNom;
    private JTextField campMissatge;
    private JButton btnConnectar;
    private JButton btnEnviar;
    
    private Socket socket;
    private BufferedReader entradaServidor;
    private PrintWriter sortidaServidor;
    private boolean connectat = false;

    public ClientXatSwing() {
        configurarFinestra();
    }

    private void configurarFinestra() {
        setTitle("Client de Chat");
        setSize(500, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel superior - Connexió
        JPanel panelConnexio = new JPanel();
        panelConnexio.setLayout(new FlowLayout());
        panelConnexio.setBackground(new Color(100, 149, 237));
        panelConnexio.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblNom = new JLabel("Nom:");
        lblNom.setForeground(Color.WHITE);
        lblNom.setFont(new Font("Arial", Font.BOLD, 12));
        
        campNom = new JTextField(15);
        campNom.setFont(new Font("Arial", Font.PLAIN, 12));
        
        btnConnectar = new JButton("Connectar");
        btnConnectar.setFont(new Font("Arial", Font.BOLD, 12));
        btnConnectar.setBackground(new Color(46, 204, 113));
        btnConnectar.setForeground(Color.WHITE);
        btnConnectar.setFocusPainted(false);

        panelConnexio.add(lblNom);
        panelConnexio.add(campNom);
        panelConnexio.add(btnConnectar);

        // Àrea de missatges
        areaMissatges = new JTextArea();
        areaMissatges.setEditable(false);
        areaMissatges.setFont(new Font("Courier New", Font.PLAIN, 12));
        areaMissatges.setBackground(new Color(245, 245, 245));
        areaMissatges.setLineWrap(true);
        areaMissatges.setWrapStyleWord(true);
        JScrollPane scrollMissatges = new JScrollPane(areaMissatges);
        scrollMissatges.setBorder(BorderFactory.createTitledBorder("Missatges"));

        // Panel inferior - Enviar missatges
        JPanel panelEnviar = new JPanel();
        panelEnviar.setLayout(new BorderLayout(5, 5));
        panelEnviar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        campMissatge = new JTextField();
        campMissatge.setFont(new Font("Arial", Font.PLAIN, 12));
        campMissatge.setEnabled(false);
        
        btnEnviar = new JButton("Enviar");
        btnEnviar.setFont(new Font("Arial", Font.BOLD, 12));
        btnEnviar.setBackground(new Color(52, 152, 219));
        btnEnviar.setForeground(Color.WHITE);
        btnEnviar.setFocusPainted(false);
        btnEnviar.setEnabled(false);

        panelEnviar.add(campMissatge, BorderLayout.CENTER);
        panelEnviar.add(btnEnviar, BorderLayout.EAST);

        // Layout principal
        setLayout(new BorderLayout());
        add(panelConnexio, BorderLayout.NORTH);
        add(scrollMissatges, BorderLayout.CENTER);
        add(panelEnviar, BorderLayout.SOUTH);

        // Listeners
        btnConnectar.addActionListener(e -> connectar());
        
        btnEnviar.addActionListener(e -> enviarMissatge());
        
        campMissatge.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    enviarMissatge();
                }
            }
        });

        // Tancar connexió en tancar la finestra
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                desconnectar();
            }
        });

        setVisible(true);
        afegirMissatge("=== CLIENT DE CHAT ===");
        afegirMissatge("Introdueix el teu nom i prem Connectar");
    }

    private void connectar() {
        String nom = campNom.getText().trim();
        
        if (nom.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Has d'introduir un nom!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            afegirMissatge("Connectant a " + ADRECA_SERVIDOR + ":" + PORT + " ...");
            
            socket = new Socket(ADRECA_SERVIDOR, PORT);
            entradaServidor = new BufferedReader(
                new InputStreamReader(socket.getInputStream()));
            sortidaServidor = new PrintWriter(socket.getOutputStream(), true);
            
            connectat = true;
            
            // Actualitzar interfície
            campNom.setEnabled(false);
            btnConnectar.setEnabled(false);
            campMissatge.setEnabled(true);
            btnEnviar.setEnabled(true);
            setTitle("Client de Chat - " + nom);
            
            afegirMissatge("Connexió establerta!");
            
            // Fil receptor
            Thread receptor = new Thread(() -> {
                try {
                    String missatgeServidor;
                    while ((missatgeServidor = entradaServidor.readLine()) != null) {
                        String msg = missatgeServidor;
                        SwingUtilities.invokeLater(() -> afegirMissatge(msg));
                    }
                } catch (IOException e) {
                    if (connectat) {
                        SwingUtilities.invokeLater(() -> 
                            afegirMissatge("[INFO] Connexió amb el servidor tancada."));
                    }
                }
            });
            receptor.setDaemon(true);
            receptor.start();
            
            // Enviar nom al servidor
            sortidaServidor.println(nom);
            
            campMissatge.requestFocus();
            
        } catch (IOException e) {
            afegirMissatge("[ERROR] No s'ha pogut connectar al servidor");
            afegirMissatge("Comprova que el servidor està en marxa.");
            JOptionPane.showMessageDialog(this, 
                "No s'ha pogut connectar al servidor!\nComprova que està en marxa.", 
                "Error de Connexió", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void enviarMissatge() {
        if (!connectat) return;
        
        String missatge = campMissatge.getText().trim();
        if (missatge.isEmpty()) return;
        
        sortidaServidor.println(missatge);
        
        if (missatge.equalsIgnoreCase("sortir")) {
            desconnectar();
        }
        
        campMissatge.setText("");
        campMissatge.requestFocus();
    }

    private void desconnectar() {
        if (connectat) {
            try {
                connectat = false;
                if (sortidaServidor != null) {
                    sortidaServidor.println("sortir");
                }
                if (socket != null) {
                    socket.close();
                }
                afegirMissatge("Desconnectat del servidor.");
            } catch (IOException e) {
                afegirMissatge("[ERROR] Error en desconnectar: " + e.getMessage());
            }
        }
    }

    private void afegirMissatge(String missatge) {
        SwingUtilities.invokeLater(() -> {
            areaMissatges.append(missatge + "\n");
            areaMissatges.setCaretPosition(areaMissatges.getDocument().getLength());
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ClientXatSwing());
    }
}
