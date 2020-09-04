/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.meli.quasar.controllers;

import com.meli.quasar.models.SateliteRequest;
import com.meli.quasar.models.TopSecretRequest;
import com.meli.quasar.models.TopSecretResponse;
import com.meli.quasar.services.TopSecretService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author Pc
 */
@RestController
public class QuasarAPI {
    
    @Autowired
    private TopSecretService topSecretService;
    
    @PostMapping("/topsecret")
    public TopSecretResponse topSecret(@RequestBody TopSecretRequest request) throws Exception {
        return topSecretService.topSecret(request);
    }
    
    @RequestMapping(value = "/topsecret_split/{satelite_name}", method = RequestMethod.POST)
    public TopSecretResponse topSecretSplit(@PathVariable("satelite_name") String sateliteName, @RequestBody SateliteRequest request) throws Exception {
        return topSecretService.topSecretSplitPost(sateliteName, request);
    }
    
    @RequestMapping(value = "/topsecret_split/", method =  RequestMethod.GET)
    public TopSecretResponse topSecretSplit() throws Exception {
        return topSecretService.topSecretSplitGet();
    }
            
}
