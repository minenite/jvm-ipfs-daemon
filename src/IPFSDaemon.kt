package fr.rhaz.ipfs

import org.apache.commons.compress.archivers.tar.TarArchiveInputStream
import org.apache.commons.lang3.ArrayUtils
import org.apache.commons.lang3.SystemUtils.*
import java.io.*
import java.net.URL
import java.nio.channels.Channels
import java.util.zip.GZIPInputStream
import java.util.zip.ZipInputStream


operator fun File.get(path: String) = File(this, path)

fun main(args: Array<String>) = IPFSDaemon().apply{download(); start(true)}.let{Unit}

val unit = Unit

open class IPFSDaemon(val version: String, val path: File) {

    constructor(): this("0.4.17", File("."))

    open var bin: File = when{
        IS_OS_WINDOWS -> path["ipfs.exe"]
        else -> path["ipfs"]
    }

    open var store = path[".ipfs"]

    open var daemon: Process? = null

    init{ Runtime.getRuntime().addShutdownHook( Thread{daemon?.destroy()} )}

    fun download() = download(version, bin)
    fun download(version: String, bin: File){

        if(bin.exists()) return listeners.onDownloaded.call()

        val err = {throw Exception("System not supported")}

        val (os, type) = when{
            IS_OS_WINDOWS -> listOf("windows", ".zip")
            IS_OS_LINUX -> listOf("linux", ".tar.gz")
            IS_OS_FREE_BSD -> listOf("freebsd", ".tar.gz")
            IS_OS_MAC -> listOf("darwin", ".tar.gz")
            else -> err()
        }

        val arch = when(OS_ARCH){
            "amd64" -> "amd64"
            "i386", "x86", "x86_64", "ia64" -> "386"
            "arm" -> "arm"
            else -> err()
        }

        val archive = bin.parentFile["go-ipfs$type"]
        val url = "https://dist.ipfs.io/go-ipfs/v$version/go-ipfs_v${version}_$os-$arch$type"

        listeners.onDownloading.call()
        download(URL(url), archive)

        var archivepath = "go-ipfs/ipfs"
        if(IS_OS_WINDOWS) archivepath += ".exe"

        when(type){
            ".tar.gz" -> extractTarGz(archive, archivepath, bin)
            ".zip" -> extractZip(archive, archivepath, bin)
        }
        listeners.onDownloaded.call()
    }

    fun process(vararg args: String) = process(bin, store, *args)

    open var start: (gobble: Boolean) -> Unit = start@{
        val old = daemon
        if(old != null && old.isAlive) return@start

        store["repo.lock"].delete()

        listeners.onInitializing.call()
        process("init").waitFor()

        listeners.onStarting.call()
        val daemon = process("daemon")
        this.daemon = daemon

        if(it) gobble(daemon)
        daemon.waitFor()
    }

    open var callback: (Process, String) -> Unit = {_, it -> println(it)}

    fun gobble(process: Process){
        Thread{gobble(process.inputStream, process, callback)}.start()
        Thread{gobble(process.errorStream, process, callback)}.start()
    }

    open val listeners = Listeners()
    fun MutableList<Runnable>.call() = forEach{it.run()}
    inner class Listeners{
        val onDownloading = mutableListOf<Runnable>()
        val onDownloaded = mutableListOf<Runnable>()
        val onInitializing = mutableListOf<Runnable>()
        val onStarting = mutableListOf<Runnable>()
    }

}

fun download(url: URL, file: File){
    val rbc = Channels.newChannel(url.openStream())
    val fos = FileOutputStream(file)
    fos.channel.transferFrom(rbc, 0, Long.MAX_VALUE)
    fos.close()
}

fun gobble(process: Process, callback: (Process, String) -> Unit){
    Thread{gobble(process.inputStream, process, callback)}.start()
    Thread{gobble(process.errorStream, process, callback)}.start()
}

fun gobble(input: InputStream, process: Process, callback: (Process, String) -> Unit) {
    val reader = BufferedReader(InputStreamReader(input))
    while(true) callback(process, reader.readLine()?:return)
}

fun process(bin: File, store: File, vararg args: String): Process {
    val cmd = ArrayUtils.insert(0, args, bin.path)
    return Runtime.getRuntime().exec(cmd, arrayOf("IPFS_PATH=${store.absolutePath}"))
}

fun extractTarGz(arch: File, path: String,  destination: File) =
    TarArchiveInputStream(GZIPInputStream(FileInputStream(arch))).use {
        while (true) {
            val te = it.nextEntry ?: return
            if (te.name == path){
                val out = FileOutputStream(destination)
                it.copyTo(out, DEFAULT_BUFFER_SIZE)
                out.close(); return
            }
        }
    }

fun extractZip(zip: File, path: String, destination: File) =
    ZipInputStream(FileInputStream(zip)).use {
        while(true) {
            val ze = it.nextEntry ?: return
            if (ze.name == path) {
                val out = FileOutputStream(destination)
                it.copyTo(out, DEFAULT_BUFFER_SIZE)
                out.close(); return
            }
        }
    }