import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Main extends JFrame {
    private Font font;
    private JPanel panelArbol;
    private JTextField texto;
    private RBT<Integer> rbt = new RBT<>();
    private Map<Nodo<Integer>, Point> posiciones = new HashMap<>();
    
    public Main() {
        super("Graficar RBT");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
        getContentPane().setBackground(Color.BLUE);
        setLocationRelativeTo(null);
        font = new Font("Arial", Font.BOLD, 50);
        setFont(font);

        panelArbol = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                dibujarArbol((Graphics2D) g);
            }
        };
        panelArbol.setBackground(Color.WHITE);
        panelArbol.setPreferredSize(new Dimension(3000, 1500));
        
        JScrollPane scrollPane = new JScrollPane(panelArbol);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(1, 3));
        this.add(panel, BorderLayout.NORTH);
        
        JButton boton = new JButton("Insertar");
        boton.setFont(font);
        boton.addActionListener(e -> this.dibujar());
        
        JButton botonBorrar = new JButton("Eliminar");
        botonBorrar.setFont(font);
        botonBorrar.addActionListener(e -> this.eliminar());
        
        texto = new JTextField(40);
        texto.setFont(font);
        texto.setHorizontalAlignment(JTextField.CENTER);
        
        panel.add(texto);
        panel.add(boton);
        panel.add(botonBorrar);
        
        setVisible(true);
    }

    public void calcularPosiciones(Nodo<Integer> nodo, int nivel, Map<Nodo<Integer>, Point> posiciones, int ancho, int alto, int x, int aa) {
        if (nodo == null) return;
        int distancia = (ancho / (int)Math.pow(2, nivel + 2)); 
        int y = 120 + (nivel - 1) * 120; 
        posiciones.put(nodo, new Point(x, y));
        calcularPosiciones(nodo.left, nivel + 1, posiciones, ancho, alto, x - distancia, aa);
        calcularPosiciones(nodo.right, nivel + 1, posiciones, ancho, alto, x + distancia, aa);
    }

    public void dibujar() {
        try {
            int value = Integer.parseInt(texto.getText());
            rbt.insert(value);
            actualizarArbol();
            texto.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un número válido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void eliminar() {
        try {
            int value = Integer.parseInt(texto.getText());
            rbt.delete(value);
            actualizarArbol();
            texto.setText("");
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Por favor ingrese un número válido", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarArbol() {
        int aa = (int) Math.ceil(Math.log(rbt.size + 1) / Math.log(2));
        aa = Math.max(aa, 1) + 2;

        int ancho = panelArbol.getPreferredSize().width;
        int alto = panelArbol.getPreferredSize().height;

        posiciones.clear();
        int xCentro = ancho / 2;
        calcularPosiciones(rbt.root, 1, posiciones, ancho, alto, xCentro, aa);
        panelArbol.repaint();
    }
    
    private void dibujarArbol(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        int diameter = 100;

        g2.setColor(Color.BLACK);
        g2.setStroke(new BasicStroke(3));
        for (Map.Entry<Nodo<Integer>, Point> entry : posiciones.entrySet()) {
            Nodo<Integer> nodo = entry.getKey();
            Point p = entry.getValue();

            if (nodo.left != null && posiciones.containsKey(nodo.left)) {
                Point pLeft = posiciones.get(nodo.left);
                g2.drawLine(p.x, p.y, pLeft.x, pLeft.y);
            }
            if (nodo.right != null && posiciones.containsKey(nodo.right)) {
                Point pRight = posiciones.get(nodo.right);
                g2.drawLine(p.x, p.y, pRight.x, pRight.y);
            }
        }

        for (Map.Entry<Nodo<Integer>, Point> entry : posiciones.entrySet()) {
            Nodo<Integer> nodo = entry.getKey();
            Point p = entry.getValue();

            int x = p.x - diameter / 2;
            int y = p.y - diameter / 2;

            g2.setColor(nodo.color);
            g2.fillOval(x, y, diameter, diameter);

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawOval(x, y, diameter, diameter);

            g2.setColor(nodo.color == Color.BLACK ? Color.WHITE : Color.BLACK);
            g2.setFont(font);
            String text = "" + nodo.elemento;
            FontMetrics fm = g2.getFontMetrics();
            int textX = x + (diameter - fm.stringWidth(text)) / 2;
            int textY = y + ((diameter - fm.getHeight()) / 2) + fm.getAscent();
            g2.drawString(text, textX, textY);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new Main();
        });
    }
}