package trello;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import telegram.SendTask;

public class Tarefas {
    public List lista;
    public JSONObject my_obj = new JSONObject();
    public String status;
    public Integer numAtrasado = 0;
    public Integer numConcluido = 0;
    public Integer numAndamento = 0;
    
    //Criando objeto da classe Data para a data atual
    Data d = new Data();
    SendTask st = new SendTask();
    
    //Definindo ArrayList
    public Tarefas() {
        this.lista = new ArrayList();
    }
    
    //Método que pega informações do board do Trello e manipula as datas de entrega das tarefas
    public void getListaTarefas() throws ParseException{
        
        //Pegando resposta do Board do Trello via API
         HttpResponse<JsonNode> response = Unirest.get("https://api.trello.com/1/boards/5ed2b6286c51903adc716cf6/cards?key=19ef4d38ec2193e2ba75895b202a575b&token=29cb8472928cceb6def8ad9725058918be746fe6b08358d26195e5915b6e22de")
        .asJson();
        
        //Criando um Array com as linhas de JSON como resposta
        JSONArray j1 = response.getBody().getArray();
        
        //For que adiciona as linhas do JSON no objeto JSON
        for (int i = 0; i < j1.length(); i++) {
            my_obj = j1.getJSONObject(i);
            
            //Convertendo a data do JSON para formato Date
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
            String dateInString = my_obj.getString("due");
            Date date = sdf.parse(dateInString);
            
            //Convertendo formato Date para Calendar
            Calendar dataEntrega = d.dataParaCalendar(date);
            
            //Atribuindo dia/mes/ano para variáveis
            Integer diaEntrega = dataEntrega.get(Calendar.DAY_OF_YEAR);
            Integer mesEntrega = dataEntrega.get(Calendar.MONTH)+ 1;
            Integer anoEntrega = dataEntrega.get(Calendar.YEAR);
            
            //Criando variáveis de diferença de dia/mes
            Integer difDia = (diaEntrega - d.diaAtual);
            // 7 - 18
            
            //Fazendo if para saber se está ou não atrasada a tarefa
            if(difDia <= 0 && my_obj.getString("dueComplete").equals("false") && my_obj.getString("idMembers").equals("5cc88244e349ac140e28bcfc"))
            {
                status = "Atrasado";
                numAtrasado++;  
            }
            else if(difDia > 0 && my_obj.getString("dueComplete").equals("false")&& my_obj.getString("idMembers").equals("5cc88244e349ac140e28bcfc")){
                status = "Em andamento";
                numAndamento++;
            }
            
            else if(my_obj.getString("dueComplete").equals("true")&& my_obj.getString("idMembers").equals("5cc88244e349ac140e28bcfc")){
                
                if(difDia == 1){
                    st.send("Atencao, você tem uma tarefa que vence amanha, termine o quanto antes!");
                }
                else{
                    status = "Concluída";
                    numConcluido++;
                }
            }
            
            System.out.println("Dia hj: " + d.getDataAtual());
            System.out.println("Dia entrega: " + dataEntrega);
            System.out.println("Diferença de dias: " + difDia);
            
            
            //Captando apenas as tarefas referentes à um user e se estiver aberta
            //Capta o nome da tarefa, a data de entrega e se está ou não atrasada
            if (my_obj.getString("idMembers").equals("5cc88244e349ac140e28bcfc") && my_obj.getString("closed").equals("false")) {
                lista.add("Tarefa: " + (my_obj.getString("name")) + " -- Data de entrega: " + diaEntrega + "/" + mesEntrega + "/" + anoEntrega + " -- Atrasado: " + status);
            }    
        }
        System.out.println("Atrasadas: " + numAtrasado);
        System.out.println("Concluidas: " + numConcluido);
        System.out.println("Andamento: " + numAndamento);
     
    }
    
    //Método que exibe a mensagem ao user
    public String exibirMensagem(){
            String mensagem = "";
            for (Object object : lista) {
            mensagem += object.toString() + "\n";
        }
            return mensagem;
    }

    public void adiciona() throws ClassNotFoundException {
        
        Connection connection = new ConnectionFactory().getConnection();
        PreparedStatement stmt = null;
        ResultSet rs = null;
        String sql = "INSERT STATUS_ATIVIDADE values (?, ?, ?, ?, ?)";

        try {
            stmt = connection.prepareStatement(sql);
            stmt.setInt(1, 11);
            stmt.setInt(2, numAndamento);
            stmt.setInt(3, numConcluido);
            stmt.setInt(4, numAtrasado);
            stmt.setString(5, "12345446155");
            stmt.execute();
            stmt.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public String mandarAviso(){
        return  "Voce tem atividades " + numAtrasado + " atrasadas. Para visualizar suas tarefas digite o comando /tarefas";
    }
    
    @Override
    public String toString() {
        return "\nMENSAGEM: " + d.getDataAtual() + "\n"
                + "Atividades: " + lista.size() + "\n"
                +  exibirMensagem() + "\n"
                + "Numero de atividades atrasadas: " + (numAtrasado);
    }
}
