/*
 * Kleber, Felipe João
 */
package Telas;

import ValueObjects.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;
import java.io.*;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @Kleber, Felipe João
 */
public class TelaServidor extends javax.swing.JFrame implements Runnable {
    
    boolean verificadorUnicast; //true para ocupado
    ArrayList<Cliente> listaClientes = new ArrayList<>();
    ServerSocket socketServidor;
    DefaultTableModel modeloTabela;
    Socket socketCliente;
    InetAddress enderecoIP;
    int porta;
    Runnable conexao;
    boolean IniciadoVr;
    JSONObject mensagemRecebida_JSONObject;
    

    public TelaServidor() {
        initComponents();
        setLocationRelativeTo(null);
    }

    public void run() {
        while (socketServidor.isClosed() == false) {
            try {
                socketCliente = socketServidor.accept();
                new Thread(MultiThread()).start();
                System.out.println(" ");
                System.out.println("----- THREAD CRIADA: NOVO CLIENTE CONECTADO AO SERVIDOR -----");
            } catch (IOException ex) {
                System.out.println("Desconectado o Servidor");
                try {
                    socketCliente.close();
                } catch (IOException | NullPointerException ex1) {
                    System.out.println("socket close");
                }
            }
        }
    }

    //POPULA A TABELA COM OS CLIENTES CONECTADOS AO SERVIDOR
    public void populaTabela() {
        
        SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                   
                    //Exibe todos os valores do Arraylist tabela da interface
                    DefaultTableModel tabelaClientes = (javax.swing.table.DefaultTableModel) jTableClientes.getModel();
                    tabelaClientes.setRowCount(0);

                    for (int i = 0; i < listaClientes.size(); i++) {
                        Object[] linha = {listaClientes.get(i).getNome(), listaClientes.get(i).getIp(),
                            listaClientes.get(i).getPorta(), listaClientes.get(i).getColetorOuDoador(), listaClientes.get(i).getMaterial(), listaClientes.get(i).getDescricao()
                        };
                        tabelaClientes.addRow(linha);
                    }
               }
           });
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabelServidor = new javax.swing.JLabel();
        jLabelPorta = new javax.swing.JLabel();
        jTextFieldPorta = new javax.swing.JTextField();
        jButtonIniciar = new javax.swing.JButton();
        jButtonParar = new javax.swing.JButton();
        jScrollPaneClientes = new javax.swing.JScrollPane();
        jTableClientes = new javax.swing.JTable();
        jLabelClientes = new javax.swing.JLabel();
        jLabelLog = new javax.swing.JLabel();
        jScrollPaneLog = new javax.swing.JScrollPane();
        jTextAreaLog = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabelServidor.setText("Servidor");

        jLabelPorta.setText("Porta");

        jTextFieldPorta.setText("20100");

        jButtonIniciar.setText("Iniciar");
        jButtonIniciar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonIniciarActionPerformed(evt);
            }
        });

        jButtonParar.setText("Parar");
        jButtonParar.setEnabled(false);
        jButtonParar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPararActionPerformed(evt);
            }
        });

        jTableClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nome", "IP", "Porta", "Tipo", "Material", "Descrição"
            }
        ));
        jScrollPaneClientes.setViewportView(jTableClientes);

        jLabelClientes.setText("Lista de Clientes Conectados");

        jLabelLog.setText("Log");

        jTextAreaLog.setColumns(20);
        jTextAreaLog.setRows(5);
        jScrollPaneLog.setViewportView(jTextAreaLog);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPaneClientes, javax.swing.GroupLayout.DEFAULT_SIZE, 822, Short.MAX_VALUE)
                            .addComponent(jScrollPaneLog)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelPorta)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jTextFieldPorta)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonIniciar)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButtonParar)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabelClientes)
                                .addGap(267, 267, 267))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabelServidor)
                                .addGap(317, 317, 317))))))
            .addGroup(layout.createSequentialGroup()
                .addGap(312, 312, 312)
                .addComponent(jLabelLog)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelServidor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelPorta)
                    .addComponent(jTextFieldPorta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButtonIniciar)
                    .addComponent(jButtonParar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabelClientes)
                .addGap(14, 14, 14)
                .addComponent(jScrollPaneClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelLog)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPaneLog, javax.swing.GroupLayout.DEFAULT_SIZE, 317, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonIniciarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonIniciarActionPerformed
        jTextFieldPorta.setEnabled(false);
        jButtonIniciar.setEnabled(false);
        jButtonParar.setEnabled(true);
        
        IniciadoVr = true;

        try {
            socketServidor = new ServerSocket(Integer.parseInt(jTextFieldPorta.getText()));
        } catch (IOException ex) {
            System.out.println("VERIFICAR LINHA 201");
            //Logger.getLogger(TelaServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        jTextAreaLog.append("----- SERVIDOR INICIADO -----\n");
        System.out.println("----- SERVIDOR INICIADO -----");
        conexao = new Thread(this);
        new Thread(conexao).start();
    }//GEN-LAST:event_jButtonIniciarActionPerformed

    private void jButtonPararActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPararActionPerformed
        IniciadoVr = false;
        jTextFieldPorta.setEnabled(true);
        jButtonIniciar.setEnabled(true);
        jButtonParar.setEnabled(false);
        
        try {
            
            socketServidor.close();
            
        } catch (IOException ex) {
            System.out.println("VERIFICAR LINHA 220");
            //Logger.getLogger(TelaServidor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButtonPararActionPerformed

    public static void main(String args[]) {

        //CÓDIGO UTILIZADO PELA INTERFACE
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaServidor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaServidor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaServidor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaServidor.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //  CRIADO PELA INTERFACE AUTOMATICAMENTE
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new TelaServidor().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonIniciar;
    private javax.swing.JButton jButtonParar;
    private javax.swing.JLabel jLabelClientes;
    private javax.swing.JLabel jLabelLog;
    private javax.swing.JLabel jLabelPorta;
    private javax.swing.JLabel jLabelServidor;
    private javax.swing.JScrollPane jScrollPaneClientes;
    private javax.swing.JScrollPane jScrollPaneLog;
    private javax.swing.JTable jTableClientes;
    private javax.swing.JTextArea jTextAreaLog;
    private javax.swing.JTextField jTextFieldPorta;
    // End of variables declaration//GEN-END:variables

    private Runnable MultiThread() throws IOException {
        Runnable multi;
        multi = new Runnable() {
            PrintWriter saida = new PrintWriter(socketCliente.getOutputStream(), true);
            //BufferedReader entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            Scanner entrada = new Scanner(new InputStreamReader(socketCliente.getInputStream()));
            
            String clienteIP, clienteTipo, clienteMaterial, clienteDescricao;
            String mensagem;
            int clientePorta;
            String clienteNome = "VAZIO";//PARA CASO VENHA NOME VAZIO
            
            @Override
            public void run() {
                Cliente clienteVO = new Cliente(); //REMOVENDO CLIENTE
                //Dá uma mensagem que o cliente se desconectou
                //Envia a lista de clientes conectados
                //Pega os dados do cliente
                clienteIP = socketCliente.getInetAddress().getHostAddress();
                clientePorta = socketCliente.getPort();
                int vr = 1;//para desconexão
                //while (((mensagem = entrada.readLine()) != null) && IniciadoVr && vr) {
                while (entrada.hasNextLine() && vr==1) {
                    mensagem = entrada.nextLine();
                    System.out.println(" ");
                    System.out.println("Recebido do cliente: " + mensagem);
                    jTextAreaLog.append("Recebido do cliente: " + mensagem + "\n"); //mostra no chat a mensagem de está sendo enviada
                    
                    JSONObject enviaObjJSON = new JSONObject();
                    //Pega a mensagem recebida do cliente tipo STRING JSON converte em JSONObject novamente
                    JSONObject mensagem_JSONObject = new JSONObject(mensagem);
                    
                    //verificar se exist action
                    if (mensagem_JSONObject.has("action")) {
                        //Recebe do cliente
                        switch (mensagem_JSONObject.getString("action")) {
                            case "connect":
                                //se não existir NOME TIPO E MATERIAL ele nao atualiza a
                                if (mensagem_JSONObject.has("nome") && mensagem_JSONObject.has("tipo") && mensagem_JSONObject.has("material")) {
                                    if (!"".equals(mensagem_JSONObject.getString("nome")) && !"".equals(mensagem_JSONObject.getString("material")) && !"".equals(mensagem_JSONObject.getString("tipo")) ) {
                                        
                                        clienteNome = mensagem_JSONObject.getString("nome");
                                        clienteTipo = mensagem_JSONObject.getString("tipo");
                                        clienteMaterial = mensagem_JSONObject.getString("material");
                                        clienteVO.setDescricao("semDescricao");//coletor não tem descrição - USA em broadcast
                                        //coletor não possui descrição
                                        if (mensagem_JSONObject.has("descricao")) {
                                            clienteDescricao = mensagem_JSONObject.getString("descricao");
                                            clienteVO.setDescricao(clienteDescricao);
                                        }
                                        clienteVO.setNome(clienteNome);
                                        clienteVO.setIp(clienteIP);
                                        clienteVO.setPorta(clientePorta);
                                        clienteVO.setPortaString(Integer.toString(clientePorta));
                                        clienteVO.setSocketCliente(socketCliente);
                                        clienteVO.setMaterial(clienteMaterial);
                                        clienteVO.setColetorOuDoador(clienteTipo);
                                        
                                        listaClientes.add(clienteVO);
                                        jTextAreaLog.append("Cliente conectado:   " + clienteNome + " " + clienteIP + ":" + clientePorta + "\n");
                                        
                                        populaTabela();
                                        
                                        //JSON para enviar os dados do servidor para o cliente
                                        enviaObjJSON.put("action", "client_list");
                                        broadcast(enviaObjJSON);
                                    } else {
                                        System.out.println("CLIENTE DESCONECTADO: CHAVE: nome, tipo e material COM VALOR VAZIO");
                                        vr = 0;
                                    }
                                    
                                } else {
                                    System.out.println("CLIENTE DESCONECTADO: CHAVE: nome, tipo e material NÃO ENCONTRADO" );
                                    vr = 0;
                                }
                                break;
                            case "disconnect":
                                //Procura no arraylist e remove o elemento do mesmo
                                for (int i = 0; i < listaClientes.size(); i++) {
                                    if (listaClientes.get(i).getIp().equals(clienteIP) && listaClientes.get(i).getNome().equals(clienteNome) && listaClientes.get(i).getPorta() == clientePorta) {
                                        listaClientes.remove(i);
                                    }
                                }
                                
                                //Dá uma mensagem que o cliente se desconectou
                                JSONObject objJSONMsgDisconexao = new JSONObject();
                                objJSONMsgDisconexao.put("action", "chat_general_client");
                                objJSONMsgDisconexao.put("mensagem", clienteNome + ": se desconectou.");
                                System.out.println("Cliente: " + clienteNome + " se desconectou");
                                broadcast(objJSONMsgDisconexao);//enviando a lista
                                
                                //Envia a lista de clientes conectados
                                JSONObject objJSONListaClientes = new JSONObject();
                                objJSONListaClientes.put("action", "client_list");
                                broadcast(objJSONListaClientes);
                                
                                populaTabela();
                                vr = 0;//para sair do while
                                break;
                            case "chat_general_server": //MENSAGEM DE CHAT PARA TODOS
                                enviaObjJSON.put("action", "chat_general_client");
                                enviaObjJSON.put("mensagem", clienteNome + " (broadcast): " + mensagem_JSONObject.getString("mensagem"));
                                broadcast(enviaObjJSON);
                                break;
                            case "chat_room_server":
                                enviaObjJSON.put("action", "chat_room_client");
                                enviaObjJSON.put("mensagem", clienteNome + " (" + clienteMaterial + "):" + mensagem_JSONObject.getString("mensagem"));
                                multicast(enviaObjJSON, clienteMaterial);
                                break;
                            case "chat_request_server":
                                int portaDestinatario;
                                if(mensagem_JSONObject.has("destinatario")){
                                    try{
                                        portaDestinatario = Integer.parseInt(mensagem_JSONObject.getString("destinatario"));
                                    } catch (JSONException e) {
                                        portaDestinatario = mensagem_JSONObject.getInt("destinatario");
                                    }
                                    Cliente c = null;
                                    try{
                                        for (int j = 0; j < listaClientes.size(); j++) {
                                            if (listaClientes.get(j).getPorta() == portaDestinatario) {
                                                c = listaClientes.get(j);
                                            }
                                        }
                                        if(c.isStatusUnicast()==null){
                                            enviaObjJSON.put("action", "chat_request_client");
                                            enviaObjJSON.put("remetente", Integer.toString(clientePorta));
                                            unicast(enviaObjJSON, portaDestinatario);

                                        }else{
                                            enviaObjJSON.put("action", "client_busy");
                                            unicast(enviaObjJSON, clienteVO.getSocketCliente().getPort());
                                        }
                                    } catch (NullPointerException e){
                                        System.out.println("Ponto 403");
                                        enviaObjJSON.put("action", "client_busy");
                                        unicast(enviaObjJSON, clienteVO.getSocketCliente().getPort());
                                    }
                                    
                                } else  {
                                    System.out.println(" NEGADO - A action chat_request_server não contem JSON destinatario");
                                }
                                break;
                            case "chat_response_server":
                                
                                //Se o cliente aceitou o chat privado
                                try{
                                    portaDestinatario = mensagem_JSONObject.getInt("destinatario");
                                    String nomeRemetente = null;
                                    Cliente c = null;
                                    //for para encontrar o nome do cliente
                                    for (int j = 0; j < listaClientes.size(); j++) {
                                        if (listaClientes.get(j).getPorta() == portaDestinatario) {
                                            nomeRemetente = listaClientes.get(j).getNome();
                                            c = listaClientes.get(j);
                                        }
                                    }
                                    if (mensagem_JSONObject.getString("resposta").equals("true")) {
                                        System.out.println(clienteNome + " ACEITOU PEDIDO DE CHAT DE " + nomeRemetente + "(" + portaDestinatario + ")");
                                        enviaObjJSON.put("action", "chat_response_client");
                                        enviaObjJSON.put("resposta", "true");
                                        enviaObjJSON.put("remetente", Integer.toString(clientePorta));
                                        unicast(enviaObjJSON, portaDestinatario);
                                        c.setStatusUnicast(Integer.toString(clienteVO.getSocketCliente().getPort()));
                                        clienteVO.setStatusUnicast(Integer.toString(portaDestinatario));
                                    } else {
                                        System.out.println(clienteNome + " RECUSOU PEDIDO DE CHAT DE " + nomeRemetente + "(" + portaDestinatario + ")");
                                        enviaObjJSON.put("action", "chat_response_client");
                                        enviaObjJSON.put("resposta", "false");
                                        enviaObjJSON.put("remetente", Integer.toString(clientePorta));
                                        unicast(enviaObjJSON, portaDestinatario);
                                    }
                                    break;
                                } catch(JSONException e){
                                    System.out.println("destinatario 441");
                                }
                            case "chat_unicast_message_server":
                                //recebendo mensagem unicast
                                try{
                                    portaDestinatario = Integer.parseInt(mensagem_JSONObject.getString("destinatario"));
                                } catch (JSONException e) {
                                    portaDestinatario = mensagem_JSONObject.getInt("destinatario");
                                }
                                enviaObjJSON.put("action", "chat_unicast_message_client");
                                enviaObjJSON.put("mensagem", clienteNome + " (unicast): " + mensagem_JSONObject.getString("mensagem"));
                                unicast(enviaObjJSON, portaDestinatario);
                                unicast(enviaObjJSON, clienteVO.getSocketCliente().getPort());
                                break;
                                
                            case "chat_unicast_close_server":
                                //CANCELANDO CHAT
                                try{
                                    portaDestinatario = Integer.parseInt(mensagem_JSONObject.getString("destinatario"));
                                } catch (JSONException e) {
                                    portaDestinatario = mensagem_JSONObject.getInt("destinatario");
                                }
                                
                                Cliente c =null;
                                for (int j = 0; j < listaClientes.size(); j++) {
                                        if (listaClientes.get(j).getPorta() == portaDestinatario) {
                                            c = listaClientes.get(j);
                                        }
                                    }
                                c.setStatusUnicast(null);
                                clienteVO.setStatusUnicast(null);
                                
                                enviaObjJSON.put("action", "chat_unicast_close_client");
                                unicast(enviaObjJSON, portaDestinatario);
                                
                                break;
                            case "client_busy":
                                if(verificadorUnicast==true){
                                    System.out.println("Ocupado");
                                    Integer portaDestinatarioRecusado = Integer.parseInt(mensagemRecebida_JSONObject.getString("remetente"));

                                        JSONObject mensagemChatJSONObject = new JSONObject();
                                        mensagemChatJSONObject.put("action", "client_busy");
                                        mensagemChatJSONObject.put("resposta", "false");
                                        mensagemChatJSONObject.put("destinatario", Integer.toString(portaDestinatarioRecusado));
                                        saida.println(mensagemChatJSONObject);
                                }
                            default:
                                System.out.println("CLIENTE DESCONECTADO: PROTOCOLO NÃO RECONHECIDO");
                                vr = 0;
                        }
                    } else {                        
                        System.out.println("CLIENTE DESCONECTADO: ENTRADA NAO RECONHECIDO");
                        vr = 0;
                    }
                    
                }
                if(vr == 0){
                    saida.close();
                    entrada.close();
                } else {
                    System.out.println("----- CLIENTE: " + clienteNome + " PERDEU CONEXÃO linha 454 -----");                    //Procura no arraylist e remove o elemento do mesmo
                    for (int i = 0; i < listaClientes.size(); i++) {
                        if (listaClientes.get(i).getIp().equals(clienteIP) && listaClientes.get(i).getNome().equals(clienteNome) && listaClientes.get(i).getPorta() == clientePorta) {
                            listaClientes.remove(i);
                        }
                    }
                    //Dá uma mensagem que o cliente se desconectou
                    JSONObject objJSONMsgDisconexao = new JSONObject();
                    objJSONMsgDisconexao.put("action", "chat_general_client");
                    objJSONMsgDisconexao.put("mensagem", clienteNome + ": se desconectou.");
                    broadcast(objJSONMsgDisconexao);
                    
                    //Envia a lista de clientes conectados
                    JSONObject objJSONListaClientes = new JSONObject();
                    objJSONListaClientes.put("action", "client_list");
                    broadcast(objJSONListaClientes);
                    
                    populaTabela();
                    saida.close();
                    entrada.close();
                }
            }
        };
        return multi;
    }

    private void broadcast(JSONObject objJSON) {
        Socket clienteSocket = null;
        String stringRecebidaJSON, stringEnviadaJSON;
        PrintStream saida;

        //Serializa o para uma string
        stringRecebidaJSON = objJSON.toString();

        //Instanciando um obj JSON para enviar  dados do servidor para o cliente
        JSONObject enviaObjJSON = new JSONObject();

        //Envia para o cliente
        switch (objJSON.getString("action")) {
            case "client_list":
                enviaObjJSON.put("action", "client_list");
                JSONArray ja = new JSONArray();
                //Gera o array da lista dentro do JSON obj para cada cliente
                for (Cliente clienteVO : listaClientes) {
                    JSONObject jo = new JSONObject();
                    jo.put("nome", clienteVO.getNome());
                    jo.put("material", clienteVO.getMaterial());
                    jo.put("tipo", clienteVO.getColetorOuDoador());
                    //COLETOR NÃO TEM DESCRIÇÃO
                    if(clienteVO.getColetorOuDoador().equals("D")){
                        jo.put("descricao", clienteVO.getDescricao());
                    }
                    
                    jo.put("porta", Integer.toString(clienteVO.getPorta()));//convertendo para String
                    ja.put(jo);
                }
                enviaObjJSON.put("lista", ja);
                stringEnviadaJSON = enviaObjJSON.toString();
                break;
            case "chat_general_client":
                stringEnviadaJSON = stringRecebidaJSON;
                break;
            default:
                stringEnviadaJSON = stringRecebidaJSON;
                break;
        }

        System.out.println("Broadcast para os clientes: " + stringEnviadaJSON);
         jTextAreaLog.append("Broadcast para os clientes: " + stringEnviadaJSON + "\n");
         jTextAreaLog.append("---------------------------------------------\n");
         
        // Percorre o array para enviar para todos os clientes
        for (int j = 0; j < listaClientes.size(); j++) {
            clienteSocket = listaClientes.get(j).getSocketCliente();
            try {
                saida = new PrintStream(clienteSocket.getOutputStream());
                saida.println(stringEnviadaJSON);// enviando para todos os clientes
            } catch (IOException ex) {
                listaClientes.remove(j);
                JSONObject objJSONListaClientes = new JSONObject();
                objJSONListaClientes.put("action", "client_list");
                broadcast(objJSONListaClientes);

                populaTabela();
            }
        }
    }

    private void multicast(JSONObject objJSON, String material) {
        Socket clienteSocket = null;
        String stringRecebidaJSON, stringEnviadaJSON;
        PrintStream saida;

        //Serializa o para uma string
        stringRecebidaJSON = objJSON.toString();

        //Envia para o cliente
        switch (objJSON.getString("action")) {
            case "chat_room_client":
                stringEnviadaJSON = stringRecebidaJSON;
                break;
            default:
                stringEnviadaJSON = stringRecebidaJSON;
                break;
        }

               
        System.out.println("Multicast para material " + material + ": " + stringEnviadaJSON);
         jTextAreaLog.append("Multicast para material " + material + ": " + stringEnviadaJSON);
         jTextAreaLog.append("---------------------------------------------\n");
        

        // Percorre o array para enviar para todos os clientes do mesmo material
        for (int j = 0; j < listaClientes.size(); j++) {
            clienteSocket = listaClientes.get(j).getSocketCliente();
            if (listaClientes.get(j).getMaterial().equals(material)) {
                try {
                    saida = new PrintStream(clienteSocket.getOutputStream());
                    saida.println(stringEnviadaJSON);// enviando para todos os clientes do material
                } catch (IOException ex) {
                    System.out.println("Multicast 584");
                    listaClientes.remove(j);
                }
            }
        }
    }

    private void unicast(JSONObject objJSON, int portaDestinatario) {
        Socket clienteSocket = null;
        String stringRecebidaJSON, stringEnviadaJSON;
        PrintStream saida;

        //Serializa o para uma string
        stringRecebidaJSON = objJSON.toString();

        System.out.println("Unicast para o cliente: " + stringRecebidaJSON);

        // Percorre o array para enviar para o cliente do mesmo material
        for (int j = 0; j < listaClientes.size(); j++) {
            clienteSocket = listaClientes.get(j).getSocketCliente();
            if (listaClientes.get(j).getPorta() == portaDestinatario) {
                try {
                    saida = new PrintStream(clienteSocket.getOutputStream());
                    saida.println(stringRecebidaJSON);// enviando para o cliente unicast
                } catch (IOException ex) {
                    System.out.println("UNICAST 637");
                    listaClientes.remove(j);
                }
            }
        }
    }
}
