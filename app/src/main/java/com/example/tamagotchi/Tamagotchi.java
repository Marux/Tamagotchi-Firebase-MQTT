package com.example.tamagotchi;
import java.util.ArrayList;
import java.util.List;
public class Tamagotchi implements TamagotchiListener{
    private List<TamagotchiListener> listeners = new ArrayList<>();

    private String Nombre;
    private int Energia;
    private int Felicidad;
    private int SaludMental;
    private int Hambre;
    private int Dinero;
    private int Higiene;
    private int Vejiga;
    private boolean Vivo;

    public Tamagotchi(String nombre)
    {
        Nombre = nombre;
        Energia = 50;
        Felicidad = 50;
        SaludMental = 50;
        Hambre = 50;
        Dinero = 0;
        Higiene = 50;
        Vejiga = 0;
        Vivo = true;
    }

    @Override
    public void onTamagotchiAction(String action) {
        // Realiza acciones según la acción recibida
        switch (action) {
            case "MostrarEstado":
                mostrarEstado();
                break;
            case "Alimentar":
                alimentar();
                break;
            case "Jugar":
                jugar();
                break;
            case "PasarTiempo":
                pasarTiempo();
                break;
            case "MandarloChambear":
                mandarloChambear();
                break;
            case "Bailar":
                bailar();
                break;
            case "Salir":
                salir();
                break;
            default:
                // Manejar cualquier otro caso si es necesario
        }
    }

    private void mostrarEstado() {
        // Implementa la lógica para mostrar el estado
        // Puedes imprimir el estado o actualizar la interfaz de usuario
        notifyObservers("Nombre: " + Nombre + " " +
                "Energía: " + Energia + " " +
                "Felicidad: " + Felicidad + "\n" +
                "Salud Mental: " + SaludMental + " " +
                "Hambre: " + Hambre + " " +
                "Dinero: " + Dinero + " " +
                "Higiene: " + Higiene + " " +
                "Vejiga: " + Vejiga);
        System.out.println("Estado actual del Tamagotchi");
        System.out.println("Nombre: " + Nombre);
        System.out.println("Energía: " + Energia);
        System.out.println("Felicidad: " + Felicidad);
        System.out.println("Salud Mental: " + SaludMental);
        System.out.println("Hambre: " + Hambre);
        System.out.println("Dinero: " + Dinero);
        System.out.println("Higiene: " + Higiene);
        System.out.println("Vejiga: " + Vejiga);
    }

    private void alimentar() {
        if (Vivo)
        {
            System.out.println(Nombre + " está comiendo. ¡Yum, yum!");
            Energia += 20;
            Felicidad += 10;
            SaludMental +=15;
            Hambre +=20;
            Dinero -=15;
            Vejiga +=15;
            actualizarEstado();
        }
        else
        {
            System.out.println(Nombre + "ya no está vivo. No puedes alimentarlo.");
        }
    }

    private void jugar() {
        if (Vivo)
        {
            //Console.WriteLine($"{Nombre} está jugando. ¡Divertido!");
            Energia -= 10;
            Felicidad += 30;
            SaludMental += 15;
            Hambre -= 5;
            Higiene -= 5;
            actualizarEstado();
        }
        else
        {
            //Console.WriteLine($"{Nombre} ya no está vivo. No puedes jugar con él.");
        }    }

    private void pasarTiempo() {
        if (Vivo)
        {
            //Console.WriteLine($"Pasa el tiempo para {Nombre}...");
            Energia -= 10;
            Felicidad -= 5;
            SaludMental -=5;
            Hambre -=5;
            Higiene -=5;
            actualizarEstado();

            if (Energia <= 0 || Felicidad <= 0)
            {
                Vivo = false;
                //Console.WriteLine($"{Nombre} se ha cansado y triste. Ha fallecido.");
            }
        }
        else
        {
            //Console.WriteLine($"{Nombre} ya no está vivo. No hay tiempo que pasar.");
        }    }

    private void mandarloChambear() {
        if(Vivo)
        {
            //Console.WriteLine($"Ve a Chambear {Nombre}...");
            Energia -= 10;
            Felicidad -= 15;
            SaludMental -=15;
            Hambre -=5;
            Dinero += 30;
            Higiene -=5;
            actualizarEstado();

            if (Energia <= 0 || Felicidad <= 0 || SaludMental <=0)
            {
                Vivo = false;
                //Console.WriteLine($"{Nombre} Ha sufrido explotación laboral y se estresó. Ha fallecido.");
            }
        }
    }

    private void bailar() {
        Energia -= 10;
        actualizarEstado();

        //Console.WriteLine($"{Nombre} está bailando...");

        for (int i = 0; i < 5; i++)
        {
            //Console.Clear(); // Limpiar la consola para la animación
            //Console.WriteLine($"{Nombre} está bailando...");

            // Alternar entre dos posiciones de baile
            if (i % 2 == 0)
            {
                //Console.WriteLine("    /|,,/|");
                //Console.WriteLine("    (>'.')> ");
                //Console.WriteLine("   ~( \\\\ )");
            }
            else
            {
                //Console.WriteLine("      |\\,,|\\");
                //Console.WriteLine("     <('.'<) ");
                //Console.WriteLine("      ( // )~");
            }
            //Thread.Sleep(500); // Pausa para hacer más lenta la animación
        }
        //Console.Clear(); // Limpiar la consola al final de la animación
        mostrarEstado(); // Mostrar el estado después de bailar
    }

    private void salir() {
        // Implementa la lógica para salir (cerrar la app)
    }

    private void actualizarEstado() {
        Energia = Math.max(0, Math.min(Energia, 1000));
        Felicidad = Math.max(0, Math.min(Felicidad, 1000));
        SaludMental = Math.max(0, Math.min(SaludMental, 1000));
        Hambre = Math.max(0, Math.min(Hambre, 1000));
        Dinero = Math.max(0, Math.min(Dinero, 1000));
        Higiene = Math.max(0, Math.min(Higiene, 1000));
        Vejiga = Math.max(0, Math.min(Vejiga, 1000));
        mostrarEstado();
    }
    @Override
    public void addObserver(TamagotchiListener listener) {
        listeners.add(listener);
    }

    public void removeObserver(TamagotchiListener listener) {
        listeners.remove(listener);
    }

    private void notifyObservers(String action) {
        for (TamagotchiListener listener : listeners) {
            listener.onTamagotchiAction(action);
        }
    }


}
