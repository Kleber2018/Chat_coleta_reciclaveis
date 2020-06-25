
Exercício para desenvolver um sistema de chat para doações de reciclaveis.

Desenvolvido com: Java, Thread, Socket


------------------------------------------------------------------------------------------------------

 PROTOCOLO QUE SERÁ UTILIZADO: TCP
   ***IMPORTANTE***
   Ter atenção com problemas relacionados ao uso de Thread 

------------------------------------------------------------------------------------------------------

 Link com chat TCP que usa JSON para troca de mensagens
  https://github.com/robjean9/Chat-TCP

====================================================================================================== 
 
 ****REQUISITOS****
 
 Materiais: 

    Óleo;
    Metal;
    Roupa;
    Papel;
    Plástico;
    Eletrônico;
 
 O sistema deve conter os seguintes formas de troca de mensagens:  
	 UNICAST   : Há apenas um remetente e um destinátario na troca de mensagens.
	 MULTICAST : Há apenas um remetente e é enviado para um grupo específico de destinátarios.
	 BROADCAST : Há apenas um remetente mas as informações são enviadas para todos os receptores conectados.
 
 Doador/Coletor ==== PARA ===> Servidor 
 
#	 => Entrar DOADOR (O doador deve informar o que esta doando e descrever a doação)
#	 => Entrar COLETOR (O coletor deve informar o que esta coletando)
					   
  *Requisitos em comum (Usados tanto para Coletor como para Doador)
	  => Sair do Sistema;
         => Enviar mensagem de chat (BROADCAST); //chat geral
        => Envia mensagem para Doadores/Coletores específicos por MATERIAL (MULTICAST);
	  => Solicitar chat (UNICAST); 
         => Aceitar solicitação de chat (UNICAST);
	  => Rejeitar solicitação do chat (UNICAST);
	  => Enviar mensagem chat (UNICAST);
         => Encerrar chat (UNICAST); 
          
 Requisito do Doador/Coletor (Não tem protocolo)
         => lado cliente deve armazenar cada mensagem enviada e recebida. (exemplo: tela, terminal(system.out.printline), etc...)
		     
 Servidor ===para====> Doador/Coletor

         => Encaminhar uma determinada mensagem para o chat geral (BroadCast); //replica a mensagem broadcast
         => Encaminhar a mensagem para Doadores/Coletores específicos por MATERIAL, informando a mensagem e o remetente. (MULTICAST)
             Obs:. Deve-se enviar somente de acordo com o MATERIAL selecionado no momento do acesso do sistema
        => Enviar solicitação do chat (UNICAST) para destinatário (esse destinatário pode ser um coletor ou doador)
         => Informar ao remetente(coletor/doador) que o destinátario(coletor/doador) REJEITOU o pedido do chat enviado anteriormente.
         => Informar ao remetente(coletor/doador) que o destinátario(coletor/doador) ACEITOU o pedido do chat enviado anteriormente.
	  => Encaminhar mensagem para o destinatário (UNICAST);
         => Servidor informa Coletor/Doador que o outro encerrou o chat em (UNICAST)
         => Enviar para todos os conectados a lista atualizada de doadores e coletores a cada entrada ou saída de um dos Doador/Coletor; 
             Obs:. (O filtro por MATERIAL é uma opção da tela do coletor/doador) 

  Protocolos para tratamento de erro
         => Quando estiver estabelecida uma conexão unicast e o doador/coletor desconectar "bruscamente" ou sair de forma expontanea, o servidor deve informar.
#         => Informar o cliente que não foi possivel processar a solicitação.
#         => Em caso de disputa entre dois ou mais coletores/doadores para estabelecer,
		    "ao mesmo tempo", um chat unicast com coletor/doador específico:
			 O critério estabelecido será: o primeiro ganha. 
			 Obs:. O servidor deve informar ao(s) coletor(es)/doador(es) que "perdeu a disputa". 
				   Exemplo: 'Lamento! No momento está pessoa já está em uma negocição'.

          
 Requisito do Servidor (Não tem protocolo)
         => ADICIONAR Doador/Coletar em uma lista ( ArrayList / Arquivo .txt ) quando ENTRA no sistema; 
             Deve estar nessa lista o: 
             IP, 
             porta, 
             nome,
             tipo,
             material,
             ocupado,               // True = (Esta em chat unicast)  // False = (Esta disponível)
             porta_coversa_unicast, // porta efêmera                  // null ou 0
             (para o doador a descrição do que esta doando)
         => servidor deve armazenar cada mensagem enviada e recebida. (exemplo: tela, terminal(system.out.printline), etc...)
         => (OPCIONAL POR GRUPO) ataque ddos, verificar se um mesmo doador/coletor faz uma grande quantidade de requisições em um curto espaço de tempo.
         => REMOVER Doador/Coletar em uma lista ( ArrayList / Arquivo .txt ) quando SAI do sistema;

====================================================================================================== 
//Doador -> SERVIDOR === Entrar no Sistema //
{
    "action":"connect",
    "nome":"O nome não pode ser String Vazia ou somente espaços em branco",
    "tipo":"D",
    "material":"o material obrigatoriamente deve ser um dos materiais ao lado"      //oleo | metal | roupa | papel | plastico | eletronico
    "descricao":"a mensagem não pode ser String Vazia ou somente espaços em branco"
}

