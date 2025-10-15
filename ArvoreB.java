import java.util.ArrayList;
import java.util.List;

/**
 * Classe que representa um Nó (Página) da Árvore B.
 * O grau mínimo 't' é fixado em 2, resultando em:
 * MAX_CHAVES = 2*t - 1 = 3
 * MIN_CHAVES = t - 1 = 1
 */
class NoArvoreB {
    // Constantes definidas para t=2 (Ordem 4)
    final int MAX_CHAVES = 3; 
    final int MIN_CHAVES = 1;
    
    // Lista de chaves ordenadas dentro do nó
    List<Integer> chaves; 
    // Lista de ponteiros para os filhos. O número de filhos é sempre 'chaves.size() + 1'
    List<NoArvoreB> filhos;
    
    boolean ehFolha;

    public NoArvoreB(boolean ehFolha) {
        this.ehFolha = ehFolha;
        this.chaves = new ArrayList<>();
        this.filhos = new ArrayList<>();
    }

    // Método auxiliar para exibir o nó
    @Override
    public String toString() {
        return chaves.toString();
    }
}

/**
 * Classe principal da Árvore B.
 */
public class ArvoreB {

    private NoArvoreB raiz;

    public ArvoreB() {
        // Inicialmente, a árvore está vazia.
    }

    /**
     * Insere uma chave na Árvore B.
     * @param chave O valor inteiro a ser inserido.
     */
    public void inserir(int chave) {
        if (raiz == null) {
            // Caso 1: Árvore vazia. Cria uma nova raiz (que é folha).
            raiz = new NoArvoreB(true);
            raiz.chaves.add(chave);
            return;
        }

        // Caso 2: A raiz está cheia (overflow na raiz).
        if (raiz.chaves.size() == raiz.MAX_CHAVES) {
            NoArvoreB novaRaiz = new NoArvoreB(false);
            novaRaiz.filhos.add(raiz);
            
            // Faz a cisão (split) do nó original (antiga raiz)
            cisaoFilho(novaRaiz, 0, raiz);
            
            // Atualiza a nova raiz e insere recursivamente
            raiz = novaRaiz;
            inserirNaoCheio(raiz, chave);
        } else {
            // Caso 3: Raiz não cheia. Insere recursivamente.
            inserirNaoCheio(raiz, chave);
        }
    }
    
    /**
     * Insere uma chave 'chave' no nó 'x', garantindo que 'x' não está cheio.
     */
    private void inserirNaoCheio(NoArvoreB x, int chave) {
        int i = x.chaves.size() - 1;

        if (x.ehFolha) {
            // O nó é uma folha, insere a chave e mantém a ordem.
            x.chaves.add(0); // Adiciona um placeholder (ajusta o tamanho)
            while (i >= 0 && chave < x.chaves.get(i)) {
                x.chaves.set(i + 1, x.chaves.get(i));
                i--;
            }
            x.chaves.set(i + 1, chave);
        } else {
            // O nó é interno. Encontra o filho correto (busca binária).
            while (i >= 0 && chave < x.chaves.get(i)) {
                i--;
            }
            i++; // 'i' é o índice do filho onde a chave deve descer

            // Verifica se o filho está cheio (se houver overflow, faz a cisão)
            if (x.filhos.get(i).chaves.size() == x.MAX_CHAVES) {
                cisaoFilho(x, i, x.filhos.get(i));
                
                // Após a cisão, determina em qual dos novos filhos a chave deve continuar.
                if (chave > x.chaves.get(i)) {
                    i++;
                }
            }
            inserirNaoCheio(x.filhos.get(i), chave);
        }
    }
    
    /**
     * Realiza a cisão (split) de um filho cheio 'y' do nó pai 'x'.
     * O filho 'y' está no índice 'i' de 'x.filhos'.
     */
    private void cisaoFilho(NoArvoreB x, int i, NoArvoreB y) {
        // y (o nó cheio) será dividido em dois: y (o original) e z (o novo nó)
        NoArvoreB z = new NoArvoreB(y.ehFolha);

        // 1. Chave Promovida (Mediana):
        // A chave do meio (índice MIN_CHAVES = 1, para t=2) de y é promovida.
        int chavePromovida = y.chaves.get(y.MIN_CHAVES); 
        
        // 2. Cisão: Copia as chaves maiores para o novo nó 'z'.
        // Z recebe as chaves na posição 2 (d+1) e 3 (2d).
        for (int j = 0; j < y.MIN_CHAVES; j++) {
            z.chaves.add(y.chaves.get(y.MIN_CHAVES + j + 1));
        }
        
        // 3. Se 'y' não for folha, 'z' também herda os filhos.
        if (!y.ehFolha) {
            // 'z' recebe os últimos 't' (ou MIN_CHAVES + 1) filhos de 'y'.
            for (int j = 0; j <= y.MIN_CHAVES+1; j++) {
                z.filhos.add(y.filhos.get(y.MIN_CHAVES + j + 1));
            }
            // Remove os ponteiros de 'y' que foram para 'z'
            y.filhos.subList(y.MIN_CHAVES + 1, y.filhos.size()).clear();
        }

        // 4. Finaliza a partição em 'y'.
        // Remove a chave promovida (índice 1) e as chaves que foram para 'z'.
        y.chaves.subList(y.MIN_CHAVES, y.chaves.size()).clear();

        // 5. Propagação: Insere a Chave Promovida no Pai 'x'.
        x.filhos.add(i + 1, z); // O novo nó 'z' é adicionado como novo filho à direita de 'y'.
        x.chaves.add(i, chavePromovida); // A chave promovida é inserida no pai 'x'.
    }

    // --- Métodos de Visualização ---
    
    public void imprimir() {
        System.out.println("Estrutura da Árvore:");
        if (raiz != null) {
            imprimirNo(raiz, 0);
        } else {
            System.out.println("Árvore vazia.");
        }
    }

    private void imprimirNo(NoArvoreB no, int nivel) {
        // Usa espaços para recuo
        String recuo = "  ".repeat(nivel); 
        // Imprime o nó atual (lista de chaves)
        System.out.println(recuo + no.chaves.toString());

        if (!no.ehFolha) {
            // Imprime os filhos recursivamente
            for (NoArvoreB filho : no.filhos) {
                imprimirNo(filho, nivel + 1);
            }
        }
    }

    // --- Execução do Exemplo ---

    public static void main(String[] args) {
        ArvoreB arvore = new ArvoreB();
        int[] insercoes = {10, 20, 5, 6, 12, 30, 7, 17};

        for (int chave : insercoes) {
            System.out.println("\n--- Inserindo: " + chave + " ---");
            arvore.inserir(chave);
            arvore.imprimir();
        }
        
        System.out.println("\n*** ÁRVORE B FINAL (Ordem 4) ***");
        arvore.imprimir();
    }
}