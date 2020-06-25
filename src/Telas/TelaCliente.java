/*
 *Kleber, Felipe, João
 */
package Telas;

//import static Telas.TelaServidor.listaClientes;
import ValueObjects.Cliente;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject; //Necessário instalar a biblioteca json-20180813.jar

public class TelaCliente extends javax.swing.JFrame {

    ArrayList<Cliente> listaClientes = new ArrayList<>();
    //UTILIZADO PELO SOCKET
    Socket socketCliente;
    PrintStream saida; // para enviar para o servidor
    BufferedReader entrada = null; // para receber do servidor
   
    DefaultTableModel modelo;
    String IPAddress;
    Runnable conexao;
    
    boolean verificadorUnicast; //true para ocupado
    int portaDestinatario, portaDestinatarioRecusado;//para unicast
    
    //CONSTRUTOR - É chamado pela classe Main ao executar o cliente
    public TelaCliente() throws IOException {
        initComponents();
        setLocationRelativeTo(null);
    }
    
    public void habilitaBotoesUnicast(boolean vr){
       //se true significa que entá acontecendo um unicast
       verificadorUnicast = vr;//true para chats unicast
        jButtonEnviarUnicast.setEnabled(vr);
        jTextFieldMensagemUnicast.setEnabled(vr);
        jButtonEnviarUnicast.setEnabled(vr);
        jButtonCancelarUnicast.setEnabled(vr);
        jRadioButtonPrivado.setEnabled(!vr);
        jButtonSolicitarChat.setEnabled(!vr);
    }
    
    public void desconectar(){
        jTextFieldMensagem.setEnabled(false);
        jTextFieldPorta.setEnabled(true);
        jTextFieldIPServidor.setEnabled(true);
        jTextFieldNome.setEnabled(true);
        jButtonEnviar.setEnabled(false);
        jButtonConectar.setEnabled(true);
        jButtonDesconectar.setEnabled(false);
        jRadioButtonColetor.setEnabled(!false);
        jRadioButtonDoador.setEnabled(!false);
        jComboBoxMaterial.setEnabled(!false);
        jRadioButtonTodos.setEnabled(!true);
        jRadioButtonMaterial.setEnabled(!true);
        jRadioButtonPrivado.setEnabled(!true);
        jTextFieldPrivado.setEnabled(!true);
        jTextFieldDescricao.setEnabled(!false);
        jButtonSolicitarChat.setEnabled(false);
        jRadioButtonTodos.setSelected(true);
        
        DefaultTableModel tabelaClientes = (javax.swing.table.DefaultTableModel) jTableClientes.getModel();
        tabelaClientes.setRowCount(0);

        JSONObject clienteDesconectando = new JSONObject();

        //Preenche o objeto JSON com a action para desconectar
        clienteDesconectando.put("action", "disconnect");
        String clienteDesconectandoJsonString = clienteDesconectando.toString();

        try {
            //IPAddress = InetAddress.getLocalHost().getHostAddress(); //Pega o endereço
            saida = new PrintStream(socketCliente.getOutputStream());
            System.out.println("DESCONECTADO DO SERVIDOR: " + clienteDesconectandoJsonString);
            saida.println(clienteDesconectandoJsonString);//Envia uma String JSON
            saida.close();
            entrada.close();
        } catch (IOException ex) {
            System.out.println("DESCONECTADO  LINHA 734 " + clienteDesconectandoJsonString);
        }
        
        jTextAreaChat.append("Desconectado...\n");
    }

