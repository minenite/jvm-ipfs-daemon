# IPFS daemon for JVM
Download, initialize, and run an IPFS node using only Java/Kotlin

### Usage

- [Kotlin example](https://github.com/RHazDev/IPFS-Daemon/blob/master/src/KotlinTest.kt):

      IPFSDaemon().apply{
        download()
        start(true)
      }

- [Java example](https://github.com/RHazDev/IPFS-Daemon/blob/master/src/JavaTest.java)

      IPFSDaemon ipfsd = new IPFSDaemon();
      ipfsd.download();
      ipfsd.start(true);

### Implement it
    
- Gradle: put the .JAR file in a "lib" folder and add this to your build.gradle

      repositories {
          flatDir { dirs 'lib' }
      }
      
      dependencies {
          compile name: 'ipfs-daemon-1.0'
      }
      
      jar {
          from { configurations.compile.collect { it.isDirectory() ? it : zipTree(it) } }
      }

- Maven: put the .JAR file in a lib folder and add this to your pom.xml

      <dependencies>
        <dependency>
            <groupId>fr.rhaz.ipfs</groupId>
            <artifactId>ipfs-daemon</artifactId>
            <version>1.0</version>
            <scope>system</scope>
            <systemPath>${basedir}/lib/ipfs-daemon-1.0.jar</systemPath>
        </dependency>
      </dependencies>
