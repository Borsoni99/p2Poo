package cli;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import entidades.evento.Evento;
import entidades.evento.Exposicao;
import entidades.evento.Jogo;
import entidades.evento.Show;

import entidades.ingresso.Ingresso;
import entidades.ingresso.TipoIngresso;
import entidades.ingresso.IngExposicao;
import entidades.ingresso.IngJogo;
import entidades.ingresso.IngShow;

public class Cli {
    public static int executar() {
        List<Evento> eventos = new ArrayList<>();
        Scanner leitor = new Scanner(System.in);
        int opcao;
        Evento eventoBuscado;

        System.out.println("Seja bem-vindo ao programa de venda de ingressos de eventos!");

        while (true) {
            menu();
            opcao = leitor.nextInt();
            leitor.nextLine();
            switch (opcao) {
                case 1:
                    cadastrarEvento(leitor, eventos);
                    System.out.println("Eventos cadastrados com sucesso!");
                    break;
                case 2:
                    exibirEvento(eventos);
                    break;
                case 3:
                    exibirIngressosRestantes(eventos);
                    break;
                case 4:
                    eventoBuscado = buscarEventoPorNome(eventos, leitor);
                    System.out.println(eventoBuscado);
                    break;
                case 5:
                    excluirEvento(eventos, leitor);
                    break;
                case 6:
                    atualizaEvento(eventos, leitor);
                    break;
                case 11:
                    venderIngresso(eventos, leitor);
                    break;
                default:
                    persistirDados(eventos);
                    leitor.close();
                    System.out.println("Volte sempre!");
                    return 0;
            }
        }
    }

    private static void persistirDados(List<Evento> eventos) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("dados_eventos.txt"))) {
            for (Evento evento : eventos) {
                writer.write(evento.toString());
                writer.newLine();
            }
            System.out.println("Dados persistidos com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao persistir dados: " + e.getMessage());
        }
    }

    private static Evento buscarEventoPorNome(List<Evento> eventos, Scanner leitor) {
        System.out.print("Digite o nome do evento que você quer: ");
        String busca = leitor.nextLine();
        for (Evento evento : eventos) {
            if (evento.getNome().equalsIgnoreCase(busca)) {
                return evento;
            }
        }
        return null;
    }

    private static Evento atualizaEvento(List<Evento> eventos, Scanner leitor) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        System.out.print("Digite o nome do evento que você quer atualizar: ");
        String busca = leitor.nextLine();


        for (Evento evento : eventos) {
            if (evento.getNome().equalsIgnoreCase(busca)) {
                System.out.print("Informe a nova data do evento (formato dd/MM/yyyy) | Caso não queira mudar a data deixe em branco: ");
                String dataString = leitor.nextLine();
                System.out.print("Informe o local do evento | Caso não queira mudar o local deixe em branco: ");
                String local = leitor.nextLine();

                if (dataString != ""){
                    LocalDate data = LocalDate.parse(dataString, formatter);
                    evento.SetData(data);
                }

                if (local != ""){
                    evento.SetLocal(local);
                }
                return evento;
            }
        }
        return null;
    }


    private static void menu() {
        System.out.println("\nDigite a opção desejada ou qualquer outro valor para sair:");
        System.out.println("1 - Cadastrar um novo evento;");
        System.out.println("2 - Exibir evento cadastrado;");
        System.out.println("3 - Exibir ingressos restantes;");
        System.out.println("4 - Buscar evento por nome;");
        System.out.println("5 - Excluir evento;");
        System.out.println("6 - Atualizar evento;");
        System.out.println("11 - Vender um ingresso;");
    }

    private static Ingresso venderIngresso(List<Evento> eventos, Scanner leitor) {
        Evento evento = buscarEventoPorNome(eventos, leitor);
        if (evento == null) {
            return null;
        }

        String tipo;
        TipoIngresso tipoIngresso;
        int quantidade;
        Ingresso ingresso;

        System.out.print("Informe o tipo do ingresso (meia ou inteira): ");
        tipo = leitor.next();
        if (!(tipo.equals("meia") || tipo.equals("inteira"))) {
            System.out.println("Tipo selecionado inválido!");
            return null;
        }

        tipoIngresso = tipo.equals("meia") ? TipoIngresso.MEIA : TipoIngresso.INTEIRA;

        System.out.print("Informe quantos ingressos você deseja: ");
        quantidade = leitor.nextInt();

        if (!evento.isIngressoDisponivel(tipoIngresso, quantidade)) {
            System.out.println("Não há ingressos disponíveis desse tipo!");
            return null;
        }

        if (evento instanceof Jogo) {
            int percentual;

            System.out.print("Informe o percentual do desconto de sócio torcedor: ");
            percentual = leitor.nextInt();
            ingresso = new IngJogo(evento, tipoIngresso, percentual);
        } else if (evento instanceof Show) {
            String localizacao;

            System.out.print("Informe a localização do ingresso (pista ou camarote): ");
            localizacao = leitor.next();

            if (!(localizacao.equals("pista") || localizacao.equals("camarote"))) {
                System.out.println("Localização inválida!");
                return null;
            }
            ingresso = new IngShow(evento, tipoIngresso, localizacao);
        } else {
            String desconto;

            System.out.print("Informe se possui desconto social (s/n): ");
            desconto = leitor.next();

            ingresso = new IngExposicao(evento, tipoIngresso, desconto.equals("s"));
        }

        evento.venderIngresso(tipoIngresso, quantidade);
        System.out.println("Ingresso vendido com sucesso!");
        return ingresso;
    }

    private static void exibirIngressosRestantes(List<Evento> eventos) {
        for (Evento evento : eventos) {
            if (evento == null) {
                System.out.println("Evento ainda não foi cadastrado!");
            } else {
                System.out.println("evento: " +evento.getNome()+ ", Ingressos inteira restantes: " + evento.getIngressosInteira()+ ", Ingressos meia restantes: " + evento.getIngressosMeia());
            }
        }
    }

    private static void exibirEvento(List<Evento> eventos) {
         for (Evento evento : eventos) {
            if (evento == null) {
                System.out.println("Evento ainda não foi cadastrado!");
            } else {
                System.out.println(evento);
            }
        }

    }

    private static Evento excluirEvento(List<Evento> eventos, Scanner leitor) {
        System.out.print("Digite o nome do evento que você quer excluir: ");
        String busca = leitor.nextLine();

        for (int i = 0; i < eventos.size(); i++) {
            Evento evento = eventos.get(i);
            if (evento.getNome().equalsIgnoreCase(busca)) {
                eventos.remove(i);
                System.out.println("Evento removido com sucesso.");
                return evento;
            }
        }

        System.out.println("Este evento não existe na lista.");
        return null;
    }



