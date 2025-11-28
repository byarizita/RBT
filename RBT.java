import java.awt.Color;

public class RBT<T extends Comparable<T>> {
    public Nodo<T> root;
    public int size;
    
    public RBT() {
        this.root = null;
        this.size = 0;
    }

    public void insert(T elemento) {
        Nodo<T> newNode = new Nodo<>(elemento);
        newNode.color = Color.RED; 
        size++;
        
        if (root == null) {
            root = newNode;
            root.color = Color.BLACK; // raíz siempre debe ser negra
            return;
        }
        
        // inserción BST normal
        Nodo<T> actual = root;
        while (true) {
            if (actual.elemento.compareTo(elemento) > 0) {
                if (actual.left == null) {
                    actual.left = newNode;
                    newNode.parent = actual;
                    break;
                }
                actual = actual.left;
            } else {
                if (actual.right == null) {
                    actual.right = newNode;
                    newNode.parent = actual;
                    break;
                }
                actual = actual.right;
            }
        }
        
        // reparar árbol después de inserción
        fixInsert(newNode);
    }

    
    private void fixInsert(Nodo<T> node) {
        while (node != root && node.parent.color == Color.RED) {
            if (node.parent == node.parent.parent.left) {
                Nodo<T> uncle = node.parent.parent.right;
                
                // Caso 1: Tío es rojo
                if (uncle != null && uncle.color == Color.RED) {
                    node.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                } else {
                    // Caso 2: Nodo es hijo derecho
                    if (node == node.parent.right) {
                        node = node.parent;
                        rotateLeft(node);
                    }
                    // Caso 3: Nodo es hijo izquierdo
                    node.parent.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    rotateRight(node.parent.parent);
                }
            } else {
                Nodo<T> uncle = node.parent.parent.left;
                
                // Caso 1: Tío es rojo
                if (uncle != null && uncle.color == Color.RED) {
                    node.parent.color = Color.BLACK;
                    uncle.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    node = node.parent.parent;
                } else {
                    // Caso 2: Nodo es hijo izquierdo
                    if (node == node.parent.left) {
                        node = node.parent;
                        rotateRight(node);
                    }
                    // Caso 3: Nodo es hijo derecho
                    node.parent.color = Color.BLACK;
                    node.parent.parent.color = Color.RED;
                    rotateLeft(node.parent.parent);
                }
            }
        }
        root.color = Color.BLACK;
    }

    // ===== ELIMINACIÓN =====
    public void delete(T elemento) {
        Nodo<T> node = search(elemento);
        if (node == null) {
            System.out.println("Elemento no encontrado");
            return;
        }
        deleteNode(node);
        size--;
    }

    // Buscar nodo con el elemento
    private Nodo<T> search(T elemento) {
        Nodo<T> actual = root;
        while (actual != null) {
            int cmp = actual.elemento.compareTo(elemento);
            if (cmp == 0) return actual;
            else if (cmp > 0) actual = actual.left;
            else actual = actual.right;
        }
        return null;
    }

    // eliminar nodo del árbol
    private void deleteNode(Nodo<T> node) {
        Nodo<T> y = node;
        Nodo<T> x;
        Color originalColor = y.color;
        
        if (node.left == null) {
            x = node.right;
            transplant(node, node.right);
        } else if (node.right == null) {
            x = node.left;
            transplant(node, node.left);
        } else {
            y = minimum(node.right);
            originalColor = y.color;
            x = y.right;
            
            if (y.parent == node) {
                if (x != null) x.parent = y;
            } else {
                transplant(y, y.right);
                y.right = node.right;
                y.right.parent = y;
            }
            
            transplant(node, y);
            y.left = node.left;
            y.left.parent = y;
            y.color = node.color;
        }
        
        if (originalColor == Color.BLACK && x != null) {
            fixDelete(x);
        }
    }