//Coletor -> SERVIDOR === Entrar no Sistema
{
    "action":"connect",
    "nome":"O nome não pode ser String Vazia ou somente espaços em branco",
    "tipo":"C",
    "material":"o material obrigatoriamente deve ser um dos materiais ao lado"     //oleo | metal | roupa | papel | plastico | eletronico
}


//Coletor|Doador -> SERVIDOR === Sair do Sistema
{
    "action":"disconnect"
}

//Coletor|Doador -> Servidor === Enviar Mensagem Broadcast (chat geral)
{   
    "action":"chat_general_server",
    "mensagem":"a mensagem não pode ser String Vazia ou somente espaços em branco"
}

//Servidor -> Coletor|Doador === Replica Mensagem Broadcast (chat geral)
{   
    "action":"chat_general_client",
    "mensagem":"a mensagem não pode ser String Vazia ou somente espaços em branco"
}

//Coletor|Doador -> Servidor === Enviar Mensagem Multicast (Sala de Chat)
{   
    "action":"chat_room_server",
    "mensagem":"a mensagem não pode ser String Vazia ou somente espaços em branco"
}

//Servidor -> Coletor|Doador === Replica Mensagem Multicast (Sala de Chat)
{   
    "action":"chat_room_client",
    "mensagem":"a mensagem não pode ser String Vazia ou somente espaços em branco"
}

//Coletor|Doador -> Servidor === Solicitar Unicast (Chat com um usuário)
{   
    "action":"chat_request_server",
    "destinatario":"12345"//Porta efêmera 
}

//Servidor -> Coletor|Doador === Encaminha solicitação de Unicast
{
    "action":"chat_request_client",
    "remetente":"12345"//Porta efêmera
}

//Coletor|Doador -> Servidor === Encaminha resposta da solicitação Unicast para servidor
{
    "action":"chat_response_server",
    "destinatario":"12345",//Porta efêmera
    "resposta":"a resposta não pode ser String Vazia ou somente espaços em branco"// "true"|"false"
}

// Servidor -> Coletor|Doador === Encaminha resposta de Unicast para cliente
{
    "action":"chat_response_client",
    "remetente":"12345",//Porta efêmera
    "resposta":"a resposta não pode ser String Vazia ou somente espaços em branco"// "true"|"false"
}

// Coletor|Doador -> Servidor === Encaminha mensagem Unicast para o servidor 
{
    "action":"chat_unicast_message_server",
    "destinatario":"12345",//Porta efêmera
    "mensagem":"a mensagem não pode ser String Vazia ou somente espaços em branco"
}

// Servidor -> Coletor|Doador === Servidor encaminha mensagem para destinatário 
{
    "action":"chat_unicast_message_client",
    "remetente":"12345",//Porta efêmera
    "mensagem":"a mensagem não pode ser String Vazia ou somente espaços em branco"
}

\\ Coletor|Doador -> Servidor === Fechar chat Unicast
{
    "action":"chat_unicast_close_server",
    "destinatario":"12345"//Porta efêmera
}

// Servidor -> Coletor|Doador === Servidor envia mensagem de fechamento do chat para quem estava do outro lado
{
    "action":"chat_unicast_close_client",
    "remetente":"12345"//Porta efêmera
}

// Servidor -> Coletor|Doador === Servidor envia lista de pessoas conectadas para os clientes a cada entrada ou saída 
{
    "action":"client_list",
    "lista":[{"nome":"O nome nao pode ser String Vazia ou somente espacos em branco",
              "tipo":"C",   //deve ser preenchido com 'C' para coletor ou 'D' para doador
              "material":"O material nao pode ser String Vazia ou somente espacos em branco",
              "descricao":"A descricao nao pode ser String Vazia ou somente espacos em branco",
              "porta":"Preencher com o número da porta do cliente"}]
}

____________________________________________________________________________
Tratamento de erro

// Servidor -> Coletor|Doador === Servidor envia mensagem de desconexão do cliente
{
    "action":"client_disconnect"
}

//Servidor -> Coletor|Doador === Servidor informa que o cliente não esta mais conectado
{
    "action":"chat_request_error"
}

\\Servidor -> Coletor|Doador === Servidor informa que cliente ja está em negociação
{
   "action":"client_busy"
}

\\Servidor -> Coletor|Doador === Servidor informa que a solicitação do cliente não pode ser processada
{
   "action":"request_error"
}

====================================================================================================== 
 
 CONCLUÍDO

OK	=> Minicurso de JSON;
OK	=> Estabelecer um padrão de mensagens (JSON) entre os clientes e o servidor;

 PRÓXIMOS PASSOS
	
??	=> Definir datas das apresentações parciais;
           Login                 30/05/2019
           BroadCast             11/06/2019
           MultiCast             25/06/2019
           UniCast               09/07/2019
        => Encontrar dormas de validar Json, evitando assim que seja enviado um         
	=> Antes da entrega do projeto final deve-se: 
	   - Realizar um "ajuste fino", para econtrar possíveis falhas/erros; 
	   - Buscar formas de tratar estas falhas;
	   - Tratar as falhas e testa-las novamente;
    LEMBRANDO que a ideia principal do trabalho em conjunto,
	é compartilhar ideias e soluçoes para que o coletivo seja beneficiado.
