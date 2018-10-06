<h4 align=center>
    <img src="https://i.imgur.com/rIDSSyw.png"><br>
    :checkered_flag: Download, initialize, and run an IPFS node using only Java/Kotlin :checkered_flag:
</h4><hr>

### Usage

- [Kotlin example](https://github.com/RHazDev/IPFS-Daemon/blob/master/test/KotlinTest.kt)

      IPFSDaemon().apply{
        download()
        start()
      }

- [Java example](https://github.com/RHazDev/IPFS-Daemon/blob/master/test/JavaTest.java)

      IPFSDaemon ipfsd = new IPFSDaemon();
      ipfsd.download();
      ipfsd.start();

### Implement it
    
- Gradle: add this to your build.gradle

      repositories {
          maven { url "https://mymavenrepo.com/repo/NIp3fBk55f5oF6VI1Wso/" }
      }
      
      dependencies {
          compile 'fr.rhaz.ipfs:ipfs-daemon:1.0.8'
      }
      
      jar {
          from { configurations.compile.collect { it.isDirectory() ? it : zipTree(it) } }
      }

- Maven: add this to your pom.xml

      <repositories>
        <repository>
            <id>rhazdev</id>
            <url>https://mymavenrepo.com/repo/NIp3fBk55f5oF6VI1Wso/</url>
        </repository>
      </repositories>

      <dependencies>
        <dependency>
            <groupId>fr.rhaz.ipfs</groupId>
            <artifactId>ipfs-daemon</artifactId>
            <version>1.0.8</version>
            <scope>compile</scope>
        </dependency>
      </dependencies>
