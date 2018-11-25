package features;

public class Feature {
    private String nome;
    private String descricao;
    private int tipo;
    protected boolean subFeature;

    public static final int TIPO_COMUM = 0;
    public static final int TIPO_VARIAVEL = 1;

    public Feature(String nome, String descricao, int tipo) {
        this.nome = nome;
        this.descricao = descricao;
        this.tipo = tipo;
        this.subFeature = false;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public String toString() {
        return this.nome;
    }

    public int getTipo() {
        return this.tipo;
    }

    public boolean isSubFeature() {
        return this.subFeature;
    }
}