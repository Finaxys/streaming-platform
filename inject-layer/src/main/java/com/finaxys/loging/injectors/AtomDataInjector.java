package com.finaxys.loging.injectors;

import com.finaxys.utils.InjectLayerException;
import java.util.List;

public abstract class AtomDataInjector {

    abstract public void closeOutput() throws InjectLayerException;

    abstract public void createOutput() throws InjectLayerException;

    abstract public void send(String message) throws InjectLayerException;

    public void send(List<String> list) throws InjectLayerException {
        for (String message : list)
            this.send(message);
    }
}
