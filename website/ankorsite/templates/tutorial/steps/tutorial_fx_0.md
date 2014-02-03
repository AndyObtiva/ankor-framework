### The Application

In this tutorial we'll be building a simple Ankor application.
We'll explain how to setup Ankor, write the first
view model and bind the model to your UI components. While Ankor is generally independent of the transport layer, 
in this tutorial we'll be using WebSockets. They are supported by both JavaFX and web browsers 
and Ankor has some utility classes that make using WebSockets easy.

This his how the app will look like when we are done:

![fx-step-0-1](/static/images/tutorial/fx-step-0-1.png)

This might looks familiar to you (it's the infamous todo app form [TodoMVC](http://todomvc.com/)).
However it is fully written and styled using JavaFX.

### Before you start

Please make sure that all software components are installed properly. Especially check the Java FX section to make sure
that JavaFX is on the classpath.

<div class="tabbable ">
    <ul class="nav nav-tabs">
        <li class="active"><a href="#tab1" data-toggle="tab">Java</a></li>
        <li><a href="#tab2" data-toggle="tab">Maven</a></li>
        <li><a href="#tab3" data-toggle="tab">Java FX</a></li>
        <li><a href="#tab4" data-toggle="tab">Git</a></li>
    </ul>
    <div class="tab-content">
        <div class="tab-pane active" id="tab1">
            <p>JDK 1.7, download from <a href="http://www.oracle.com/technetwork/java/javase/downloads/index.html">here</a>.</p>
            <p>Make sure that<p></p>
            <ul>
                <li>JAVA_HOME exists in your user variables (JDK installation directory)</li>
                <li>and that %JAVA_HOME%\bin is in your Path environment variable</li>
            </ul>
            <p>Open command line and test</p>
            <pre><code>java -version</code></pre>
        </div>
        <div class="tab-pane" id="tab2">
            <p>Maven 3.0.5 or higher, download from <a href="http://maven.apache.org/download.cgi">here</a>.</p>
            <p>Make sure that<p></p>
            <ul>
                <li>MAVEN_HOME exists in your user variables (JDK installation directory)</li>
                <li>and that %MAVEN_HOME%\bin is in your Path environment variable</li>
            </ul>
            <p>Open command line and test</p>
            <pre><code>mvn -version</code></pre>
        </div>
        <div class="tab-pane" id="tab3">
            <p>To get JavaFX on the Classpath execute</p>
            <pre><code>mvn com.zenjava:javafx-maven-plugin:2.0:fix-classpath</code></pre>
            <p>On unix systems you may need <code>sudo mvn ...</code></p>
            <p>If you are interested in why this is needed read <a href="http://zenjava.com/javafx/maven/fix-classpath.html">"Getting JavaFX on the Classpath"</a></p>
        </div>
        <div class="tab-pane" id="tab4">
            <p>Install Git, download from <a href="http://git-scm.com/download">the Git site</a>.</p>
        </div>
    </div>
</div>

### Get the code

Clone the git repository from:

    git clone https://github.com/ankor-io/ankor-todo.git

The folder ankor-todo is empty. To get the first tutorial step, checkout branch `fx-step-0`.
This is how you may switch between tutorial steps later.

    cd ankor-todo
    git checkout -f fx-step-0

Now you got a maven project based on two modules:

    client : Todo Sample - JavaFX Client
    server : Todo Sample - Server