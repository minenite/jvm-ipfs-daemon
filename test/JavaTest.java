import fr.rhaz.ipfs.IPFSDaemon;
import kotlin.Unit;

import java.io.File;

public class JavaTest {
    public static void main(String... args){

        // We will use go-ipfs 0.4.12 and install it in "/test" folder
        IPFSDaemon ipfsd = new IPFSDaemon("0.4.12", new File("test"));

        // We will tell the user if the download starts
        // You can add as many listeners as you want
        ipfsd.getListeners().getOnDownloading().add(() -> {
            System.out.println("Downloading go-ipfs 0.4.12...");
        });

        // Download the file if it does not exists
        // /!\ You may need to run it asynchronously
        ipfsd.download();

        // Print all output, but replace "Daemon is started" with "IPFS is ready!"
        ipfsd.setCallback(msg -> {
            if(msg.equals("Daemon is started"))
                msg = "IPFS is ready!";
            System.out.println(msg);
            return Unit.INSTANCE; // You need to return Unit because Kotlin cannot do it automatically
        });

        // Init, start, and output to the callback
        // false: if you do not want output
        // /!\ You may need to run it asynchronously
        ipfsd.getStart().invoke(true);
    }
}
