package FileCatalog.client.view;

public class StdOut {

    synchronized void print(String output) {
        System.out.print(output);
    }
    synchronized void println(String output) {
        System.out.println(output);
    }

}
