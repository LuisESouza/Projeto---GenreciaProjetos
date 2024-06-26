/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Controle;

import Modelo.EquipeProjetoM;
import controle.BancoDados;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
     * A classe EquipeProjetoC gerencia operações específicas relacionadas à equipe de projetos de um projeto. Esta classe é uma extensão da classe EquipeC.
     * Ela inclui métodos para cadastrar a equipe de projetos, adicionar integrantes, cadastrar responsáveis por etapas e 
     * recuperar informações da equipe do banco de dados.
     * 
    */
public class EquipeProjetoC extends EquipeC{
    EquipeProjetoM equipeProjGeral = new EquipeProjetoM("","", "", "", null, null, 0);
    private BancoDados bd = new BancoDados();
    private ResultSet rSet;
    
    /**
     * Cadastra uma nova equipe de projetos, solicitando informações como o ID da equipe, o responsável geral e o nome do projeto. As informações são armazenadas no banco de dados.
     */
    @Override
    public void cadastrarEquipe(){
        Scanner leitor = new Scanner(System.in);
       try{ 
        System.out.println("Informe o id da equipe a ser cadastrada: ");
        int idEquipe = leitor.nextInt();
        
        //limpando buffer do telcado
        leitor.nextLine();
        
        System.out.println("Informe o responsável geral da equipe: ");
        equipeProjGeral.setResponsavelGeral(leitor.nextLine()); 
        
        System.out.println("Digite o nome do projeto: ");
        String nomeProjeto = leitor.nextLine().toUpperCase();
        
        //mandar para o BD
        salvarEquipe(idEquipe, equipeProjGeral.getResponsavelGeral());
        salvarEquipeProjeto(idEquipe, nomeProjeto);
        
        System.out.println("Equipe salva com sucesso");
       }catch(Exception e){
           System.out.println("ERRO:" + e.getMessage());
       }
    }
    
    /**
     * Salva o ID e o responsável geral da equipe no banco de dados.
     * 
     * @param id ID da equipe.
     * @param respGeral Responsável geral da equipe.
    */
    @Override
    public void salvarEquipe(int id, String respGeral){
        try{
           bd.conexao();
           String sql = "insert into equipes values(" + id + ", '" + respGeral + "');";
                   
           bd.getStatement().execute(sql);
           
           bd.desconecta();
           
       }catch(Exception e){
           System.out.println("ERRO AO SALVAR DADOS: " + e.getMessage());
       }
    }
    
     /**
     * Salva o ID da equipe e o nome do projeto em que ela está atuando.
     * 
     * @param id ID da equipe.
     * @param nomeProjeto Nome do projeto.
     */
    @Override
    public void salvarEquipeProjeto(int id, String nomeProjeto){
        try{
           bd.conexao();
           String sql = "insert into equipe_projeto values(" + id + ", '" + nomeProjeto + "');";                   
           
           bd.getStatement().execute(sql);
           
           bd.desconecta();
           
       }catch(Exception e){
           System.out.println("ERRO AO SALVAR DADOS: " + e.getMessage());
       }
    
    }
    
    
    
     /**
     * Solicita o ID e a quantidade de integrantes da equipe de projetos.
     * 
     * @param idEquipe ID da equipe.
     */
    @Override
    public void cadastrarIntegrante(int idEquipe){
        Scanner leitor = new Scanner(System.in);
        try{ 
        System.out.println("Digite quantos integrantes a equipe de projetos têm: ");
        int qtde = leitor.nextInt();
        
        adicionarIntegrantes(qtde, idEquipe);
        }catch(Exception e){
            System.out.println("ERRO AO SALVAR DADOS: " + e.getMessage());
        }
    }
    
