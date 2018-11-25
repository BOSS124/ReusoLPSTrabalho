package features;

public class Produtos extends Feature {
    public Produtos() {
        super("Produtos", "desc", Feature.TIPO_COMUM);
        this.subFeature = true;
    }
}