    // reparar arbool después de eliminación
    private void fixDelete(Nodo<T> node) {
        while (node != root && node.color == Color.BLACK) {
            if (node == node.parent.left) {
                Nodo<T> sibling = node.parent.right;
                
                // Caso 1: Hermano es rojo
                if (sibling != null && sibling.color == Color.RED) {
                    sibling.color = Color.BLACK;
                    node.parent.color = Color.RED;
                    rotateLeft(node.parent);
                    sibling = node.parent.right;
                }
                
                if (sibling != null) {
                    // Caso 2: Ambos hijos del hermano son negros
                    if ((sibling.left == null || sibling.left.color == Color.BLACK) &&
                        (sibling.right == null || sibling.right.color == Color.BLACK)) {
                        sibling.color = Color.RED;
                        node = node.parent;
                    } else {
                        // Caso 3: Hijo derecho del hermano es negro
                        if (sibling.right == null || sibling.right.color == Color.BLACK) {
                            if (sibling.left != null) sibling.left.color = Color.BLACK;
                            sibling.color = Color.RED;
                            rotateRight(sibling);
                            sibling = node.parent.right;
                        }
                        // Caso 4: Hijo derecho del hermano es rojo
                        sibling.color = node.parent.color;
                        node.parent.color = Color.BLACK;
                        if (sibling.right != null) sibling.right.color = Color.BLACK;
                        rotateLeft(node.parent);
                        node = root;
                    }
                } else {
                    break;
                }
            } else {
                Nodo<T> sibling = node.parent.left;
                
                // Caso 1: Hermano es rojo
                if (sibling != null && sibling.color == Color.RED) {
                    sibling.color = Color.BLACK;
                    node.parent.color = Color.RED;
                    rotateRight(node.parent);
                    sibling = node.parent.left;
                }
                
                if (sibling != null) {
                    // Caso 2: Ambos hijos del hermano son negros
                    if ((sibling.right == null || sibling.right.color == Color.BLACK) &&
                        (sibling.left == null || sibling.left.color == Color.BLACK)) {
                        sibling.color = Color.RED;
                        node = node.parent;
                    } else {
                        // Caso 3: Hijo izquierdo del hermano es negro
                        if (sibling.left == null || sibling.left.color == Color.BLACK) {
                            if (sibling.right != null) sibling.right.color = Color.BLACK;
                            sibling.color = Color.RED;
                            rotateLeft(sibling);
                            sibling = node.parent.left;
                        }
                        // Caso 4: Hijo izquierdo del hermano es rojo
                        sibling.color = node.parent.color;
                        node.parent.color = Color.BLACK;
                        if (sibling.left != null) sibling.left.color = Color.BLACK;
                        rotateRight(node.parent);
                        node = root;
                    }
                } else {
                    break;
                }
            }
        }
        node.color = Color.BLACK;
    }
    private void transplant(Nodo<T> u, Nodo<T> v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        if (v != null) {
            v.parent = u.parent;
        }
    }

    // mínimo en un subárbol
    private Nodo<T> minimum(Nodo<T> node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // (rotaciones)
    
    // rotación a la izquierda
    private void rotateLeft(Nodo<T> node) {
        Nodo<T> rightChild = node.right;
        node.right = rightChild.left;
        
        if (rightChild.left != null) {
            rightChild.left.parent = node;
        }
        
        rightChild.parent = node.parent;
        
        if (node.parent == null) {
            root = rightChild;
        } else if (node == node.parent.left) {
            node.parent.left = rightChild;
        } else {
            node.parent.right = rightChild;
        }
        
        rightChild.left = node;
        node.parent = rightChild;
    }

    // rotación a la derecha
    private void rotateRight(Nodo<T> node) {
        Nodo<T> leftChild = node.left;
        node.left = leftChild.right;
        
        if (leftChild.right != null) {
            leftChild.right.parent = node;
        }
        
        leftChild.parent = node.parent;
        
        if (node.parent == null) {
            root = leftChild;
        } else if (node == node.parent.right) {
            node.parent.right = leftChild;
        } else {
            node.parent.left = leftChild;
        }
        
        leftChild.right = node;
        node.parent = leftChild;
    }
    
    public void inOrder() {
        if (root == null) {
            System.out.println("El arbol esta vacio");
            return;
        }
        inOrderRecursivo(root);
    }

    public void inOrderRecursivo(Nodo<T> nodo) {
        if (nodo.left != null) {
            inOrderRecursivo(nodo.left);
        }
        System.out.println(nodo.elemento);
        if (nodo.right != null) {
            inOrderRecursivo(nodo.right);
        }
    }
}