    /**
     * Adiciona informações básicas sobre os integrantes da equipe de projetos.
     * 
     * @param qtde Quantidade de integrantes.
     * @param id ID da equipe.
     */
    @Override
    public void adicionarIntegrantes(int qtde, int id){ 
        Scanner leitor = new Scanner(System.in);
        
        try{
        for(int i = 0; i < qtde; i++){
            System.out.println("Informe o nome do "+ (i+1) + "° integrante: ");
            equipeProjGeral.setIntegrantes(leitor.nextLine().toLowerCase());
            
            System.out.println("Informe o cargo na administração do projeto para o "+ (i+1) + "° integrante: ");
            equipeProjGeral.setCargoProjeto(leitor.nextLine());
            
            System.out.println("O "+ (i+1) + "° integrante é responsável por alguma etapa?: ");
            char eResponsavelEtapa = Character.toUpperCase(leitor.nextLine().charAt(0) );
            
            cadastrarResponsaveisEtapa(eResponsavelEtapa);
            
            //mandar para o banco de dados
            if(eResponsavelEtapa == 'S')
                salvarIntegrantes(eResponsavelEtapa, equipeProjGeral);
            else
                salvarIntegrantes(eResponsavelEtapa, equipeProjGeral, "");
                
            salvarEquipeIntegrante(id, equipeProjGeral.getIntegrantes());
            
            System.out.println("Integrante salvo com sucesso!!!");

            }
        }catch(Exception e){
            System.out.println("ERRO AO SALVAR DADOS: " + e.getMessage());
        }
    }
    
   /**
      * Cadastra os responsáveis por etapas, coletando informações e salvando-as.
      * 
      * @param eResponsavelEtapa Indica se o integrante é responsável por uma etapa ('S' ou 'N').
     */
    @Override
    public void cadastrarResponsaveisEtapa(char eResponsavelEtapa){
        Scanner leitor = new Scanner(System.in);
        
        
        switch(eResponsavelEtapa){
            case 'S' -> {
                System.out.println("Digite a etapa: ");
                equipeProjGeral.setEtapa(leitor.nextInt());
                
                //limpando buffer do teclado
                leitor.nextLine();
            
                System.out.println("Digite a data de inicio da etapa: ");
                equipeProjGeral.setInicioEtapa(converteStringParaData(leitor.nextLine()));
        
                System.out.println("Digite a data final da etapa: ");
                equipeProjGeral.setFimEtapa(converteStringParaData(leitor.nextLine()));
                
                while( !verificaDatasEtapa(equipeProjGeral.getInicioEtapa(), equipeProjGeral.getFimEtapa()) ){
                    System.out.println("Digite a data de inicio da etapa: ");
                    equipeProjGeral.setInicioEtapa( converteStringParaData( leitor.nextLine() ) );

                    System.out.println("Digite a data final da etapa: ");
                    equipeProjGeral.setFimEtapa(converteStringParaData(leitor.nextLine() ) );
                }
            }
            case 'N' -> {
                // -1 significa que ele não é responsável por uma etapa
                equipeProjGeral.setEtapa(-1);
                equipeProjGeral.setInicioEtapa(null);
                equipeProjGeral.setFimEtapa(null);
            }
            default ->{
                System.out.println("Valor incorreto, integrante não receberá responsabiliade de etapas");
                equipeProjGeral.setEtapa(-1);
                equipeProjGeral.setInicioEtapa(null);
                equipeProjGeral.setFimEtapa(null);
            }
            
        }
            
    }
    
    /**
     * Verifica se as datas de início e fim da etapa são válidas.
     * 
     * @param dateInicio Data de início da etapa.
     * @param dateFinal Data de fim da etapa.
     * @return true se as datas são válidas, false caso contrário.
     */
    public boolean verificaDatasEtapa(LocalDate dateInicio, LocalDate dateFinal){
        if( dateFinal.getYear() < dateInicio.getYear() ||  ( dateFinal.getYear() == dateInicio.getYear() && dateFinal.getMonthValue() < dateInicio.getMonthValue() ) ){
            System.out.println("Datas da etapa Inválidas");
            return false;
        }
        
        return true;
    }
    
    /**
     * Converte uma String que representa uma data para um objeto LocalDate.
     * 
     * @param dataStr String representando a data.
     * @return Objeto LocalDate.
    */
    public LocalDate converteStringParaData(String dataStr){
        LocalDate data = null;
        
        try{
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        data = LocalDate.parse(dataStr, formato);
                
        }catch(Exception e){ 
            System.out.println("Erro: " + e.getMessage());
        }
        
        return data;
    }
    
