import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

enum EventoCategoria {
    FESTA,
    ESPORTE,
    SHOW,
    OUTRO
}

class Usuario {
    private String nome;
    private String cidade;
    private List<Evento> eventosConfirmados;

    public Usuario(String nome, String cidade) {
        this.nome = nome;
        this.cidade = cidade;
        this.eventosConfirmados = new ArrayList<>();
    }

    public void adicionarEventoConfirmado(Evento evento) {
        eventosConfirmados.add(evento);
    }

    public void removerEventoConfirmado(Evento evento) {
        eventosConfirmados.remove(evento);
    }

    public List<Evento> getEventosConfirmados() {
        return eventosConfirmados;
    }
}

class Evento {
    private String nome;
    private String endereco;
    private EventoCategoria categoria;
    private LocalDateTime horario;
    private String descricao;

    public Evento(String nome, String endereco, EventoCategoria categoria, LocalDateTime horario, String descricao) {
        this.nome = nome;
        this.endereco = endereco;
        this.categoria = categoria;
        this.horario = horario;
        this.descricao = descricao;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public EventoCategoria getCategoria() {
        return categoria;
    }

    public LocalDateTime getHorario() {
        return horario;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return "Nome: " + nome + ", Endereço: " + endereco + ", Categoria: " + categoria +
                ", Horário: " + horario.format(formatter) + ", Descrição: " + descricao;
    }
}

class EventoManager {
    private List<Evento> eventos;

    public EventoManager() {
        this.eventos = new ArrayList<>();
        carregarEventos();
    }

    public void cadastrarEvento(Evento evento) {
        eventos.add(evento);
        salvarEventos();
    }

    public void listarEventos() {
        for (int i = 0; i < eventos.size(); i++) {
            System.out.println(i + ": " + eventos.get(i));
        }
    }

    public void participarEvento(Evento evento, Usuario usuario) {
        usuario.adicionarEventoConfirmado(evento);
        salvarEventos();
    }

    public List<Evento> getEventos() {
        return eventos;
    }

    private void carregarEventos() {
        try (BufferedReader br = new BufferedReader(new FileReader("events.data"))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                eventos.add(stringParaEvento(linha));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void salvarEventos() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter("events.data"))) {
            for (Evento evento : eventos) {
                bw.write(eventoParaString(evento));
                bw.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String eventoParaString(Evento evento) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return evento.getNome() + "," + evento.getEndereco() + "," +
                evento.getCategoria() + "," + evento.getHorario().format(formatter) + "," +
                evento.getDescricao();
    }

    private Evento stringParaEvento(String linha) {
        String[] dados = linha.split(",");
        String nome = dados[0];
        String endereco = dados[1];
        EventoCategoria categoria = EventoCategoria.valueOf(dados[2]);
        LocalDateTime horario = LocalDateTime.parse(dados[3], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String descricao = dados[4];
        return new Evento(nome, endereco, categoria, horario, descricao);
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        EventoManager eventoManager = new EventoManager();
        Usuario usuario = new Usuario("João", "São Paulo");

        boolean sair = false;
        while (!sair) {
            System.out.println("Bem-vindo ao Sistema de Cadastro e Notificação de Eventos!");
            System.out.println("1. Cadastrar evento");
            System.out.println("2. Listar eventos");
            System.out.println("3. Participar de evento");
            System.out.println("4. Visualizar eventos confirmados");
            System.out.println("5. Sair");

            System.out.print("Escolha uma opção: ");
            int opcao = scanner.nextInt();
            scanner.nextLine();  // Consumir quebra de linha

            switch (opcao) {
                case 1:
                    System.out.println("Cadastro de Evento:");
                    System.out.print("Nome do evento: ");
                    String nomeEvento = scanner.nextLine();
                    System.out.print("Endereço: ");
                    String endereco = scanner.nextLine();
                    System.out.print("Categoria (FESTA, ESPORTE, SHOW, OUTRO): ");
                    String categoriaStr = scanner.nextLine();
                    EventoCategoria categoria = EventoCategoria.valueOf(categoriaStr.toUpperCase());
                    System.out.print("Data e hora (yyyy-MM-dd HH:mm): ");
                    String dataHoraStr = scanner.nextLine();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime dataHora = LocalDateTime.parse(dataHoraStr, formatter);
                    System.out.print("Descrição: ");
                    String descricao = scanner.nextLine();

                    Evento evento = new Evento(nomeEvento, endereco, categoria, dataHora, descricao);
                    eventoManager.cadastrarEvento(evento);
                    System.out.println("Evento cadastrado com sucesso!");
                    break;
                case 2:
                    System.out.println("Lista de Eventos:");
                    eventoManager.listarEventos();
                    break;
                case 3:
                    System.out.println("Digite o índice do evento que deseja participar: ");
                    int indiceEvento = scanner.nextInt();
                    scanner.nextLine();  // Consumir quebra de linha

                    if (indiceEvento >= 0 && indiceEvento < eventoManager.getEventos().size()) {
                        Evento eventoSelecionado = eventoManager.getEventos().get(indiceEvento);
                        eventoManager.participarEvento(eventoSelecionado, usuario);
                        System.out.println("Você participou do evento: " + eventoSelecionado.getNome());
                    } else {
                        System.out.println("Índice do evento inválido!");
                    }
                    break;
                case 4:
                    List<Evento> eventosConfirmados = usuario.getEventosConfirmados();
                    if (eventosConfirmados.isEmpty()) {
                        System.out.println("Nenhum evento confirmado.");
                    } else {
                        System.out.println("Eventos Confirmados:");
                        for (Evento eventoConfirmado : eventosConfirmados) {
                            System.out.println(eventoConfirmado);
                        }
                    }
                    break;
                case 5:
                    System.out.println("Saindo do programa...");
                    sair = true;
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
            }
        }
        scanner.close();
    }
}
