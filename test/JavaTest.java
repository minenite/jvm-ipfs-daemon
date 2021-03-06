import fr.rhaz.ipfs.IPFSDaemon;
import static fr.rhaz.ipfs.IPFSDaemonKt.getUnit;
import static fr.rhaz.ipfs.IPFSDaemonKt.listener;

import java.io.File;

public class JavaTest {
    public static void main(String... args){

        // We will use go-ipfs 0.4.12 and install it in "/test" folder
        IPFSDaemon ipfsd = new IPFSDaemon("0.4.12", new File("test"));

        // We will tell the user if the download starts
        // You can add as many listeners as you want
        ipfsd.getListeners().getOnDownloading().add(
            listener(() -> {
                System.out.println("Downloading go-ipfs 0.4.12...");
            })
        );

        // Download the file if it does not exists
        // /!\ You may need to run it asynchronously
        ipfsd.download();

        // Print all output, but replace "Daemon is ready" with "IPFS is ready!"
        ipfsd.setCallback(
            listener((process, msg) -> {
                if(msg.equals("Daemon is ready"))
                    msg = "IPFS is ready!";
                System.out.println(msg);
            })
        );

        // Init, start, and output to the callback
        // /!\ You may need to run it asynchronously
        ipfsd.start();
    }
}
