import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;

import features.*;

public class Configurador extends JFrame {
    JList naoInstanciado;
    JList instanciado;
    JButton in;
    JButton out;
    JButton salvar;

    ActionListener listener;
    private static final long serialVersionUID = 1l;

    private ArrayList<Feature> features;
    
    
    public Configurador() {
        super("LPS Configurador GUI");
        listener = new ConfiguradorActionListener(this);
    }

    private void carregarInstFeatures() {
        try {
            DefaultListModel model = (DefaultListModel) instanciado.getModel();
            FileInputStream fis = new FileInputStream("lps.bin");
            ObjectInputStream ois = new ObjectInputStream(fis);
            Object obj;
            while((obj = ois.readObject()) != null) {
                Class <?> c = (Class) obj;
                Constructor<?> constructor = c.getConstructor();
                model.addElement(constructor.newInstance()); 
            }
            ois.close();
        }
        catch(FileNotFoundException ex) {
            JOptionPane.showMessageDialog(this, "O arquivo lps.bin não existe", "Erro", JOptionPane.ERROR_MESSAGE);
        }
        catch(Exception ex) {

        }
    }

    public Configurador init() {
        features = new ArrayList<Feature>();
        features.add(new Login());
        features.add(new Pedidos());
        features.add(new Cardapio());
        features.add(new Pagamento());
        features.add(new Financas());
        features.add(new Estoque());


        setLayout(new GridLayout(1, 3));

        naoInstanciado = new JList<Feature>(new DefaultListModel<Feature>());
        naoInstanciado.setLayoutOrientation(JList.VERTICAL);
        add(naoInstanciado);

        JPanel optPanel = new JPanel(new GridLayout(3, 1));
        out = new JButton("<<");
        out.addActionListener(listener);
        in = new JButton(">>");
        in.addActionListener(listener);
        salvar = new JButton("Salvar");
        salvar.addActionListener(listener);
        optPanel.add(out);
        optPanel.add(in);
        optPanel.add(salvar);
        add(optPanel);

        instanciado = new JList<Feature>(new DefaultListModel<Feature>());
        instanciado.setLayoutOrientation(JList.VERTICAL);
        add(instanciado);

        pack();
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);

        carregarInstFeatures();
        DefaultListModel instModel = (DefaultListModel) instanciado.getModel();
        DefaultListModel naoInstModel = (DefaultListModel) naoInstanciado.getModel();

