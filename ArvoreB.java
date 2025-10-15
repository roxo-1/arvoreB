import java.util.ArrayList;
import java.util.List;

/**
 * Classe que representa um Nó (Página) da Árvore B.
 * O grau mínimo 't' é fixado em 2, resultando em:
 * MAX_CHAVES = 2*t - 1 = 3
 * MIN_CHAVES = t - 1 = 1
 */
class NoArvoreB {
    int t;//grau minímo
    boolean ehFolha;
    List<Integer> chaves;
    List<NoArvoreB> filhos;
    
    public NoArvoreB(boolean ehFolha, int t) {
        this.ehFolha = ehFolha;
        this.t = t;
        this.chaves = new ArrayList<>();
        this.filhos = new ArrayList<>();
    }

    public int getMinChaves(){
        return t-1;
    }

    public int getMaxChaves(){
        return 2*t -1;
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
    int t = 2;

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
            raiz = new NoArvoreB(true, t);
            raiz.chaves.add(chave);
            return;
        }

        // Caso 2: A raiz está cheia (overflow na raiz).
        if (raiz.chaves.size() == raiz.getMaxChaves()) {
            NoArvoreB novaRaiz = new NoArvoreB(false, t);
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
            if (x.filhos.get(i).chaves.size() == x.getMaxChaves()) {
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
    private void cisaoFilho(NoArvoreB pai, int indice, NoArvoreB cheio) {
        int tLocal = cheio.t; // grau mínimo
        NoArvoreB novo = new NoArvoreB(cheio.ehFolha, tLocal);

        // guarda a chave mediana antes de alterar as listas
        int mediana = cheio.chaves.get(tLocal - 1);

        // 1) copia as últimas (t-1) chaves de 'cheio' para 'novo'
        for (int j = 0; j < tLocal - 1; j++) {
            novo.chaves.add(cheio.chaves.get(tLocal + j));
        }

        // 2) se não for folha, mover os últimos t filhos
        if (!cheio.ehFolha) {
            for (int j = 0; j < tLocal; j++) {
                novo.filhos.add(cheio.filhos.get(tLocal + j));
            }
        }

        // 3) reduzir 'cheio' removendo chaves e filhos movidos
        // 'cheio' deve ficar com as primeiras (t-1) chaves (índices 0..t-2)
        cheio.chaves.subList(tLocal - 1, cheio.chaves.size()).clear(); // remove mediana e as da direita
        if (!cheio.ehFolha) {
            cheio.filhos.subList(tLocal, cheio.filhos.size()).clear();
        }

        // 4) inserir 'novo' como filho do pai à direita de 'cheio'
        pai.filhos.add(indice + 1, novo);

        // 5) inserir a mediana no pai
        pai.chaves.add(indice, mediana);
    }

    // --- Métodos de Visualização ---
    
   public void imprimir() {
        imprimirNo(raiz, 0);
    }

    private void imprimirNo(NoArvoreB no, int nivel) {
        if (no == null) return;
        String indent = " ".repeat(nivel * 4);
        System.out.println(indent + "Nivel " + nivel + " chaves: " + no.chaves);
        for (NoArvoreB f : no.filhos) {
            imprimirNo(f, nivel + 1);
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