/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meli.quasar.models;

import java.util.List;

public class TopSecretRequest {
    
    private List<Satelites> satelites;
    
    public List<Satelites> getSatelites() {
            return satelites;
    }

    public void setSatelites(List<Satelites> satelites) {
            this.satelites = satelites;
    }
}
