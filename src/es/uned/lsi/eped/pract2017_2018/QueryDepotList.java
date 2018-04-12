/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uned.lsi.eped.pract2017_2018;

import es.uned.lsi.eped.DataStructures.IteratorIF;
import es.uned.lsi.eped.DataStructures.List;
import es.uned.lsi.eped.DataStructures.ListIF;

/**
 *
 * @author ashh412
 */
public class QueryDepotList implements QueryDepotIF {

    ListIF queryList = new List();
    int numQuery;// para guardar la query si tiene frecuencia

    /* Devuelve el número de consultas diferentes (sin contar repeticiones) */
 /* que hay almacenadas en el dep�sito */
 /* @returns el n�mero de consultas diferentes almacenadas */
    @Override
    public int numQueries() {
        return queryList.size();
    }

    /* Consulta la frecuencia de una consulta en el dep�sito */
 /* @returns la frecuencia de la consulta. Si no est�, devolver� 0 */
 /* @param el texto de la consulta */
    @Override
    public int getFreqQuery(String q) {
        Query localQuery;
        List lista;

        numQuery = 0;
        //Buscamos la consulta
        if (queryList.size() > 0) {
            IteratorIF<Query> myIterator = queryList.iterator();
            while (myIterator.hasNext()) {
                localQuery = myIterator.getNext();
                numQuery++;
                if (localQuery.getText().equals(q)) {
                    return localQuery.getFreq();
                }
            }
        }
//      
//No hay coincidencias

        return 0;
    }

    /* Dado un prefijo de consulta, devuelve una lista, ordenada por */
 /* frecuencias de mayor a menor, de todas las consultas almacenadas */
 /* en el dep�sito que comiencen por dicho prefijo */
 /* @returns la lista de consultas ordenada por frecuencias y orden */
 /* lexicogr�fico en caso de coincidencia de frecuencia */
 /* @param el prefijo */
    @Override
    public ListIF<Query> listOfQueries(String prefix) {

        return (List) search(queryList, prefix);

    }

    //Propósito: buscar elementos en una lista que empiecen por una cadena
    //Recibe una cadena de texto con la cadena a buscar
    //Devuelve una lista con las coincidencias ordenada por frecuencia
    private ListIF<Query> search(ListIF lista, String prefix) {
        List orderList = new List();
        Query localQuery;
        boolean working = false;
//hacemos un pivote para buscar el elemento
//Desde que deje de encontrar coincidencias salir

        //Buscamos la consulta     
        if (lista.size() > 0) {

            IteratorIF<Query> myIterator = lista.iterator();
            while (myIterator.hasNext()) {

                localQuery = myIterator.getNext();
                if (localQuery.getText().startsWith(prefix)) {
                    orderList = (List) orderInsert(orderList, localQuery, true);
                    working = true;
                } else {
                    if (working == true) {
                        break;
                    }
                }
            }
        }
        return orderList;
    }
//Deprecated
//Realizamos una búsqueda recursiva dicotómica del primer elemento coincidente

    private ListIF searchString(int lowerbound, int upperbound, String searchedString, boolean exactMatch) {
        List orderList = new List();
        Query localQuery;
        int interval = upperbound - lowerbound;
        int pivote = (lowerbound + interval / 2);//+ 1;
        int position;

        localQuery = (Query) queryList.get(pivote);

        //caso base
        if ((lowerbound > upperbound)) {
            return null;
        }

        //caso base
        if (interval == 0 && exactMatch) {
            if (localQuery.getText().equals(searchedString)) {

                return (List) orderInsert(orderList, localQuery, false);
            }
            return null;
        }

        position = localQuery.getText().compareTo(searchedString);

        //caso base
        if (position == 0 && exactMatch) {
            //   System.out.println("======Encontrado=====" + searchedString);
            return (List) orderInsert(orderList, localQuery, false);

        }
//Hemos encontrado coincidencias. Buscamos el primer registro
        if (!exactMatch && localQuery.getText().startsWith(searchedString)) {
            //si no es el primer elemento seguimos buscando a la izquierda
            int pivoteAux = pivote;
            localQuery = (Query) queryList.get(--pivoteAux);
            //Si no hay coincidencias a la izquierda tenemos el primer elemento
            if ((pivote == 1) || !(localQuery.getText().startsWith(searchedString))) {

                localQuery = (Query) queryList.get(pivote);
                //Ciclamos mientras existan coincidencias
                while (true) {

                    if (localQuery.getText().startsWith(searchedString)) {

                        orderList = (List) orderInsert(orderList, localQuery, true);
                        pivote++;
                        localQuery = (Query) queryList.get(pivote);

                    } else {
                        break;
                    }
                }
                return orderList;

            } else {//A la izquierda hay una coincidencia. Seguimos buscando el primer elemento

                return searchString(lowerbound, pivoteAux, searchedString, exactMatch);
            }

            //busqueda recursiva 
        } else //seguimos buscando
        {
            //1 que sea mayor
            if (position > 0) {
                return searchString(lowerbound, pivote - 1, searchedString, exactMatch);

            }
            //2 que sea menor
            if (position < 0) {
                return searchString(pivote + 1, upperbound, searchedString, exactMatch);
            }

        }

        return null;
    }

    /* Incrementa en uno la frecuencia de una consulta en el dep�sito */
 /* Si la consulta no exist�a en la estructura, la deber� a�adir */
 /* @param el texto de la consulta */
    @Override
    public void incFreqQuery(String q) {
        int freq;

        //No encontramos el elemento, realizamos una insercion ordenada
        if (!(q == null)) {
            freq = getFreqQuery(q);
            if (freq == 0) {
                Query query = new Query(q);
                queryList = orderInsert(queryList, query, false);
                //    System.out.println("Agregado elemento " + q);

            } else {

                Query query = (Query) queryList.get(numQuery);

                query.setFreq(freq + 1);
                //  System.out.println("Incrementando frecuencia " + query.getText() + " " + query.getFreq());
            }
        }
    }

    //Propósito: Insertar nuevas cadenas en la lista de forma ordenada
    //Parametros:
    //1. Lista sobre la que hace la inserción IN ListIF
    //2. Query a insertar IN Query
    //3. Flag. Si verdadero ordena por frecuencia. Si falso alfabéticamente IN boolean
    //4. Lista ordenada con la nueva inserción OUT  ListIF
    public ListIF orderInsert(ListIF lista, Query query, boolean freq) {
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
                int compString = qText.compareTo(lText);

//si la nueva query es mayor la insertamos a la izquierda.
                if (compString < 0) {
                    break;
                }
//Si son iguales ordenamos por frecuencia
                if (freq) { //Si es mayor insertamos, en caso contrario seguimos ciclando
                    if (query.getFreq() > localQuery.getFreq()) {
                        break;
                    }
                }
                //si es menor seguimos ciclando
                i++;
                //System.out.println(localQuery.getFreq() + "  " + localQuery.getText());
            }
            lista.insert(query, i);

        }
        return lista;
    }

    public void print(ListIF lista) {
        Query localQuery;

        IteratorIF<Query> myIterator = lista.iterator();
        while (myIterator.hasNext()) {
            localQuery = myIterator.getNext();
            System.out.println(localQuery.getFreq() + "  " + localQuery.getText());
        }
    }

}
