package features;

public class Produtos extends Feature {
    public Produtos() {
        super("Produtos", "SubFeature que representa os produtos\nem estoque", Feature.TIPO_COMUM);
        this.subFeature = true;
    }
}