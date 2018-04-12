/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uned.lsi.eped.pract2017_2018;

import es.uned.lsi.eped.DataStructures.GTree;
import es.uned.lsi.eped.DataStructures.IteratorIF;
import es.uned.lsi.eped.DataStructures.List;
import es.uned.lsi.eped.DataStructures.ListIF;

/**
 *
 * @author ashh412
 */
public class QueryDepotTree implements QueryDepotIF {

    //private BTree btree;
    private final GTreefq gtree;
    //Contador de cantidad de consultas
    private int numQueries;

    //Extendemos el arbol para crear un flag 
    //que identifique el nodo frecuencia
    private class GTreefq extends GTree {

        private boolean bfreq;

        private boolean isFreq() {
            return bfreq;
        }

        private void setFreq(boolean bfreq) {
            this.bfreq = bfreq;
        }
    }

    //Inicializamos el nodo raiz en el constructor
    public QueryDepotTree() {
        gtree = new GTreefq();
        gtree.setRoot("");
    }

    //Devuele el numero de consultas
    @Override
    public int numQueries() {
        return numQueries;
    }

    @Override
    public int getFreqQuery(String q) {

        return searchFreq(gtree, q);

    }

    /* Dado un prefijo de consulta, devuelve una lista, ordenada por */
 /* frecuencias de mayor a menor, de todas las consultas almacenadas */
 /* en el dep�sito que comiencen por dicho prefijo */
 /* @returns la lista de consultas ordenada por frecuencias y orden */
 /* lexicogr�fico en caso de coincidencia de frecuencia */
 /* @param el prefijo */
    @Override
    public ListIF<Query> listOfQueries(String prefix) {

        ListIF list = searchString(gtree, prefix, 1);

        return list;

    }
//Proposito: Buscar queries coincidentes en el arbol de busqueda
//Devuelve una lista de queries
//recorremos los hijos y si son hojas guardamos la frecuencia y agregamos la query a la lista
//En caso contrario agregamos un nodo a las consultas posibles y seguimos profundizando    

    private ListIF searchMatch(GTreefq tree, Query query, ListIF list) {

        IteratorIF<GTreefq> childIt = tree.getChildren().iterator();
        while (childIt.hasNext()) {
            GTreefq auxTree = childIt.getNext();

            if (auxTree.isLeaf()) {

                query.setFreq((int) auxTree.getRoot());
                list = orderInsert(list, query);
            } else {//profundizamos por la izquierda
                Query lquery = new Query(query.getText().concat((String) auxTree.getRoot()));
                searchMatch(auxTree, lquery, list);

            }
        }

        return list;

    }

    //Propósito: Insertar nuevas cadenas en la lista de forma ordenada
    //Parametros:
    //1. Lista sobre la que hace la inserción IN ListIF
    //2. Query a insertar IN Query
    //returns: Lista ordenada con la nueva inserción  ListIF
    public ListIF orderInsert(ListIF lista, Query query) {
        //  Query query = new Query(q);
        Query localQuery;
        int i = 1;

        if (lista.size() == 0) {
            lista.insert(query, 1);
        } else {
            IteratorIF<Query> myIterator = lista.iterator();
            while (myIterator.hasNext()) {
                localQuery = myIterator.getNext();
                String lText = localQuery.getText();
                String qText = query.getText();
//Compara la nueva cadena con las cadenas existentes

//si la nueva query es mayor la insertamos a la izquierda.
                if (query.getFreq() > localQuery.getFreq()) {
                    break;
                }
//Si son iguales ordenamos de forma lexicografica
                if (query.getFreq() == localQuery.getFreq()) {
                    int compString = qText.compareTo(lText);
                    if (compString < 0) {
                        break;
                    }
                    //si es menor seguimos ciclando
                }
                     i++;
            }
            lista.insert(query, i);

        }
        return lista;
    }
    //Proposito: Posicionarse en el arbol de busqueda
    //Al posicionarse llama a searchMatch para obtener una lista con 
    //todas las consultas predictivas

