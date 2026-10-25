package com.gamerpc;

import java.util.function.Consumer;

/** Puente entre el código común y el cliente (para abrir la pantalla de la PC). */
public class PCHooks {
    /** El cliente lo reemplaza al iniciar. El parámetro indica si hay Internet. */
    public static Consumer<Boolean> opener = internet -> {};
}