        for(int i = 0; i < features.size(); i++) {
            Feature feature = features.get(i);
            boolean found = false;
            for(int j = 0; j < instModel.getSize(); j++) {
                Feature f = (Feature) instModel.getElementAt(j);
                if(f.getClass() == feature.getClass()) {
                    found = true;
                    break;
                }
            }
            if(!found)
                naoInstModel.addElement(feature);
        }
        return this;
    }

    private class ConfiguradorActionListener implements ActionListener {
        public Configurador configurador;

        public ConfiguradorActionListener(Configurador configurador) {
            this.configurador = configurador;
        }

        private Feature perguntarFeatureOpcional(Feature feature) {
            if(JOptionPane.showConfirmDialog(
                configurador,
                "Deseja adicionar a feature opcional '" + feature.toString() + "'?",
                "Feature Opcional",
                JOptionPane.YES_NO_OPTION
            ) == JOptionPane.YES_OPTION) return feature;
            else return null;
        }

        public void actionPerformed(ActionEvent e) {
            Object source = e.getSource();
            DefaultListModel instModel = (DefaultListModel) instanciado.getModel();
            DefaultListModel naoInstModel = (DefaultListModel) naoInstanciado.getModel();
            if(source == in) {
                Feature feature = (Feature) naoInstanciado.getSelectedValue();
                if(feature == null) {
                    JOptionPane.showMessageDialog(configurador, "Selecione uma feature para ser instanciada", "Erro", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    Class c = feature.getClass();
                    if(c == Pedidos.class) {
                        instModel.addElement(feature);
                        naoInstModel.removeElement(feature);

                        Feature featureOpcional = perguntarFeatureOpcional(new SinalSenha());

                        if(featureOpcional != null) {
                            instModel.addElement(featureOpcional);
                        }

                        naoInstModel.removeElement(feature);
                    }
                    else if(c == Cardapio.class) {
                        instModel.addElement(feature);
                        naoInstModel.removeElement(feature);
                        instModel.addElement(new ProdutoVenda());

                        Feature featureOpcional = perguntarFeatureOpcional(new Promocao());

                        if(featureOpcional != null) {
                            instModel.addElement(featureOpcional);
                        }

                        featureOpcional = perguntarFeatureOpcional(new Combo());

                        if(featureOpcional != null) {
                            instModel.addElement(featureOpcional);
                        }

                        naoInstModel.removeElement(feature);
                    }
                    else if(c == Pagamento.class) {
                        instModel.addElement(feature);
                        naoInstModel.removeElement(feature);

                        Feature featureOpcional = perguntarFeatureOpcional(new Dinheiro());
                        if(featureOpcional != null) {
                            instModel.addElement(featureOpcional);
                        }

                        featureOpcional = perguntarFeatureOpcional(new Cartao());
                        if(featureOpcional != null) {
                            instModel.addElement(featureOpcional);
                        }

                        naoInstModel.removeElement(feature);
                    }
                    else if(c == Financas.class) {
                        instModel.addElement(feature);
                        naoInstModel.removeElement(feature);

                        Feature featureOpcional = perguntarFeatureOpcional(new Relatorio());
                        if(featureOpcional != null) {
                            instModel.addElement(featureOpcional);
                        }

                        naoInstModel.removeElement(feature);
                    }
                    else if(c == Estoque.class) {
                        instModel.addElement(feature);
                        naoInstModel.removeElement(feature);
                        instModel.addElement(new Produtos());

                        Feature featureOpcional = perguntarFeatureOpcional(new Fornecedores());
                        if(featureOpcional != null) {
                            instModel.addElement(featureOpcional);
                        }

                        naoInstModel.removeElement(feature);
                    }
                    else {
                        instModel.addElement(feature);
                        naoInstModel.removeElement(feature);
                    }
                }
                
            }
            else if(source == out) {
                Feature feature = (Feature) instanciado.getSelectedValue();
                if(feature == null) {
                    JOptionPane.showMessageDialog(null, "Selecione uma feature para ser removida", "Erro", JOptionPane.ERROR_MESSAGE);
                }
                else {
                    Class c = feature.getClass();
                    if(c == Pedidos.class) {
                        for(int i = 0; i < instModel.getSize(); i++) {
                            Feature f = (Feature) instModel.getElementAt(i);
                            Class fClass = f.getClass();
                            if(fClass == SinalSenha.class) {
                                instModel.removeElement(f);
                            }
                        }
                        naoInstModel.addElement(feature);
                        instModel.removeElement(feature);
                    }
                    else if(c == Cardapio.class) {
                        for(int i = 0; i < instModel.getSize(); i++) {
                            Feature f = (Feature) instModel.getElementAt(i);
                            Class fClass = f.getClass();
                            if(fClass == Promocao.class || fClass == ProdutoVenda.class || fClass == Combo.class) {
                                instModel.removeElement(f);
                            }
                        }
                        naoInstModel.addElement(feature);
                        instModel.removeElement(feature);
                    }
                    else if(c == Pagamento.class) {
                        for(int i = 0; i < instModel.getSize(); i++) {
                            Feature f = (Feature) instModel.getElementAt(i);
                            Class fClass = f.getClass();
                            if(fClass == Dinheiro.class || fClass == Cartao.class) {
                                instModel.removeElement(f);
                            }
                        }
                        naoInstModel.addElement(feature);
                        instModel.removeElement(feature);
                    }
                    else if(c == Financas.class) {
                        for(int i = 0; i < instModel.getSize(); i++) {
                            Feature f = (Feature) instModel.getElementAt(i);
                            Class fClass = f.getClass();
                            if(fClass == Relatorio.class) {
                                instModel.removeElement(f);
                            }
                        }
                        naoInstModel.addElement(feature);
                        instModel.removeElement(feature);
                    }
                    else if(c == Estoque.class) {
                        for(int i = 0; i < instModel.getSize(); i++) {
                            Feature f = (Feature) instModel.getElementAt(i);
                            Class fClass = f.getClass();
                            if(fClass == Produtos.class || fClass == Fornecedores.class) {
                                instModel.removeElement(f);
                            }
                        }
                        naoInstModel.addElement(feature);
                        instModel.removeElement(feature);
                    }
                    else if(c == Produtos.class) {
                        for(int i = 0; i < instModel.getSize(); i++) {
                            Feature f = (Feature) instModel.getElementAt(i);
                            Class fClass = f.getClass();
                            if(fClass == Estoque.class || fClass == Fornecedores.class) {
                                instModel.removeElement(f);
                            }
                        }
                        naoInstModel.addElement(new Estoque());
                        instModel.removeElement(feature);
                    }
                    else {
                        if(feature.getTipo() == Feature.TIPO_COMUM && !feature.isSubFeature())
                            naoInstModel.addElement(feature);
                        instModel.removeElement(feature);
                    }
                }
            }
            else if(source == salvar) {
                try {
                    FileOutputStream fos = new FileOutputStream("lps.bin");
                    ObjectOutputStream oos = new ObjectOutputStream(fos);
                    for(int i = 0; i < instModel.getSize(); i++) {
                        oos.writeObject(instModel.getElementAt(i).getClass());
                    }
                    oos.close();
                    JOptionPane.showMessageDialog(configurador, "Salvo com sucesso", "Salvar", JOptionPane.PLAIN_MESSAGE);
                }
                catch(Exception ex) {
                    JOptionPane.showMessageDialog(configurador, "Não foi possível salvar", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    

    public static void main(String[] args) {
        Configurador configurador = new Configurador().init();
        configurador.setVisible(true);
    }
}