package com.example;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("--- SISTEMA DE GERENCIAMENTO DE AGENTES ---");
    
    try (agenteDAO dao = new agenteDAO("agentes_db")) {

            System.out.println("\n[1] Inserindo novos agentes...");
            agente a1 = new agente("Ana Silva", 25, 1.68);
            agente a2 = new agente("Carlos Souza", 32, 1.75);
            
            dao.inserirAgente(a1);
            dao.inserirAgente(a2);
            System.out.println("Agentes cadastrados com sucesso!");

            System.out.println("\n[2] Listando todos os agentes do banco:");
            List<agente> lista = dao.listarTodas();
            for (agente agente : lista) {
                System.out.println("ID: " + agente.getId() + 
                                   " | Nome: " + agente.getNome() + 
                                   " | Idade: " + agente.getIdade() + 
                                   " | Altura: " + agente.getAltura());
            }

            System.out.println("\n[3] Buscando agente com ID 1:");
            agente encontrado = dao.buscarPorId(1);
            if (encontrado != null) {
                System.out.println("Encontrado: " + encontrado.getNome() + " (" + encontrado.getIdade() + " anos)");
            } else {
                System.out.println("Agente não encontrado.");
            }

          
            System.out.println("\n[4] Atualizando dados do agente ID 1...");
            dao.atualizarAgente(1, "Ana Silva Santos", 26, 1.70);
            
            agente atualizado = dao.buscarPorId(1);
            System.out.println("Dados atualizados - Nome: " + atualizado.getNome() + ", Idade: " + atualizado.getIdade());

            System.out.println("\n[5] Excluindo o agente ID 2...");
            dao.excluirAgente(2);
            
            System.out.println("\nListagem final após a exclusão:");
            dao.listarTodas().forEach(a -> System.out.println("- " + a.getNome()));

        } catch (Exception e) {
            System.out.println("Erro crítico na execução: " + e.getMessage());
        }

        System.out.println("\n--- FIM DA EXECUÇÃO ---");
    }
}