    //MÉTODO É INICIADO ATRAVÉS DO BOTÃO CONECTAR - FAZ A CONEXÃO COM O SERVIDOR
    public void Conectando() throws IOException {
        Cliente clienteVO = new Cliente();
        String tipoCliente;
        
        //Feedback na interface e no terminal
        jTextAreaChat.append("----- CONECTADO AO SERVIDOR " + jTextFieldNome.getText() + "-----\n");
        System.out.println("---- CONECTADO AO SERVIDOR " + jTextFieldNome.getText() + "----\n");

        //Enable e Disable de botões
        jButtonEnviar.setEnabled(true);
        jTextFieldMensagem.setEnabled(true);
        jTextFieldNome.setEnabled(false);
        jTextFieldIPServidor.setEnabled(false);
        jTextFieldPorta.setEnabled(false);
        jButtonDesconectar.setEnabled(true);
        jButtonConectar.setEnabled(false);
        jRadioButtonColetor.setEnabled(false);
        jRadioButtonDoador.setEnabled(false);
        jComboBoxMaterial.setEnabled(false);
        jRadioButtonTodos.setEnabled(true);
        jRadioButtonMaterial.setEnabled(true);
        jRadioButtonPrivado.setEnabled(true);
        jTextFieldDescricao.setEnabled(false);
        jButtonEnviarUnicast.setEnabled(false);

        //COLETA DE DADOS
        //Radio button de seleção se é coletor ou doador
        if (jRadioButtonColetor.isSelected()) {
            tipoCliente = "C";
        } else {
            tipoCliente = "D";
        }
        clienteVO.setMaterial(jComboBoxMaterial.getSelectedItem().toString());

        //Instanciando um JSON e envia os dados do cliente para atualizar o array list do servidor
        JSONObject clienteConectando = new JSONObject();

        //Preenche o objeto com os campos
        clienteConectando.put("action", "connect");
        clienteConectando.put("nome", jTextFieldNome.getText());
        clienteConectando.put("tipo", tipoCliente);
        clienteConectando.put("material", clienteVO.getMaterial());
        
        if(tipoCliente == "D"){
            clienteConectando.put("descricao", jTextFieldDescricao.getText());
        }

        //Serializa o JSON para uma string
        String clienteConectandoJsonString = clienteConectando.toString();

        //Envia mensagem a string ao servidor
        saida = new PrintStream(socketCliente.getOutputStream());
        System.out.println("Enviado para o servidor ao se conectar: " + clienteConectandoJsonString);
        saida.println(clienteConectandoJsonString);

        //Thread de escuta do cliente para as mensagens do servidor
        new Thread() {
            @Override
            public void run() {
                boolean vr = true;
                try {
                    entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));//RECEBE A MENSAGEM DO SERVIDOR
                } catch (IOException ex) {
                    System.out.println("ERRO NA ENTRADA");
                    vr = false;
                }
                
                while (true && vr) {
                    //CONVERTENDO String para JSON Object
                    
                    JSONObject mensagemRecebida_JSONObject;
                    String nomeRemetente;
                     
                    try {
                        mensagemRecebida_JSONObject = new JSONObject(entrada.readLine());
                        System.out.println("Recebido:" + mensagemRecebida_JSONObject);
                        
                        if (mensagemRecebida_JSONObject.has("action")) {
                            //Recebe do servidor
                            switch (mensagemRecebida_JSONObject.getString("action")) {
                                case "chat_general_client":
                                    if(mensagemRecebida_JSONObject.has("mensagem")){
                                        jTextAreaChat.append(mensagemRecebida_JSONObject.getString("mensagem") + "\n");
                                    } else  {
                                        System.out.println(" NEGADO - A action chat_general_client não contem JSON mensagem");
                                    }
                                    break;
                                case "chat_room_client":
                                    if(mensagemRecebida_JSONObject.has("mensagem")){
                                        jTextAreaChat.append(mensagemRecebida_JSONObject.getString("mensagem") + "\n");
                                    } else  {
                                        System.out.println(" NEGADO - A action chat_general_client não contem JSON mensagem");
                                    }
                                    break;
                                case "chat_request_client":
                                    if(!verificadorUnicast){ //recusa o chat aso esteja ocupado
                                        nomeRemetente = null;
                                        if(mensagemRecebida_JSONObject.has("remetente")){
                                            try{
                                                portaDestinatario = Integer.parseInt(mensagemRecebida_JSONObject.getString("remetente"));
                                            } catch (JSONException e) {
                                                portaDestinatario = mensagemRecebida_JSONObject.getInt("remetente");
                                            }
                                        } else  {
                                            System.out.println(" NEGADO - A action chat_request_client não contem JSON remetente");
                                        }
                                        //for para encontrar o nome do cliente na lista de clientes conectados
                                        for (int j = 0; j < listaClientes.size(); j++) {
                                            if (listaClientes.get(j).getPorta() == portaDestinatario){
                                                nomeRemetente = listaClientes.get(j).getNome();
                                            }
                                        }

                                        // showConfirmDialog: 0= Sim ; 1= Não; 2= Cancelar
                                        int showConfirmDialog = JOptionPane.showConfirmDialog(rootPane, nomeRemetente + " deseja iniciar um chat privado você. Aceitar?", "Chat Privado - " + nomeRemetente, WIDTH);

                                        //Se aceitou conexão
                                        if (showConfirmDialog == 0) {
                                            System.out.println("VOCÊ ACEITOU O CHAT PRIVADO COM " + nomeRemetente);
                                            JSONObject mensagemChatJSONObject = new JSONObject();
                                            mensagemChatJSONObject.put("action", "chat_response_server");
                                            mensagemChatJSONObject.put("resposta", "true");
                                            mensagemChatJSONObject.put("destinatario", Integer.toString(portaDestinatario));
                                            saida.println(mensagemChatJSONObject);
                                            jTextAreaChatUnicast.append("você aceitou o chat unicast"  + "\n");
                                            habilitaBotoesUnicast(true);

                                        }
                                        //Se recusou conexão
                                        else {
                                            System.out.println("VOCÊ RECUSOU O CHAT PRIVADO COM " + nomeRemetente);
                                            jTextAreaChatUnicast.append("VOCÊ RECUSOU O CHAT PRIVADO COM " + nomeRemetente);
                                            JSONObject mensagemChatJSONObject = new JSONObject();
                                            mensagemChatJSONObject.put("action", "chat_response_server");
                                            mensagemChatJSONObject.put("resposta", "false");
                                            mensagemChatJSONObject.put("destinatario", Integer.toString(portaDestinatario));
                                            saida.println(mensagemChatJSONObject);
                                            //verificadorUnicast = false;
                                            habilitaBotoesUnicast(false);
                                        }
                                    }else { //caso já esteja em um chat unicast
                                        System.out.println("Recusado o chat unicast");
                                        portaDestinatarioRecusado = Integer.parseInt(mensagemRecebida_JSONObject.getString("remetente"));

                                        JSONObject mensagemChatJSONObject = new JSONObject();
                                        mensagemChatJSONObject.put("action", "chat_response_server");
                                        mensagemChatJSONObject.put("resposta", "false");
                                        mensagemChatJSONObject.put("destinatario", Integer.toString(portaDestinatarioRecusado));
                                        saida.println(mensagemChatJSONObject);
                                    }
                                    
                                    break;
                                case "chat_response_client":
                                    nomeRemetente = null;
                                    if(mensagemRecebida_JSONObject.has("remetente")){
                                        try{
                                            portaDestinatario = Integer.parseInt(mensagemRecebida_JSONObject.getString("remetente"));
                                        } catch (JSONException e) {
                                            portaDestinatario = mensagemRecebida_JSONObject.getInt("remetente");
                                        }
                                    } else  {
                                        System.out.println(" NEGADO - A action chat_response_client não contem JSON remetente");
                                    }
                                    
                                    //for para encontrar o nome do cliente na lista de clientes conectados
                                    for (int j = 0; j < listaClientes.size(); j++) {
                                        if (listaClientes.get(j).getPorta() == portaDestinatario){
                                            nomeRemetente = listaClientes.get(j).getNome();
                                        }
                                    }
                                    
                                    //PAREI AQUI 13-05-2019
                                    //Se a resposta do cliente solicitado para o chat privado for positiva
                                    if (mensagemRecebida_JSONObject.getString("resposta").equals("true")){
                                        System.out.println(nomeRemetente + " aceitou conversar em privado com você " + jTextFieldNome.getText());
                                        jTextAreaChatUnicast.append("chat unicast iniciado!" + "\n");
                                        
                                        habilitaBotoesUnicast(true);
                                    }
                                    //Se a resposta do cliente solicitado para o chat privado for negativa
                                    else if (mensagemRecebida_JSONObject.getString("resposta").equals("false")) {
                                        jTextAreaChatUnicast.append("chat unicast não iniciado!");
                                        System.out.println(nomeRemetente + " não aceitou conversar em privado com você " + jTextFieldNome.getText());
                                        habilitaBotoesUnicast(false);
                                    }
                                    break;
                                case "chat_unicast_message_client":
                                    jTextAreaChatUnicast.append(mensagemRecebida_JSONObject.getString("mensagem") + "\n");
                                    break;
                                case "chat_unicast_close_client":
                                    jTextAreaChatUnicast.append("Chat Unicast finalizado" + "\n");
                                    System.out.println("Chat Unicast finalizado");
                                    habilitaBotoesUnicast(false);
                                    break;
                                case "client_list": //Recebe a lista de clientes do servidor
                                    listaClientes.clear(); //Limpa o arraylist atual
                                    if(mensagemRecebida_JSONObject.has("lista")){
                                        JSONArray arrayListaJSON = mensagemRecebida_JSONObject.getJSONArray("lista"); //Atribui o array de clientes do JSON enviado pelo servidor nesta variável
                                        
                                        //Pega as informações do array de clientes e adiciona ao arraylist que irá exibir na tabela da tela.
                                        for (int i = 0; i < arrayListaJSON.length(); i++) {
                                            Cliente adicionandoCliente = new Cliente();
                                            JSONObject clienteJSON = arrayListaJSON.getJSONObject(i);
                                            //Atribui os dados no objeto adicionandoCliente
                                            if (clienteJSON.has("porta") && clienteJSON.has("nome") && clienteJSON.has("tipo") && clienteJSON.has("material")) {
                                                if ("".equals(clienteJSON.getString("nome")) || "".equals(clienteJSON.getString("material")) || "".equals(clienteJSON.getString("tipo")) ) {
                                                    
                                                    System.out.println("NEGADO - A action client_list com campos nome, tipo, material e porta INVÁLIDOS");
                                                } else {
                                                    adicionandoCliente.setNome(clienteJSON.getString("nome"));
                                                    adicionandoCliente.setMaterial(clienteJSON.getString("material"));
                                                    try{
                                                        adicionandoCliente.setPortaString(clienteJSON.getString("porta"));
                                                    } catch (JSONException e) {
                                                        adicionandoCliente.setPortaString(Integer.toString(clienteJSON.getInt("porta")));
                                                    }
                                                    adicionandoCliente.setColetorOuDoador(clienteJSON.getString("tipo"));
                                                    adicionandoCliente.setPorta(Integer.parseInt(adicionandoCliente.getPortaString()));
                                                    if(clienteJSON.getString("tipo").equals("D")){
                                                        if(clienteJSON.has("descricao")){
                                                            adicionandoCliente.setDescricao(clienteJSON.getString("descricao"));
                                                        }
                                                    }
                                                    listaClientes.add(adicionandoCliente); //Adiciona ao arrayList
                                                }
                                            }
                                        }
                                        populaTabela();
                                    }
                                    break;
                                case "client_disconnect":
                                    vr = false;
                                    break;
                                case "disconnect":
                                    vr = false;
                                    break;
                                case "chat_request_error":
                                    System.out.println("ACTION NÃO MAIS UTILIZADA -> " + mensagemRecebida_JSONObject.getString("action"));
                                    break;
                                case "client_busy":
                                    JOptionPane.showMessageDialog(null, "O usuário ja está em uma conversa privada.");
                                    habilitaBotoesUnicast(false);
                                    break;
                                default:
                                    System.out.println("ACTION NÃO RECONHECIDA -> " + mensagemRecebida_JSONObject.getString("action"));
                                    break;
                            }
                        }
                    } catch (HeadlessException | IOException | NullPointerException| NumberFormatException | JSONException | IndexOutOfBoundsException erro) {
                        System.out.println("CONEXÃO FINALIZADA");
                        desconectar();
                        
                        vr = false;
                    }
                }
            }
        }.start();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroupColetorDoador = new javax.swing.ButtonGroup();
        buttonGroupMensagem = new javax.swing.ButtonGroup();
        jButtonConectar = new javax.swing.JButton();
        jLabelPorta = new javax.swing.JLabel();
        jLabelIPServidor = new javax.swing.JLabel();
        jTextFieldIPServidor = new javax.swing.JTextField();
        jTextFieldPorta = new javax.swing.JTextField();
        jLabelNome = new javax.swing.JLabel();
        jTextFieldNome = new javax.swing.JTextField();
        jLabelMaterial = new javax.swing.JLabel();
        jComboBoxMaterial = new javax.swing.JComboBox<>();
        jRadioButtonColetor = new javax.swing.JRadioButton();
        jRadioButtonDoador = new javax.swing.JRadioButton();
        jScrollPaneClientes = new javax.swing.JScrollPane();
        jTableClientes = new javax.swing.JTable();
        jRadioButtonTodos = new javax.swing.JRadioButton();
        jRadioButtonMaterial = new javax.swing.JRadioButton();
        jRadioButtonPrivado = new javax.swing.JRadioButton();
        jTextFieldPrivado = new javax.swing.JTextField();
        jTextFieldMensagem = new javax.swing.JTextField();
        jLabelMensagem = new javax.swing.JLabel();
        jLabelChat = new javax.swing.JLabel();
        jButtonEnviar = new javax.swing.JButton();
        jButtonDesconectar = new javax.swing.JButton();
        jLabelDescricao = new javax.swing.JLabel();
        jLabelTipo = new javax.swing.JLabel();
        jTextFieldDescricao = new javax.swing.JTextField();
        jButtonSolicitarChat = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jScrollPaneChat = new javax.swing.JScrollPane();
        jTextAreaChat = new javax.swing.JTextArea();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextAreaChatUnicast = new javax.swing.JTextArea();
        jLabelChat1 = new javax.swing.JLabel();
        jLabelMensagem1 = new javax.swing.JLabel();
        jTextFieldMensagemUnicast = new javax.swing.JTextField();
        jButtonEnviarUnicast = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jButtonCancelarUnicast = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jButtonConectar.setText("Conectar");
        jButtonConectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonConectarActionPerformed(evt);
            }
        });

        jLabelPorta.setText("Porta:");

        jLabelIPServidor.setText("IP do Servidor:");

        jTextFieldIPServidor.setText("127.0.0.1");
        jTextFieldIPServidor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldIPServidorActionPerformed(evt);
            }
        });

        jTextFieldPorta.setText("20100");
        jTextFieldPorta.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPortaActionPerformed(evt);
            }
        });

        jLabelNome.setText("Nome:");

        jTextFieldNome.setText("Felipe");
        jTextFieldNome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldNomeActionPerformed(evt);
            }
        });

        jLabelMaterial.setText("Material:");

        jComboBoxMaterial.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "eletronico", "metal", "oleo", "papel", "plastico", "roupa" }));
        jComboBoxMaterial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxMaterialActionPerformed(evt);
            }
        });

        buttonGroupColetorDoador.add(jRadioButtonColetor);
        jRadioButtonColetor.setText("Coletor");
        jRadioButtonColetor.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonColetorActionPerformed(evt);
            }
        });

        buttonGroupColetorDoador.add(jRadioButtonDoador);
        jRadioButtonDoador.setSelected(true);
        jRadioButtonDoador.setText("Doador");
        jRadioButtonDoador.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonDoadorActionPerformed(evt);
            }
        });

        jTableClientes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nome", "Porta", "Tipo", "Material", "Descrição"
            }
        ));
        jScrollPaneClientes.setViewportView(jTableClientes);

        buttonGroupMensagem.add(jRadioButtonTodos);
        jRadioButtonTodos.setSelected(true);
        jRadioButtonTodos.setText("Todos");
        jRadioButtonTodos.setEnabled(false);
        jRadioButtonTodos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonTodosActionPerformed(evt);
            }
        });

        buttonGroupMensagem.add(jRadioButtonMaterial);
        jRadioButtonMaterial.setText("Todos do material");
        jRadioButtonMaterial.setEnabled(false);
        jRadioButtonMaterial.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonMaterialActionPerformed(evt);
            }
        });

        buttonGroupMensagem.add(jRadioButtonPrivado);
        jRadioButtonPrivado.setText("Privado");
        jRadioButtonPrivado.setEnabled(false);
        jRadioButtonPrivado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButtonPrivadoActionPerformed(evt);
            }
        });

        jTextFieldPrivado.setText("Porta");
        jTextFieldPrivado.setEnabled(false);
        jTextFieldPrivado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextFieldPrivadoActionPerformed(evt);
            }
        });

        jTextFieldMensagem.setText("Digite aqui...");
        jTextFieldMensagem.setEnabled(false);

        jLabelMensagem.setText("Mensagem Broad/Multi");

        jLabelChat.setText("Chat");

        jButtonEnviar.setText("Enviar");
        jButtonEnviar.setEnabled(false);
        jButtonEnviar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEnviarActionPerformed(evt);
            }
        });

        jButtonDesconectar.setText("Desconectar");
        jButtonDesconectar.setEnabled(false);
        jButtonDesconectar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDesconectarActionPerformed(evt);
            }
        });

        jLabelDescricao.setText("Descrição:");

        jLabelTipo.setText("Tipo:");

        jTextFieldDescricao.setText("Possuo 12kg de ferro");

        jButtonSolicitarChat.setText("Solicitar Unicast");
        jButtonSolicitarChat.setEnabled(false);
        jButtonSolicitarChat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSolicitarChatActionPerformed(evt);
            }
        });

        jTextAreaChat.setColumns(20);
        jTextAreaChat.setRows(5);
        jScrollPaneChat.setViewportView(jTextAreaChat);

        jTabbedPane1.addTab("BROAD/MULTICAST", jScrollPaneChat);

        jTextAreaChatUnicast.setColumns(20);
        jTextAreaChatUnicast.setRows(5);
        jScrollPane1.setViewportView(jTextAreaChatUnicast);

        jLabelChat1.setText("UNICAST");

        jLabelMensagem1.setText("Mensagem unicast");

        jTextFieldMensagemUnicast.setText("Digite aqui...");
        jTextFieldMensagemUnicast.setEnabled(false);

        jButtonEnviarUnicast.setText("Enviar");
        jButtonEnviarUnicast.setEnabled(false);
        jButtonEnviarUnicast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonEnviarUnicastActionPerformed(evt);
            }
        });

        jButtonCancelarUnicast.setText("Cancelar Unicast");
        jButtonCancelarUnicast.setEnabled(false);
        jButtonCancelarUnicast.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCancelarUnicastActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelIPServidor)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldIPServidor, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabelPorta)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldPorta, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelTipo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jRadioButtonDoador)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jRadioButtonColetor)
                .addGap(89, 89, 89)
                .addComponent(jLabelMaterial)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxMaterial, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPaneClientes))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jLabelMensagem)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jRadioButtonTodos)
                                .addGap(18, 18, 18)
                                .addComponent(jRadioButtonMaterial)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jTextFieldMensagem, javax.swing.GroupLayout.PREFERRED_SIZE, 227, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 596, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addGap(6, 6, 6)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jRadioButtonPrivado)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(jTextFieldPrivado, javax.swing.GroupLayout.PREFERRED_SIZE, 191, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jButtonSolicitarChat))
                                            .addGroup(layout.createSequentialGroup()
                                                .addComponent(jLabelMensagem1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(jTextFieldMensagemUnicast, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGap(18, 18, 18)
                                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jButtonEnviarUnicast, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jButtonCancelarUnicast))))
                                .addGap(11, 11, 11)))))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabelNome)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldNome, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabelDescricao)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextFieldDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, 325, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 294, Short.MAX_VALUE)
                .addComponent(jButtonConectar, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonDesconectar, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(layout.createSequentialGroup()
                .addGap(542, 542, 542)
                .addComponent(jLabelChat)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jButtonEnviar, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 569, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabelChat1)
                .addGap(246, 246, 246))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabelIPServidor)
                    .addComponent(jTextFieldIPServidor, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelPorta)
                    .addComponent(jTextFieldPorta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelMaterial)
                    .addComponent(jComboBoxMaterial, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelTipo)
                    .addComponent(jRadioButtonColetor)
                    .addComponent(jRadioButtonDoador))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelNome)
                                .addComponent(jTextFieldNome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabelDescricao)
                                .addComponent(jTextFieldDescricao, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jButtonConectar)
                                .addComponent(jButtonDesconectar)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPaneClientes, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabelChat))
                    .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldMensagem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelMensagem)
                            .addComponent(jButtonEnviar)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabelChat1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jTextFieldMensagemUnicast, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelMensagem1)
                            .addComponent(jButtonEnviarUnicast))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButtonTodos)
                    .addComponent(jRadioButtonMaterial)
                    .addComponent(jTextFieldPrivado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButtonPrivado)
                    .addComponent(jButtonSolicitarChat)
                    .addComponent(jButtonCancelarUnicast))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonConectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonConectarActionPerformed
        try {
            socketCliente = new Socket(jTextFieldIPServidor.getText(), Integer.parseInt(jTextFieldPorta.getText()));
            
            //socketCliente = new Socket("10.20.8.54", 20100);

            Conectando();

        } catch (IOException ex) {
            System.out.println("669 socket no botão conectar");
        }
    }//GEN-LAST:event_jButtonConectarActionPerformed

    private void jTextFieldPortaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPortaActionPerformed
    }//GEN-LAST:event_jTextFieldPortaActionPerformed

    private void jTextFieldNomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldNomeActionPerformed
    }//GEN-LAST:event_jTextFieldNomeActionPerformed

    private void jRadioButtonTodosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonTodosActionPerformed
        jTextFieldPrivado.setEnabled(!true);
        jTextFieldMensagem.setEnabled(!false);
        jButtonEnviar.setEnabled(!false);
        jButtonSolicitarChat.setEnabled(false);
    }//GEN-LAST:event_jRadioButtonTodosActionPerformed

    private void jRadioButtonMaterialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonMaterialActionPerformed
        jTextFieldPrivado.setEnabled(!true);
        jTextFieldMensagem.setEnabled(!false);
        jButtonEnviar.setEnabled(!false);
        jButtonSolicitarChat.setEnabled(false);
    }//GEN-LAST:event_jRadioButtonMaterialActionPerformed

    private void jButtonEnviarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEnviarActionPerformed

        //Instanciando um JSON para enviar os dados do cliente para o Chat
        JSONObject mensagemChatJSONObject = new JSONObject();
             //Mensagem em broadcast
            if (jRadioButtonTodos.isSelected()) {
                mensagemChatJSONObject.put("action", "chat_general_server"); //código de chat geral para o servidor
                mensagemChatJSONObject.put("mensagem", jTextFieldMensagem.getText());
                System.out.println("enviando(B):" + mensagemChatJSONObject);
                saida.println(mensagemChatJSONObject);
            }
            //Mensagem em multicast
            if (jRadioButtonMaterial.isSelected()) {
                mensagemChatJSONObject.put("action", "chat_room_server"); //código de chat geral para o servidor
                mensagemChatJSONObject.put("mensagem", jTextFieldMensagem.getText());
                System.out.println("enviando (M):" + mensagemChatJSONObject);
                saida.println(mensagemChatJSONObject);
            }
        
        
        jTextFieldMensagem.setText(""); //Limpa o campo após enviar a mensagem
    }//GEN-LAST:event_jButtonEnviarActionPerformed

    public void populaTabela() {
        SwingUtilities.invokeLater(new Runnable() {
               @Override
               public void run() {
                   try{
                        DefaultTableModel tabelaClientes = (javax.swing.table.DefaultTableModel) jTableClientes.getModel();
                        tabelaClientes.setRowCount(0);
                        for (int i = 0; i < listaClientes.size(); i++) {
                            Object[] linha = {listaClientes.get(i).getNome(),
                                listaClientes.get(i).getPorta(), listaClientes.get(i).getColetorOuDoador(), listaClientes.get(i).getMaterial(), listaClientes.get(i).getDescricao()
                            };
                            tabelaClientes.addRow(linha);
                        }
                   } catch (NullPointerException | IndexOutOfBoundsException e){
                       System.out.println("743");
                   }
               }
           });
        //Exibe todos os valores do Arraylist tabela da interface
    }

    private void jButtonDesconectarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDesconectarActionPerformed
        jTextFieldMensagem.setEnabled(false);
        jTextFieldPorta.setEnabled(true);
        jTextFieldIPServidor.setEnabled(true);
        jTextFieldNome.setEnabled(true);
        jButtonEnviar.setEnabled(false);
        jButtonConectar.setEnabled(true);
        jButtonDesconectar.setEnabled(false);
        jRadioButtonColetor.setEnabled(!false);
        jRadioButtonDoador.setEnabled(!false);
        jComboBoxMaterial.setEnabled(!false);
        jRadioButtonTodos.setEnabled(!true);
        jRadioButtonMaterial.setEnabled(!true);
        jRadioButtonPrivado.setEnabled(!true);
        jTextFieldPrivado.setEnabled(!true);
        jTextFieldDescricao.setEnabled(!false);
        jButtonSolicitarChat.setEnabled(false);
        jRadioButtonTodos.setSelected(true);
        
        DefaultTableModel tabelaClientes = (javax.swing.table.DefaultTableModel) jTableClientes.getModel();
        tabelaClientes.setRowCount(0);

        JSONObject clienteDesconectando = new JSONObject();

        //Preenche o objeto JSON com a action para desconectar
        clienteDesconectando.put("action", "disconnect");
        String clienteDesconectandoJsonString = clienteDesconectando.toString();

        try {
           
            //Manda mensagem ao servidor
            saida = new PrintStream(socketCliente.getOutputStream());
            System.out.println("DESCONECTADO DO SERVIDOR 787: " + clienteDesconectandoJsonString);
            saida.println(clienteDesconectandoJsonString);//Envia uma String JSON
            saida.close();
           
        } catch (IOException ex) {
            System.out.println("DESCONECTADO  LINHA 734 " + clienteDesconectandoJsonString);
        }
        
        jTextAreaChat.append("Desconectado...\n");
    }//GEN-LAST:event_jButtonDesconectarActionPerformed

    private void jTextFieldIPServidorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldIPServidorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldIPServidorActionPerformed

    private void jRadioButtonPrivadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonPrivadoActionPerformed
        jTextFieldPrivado.setEnabled(true);
        
        jButtonSolicitarChat.setEnabled(true);
    }//GEN-LAST:event_jRadioButtonPrivadoActionPerformed

    private void jButtonSolicitarChatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSolicitarChatActionPerformed
        if (jRadioButtonPrivado.isSelected()) {
            JSONObject mensagemChatJSONObject = new JSONObject();
            mensagemChatJSONObject.put("action", "chat_request_server"); //código de chat geral para o servidor
            mensagemChatJSONObject.put("mensagem", jTextFieldPrivado.getText());
            String portaDestiString = jTextFieldPrivado.getText();
            try{
                portaDestinatario = Integer.parseInt(portaDestiString);//para usar na hora de enviar as mensagens
                mensagemChatJSONObject.put("destinatario", portaDestiString);
                jTextFieldMensagemUnicast.setText("");
                jTextAreaChatUnicast.append("solicitando Unicast porta : " + portaDestiString  + "\n");
                System.out.println("solicitando Unicast: " + mensagemChatJSONObject);
                saida.println(mensagemChatJSONObject);
               
                
            } catch (NumberFormatException e){
                jTextAreaChatUnicast.append("Porta inválida");
                System.out.println("Porta inválida");
               jTextFieldMensagemUnicast.setText(""); 
            }
            
        }        // TODO add your handling code here:
    }//GEN-LAST:event_jButtonSolicitarChatActionPerformed

    private void jRadioButtonDoadorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonDoadorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonDoadorActionPerformed

    private void jRadioButtonColetorActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButtonColetorActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButtonColetorActionPerformed

    private void jTextFieldPrivadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextFieldPrivadoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextFieldPrivadoActionPerformed

    private void jComboBoxMaterialActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxMaterialActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jComboBoxMaterialActionPerformed

    private void jButtonEnviarUnicastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonEnviarUnicastActionPerformed
        ////Instanciando um JSON para enviar os dados do cliente para o Chat
        JSONObject mensagemChatJSONObject = new JSONObject();
        
        //mensagem em unicast
        mensagemChatJSONObject.put("action","chat_unicast_message_server"); //código de chat unicast para o servidor
        mensagemChatJSONObject.put("destinatario", Integer.toString(portaDestinatario)); //código de chat geral para o servidor
        mensagemChatJSONObject.put("mensagem", jTextFieldMensagemUnicast.getText());
        System.out.println("enviando (U):" + mensagemChatJSONObject);
       saida.println(mensagemChatJSONObject);
        
        jTextFieldMensagemUnicast.setText(""); //Limpa o campo após enviar a mensagem
    }//GEN-LAST:event_jButtonEnviarUnicastActionPerformed

    private void jButtonCancelarUnicastActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCancelarUnicastActionPerformed
        ////Instanciando um JSON para enviar os dados do cliente para o Chat
        JSONObject mensagemChatJSONObject = new JSONObject();
        
        //mensagem em unicast
        mensagemChatJSONObject.put("action","chat_unicast_close_server"); //código de chat unicast para o servidor
        mensagemChatJSONObject.put("destinatario", Integer.toString(portaDestinatario)); //código de chat geral para o servidor
        System.out.println("enviando(U):" + mensagemChatJSONObject);
        saida.println(mensagemChatJSONObject);
        
        jTextFieldMensagemUnicast.setText(""); //Limpa o campo após enviar a mensagem
        habilitaBotoesUnicast(false);
    }//GEN-LAST:event_jButtonCancelarUnicastActionPerformed

    public static void main(String args[]) {

        //Essa parte pertence à interface
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(TelaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(TelaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(TelaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(TelaCliente.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Instancia a interface chamando o construtor */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new TelaCliente().setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(TelaCliente.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroupColetorDoador;
    private javax.swing.ButtonGroup buttonGroupMensagem;
    private javax.swing.JButton jButtonCancelarUnicast;
    private javax.swing.JButton jButtonConectar;
    private javax.swing.JButton jButtonDesconectar;
    private javax.swing.JButton jButtonEnviar;
    private javax.swing.JButton jButtonEnviarUnicast;
    private javax.swing.JButton jButtonSolicitarChat;
    private javax.swing.JComboBox<String> jComboBoxMaterial;
    private javax.swing.JLabel jLabelChat;
    private javax.swing.JLabel jLabelChat1;
    private javax.swing.JLabel jLabelDescricao;
    private javax.swing.JLabel jLabelIPServidor;
    private javax.swing.JLabel jLabelMaterial;
    private javax.swing.JLabel jLabelMensagem;
    private javax.swing.JLabel jLabelMensagem1;
    private javax.swing.JLabel jLabelNome;
    private javax.swing.JLabel jLabelPorta;
    private javax.swing.JLabel jLabelTipo;
    private javax.swing.JRadioButton jRadioButtonColetor;
    private javax.swing.JRadioButton jRadioButtonDoador;
    private javax.swing.JRadioButton jRadioButtonMaterial;
    private javax.swing.JRadioButton jRadioButtonPrivado;
    private javax.swing.JRadioButton jRadioButtonTodos;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPaneChat;
    private javax.swing.JScrollPane jScrollPaneClientes;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTableClientes;
    private javax.swing.JTextArea jTextAreaChat;
    private javax.swing.JTextArea jTextAreaChatUnicast;
    private javax.swing.JTextField jTextFieldDescricao;
    private javax.swing.JTextField jTextFieldIPServidor;
    private javax.swing.JTextField jTextFieldMensagem;
    private javax.swing.JTextField jTextFieldMensagemUnicast;
    private javax.swing.JTextField jTextFieldNome;
    private javax.swing.JTextField jTextFieldPorta;
    private javax.swing.JTextField jTextFieldPrivado;
    // End of variables declaration//GEN-END:variables

}
