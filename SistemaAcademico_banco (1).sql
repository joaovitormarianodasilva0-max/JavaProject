CREATE DATABASE IF NOT EXISTS SistemaAcademico;
USE SistemaAcademico;


CREATE TABLE IF NOT EXISTS aluno (
    rgm VARCHAR(20) PRIMARY KEY, 
    nome VARCHAR(100) NOT NULL,
    data_nascimento VARCHAR(10), 
    cpf VARCHAR(14) NOT NULL,
    email VARCHAR(100),
    endereco VARCHAR(150),
    municipio VARCHAR(50),
    uf VARCHAR(2),
    celular VARCHAR(15),
    curso VARCHAR(100),
    campus VARCHAR(50),
    periodo VARCHAR(20)
);


CREATE TABLE IF NOT EXISTS notas_faltas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    aluno_rgm VARCHAR(20),
    disciplina VARCHAR(100),
    semestre VARCHAR(10),
    nota DOUBLE,
    faltas INT,
    FOREIGN KEY (aluno_rgm) REFERENCES aluno(rgm) ON DELETE CASCADE
);

select * from aluno;
