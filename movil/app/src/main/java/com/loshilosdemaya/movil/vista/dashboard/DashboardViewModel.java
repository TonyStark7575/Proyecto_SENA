package com.loshilosdemaya.movil.vista.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.loshilosdemaya.movil.modelo.ResumenDashboard;
import com.loshilosdemaya.movil.repositorio.AuthRepositorio;
import com.loshilosdemaya.movil.repositorio.Resultado;

/** Datos del dashboard. Guarda el último resumen para no perderlo al girar el celular. */
public class DashboardViewModel extends ViewModel {

    private final AuthRepositorio authRepositorio = new AuthRepositorio();
    private final MediatorLiveData<Resultado<ResumenDashboard>> resumen = new MediatorLiveData<>();
    private LiveData<Resultado<ResumenDashboard>> consultaActual;

    public LiveData<Resultado<ResumenDashboard>> getResumen() {
        return resumen;
    }

    public void recargarResumen() {
        if (consultaActual != null) {
            resumen.removeSource(consultaActual);
        }
        consultaActual = authRepositorio.consultarResumen();
        resumen.addSource(consultaActual, resumen::setValue);
    }

    public LiveData<Resultado<Void>> cerrarSesion() {
        return authRepositorio.cerrarSesion();
    }
}
