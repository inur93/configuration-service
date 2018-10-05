

## Usage ##

First you need to add a `settings.xml` for your maven repository to get access to the private repo on bitbucket.
The file should be located here: `%USERPROFILE%/.m2/`. If the file does not exist just create it.
The file should look something like this:  
```xml
<settings>
    <servers>  
        <server>
            <id>dk.agenia.maven.repo</id>
            <username>{bitbucket_username}</username>
            <password>{bitbucket_password}</password>
        </server>
    </servers>
</settings>
```

Next you can use the maven repository in your maven project by simply adding the following xml in the poms `<project>` tag.
```xml
<repositories>
    <repository>
        <id>dk.agenia.maven.repo</id>
        <name>dk.agenia.maven.repo</name>
        <releases>
            <enabled>true</enabled>
        </releases>
        <snapshots>
            <enabled>false</enabled>
        </snapshots>
        <url>https://bitbucket.org/team_agenia/maven_repo/raw/releases</url>
    </repository>
</repositories>
```

Then you can add the dependency as any other like this in the `<dependencies>` tag (which is under `<project>` tag):

```xml
<dependency>
    <groupId>dk.agenia</groupId>
    <artifactId>configuration-service</artifactId>
    <version>1.1.0</version>
</dependency>
```

### Configuration ###
Per default the configuration service looks for a file named `C:\configs\default\config.properties`.
To override this configuration create a config file in resources folder named `configservice.properties` with the key `LOCATION`.

Additionally you can add a prefix on all configurations being read. This can be useful if you for example have configuration for multiple services in same file.
Then for each service you can add a unique prefix for each service avoiding any overlap between services.
To achieve this add a value for the key `PREFIX` in the same `configservice.properties` file as before.

If deploying to Heroku or other servers where you are not able to define a configuration on site, you can set the `USE_ENVIRONMENT_VARIABLES` to `true`
which will make the configservice to read configurations from system environment variables.

An example project could look like this:  


+ projectname  
    - src  
    - main  
        - java  
        - resources  
            - `configservice.properties`
    - pom.xml

where the content of `configservice.properties` is:  


```properties
LOCATION=c:/configs/projectname/config.properties  
PREFIX=PROJECTNAME
USE_ENVIRONMENT_VARIABLES=false
```

## Contribute ##

### Pre conditions ###
- You need to install putty-gen or another tool for generating the public/private ssh key.
Putty-gen can be downloaded [here](https://www.chiark.greenend.org.uk/~sgtatham/putty/latest.html).
- You use git bash for the required commands. If not already installed get it [here](https://git-scm.com/download/win) 


### Setup ###
1. Open puttygen.
2. Choose type of key to generate fx RSA.
3. Press generate and follow the instructions (You should move your mouse around to generate randomness)
4. Save the private key in `%USERPROFILE%/.ssh` as `id_rsa` (important: filename should not have any extension)
5. Log onto bitbucket and click on your avatar on lower left corner and select bitbucket settings.
6. Select 'SSH keys' under 'Security'.
7. Click 'Add key' and copy the public key from puttygen (make sure you copy the whole thing)
8. Optionally provide a label for the key and then click 'Add key'

#### Auto launch ssh-agent ####
Before you can deploy the maven repository the ssh-agent needs to be started and the private key loaded.
To see if the key is loaded run `ssh-add -l` in git bash (the directory you run from is not important here).
If no identity available you will get 'the agent has no identities'. 
Otherwise you will get a list of identities showing number of bits in the generated key and path to it's file as well as some other information.

If the identity has not been loaded run `ssh-add`. This will automatically add the `id_rsa` file located in `%USERPROFILE%/.ssh`.
Alternatively you can run `ssh-add <path-to-file>`.
If this does not work it could mean that the ssh-agent is not running in which case you will have to run ``eval `ssh-agent` `` before `ssh-add`.

This can also be done automatically by creating a `.profile` file in `%USERPROFILE%` with the following content:
```
env=~/.ssh/agent.env

agent_load_env () { test -f "$env" && . "$env" >| /dev/null ; }

agent_start () {
    (umask 077; ssh-agent >| "$env")
    . "$env" >| /dev/null ; }

agent_load_env

# agent_run_state: 0=agent running w/ key; 1=agent w/o key; 2= agent not running
agent_run_state=$(ssh-add -l >| /dev/null 2>&1; echo $?)

if [ ! "$SSH_AUTH_SOCK" ] || [ $agent_run_state = 2 ]; then
    agent_start
    ssh-add
elif [ "$SSH_AUTH_SOCK" ] && [ $agent_run_state = 1 ]; then
    ssh-add
fi

unset env
```

This will start the ssh-agent and add the identity automatically.
When you start git bash you will be prompted for the private key password.

### Deploy ###
1. <b>remember</b> to update the version number in pom file.
2. Open git bash where the `pom.xml` is located.
3. Run `mvn clean deploy`.
4. If you have set a password for your private key you will be prompted for this 2-3 times.
5. That's it.