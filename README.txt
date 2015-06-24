Q2Java v1.0.0 -  Quake II Game programming in Java

    Author:   Barry Pederson <bpederson@geocities.com>
    Homepage: http://www.planetquake.com/q2java
    Date:     June 17th, 2000

    Description: A DLL and Java classes that allow a QuakeII game
                 to be written in Java.

    Requirements: Win95, Win98, WinNT 4.0, Solaris/SPARC, or Linux/Intel
                  Quake II v 3.14 or higher
                  Sun Java JDK or JRE 1.1.4 or higher (JDK 1.2 recommended)

                  (Blackdown JDK 1.1.7_v1a with native threads for Linux)                  
                  
-------------------------------------

INSTALLING

    First, you need a working, registered copy of Quake II v3.14 or higher

    Secondly, Sun's Java JDK or JRE 1.1.4 or higher (JDK 1.2 works great), or
    for Linux users, Blackdown JDK 1.1.7_vla with native threads. 

    Make sure the contents of this zip file are extracted into a subdirectory
    of your Quake II directory (usually c:\quake2). 
    
    I'd recommend c:\quake2\q2java.  In the examples below, this is referred
    to as the <gamedir>.  For Linux users, your gamedir will probably be
    something like "/usr/local/games/quake2/q2java", especially if you 
    installed Q2 using RPM.

    Windows Users: Q2Java will use the registry to locate the current JRE or
                   JDK, so it shouldn't matter where it's been installed, and 
                   you don't have to worry about setting PATH or CLASSPATH 
                   environment variables.
      
    Linux Users: modify the included shell script "q2java_blackdown" to reflect
                 the paths to your JDK and Quake2 installations.  
                                        
    Assuming you unzipped this file into a game subdirectory named "q2java",
    you can start the sample Q2Java game and bring up the first map
    with this command:

        quake2 +set game q2java +map q2dm1
        
    Linux Users: use the "q2java_blackdown" shell script or something similar,
                 it's very important because it sets a few required 
                 environment variables.  You should be able to start a simple
                 game with something like
                 
                    ./q2java_blackdown +map q2dm1
                    
                 (you might have to "chmod 555 q2java_blackdown" to 
                 make it executable)


    -------------------------------------------------------------------
    ** VERY IMPORTANT **     JAVA 2 (or JDK 1.2)   ** VERY IMPORTANT **
    -------------------------------------------------------------------
    Unless you turn off Q2Java's security features completely (see below)
    JDK 1.2 users MUST create a Java Security policy file. A sample one is 
    included as "q2java.policy.sample".  You can simple copy/rename it to 
    "q2java.policy", and edit it with a text editor to make sure the directory 
    paths listed in the file are appropriate for your particular computer.