    private ListIF searchString(GTreefq ltree, String parseString, int pos) {
        ListIF list = new List();
//Empezamos a buscar coincidencias

        //Si nos hemos posicionado en el ultimo nodo.
        //Devolvemos la lista de coincidencias
        if (parseString.length() == pos - 1) {

            return searchMatch(ltree, new Query(parseString), list);
        }

        //Buscamos el caracter en los hijos del arbol
        IteratorIF<GTreefq> childIt = ltree.getChildren().iterator();
        while (childIt.hasNext()) {
            GTreefq lctree = childIt.getNext();

// Comprobamos los hijos excepto las hojas
            if (!lctree.isLeaf()) {
                if (lctree.getRoot().equals(getChar(parseString, pos))) {
                    //Si encontramos el caracter entonces seguimos buscando en los hijos
                    return searchString(lctree, parseString, ++pos);
                }
            }
        }
        return list;
    }

//Proposito: funcion privada que gestiona la insercion del nodo frecuencia
    private GTreefq addFrqNode(GTreefq tree) {
        GTreefq nodoFreq = new GTreefq();
        //  Query query = new Query("");
        //Agregamos un nodo al arbol pasado por parametro

        nodoFreq.setRoot(1);
        nodoFreq.setFreq(true);
        tree.addChild(1, nodoFreq);
        numQueries++;
        return tree;
    }

//Propósito: Insertar el contador e incrementarlo
    private GTreefq addFqz(GTreefq gltree) {

        //Si es leaf hemos llegado al final de un arbol sin frecuencia
        //si no existe nodo hijo de frecuencia se crea
        //si existe nodo hijo se incrementa
        if (gltree.isLeaf()) {
            //Es una hoja. Incrementamos frecuencia
            gltree = addFrqNode(gltree);
        } else //no es una hoja, luego necesariamente es la frecuencia
        {
            GTreefq nodoFreq = (GTreefq) gltree.getChild(1);
            if (nodoFreq.isFreq()) {
                nodoFreq.setRoot((int) nodoFreq.getRoot() + 1);

                //   System.out.println("Agregada frecuencia");
            } else {
                gltree = addFrqNode(gltree);
            }
        }
        return gltree;
    }

    //Busca un caracter en los hijos de un arbol y si lo encuentra duelve el subarbol
    private GTreefq findChild(GTreefq tree, String parseChar) {
        String treeChar;
        GTreefq auxTree = null;

        if (tree.getChildren().size() > 0) {
            //Creamos un iterador y en  cada iteracion
            //comprobamos la posible existencia de cadena en hijos
            IteratorIF iterGTree = tree.getChildren().iterator();

            while (iterGTree.hasNext()) {

                auxTree = (GTreefq) iterGTree.getNext();
                //Si hay coincidencia entre el caracter del  hijo
                //Devolvemos el subarbol
                if (parseChar.equals(auxTree.getRoot().toString())) {
                    break;
                }
                //No hemos encontrado nada
                auxTree = null;

            }
        }
        return auxTree;
    }

    //Propósito: Alimentar el arbol
    @Override
    public void incFreqQuery(String q) {

        if (!(q.isEmpty())) {
            GTreefq miarbol = searchInsert(gtree, q);
        }

    }

    //Proposito: Encapsular la lógica de la llamada y devolucion a getFrecuency
    private int searchFreq(GTreefq tree, String q) {
        int fqz = 0;

        //Obtenemos el nodo de frecuencia
        GTreefq ltree = getFreqTree(tree, q, 1);
        //Extramos el contenido del arbol
        if (ltree != null) {
            fqz = (int) ltree.getRoot();
        }

        return fqz;
    }

    //Proposito: obtener la frecuencia de una consulta
    //Navega por los hijos mientras vaya encontrando coincidencias
    //Al llegar al fina de la cadena devuelve un arbol con el nodo 
    //frecuencia
    //Vamos pasando un apuntador por los caracteres de la cadena
    private GTreefq getFreqTree(GTreefq ltree, String parseString, int pos) {

//vamos navegando por los nodos  
        //Buscamos el caracter en los hijos del arbol
        IteratorIF<GTreefq> childIt = ltree.getChildren().iterator();
        while (childIt.hasNext()) {
            GTreefq lctree = childIt.getNext();
            if (parseString.length() == pos - 1 && lctree.isFreq()) {
                //Nos hemos posicionado en el ultimo nodo.
                return lctree;
            } // Comprobamos los hijos excepto las hojas
            else if (lctree.getRoot().equals(getChar(parseString, pos)) && (!lctree.isLeaf())) {
                //Si encontramos el caracter entonces seguimos buscando en los hijos
                return getFreqTree(lctree, parseString, ++pos);
            }
        }
        return null;
    }