     /**
     * Salva os integrantes responsáveis por etapas no banco de dados.
     * 
     * @param eResponsavelEtapa Indica se o integrante é responsável por uma etapa ('S').
     * @param equipe Instância da classe EquipeProjetoM contendo informações do integrante.
    */
    @Override
    public void salvarIntegrantes(char eResponsavelEtapa, EquipeProjetoM equipe){
        try{
           bd.conexao();
           String sql = "insert into integrantes values('" + equipe.getIntegrantes() +"', '"+ equipe.getCargoProjeto() + "', NULL, 'P' , '" + eResponsavelEtapa +"', "+ equipe.getEtapa() +", '" + equipe.getInicioEtapa() +"', '" + equipe.getFimEtapa() + "')" ;
                   
           bd.getStatement().execute(sql);
           
           bd.desconecta();
           
       }catch(Exception e){
           System.out.println("ERRO AO SALVAR DADOS: " + e.getMessage());
       }
    
    }
    
    /**
     * Salva os integrantes não responsáveis por etapa no banco de dados.
     * 
     * @param eResponsavelEtapa Indica se o integrante é responsável por uma etapa ('N').
     * @param equipe Instância da classe EquipeProjetoM contendo informações do integrante.
     * @param dateNull String representando uma data nula.
     */
    public void salvarIntegrantes(char eResponsavelEtapa, EquipeProjetoM equipe, String dateNull){
        try{
           bd.conexao();
           String sql = "insert into integrantes values('" + equipe.getIntegrantes() +"', '"+ equipe.getCargoProjeto() + "', NULL, 'P' , '" + eResponsavelEtapa +"', NULL, NULL, NULL)" ;
                   
           bd.getStatement().execute(sql);
           
           bd.desconecta();
           
       }catch(Exception e){
           System.out.println("ERRO AO SALVAR DADOS: " + e.getMessage());
       }
    
    }
    
    /**
     * Salva o nome do integrante e o ID da equipe à qual ele pertence no banco de dados.
     * 
     * @param id ID da equipe.
     * @param nomeIntegrante Nome do integrante.
    */
    @Override
    public void salvarEquipeIntegrante(int id, String nomeIntegrante){
        try{
           bd.conexao();
           String sql = "insert into equipe_integrante values(" + id + ", '" + nomeIntegrante + "');";
                   
           bd.getStatement().execute(sql);
           
           bd.desconecta();
           
       }catch(Exception e){
           System.out.println("ERRO AO SALVAR DADOS: " + e.getMessage());
       }
    
    }

    
         
    /**
     * Resgata informações da equipe a partir do banco de dados, relacionadas a um determinado projeto.
     * 
     * @param nomeProjeto Nome do projeto.
    */
    @Override
    public void resgatarEquipe(String nomeProjeto){
         try{
           bd.conexao();
           String sql = "select equipes.responsavelGeral, equipe_projeto.codEquipe, equipe_integrante.nomeIntegrante, integrantes.* from equipe_projeto, equipe_integrante, integrantes, equipes where nomeProjeto = '" + nomeProjeto + "' and equipe_projeto.codEquipe = equipe_integrante.codEquipe and equipe_integrante.nomeIntegrante = integrantes.nomeIntegrante and equipes.codEquipe = equipe_integrante.codEquipe ;";

           rSet = bd.getStatement().executeQuery(sql);

             
            System.out.println("--------------------------EQUIPE--------------------------");  
            while(rSet.next()){
               System.out.println("Id equipe...................: " + rSet.getInt("codEquipe") + "\n"
                                +"Responsável geral............: " + rSet.getString("responsavelGeral") + "\n"
                                + "Nome........................: " + rSet.getString("nomeIntegrante") + "\n"
                                + "Cargo.......................: " + rSet.getString("cargo") + "\n"
                                + "Empresa terceirizada........: " + rSet.getString("empresaTerceira") + "\n"
                                + "Tipo da equipe..............: " + rSet.getString("tipoEquipe") + "\n"
                                + "Responsável por etapa.......: " + rSet.getString("reponsavelPorEtapa") + "\n"
                                + "Número etapa................: " + rSet.getInt("etapa") + "\n"
                                + "Data de início da etapa.....: " + rSet.getDate("dataInicio") + "\n"
                                + "Data de finalização da etapa: " + rSet.getDate("dataFim") + "\n");                           
           }
           
           bd.desconecta();
        }catch(Exception e){
           System.out.println("ERRO AO RESGATAR PROJETO: " + e.getMessage());
        }
    }

}