OPTIONS

    Q2Java should start and run a plain DM game without too much fuss.  But
    to do anything interesting you need to change some of the game parameters
    and there are 3 ways to do this (in order of priority, highest to lowest)
    command-line CVars, environment variables, and property files.
    
    Command-line CVars are set on the quake2 command line, for example:
    
        quake2 +set game q2java +set foobar mystuff +map q2dm1
        
    sets the CVar named "foobar" to the value "mystuff".
    
    Environment variables are set -before- quake2 is executed, in Windows
    with something like:
    
        set foobar=mystuff
        quake2 +set game q2java +map q2dm1
        
    Unix will be slightly different.
    
    Property files are common in Java, and are simply ASCII files with
    lines containing "name=value", for example:
    
        ---------------------------------------------------   
        |#
        |# Q2Java property file
        |#   lines beginning with pound signs are comments
        |#   blank lines are ignored
        | 
        |foobar=mystuff        
        |
        |include=c:\quake2\q2java\another.property
        ---------------------------------------------------   
                
    As an extension, Q2Java allows property files to "include"
    other property files, as shown in the example above. (Most
    other Java programs don't support that)
    
    A sample property file is included as "q2java.properties.sample", it's
    full of commented-out examples of things you can set within this file.
    Normally, when you first install Q2Java you'd want to copy/rename this
    to "q2java.properties" and use a plain text editor to modify it as needed.
    
    
    
    The options available for Q2Java are:
    
    Property File Name
    
        Q2Java will attempt to open a file by this name, to 
        look for various game options
               
        Default Value: <gamedir>\q2java.properties
        
        Overridden with: CVar "q2java_properties"
        Example: quake2 +set game q2java +set q2java_properties c:\my.properties +map q2dm1
        
    
    Java VM DLL Name (Win32 only)
    
        Normally, Q2Java will automatically locate the name of the 
        Java VM DLL using the registry, but you can override this
        and explicitly specify a DLL name.  This is handy if you have
        multiple VMs installed on your computer and want to 
        use a particular one, or you want to use a VM that's not
        automatically detected by the Q2Java DLL (such as Microsoft's)
            
        Override with: environment variable "q2java_vmdll"    
        Example: set q2java_vmdll=c:\jdk\bin\javai.dll

        Override with: property file entry "q2java_vmdll"    
        Example: q2java_vmdll=c:\jdk\bin\javai.dll
        
        
    Java Classpath
    
        Q2Java will automatically generate a classpath on Win32 and
        Solaris platforms.  But under Linux this -must- be explicitly
        set, and the included shell script "linux_q2java" does this
        for you (but you have to edit it to reflect your particular setup)   
        
        Under JDK 1.1.x, the CLASSPATH must contain whatever entries
        are required by the VM itself, such as "c:\jdk\lib\classes.zip"   
        or the equivalent on your particular platform.
    
        (JDK 1.1.x only) Override with: environment variable "Q2JAVA_CLASSPATH"
        Example: set Q2JAVA_CLASSPATH=c:\jdk\lib\classes.zip;c:\quake2\q2java\classes;
        
        Override with property file entry "java.class.path"
        Example: java.class.path=/usr/jdk/lib/classes.zip:/usr/quake2/q2java/classes

    Debug Log
    
        If you're having trouble getting Q2Java running, you might
        try turning on the debug log, which creates a text file 
        with a fairly detailed trace of what's going on inside the DLL.
        
        Normally this is disabled, but if it's enabled with a value of '1',
        the DLL will use the default debuglog filename 
        "<gamedir>\q2java_debug.log", or you can specify your own filename
        
        Enable with: CVar "q2java_debugLog" (note the uppercase 'L')
        Example: quake2 +set q2java_debugLog 1   (use default name)
                 quake2 +set q2java_debugLog c:\my.log (specified name)
                 
        Enable with: property file entry "q2java_debuglog" (not case sensitive)
        Example: q2java_debuglog=1
                 q2java_debuglog=c:\my.log                 
         
         
    Security Settings
    
        Q2Java attempts to protect against rogue game code accessing 
        resources on your machine that it has no business touching.
        
        Under JDK 1.1.x, Security comes in three levels
        
            Level 0 - no security at all everything is allowed 
                     (could be dangerous)
                     
            Level 1 - Allows file read access the the game subdirectory 
                      (usually c:\quake2\q2java) and file write access to  
                      a subdirectory of the game directory named "sandbox" 
                      (usually c:\quake2\q2java\sandbox).
            
                      Allows network access to IP ports numbered >1023

            Level 2 - (DEFAULT) Same as security level 1 but without network 
                      access permitted.

        Under JDK 1.2, Security is controlled by a Java Security Policy file,
        which are simple ASCII files that explicitly list which machine 
        resources a game is allowed to access.  A sample is included which is
        pretty equivalent to JDK 1.1.x security level 2.  
        
        When JDK 1.2 is detected, Q2Java by default attempts to use a 
        policy file named <gamedir>\q2java.policy, but that filename can 
        be overridden.  JDK 1.2 security can be disabled entirely by
        specifying "0" as the security option
        
        Override with: CVar "q2java_security"
        Example (JDK 1.1.x): quake2 +set q2java_security 1
                (JDK 1.2)  : quake2 +set q2java_security c:\my.policy
                (Any JDK)  : quake2 +set q2java_security 0
        
        Override with: policy file entry "q2java_security"
        Example (Any JDK): q2java_security=0
                (JDK 1.2): q2java_security=c:\my.policy

    Gamelets

        Q2Java game code is broken into modules called "Gamelets", and at 
        startup we need to know which modules to load.  The default is
        to load a gamelet called "q2java.baseq2.Deathmatch" - which implements 
        plain DM.
        
        Override with: CVar "gamelets" and a a list of modules
                       separated with one of these characters: + , ; / \
                       
        Example: q2java +set game q2java +set gamelets q2java.baseq2.Deathmatch+barryp.misc.GlubGlub

            modules are loaded in the order shown, so in the example above 
            "q2java.baseq2.Deathmatch" is loaded first, then "barryp.misc.GlubGlub".  
            Aliases can also be specified inbetween '[' and ']', for example
        
                +set gamelets q2java.baseq2.Deathmatch[dm]+barryp.misc.GlubGlub
            
            Which is convenient because later in the game, you can 
            refer to the q2java.baseq2.Deathmatch gamelet as "dm".  If you don't
            specify an alias, the game generates one based on the gamelet 
            classname, for the barryp.misc.GlubGlub gamelet listed above
            it would probably be "GlubGlub" (generally aliases are case-insensitive)


        Startup File
        
            If a "gamelet" cvar doesn't exist, then Q2Java looks for an 
            XML startup file named "q2java.startup" in the  Q2Java game 
            directory.  The name may be changed by setting the CVar
            "q2java_startup" on the commandline, for example:
        
               +set q2java_startup foobar.startup
           
            would cause Q2Java to try reading "foobar.startup".  
            
            The startup file is in XML format, and looks like:
        
              <startup>
                <gamelet class="q2java.baseq2.Deathmatch" name="dm"/>
                
                <gamelet class="barryp.misc.GlubGlub">
                  <!-- this is something unique to startup files..the 
                    ability to include arbitrary XML tags that will
                    be passed to the gamelet's constructor -->
                  <param name="message" value="*choke* *gasp*"/>
                </gamelet>
                
                <!-- also, startup files may contain tags that set CVars -->
                <cvar name="cvarname" value="my value"/>
              </startup>

        Lastly, if a startup file isn't found, then Q2Java looks for Java System 
        properties that would be specified in the property file as
        q2java.gamelet.<n> where <n> starts at '1' and goes up                       
        Example:
        
                q2java.gamelet.1=q2java.baseq2.Deathmatch dm
                q2java.gamelet.2=barryp.misc.GlubGlub
        
        This is equivalent to the CVar method listed above, with
        "q2java.baseq2.Deathmatch" being loaded first with the alias "dm", 
        then "barryp.misc.GlubGlub"


    Q2Java includes several gamelets you may want to try, here are a few
    of the more interesting ones.  
    
        q2java.ctf.CTF - a full implementation of Threewave CTF.
                    
        q2java.ctf.CTFTechs - Just the powerups from Threewave CTF.

        barryp.telnet.TelnetServer - a Telnet Server running inside Quake2, lets
            telnet or mud clients connect and chat with players.  Also
            allows remote administration of game server.  Check "extras.txt"
            file for more details
            
        barryp.flashgrenade.FlashGrenades - replaces handgrenade with 
            one that causes temporary blindness   
            
        barryp.rocketmania.GameModule - replaces hand-blaster with 
            hand-rocketlauncher, makes regular rocketlauncher much more powerful.                      
            
        barryp.paranoia.Paranoia
        barryp.bountyhunters.BountyHunters - two original games, try these with 
            some combination of flashgrenade, rocketmania, or ctftechs
            for interesting variations.

    More detailed module documentation can be found in the "extras.txt" file.                                


JAVADOCS
    
    The sourcecode has quite a few JavaDoc comments, and you can generate
    documentation using normal JavaDoc tools.  For Win32 users who have 
    a JDK loaded, you can run the "make-javadocs.bat" batch file to quickly
    generate docs.

MODIFYING THE JAVA CODE

    If you want to modify and recompile the sample game code, here's what I'd
    recommend:

        update your classpath environment variable to include the classes
        subdirectory, for example:

            set classpath=c:\jdk\lib\classes.zip;c:\quake2\q2java\classes;

    At this point, you should be able to run the development tools in the
    JDK to compile any changes to the classes.  As a first test, try
    modifying the class "baseq2.WelcomeMessage" and recompiling: to do this
    use a text editor to modify

        c:\quake2\q2java\classes\q2java\baseq2\WelcomeMessage.java

    Once you've saved your changes, recompile with the command:

        javac WelcomeMessage.java

    Run the game and see if your modification worked.

    Don't make changes to the classes in the q2java package, since the DLL
    expects things laid out in a certain way.  

---------------------------

Knock yourselves out

    Barry <bpederson@geocities.com>
