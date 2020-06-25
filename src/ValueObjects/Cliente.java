/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ValueObjects;

import java.net.Socket;

/**
 *
 * @author a1589482
 */
public class Cliente {
    private String ip;
    private int porta;
    private String portaString;
    private String servidor;
    private Socket socketCliente;
    private String material;
    private String coletorOuDoador;
    private String nome;
    private String descricao;
    private String statusUnicast;

    public String isStatusUnicast() {
        return statusUnicast;
    }

    public void setStatusUnicast(String statusUnicast) {
        this.statusUnicast = statusUnicast;
    }
    
    
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPorta() {
        return porta;
    }

    public void setPorta(int porta) {
        this.porta = porta;
    }

    public String getServidor() {
        return servidor;
    }

    public void setServidor(String servidor) {
        this.servidor = servidor;
    }

    public Socket getSocketCliente() {
        return socketCliente;
    }

    public void setSocketCliente(Socket socketCliente) {
        this.socketCliente = socketCliente;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getColetorOuDoador() {
        return coletorOuDoador;
    }

    public void setColetorOuDoador(String coletorOuDoador) {
        this.coletorOuDoador = coletorOuDoador;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getPortaString() {
        return portaString;
    }

    public void setPortaString(String portaString) {
        this.portaString = portaString;
    }
    
}