    //Propósito: Buscar una cadena en el arbol y si no esta insertarlo
    //Recibe la cadena a buscar y el arbol donde buscarla
    //Devuelve el arbol con el nuevo nodo insertado o el incremento de frecuencia en su caso
    private GTreefq searchInsert(GTreefq glTree, String parseString) {
        String auxChar = null; //Guarda el caracter a procesar 

        if (parseString.isEmpty()) { //Estamos al final, incrementamos la frecuencia
            return addFqz(glTree);
        }

        //resto de nodos
        auxChar = getChar(parseString, 1);
        //reducimos la cadena en 1
        parseString = extractChar(parseString);

        //Comprobamos en los hijos si existe el nodo
        GTreefq lTree = findChild(glTree, auxChar);
        if (lTree != null) {
            //proseguimos con la recursion
            return searchInsert(lTree, parseString);
        } else {
            //si ha llegado al final de los hijos y no hay hojas coincidentes
            //creamos una nodo intermedio
            GTreefq node = new GTreefq();
            node.setRoot(auxChar);

            //Agregamos el nuevo nodo al arbol
            glTree.addChild(glTree.getNumChildren() + 1, node);
            //proseguimos con la recursion
            searchInsert(node, parseString);
        }

        return glTree;
    }

//    
//    private void printChildren(GTreefq tree, int nivel) {
//
//        GTreefq auxTree = null;
//
//        if (tree.getChildren().size() > 0) {
//            //Creamos un iterador y en  cada iteracion
//            //comprobamos la posible existencia de cadena en hijos
//            IteratorIF iterGTree = tree.getChildren().iterator();
//
//            while (iterGTree.hasNext()) {
//
//                auxTree = (GTreefq) iterGTree.getNext();
//                //Si hay coincidencia entre el caracter del  hijo
//                //Devolvemos el subarbol
//                if (auxTree.getRoot() instanceof Query) {
//                    Query query = (Query) auxTree.getRoot();
//                    System.out.print("-->Frecuencia " + query.getFreq() + "== nivel " + nivel);
//
//                } else {
//                    // System.out.print(auxTree.getRoot().toString() + "== nivel " + nivel);
//                    printChildren(auxTree, nivel + 1);
//                }
//
//            }
//
//        }
//        //System.out.println("......");
//    }
//
//    private void printChildrenNR(GTreefq tree, int nivel) {
//
//        GTreefq auxTree = null;
//
//        if (tree.getChildren().size() > 0) {
//            //Creamos un iterador y en  cada iteracion
//            //comprobamos la posible existencia de cadena en hijos
//            IteratorIF iterGTree = tree.getChildren().iterator();
//
//            while (iterGTree.hasNext()) {
//
//                auxTree = (GTreefq) iterGTree.getNext();
//                //Si hay coincidencia entre el caracter del  hijo
//                //Devolvemos el subarbol
//                if (auxTree.getRoot() instanceof Query) {
//                    Query query = (Query) auxTree.getRoot();
//                    //  System.out.print("-->Frecuencia " + query.getFreq() + "== nivel " + nivel);
//
//                } else {
//                    // System.out.print(auxTree.getRoot().toString() + "== nivel " + nivel);
//                    printChildren(auxTree, nivel + 1);
//                }
//
//            }
//
//        }
//        //    System.out.println("......");
//    }
    private String getChar(String parseString, int pos) {

        String lchar = String.valueOf(parseString.charAt(pos - 1));
        return lchar;
    }

    private String extractChar(String parseString) {

        parseString = parseString.substring(1, parseString.length());

        return parseString;
    }
}
