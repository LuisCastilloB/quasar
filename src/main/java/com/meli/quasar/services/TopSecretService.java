package com.meli.quasar.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.meli.quasar.models.Position;
import com.meli.quasar.models.SateliteRequest;
import com.meli.quasar.models.Satelites;
import com.meli.quasar.models.TopSecretRequest;
import com.meli.quasar.models.TopSecretResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class TopSecretService {

    @Value("#{'${satelite.names}'.split(',')}")
    private List<String> sateliteNames;
    
    @Value("#{'${kenobi.location}'.split(',')}")
    private List<Double> kenobiLocation;
    
    @Value("#{'${skywalker.location}'.split(',')}")
    private List<Double> skywalkerLocation;
        
    @Value("#{'${sato.location}'.split(',')}")
    private List<Double> satoLocation;
    
    @Value("${epsilon}")
    private double epsilon = 0.001;
    
    /**
     * Valida si el nombre del satelite es valido
     * @param sateliteName nombre del satelite
     */
    public void isValidSateliteName(String sateliteName) {
        
        boolean isValid = sateliteNames.contains(sateliteName);
        if (!isValid) {
             throw new ResponseStatusException( HttpStatus.NOT_FOUND,"Satelite ("+sateliteName+") no valido ");
        }
    }
    
    public TopSecretResponse topSecretSplitPost(String sateliteName, SateliteRequest request) throws Exception {
        return topSecretSplit(sateliteName, request);
    }
    
    public TopSecretResponse topSecretSplitGet() throws Exception {
        SateliteRequest request = new SateliteRequest();
        return topSecretSplit("satelites", request);
    }
    
    /**
     * Procesa mensajes de satelite por separado
     * @param sateliteName nombre del satelite
     * @param request body de la peticion
     * @return
     * @throws Exception 
     */
    public TopSecretResponse topSecretSplit(String sateliteName, SateliteRequest request) throws Exception {

        if (!sateliteName.equals("satelites")) {
            isValidSateliteName(sateliteName);
            ObjectMapper writeMapper = new ObjectMapper();
            writeMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
            writeMapper.writeValue(new File("data/"+sateliteName+".json"), request);
        }
        
        ObjectMapper readMapper = new ObjectMapper();
        SateliteRequest kenobiData = readMapper.readValue(new File("data/kenobi.json"), SateliteRequest.class);
        SateliteRequest skywalkerData = readMapper.readValue(new File("data/skywalker.json"), SateliteRequest.class);
        SateliteRequest satoData = readMapper.readValue(new File("data/sato.json"), SateliteRequest.class);
        
        TopSecretRequest newRequest = new TopSecretRequest();
        List<Satelites> satelites = new ArrayList<>();
        
        Satelites kenobi = new Satelites();
        kenobi.setDistance(kenobiData.getDistance());
        kenobi.setMessage(kenobiData.getMessage());
        kenobi.setName("kenobi");
        
        Satelites skywalker = new Satelites();
        skywalker.setDistance(skywalkerData.getDistance());
        skywalker.setMessage(skywalkerData.getMessage());
        skywalker.setName("skywalker");
        
        Satelites sato = new Satelites();
        sato.setDistance(satoData.getDistance());
        sato.setMessage(satoData.getMessage());
        sato.setName("sato");
        
        satelites.add(kenobi);
        satelites.add(skywalker);
        satelites.add(sato);
        newRequest.setSatelites(satelites);
                
        return topSecret(newRequest);
    }
    
    /**
     * Procesa peticion de satelites
     * @param request body de la peticion
     * @return
     * @throws Exception 
     */
    public TopSecretResponse topSecret(TopSecretRequest request) throws Exception {

        double kenobiDistance =0, skywalkerDistance=0, satoDistance = 0;
        List<List<String>> messages = new ArrayList<>();

        TopSecretResponse topSecretResponse = new TopSecretResponse();
        List<Satelites> satelites = request.getSatelites();
        
        for (int i = 0; i < satelites.size(); i++) {
            Satelites satelite = satelites.get(i);
            isValidSateliteName(satelite.getName());
            switch (satelite.getName()) {
                case "kenobi":
                    kenobiDistance = satelite.getDistance();
                    break;
                case "skywalker":
                    skywalkerDistance = satelite.getDistance();
                    break;
                case "sato":
                    satoDistance = satelite.getDistance();
                    break;
            }
            messages.add(satelite.getMessage());
        }

        Position position = this.getLocation(kenobiDistance, skywalkerDistance, satoDistance);
        String finalMessage = this.getMessage(messages);
        topSecretResponse.setPosition(position);
        topSecretResponse.setMessage(finalMessage);
        return topSecretResponse;
    }
    
    /**
     * Procesa el mensaje completo de los satelites
     * @param messages Lista de mensajes de los satelites
     * @return 
     */
    public String getMessage(List<List<String>> messages) {

        int gap = messages.get(0).size();
        List<String> finalMessage = Arrays.asList(new String[gap]);
        messages.stream().forEach(sateliteMessages -> {
            if (gap != sateliteMessages.size()) {
                throw new ResponseStatusException( HttpStatus.NOT_FOUND,"No es posible procesar el mensaje existe desface");
            }
            IntStream.range(0, sateliteMessages.size())
                .forEach((i) -> {
                    String msg = sateliteMessages.get(i);
                    if (!msg.isEmpty()) {
                        finalMessage.set(i, msg);
                    }
                });
        });
        return String.join(" ", finalMessage);
    }

    /**
     * Calcula la ubicacion de la nave emisora
     * @param r0 distancia de kenobi
     * @param r1 distancia de skywalker
     * @param r2 distancia de sato
     * @return 
     */
    public Position getLocation(double r0, double r1, double r2) {
        
        Position position = new Position();

        double x0 = kenobiLocation.get(0);
        double y0 = kenobiLocation.get(1);

        double x1 = skywalkerLocation.get(0);
        double y1 = skywalkerLocation.get(1);

        double x2 = satoLocation.get(0);
        double y2 = satoLocation.get(1);

        double dr0r1, dx, dy, d, D, h, rx, ry;
        double point2x, point2y;
        
        //Calcula deltas entre skywaler y kenobi
        dx = x1 - x0;
        dy = y1 - y0;

        //Calcula distancia entre los centros  y el total del diametro
        d = Math.sqrt(Math.pow(dy,2) + Math.pow(dx,2));
        D = d*2;
        
        //Valida si es solucionable 
        //si la distancia al centro es mayor que los puntos a evaluar o menor a su diferencia.
        if (d > (r0 + r1) || d < Math.abs(r0 - r1)) {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND, "No es posible calcular coordenadas de la nave.");
        } 

        //Se calcula la distnacia para evaluar con punto 2
        dr0r1 = (Math.pow(r0,2) - Math.pow(r1,2) + Math.pow(d,2)) / D;

        point2x = x0 + (dx * dr0r1 / d);
        point2y = y0 + (dy * dr0r1 / d);

        h = Math.sqrt(Math.pow(r0,2) - Math.pow(dr0r1,2));

        rx = -dy * (h / d);
        ry = dx * (h / d);

        //Calcula posibles puntos de intercepccion 
        double ix1 = point2x + rx;
        double ix2 = point2x - rx;
        double iy1 = point2y + ry;
        double iy2 = point2y - ry;

        //Calcula delta de intercepcion con sato para solucion1
        dx = ix1 - x2;
        dy = iy1 - y2;
        double d1 = Math.sqrt((dy * dy) + (dx * dx));

        //Calcula delta de intercepcion con sato para solucion2
        dx = ix2 - x2;
        dy = iy2 - y2;
        double d2 = Math.sqrt((dy * dy) + (dx * dx));

        //Evalua si la diferencia entre distancias es menor a la toleracia de error
        if (Math.abs(d1 - r2) < epsilon) {
            position.setX(ix1);
            position.setY(iy1);
        } else if (Math.abs(d2 - r2) < epsilon) {
            position.setX(ix2);
            position.setY(iy2);
        } else {
            throw new ResponseStatusException( HttpStatus.NOT_FOUND,"No es posible calcular las coordenadas de la nave con exactitud");
        }
        return position;
    }
}
