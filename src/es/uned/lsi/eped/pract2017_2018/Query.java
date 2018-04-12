/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.uned.lsi.eped.pract2017_2018;

/**
 *
 * @author ashh412
 */
public class Query  {

    private final String text;
    private int freq;

    /* Construye una nueva query con el texto pasado como parámetro */
    public Query(String text) {
        this.text = text;
        this.freq = 1;
    }

    /* Modifica la frecuencia de la query */
    public void setFreq(int freq) {
        this.freq = freq;
    }

    /* Devuelve el texto de una query */
    public String getText() {
        return text;
    }

    /* Devuelve la frecuencia de una query */
    public int getFreq() {
        return freq;
    }

//          if (q1.getFreq() < q2.getFreq() ||
//                (q1.getFreq() == q2.getFreq() && q1.getText().compareTo(q2.getText()) > 0))
//            return this.LESS;
//        else if (q1.getFreq() > q2.getFreq() ||
//                (q1.getFreq() == q2.getFreq() && q1.getText().compareTo(q2.getText()) < 0))
//            return this.GREATER;
//        return this.EQUAL;
   
}
//    
//     @Override
//    public int compare(Query e1, Query e2) {
//        if (e1.getFreq() < e2.getFreq() ||
//                (e1.getFreq() == e2.getFreq() && e1.getText().compareTo(e2.getText()) > 0))
//            return this.LESS;
//        else if (e1.getFreq() > e2.getFreq() ||
//                (e1.getFreq() == e2.getFreq() && e1.getText().compareTo(e2.getText()) < 0))
//            return this.GREATER;
//        return this.EQUAL;
//    }

