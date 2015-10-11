import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Created by benny on 04.10.15.
 */
public class SleepingBarber extends Thread {
    public static Semaphore kunder  = new Semaphore(0);
    public static Semaphore barber = new Semaphore(0);
    public static Semaphore tilgangSeter = new Semaphore(1);


    // Antall tilgjengelige seter
    public static final int SETER = 4;


    public static int antallSeter = SETER;








  //Tråden for kunder

    class Kunde extends Thread{

        //Dette er en unik id for hver kunde, og en boolean verdi som blir brukt for hver kunde i venteloopen
        int iD;
        boolean ikkeKlipt = true;





        public Kunde(int i ){
            iD = i;
        }

        public void run() {
            while (ikkeKlipt) {
                try {
                    tilgangSeter.acquire(); // prøver å få tilgang til seter
                    if (antallSeter > 0) { // om det er noen ledige sitteplasser
                        System.out.println("Kunde " + this.iD + " Satt seg  ned.");
                        antallSeter--; //Sitter seg ned på en stol
                        kunder.release(); // Gir beskjed til barberen at det er en kunde
                        tilgangSeter.release();
                        try {
                            barber.acquire(); //
                            ikkeKlipt = false;
                            this.get_hArKlipp(); // klipper håret
                        } catch (InterruptedException ex) {
                        }
                    } else { // Hvis det ikke er noen ledige seter
                        System.out.println("Det er ikke noen ledige seter. Kunde " + this.iD + " drar");
                        tilgangSeter.release();
                        ikkeKlipt = false; // Kunden forlater da det ikke er noen ledige seter
                    }
                } catch (InterruptedException ex) {}
            }
        }


            //Metode for å simulere at man får seg en hårklipp

            public void get_hArKlipp(){
            System.out.println("Kunde " + this.iD + " får en hårklipp");
            try{
                TimeUnit.SECONDS.sleep((long) (Math.random() * 15));
            } catch (InterruptedException ex){}

        }





    } // siste på kunde tråden




// Barber tråden
    class Barber extends Thread {

SleepingBarber sleepingBarber = new SleepingBarber();
   
    public void run(){
        while (true) { // uendelig løkke
            try{
                kunder.acquire(); // Prøver å ta til seg en kunde, hvis ingen tilgjengelig går han i dvale
                tilgangSeter.release(); //
                antallSeter++;
                barber.release(); // Barber er klar til å klippe
                tilgangSeter.release();
                this.hArKlipp(); // Klipper hår
            } catch (InterruptedException ex){}
        }
    }
    // Simulering for å kutte hår
    public void hArKlipp(){

        System.out.println("Barberen klipper hår");
        try{
            TimeUnit.SECONDS.sleep((long) (Math.random() * 15));
        }catch (InterruptedException ex){}
        System.out.println("Barberen kan gå tilbake til å sove");
    }

} // Slutten på Barber tråden


    public static void main(String args[]) {

        SleepingBarber barberButikk = new SleepingBarber();  //Starter en ny barberbutikk
        barberButikk.start();  // Starter simuleringen
    }

    public void run(){
        Barber truls = new Barber();
        truls.start();

   // Metode for å starte nye kunder

        for (int i=1; i<16; i++) {  //lager nye kunder opp til 15stk
            Kunde kunde = new Kunde(i);
            kunde.start();
            try {
                TimeUnit.SECONDS.sleep((long)(Math.random()*10));
            } catch(InterruptedException ex) {};

        }
    }







}



