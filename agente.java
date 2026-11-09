package com.example;

public class agente{

    private int id;
    private String nome;
    private int idade;
    private double altura;

   
    public agente(String nome, int idade, double altura){
        this(0, nome, idade, altura); 
    }

    public agente(int id, String nome, int idade, double altura){
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.altura = altura;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    
    public int getIdade() { return idade; }
    public void setIdade(int idade) { this.idade = idade; }

    public double getAltura() { return altura; }
    public void setAltura(double altura) { this.altura = altura; }
}
