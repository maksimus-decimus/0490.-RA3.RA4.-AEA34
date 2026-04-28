# Xat amb Interfície Swing

## Descripció
Aquest projecte implementa un sistema de xat client-servidor amb interfície gràfica Swing, substituint la interfície de consola original.

## Característiques
- ✅ **Finestra del Servidor**: Una única finestra que mostra l'activitat del servidor, clients connectats i missatges enviats
- ✅ **Finestres de Client**: Cada client té la seva pròpia finestra independent amb:
  - Camp per introduir el nom d'usuari
  - Àrea de visualització de missatges
  - Camp de text per enviar missatges
  - Botó d'enviar

## Com executar

### 1. Compilar (ja està fet)
Els fitxers ja estan compilats a la carpeta `bin/`.

Si necessites recompilar:
```bash
javac -d bin src\servidor\ServidorXatSwing.java src\servidor\ServidorGestorDelsClientsSwing.java src\client\ClientXatSwing.java
```

### 2. Executar el Servidor
```bash
cd bin
java servidor.ServidorXatSwing
```

S'obrirà una finestra amb:
- Títol "Servidor de Chat - Port 12345"
- Indicador de clients connectats
- Àrea de log amb tota l'activitat del servidor

### 3. Executar els Clients
Obre diverses finestres de terminal i executa per cada client:
```bash
cd bin
java client.ClientXatSwing
```

Per a cada client:
1. S'obrirà una finestra independent
2. Introdueix el teu nom al camp "Nom"
3. Prem el botó "Connectar"
4. Un cop connectat, pots escriure missatges i enviar-los amb el botó "Enviar" o prement Enter

### 4. Provar amb múltiples clients
Pots executar tants clients com vulguis. Cada un tindrà la seva pròpia finestra independent.

Exemple amb 3 clients:
1. Obre la primera terminal: `java client.ClientXatSwing` (finestra Client 1)
2. Obre la segona terminal: `java client.ClientXatSwing` (finestra Client 2)
3. Obre la tercera terminal: `java client.ClientXatSwing` (finestra Client 3)

Cada finestra permet xatejar de manera independent!

## Sortir del xat
- **Client**: Escriu "sortir" o tanca la finestra
- **Servidor**: Tanca la finestra del servidor (això desconnectarà tots els clients)

## Comparació amb la versió de consola

### Versió Original (Consola)
- **Servidor**: `java servidor.ServidorXat`
- **Client**: `java client.ClientXat`
- Tota la interacció es fa a través de text a la consola

### Versió Swing (Nova)
- **Servidor**: `java servidor.ServidorXatSwing`
- **Client**: `java client.ClientXatSwing`
- Interfície gràfica amb finestres, botons i àrees de text

## Estructura de fitxers
```
src/
├── client/
│   ├── ClientXat.java                 (versió original consola)
│   ├── ClientXatSwing.java            (versió nova Swing) ✨
│   └── ClientVerificadorDisponibilitatDelServidor.java
└── servidor/
    ├── ServidorXat.java                      (versió original consola)
    ├── ServidorXatSwing.java                 (versió nova Swing) ✨
    ├── ServidorGestorDelsClients.java       (versió original consola)
    └── ServidorGestorDelsClientsSwing.java  (versió nova Swing) ✨
```

## Notes tècniques
- El servidor escolta al port **12345**
- Utilitza sockets TCP/IP per la comunicació
- Cada client s'executa en un fil independent
- Les finestres Swing s'actualitzen de forma segura amb `SwingUtilities.invokeLater()`
- El protocol de comunicació és el mateix que la versió de consola, només canvia la interfície
