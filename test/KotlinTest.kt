import fr.rhaz.ipfs.IPFSDaemon
import java.io.File

fun main(args: Array<String>){

    // We will use go-ipfs 0.4.13 and install it in "/test" folder
    val ipfsd = IPFSDaemon("0.4.13", File("test"))

    // We will tell the user if the download starts
    ipfsd.listeners.onDownloading.add{
        println("Downloading go-ipfs 0.4.13...")
    }


    // Download the file if it does not exists
    // /!\ You may need to run it asynchronously
    ipfsd.download()

    // Print all output, but replace "Daemon is ready" with "IPFS is ready!"
    ipfsd.callback = { process, msg ->
        println(
            if(msg == "Daemon is ready") "IPFS is ready!"
            else msg
        )
    }

    // Init, start, and output to the callback
    // /!\ You may need to run it asynchronously
    ipfsd.start()

}