private static void cadastrarEvento(Scanner leitor, List<Evento> eventos) {
    String nome, local, tipo;
    int ingMeia, ingInteira;
    double preco;
    LocalDate data;
    String continuar = "s";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    while (continuar.equalsIgnoreCase("s")) {
        System.out.print("Informe o nome do evento: ");
        nome = leitor.next();
        System.out.print("Informe a data do evento (formato dd/MM/yyyy): ");
        data = LocalDate.parse(leitor.next(), formatter);
        System.out.print("Informe o local do evento: ");
        local = leitor.next();
        System.out.print("Informe o número de entradas tipo meia: ");
        ingMeia = leitor.nextInt();
        System.out.print("Informe o número de entradas tipo inteira: ");
        ingInteira = leitor.nextInt();
        System.out.print("Informe o preço cheio do evento: ");
        preco = leitor.nextDouble();
        System.out.print("Informe o tipo do evento (show, jogo ou exposição): ");
        tipo = leitor.next();

        if (tipo.equals("show")) {
            String artista, genero;

            System.out.print("Informe o nome do artista: ");
            artista = leitor.next();
            System.out.print("Informe o gênero do show: ");
            genero = leitor.next();

            eventos.add(new Show(nome, data, local, ingMeia, ingInteira, preco, artista, genero));
        }

        if (tipo.equals("jogo")) {
            String esporte, casa, adversario;

            System.out.print("Informe o esporte: ");
            esporte = leitor.next();
            System.out.print("Informe a equipe da casa: ");
            casa = leitor.next();
            System.out.print("Informe a equipe adversária: ");
            adversario = leitor.next();

            eventos.add(new Jogo(nome, data, local, ingMeia, ingInteira, preco, esporte, casa, adversario));
        }

        if (tipo.equals("exposição")) {
            int idadeMin, duracao;

            System.out.print("Informe a idade mínima para entrar na exposição: ");
            idadeMin = leitor.nextInt();
            System.out.print("Informe a duração em dias da exposição: ");
            duracao = leitor.nextInt();

            eventos.add(new Exposicao(nome, data, local, ingMeia, ingInteira, preco, idadeMin, duracao));
        }
        System.out.print("Deseja cadastrar outro evento? (s/n): ");
        continuar = leitor.next();
    }
}

}
