/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package log;

import easy.office.InfoCPU;
import easy.office.InfoSO;
import easy.office.QtdHD;
import easy.office.QtdRAM;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.OperatingSystem;

/**
 *
 * @author INGRIDNAOMIIYAMA
 */
public class GerarLog {

    private String pasta = "D:\\LogEO";
    private String nomeLog;
    private String versao = "1.0";
    private int dia, mes, ano;

    private Integer hora;
    private Integer minutos;
    private Integer segundos;

    public void criarArquivo() throws IOException {

        File diretorio = new File(pasta);
        diretorio.mkdir();

        Calendar cal = Calendar.getInstance();

        dia = cal.get(Calendar.DATE);
        mes = cal.get(Calendar.MONTH) + 1;
        ano = cal.get(Calendar.YEAR);

        nomeLog = String.format("EasyOffice_%d%d%d.txt", ano, mes, dia);

        File arquivo = new File(pasta + File.separator + nomeLog);

        if (arquivo.exists()) {
            System.out.println(String.format("O arquivo %s existe", nomeLog));

        } else {
            System.out.println(String.format("O arquivo %s não existe", nomeLog));
            System.out.println(String.format("Criando arquivo %s em %s", nomeLog, pasta));
            arquivo.createNewFile();
            System.out.println(String.format("Arquivo %s criado em %s", nomeLog, pasta));
            inicioLog();

        }

    }

    public void inicioLog() throws IOException {
        System.out.println(pasta + File.separator + nomeLog);

        FileWriter fw = new FileWriter((pasta + File.separator + nomeLog), true);
        BufferedWriter bw = new BufferedWriter(fw);

        InfoSO infoSO1 = new InfoSO();
        InfoCPU infoCpu1 = new InfoCPU();
        QtdHD qtdHD1 = new QtdHD();
        QtdRAM qtdRam1 = new QtdRAM();

        bw.write(String.format("EasyOffice 2020 - Versão %s", versao));
        bw.newLine();
        bw.newLine();
        bw.write(String.format("Criação do log em: %d/%d/%d", dia, mes, ano, versao));
        bw.newLine();
        bw.newLine();
        bw.write("--------------------Especificações do computador--------------------");
        bw.newLine();
        bw.write("Sistema Operacional: " + infoSO1.nome);
        bw.newLine();
        bw.write("Processador: " + infoCpu1.nome);
        bw.newLine();
        bw.write("Memoria RAM: " + String.valueOf(qtdRam1.memoriaTotal) + "GB");
        bw.newLine();
        bw.write("Disco: " + String.valueOf(qtdHD1.hdTotal) + "GB");
        bw.newLine();
        bw.write("--------------------------------------------------------------------");
        bw.newLine();
        bw.newLine();

        bw.close();

    }

    public void registrarLog(String msgLog) {

        try {
            FileWriter fw1 = new FileWriter((pasta + File.separator + nomeLog), true);
            BufferedWriter bw1 = new BufferedWriter(fw1);

            hora = LocalDateTime.now().getHour();
            minutos = LocalDateTime.now().getMinute();
            segundos = LocalDateTime.now().getSecond();

            System.out.print(String.format("%d/%d/%d %d:%d:%d -", dia, mes, ano, hora, minutos, segundos));
            bw1.write(String.format("%d/%d/%d %d:%d:%d - %s", dia, mes, ano, hora, minutos, segundos, msgLog));
            bw1.newLine();

            bw1.close();
        } catch (IOException ex) {
            System.out.println("Erro ao abrir o arquivo");
            Logger.getLogger(GerarLog.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

//    public static void main(String[] args) throws IOException {
//        GerarLog gerarLog1 = new GerarLog();
//
//        gerarLog1.criarArquivo();
//    }
}
