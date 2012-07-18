<map version="0.9.0">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<attribute_registry SHOW_ATTRIBUTES="hide">
<attribute_name NAME="beenNodeType" RESTRICTED="true">
<attribute_value VALUE="CHANGELOG"/>
<attribute_value VALUE="NOTE"/>
<attribute_value VALUE="TODO"/>
</attribute_name>
<attribute_name NAME="isNote" RESTRICTED="true">
<attribute_value VALUE=""/>
<attribute_value VALUE="false"/>
<attribute_value VALUE="true"/>
</attribute_name>
</attribute_registry>
<node BACKGROUND_COLOR="#ffff00" COLOR="#000000" CREATED="1228084154800" ID="Freemind_Link_1099640338" MODIFIED="1229296072098" STYLE="bubble" TEXT="cz.cuni.mff.been">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="20"/>
<node COLOR="#0033ff" CREATED="1228084269007" ID="_" MODIFIED="1229296080845" POSITION="right" STYLE="bubble" TEXT="apps">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228140658219" ID="Freemind_Link_1238171130" MODIFIED="1229296080863" STYLE="fork" TEXT="ApplicationException">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228152228402" ID="ID_1407484139" MODIFIED="1228165935349" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Exceptions thrown by <font face="DejaVu Sans Mono">application</font> and <font face="DejaVu Sans Mono">plugin</font> framework.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_1532405924" MODIFIED="1228165935254" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      There were lots of unnecessary&#160;<font face="DejaVu Sans Mono">throws</font> declarations. The exception hierarchy should be checked.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228140672984" ID="Freemind_Link_1167397896" MODIFIED="1229296080869" STYLE="fork" TEXT="BeenPluginDescriptor">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228143456473" ID="ID_1021204985" MODIFIED="1228165935346" STYLE="bubble" TEXT="A very simple piece of plugin metadata. Used mainly by the GUI.">
<font NAME="DejaVu Sans Condensed" SIZE="12"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228140492134" ID="Freemind_Link_64179715" MODIFIED="1229296080889" STYLE="fork" TEXT="JPFPluginManager">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228145752001" ID="ID_1871583907" MODIFIED="1228165935329" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is the abstract ancestor of all classes dealing with BEEN modules. It implements basic methods for plugin manipulation using the JPF library.
    </p>
    <p>
      
    </p>
    <p>
      JPFPluginManager talks to the JPF library and implements basic methods for plugin manipulation. Returns Configurators, Generators and VersionProviders. Stores a list of rejected plugins to disk and attempts to delete their files.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228165630344" ID="ID_737196477" MODIFIED="1228165944811" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      &#160;The JPF library has been updated to a generics-aware version 1.5.1. Wildcard generic types removed where appropriate. Other libraries should be modified this way, too.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228140505324" ID="Freemind_Link_1197896180" MODIFIED="1229296080902" STYLE="fork" TEXT="PluginManagerLocalInterface">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228149486547" ID="ID_360669183" MODIFIED="1228165940926" STYLE="bubble" TEXT="Despite its long weird name, this interface does nothing but logging. This is mainly useful for the plugin manager.">
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228140540630" ID="Freemind_Link_734098588" MODIFIED="1229296080916" STYLE="fork" TEXT="PluginsIntegrityReport">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228149466049" ID="ID_1405746625" MODIFIED="1228165935313" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This class reports validity of all plugins found by the <font face="DejaVu Sans Mono">BenchmarkManager</font>. Metadata is returned as an array of IntegrityEntry, a nested class.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228142877681" ID="ID_1731250691" MODIFIED="1229296080921" TEXT="IntegrityErrorSeverity">
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228149461015" ID="ID_1827384064" MODIFIED="1228165935307" STYLE="bubble" TEXT="A simple enum with two members only. Probably indicates whether the message being sent is just a warning or something fatal.">
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228142993302" ID="ID_149544415" MODIFIED="1229296080930" TEXT="IntegrityEntry">
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228149463530" ID="ID_973824838" MODIFIED="1228165935290" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      One item of the whole integrity report. Contains error severity and much more. Array of these entries is returned by <font face="DejaVu Sans Mono">PluginsIntegrityReport</font>.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228084529362" ID="Freemind_Link_1446609924" MODIFIED="1229296080959" STYLE="bubble" TEXT="benchmarkmanager">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228157629405" ID="ID_29191119" MODIFIED="1229296080970" STYLE="fork" TEXT="AnalysisAdapter">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_927846053" MODIFIED="1228870284923" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A class that just provides means to access subentity data, reschedule the whole analysis or create metadata classes for a new analysis. Amazingly, it can also delete the referenced analysis, if I'm not mistaken.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_1722426635" MODIFIED="1228870268943" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      All those names need refactoring. AFAIK, this is not an Adapter. This should be called Facade or Accessor. Adapters are something abstract. Their only purpose is to provide a sane default implementation of a huge interface whose users do not need/want to implement all of its methods over and over. Adapters contain a sane default implementation of most (if not all) methods of the given interface.
    </p>
    <p>
      
    </p>
    <p>
      Furthermore, there's a getter for the whole entity interface. This Facade does not hide anything and does not server its purpose. That might be a design flaw.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629407" ID="ID_739531693" MODIFIED="1229296080978" STYLE="fork" TEXT="AnalysisEntity">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1031626005" MODIFIED="1228870509278" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A weird small class that does nothing at all. Just takes <font face="DejaVu Sans Mono">AnalysisMetadata</font>, stores a tily little part of the data and provides getters for that. Well, it's used to traverse the tree-like structure of entities and subentities, but that's just about all.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_1771873558" MODIFIED="1228865856015" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      These weird classes cause confusion.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629489" ID="ID_483154294" MODIFIED="1229296080986" STYLE="fork" TEXT="AnalysisEntityInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1362031710" MODIFIED="1228866035346" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Just a simple getter that lists (probably) all the analyses.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_1047113461" MODIFIED="1228866056180" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      <b><font face="DejaVu Sans Condensed">UNDOCUMENTED!!! GRRR.</font></b>
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629410" ID="ID_1681329135" MODIFIED="1229296081009" STYLE="fork" TEXT="AnalysisManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1609337181" MODIFIED="1228867385557" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Great logging facility No. 10, implementation type No. 4, say WOW! This time some ObjectInputStreams are used to store and restore analysis metadata.
    </p>
    <p>
      
    </p>
    <p>
      Now the positive stuff: <b>Plugins are used here.</b> Good to see how this is done. EntityStatus is used to mark entities as scheduled (planned for future execution) or dispatched (irreversibly prepared for execution). A map of per-analysis queues (implemented as synchronized lists) is used. ;-) All the jobs (tasks) are loaded as plugins and then run.
    </p>
    <p>
      
    </p>
    <p>
      There are methods for rescheduing (providing new scheduler metadata). These only work for regression analyses... Amazingly, this class has also public methods for creating, adding and listing both experiments and analyses.
    </p>
    <p>
      
    </p>
    <p>
      Directory with persistent storage is a constructor parameter. Each analysis has its own metadata directory. It seems that this class should be a singleton, but the constructor permits multiple <font face="DejaVu Sans Mono">AnalysisManager</font>s. Unfortunately, I cannot see anything that would prevent them from using the same directory.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_1378490559" MODIFIED="1228867424615" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Persistence and dispatcher threads in nested classes: a mechanism that already exists in BEEN.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228157629489" ID="ID_128265780" MODIFIED="1229296081016" STYLE="fork" TEXT="GeneratorThread">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_189742213" MODIFIED="1228868882628" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Infinite thread. Takes scheduled experiments one by one, fiddles with their meadata and enqueues them.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629489" ID="ID_1068193850" MODIFIED="1229296081239" STYLE="fork" TEXT="DispatcherThread">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1876109570" MODIFIED="1228868169244" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Starts new experiments when some are available. It's a self-starting infinite thread, just like <font face="DejaVu Sans Mono">MonitorThread</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_246323337" MODIFIED="1228868258723" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is the worst practice that could have been chosen! Instead of waiting for an event (new experiments can be run within an analysis), the thread polls regularly and scans through the whole list of analyses all the time! Remarks from <font face="DejaVu Sans Mono">MonitorThread</font> are applicable here, too.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629489" ID="ID_443984228" MODIFIED="1229296081265" STYLE="fork" TEXT="MonitorThread">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_244651373" MODIFIED="1228867763239" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Scans through the whole queue of experiments and throught the lists of their tasks on a regular basis (<font face="DejaVu Sans Mono">EXPERIMENT_MONITOR_INTERVAL</font>). Stores and removes finished experiments.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_1743913075" MODIFIED="1228867977414" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is something <b>bad</b>, to say the least. Firstly, all this stuff should be kept <i>tickless</i> if possible. Secondly, a huge synchronized block. Thirdly, declaring variables inside cycle bodies is poor programming practice. (The compiler might handle that, but that doesn't mean it's normal.)
    </p>
    <p>
      
    </p>
    <p>
      This is exactly where block-free queues would be useful. The whole <font face="DejaVu Sans Mono">AnalysisManager</font> would need them.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629412" ID="ID_1779834256" MODIFIED="1229296081273" STYLE="fork" TEXT="AnalysisMetadata">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1262724350" MODIFIED="1228868950129" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A simple data container with getters. Exposes subentities, too.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629414" ID="ID_1531290074" MODIFIED="1229296081297" STYLE="fork" TEXT="BenchmarkManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1339116099" MODIFIED="1228869492095" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Communicates with the JPF framework and handles lots of technical stuff. Initializes the framework with properties read from a config file. (Standard JVM properties mechanism is used here.) Implements huge number of methods for plugin and analysis management. There's a tiny RMI lookup cache and (grrr...) logging methods again. Asynchronous plugin downloads and uploads are used.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_2421925" MODIFIED="1228869790549" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This class is huge and implements unrelated pieces of functionality. For example, the <font face="DejaVu Sans Mono">BenchmarkManagerLocalInterface</font> should be moved away from this class and implemented elsewhere.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228157629414" ID="ID_890840223" MODIFIED="1229296081306" STYLE="fork" TEXT="PluginUploadThread">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_736267532" MODIFIED="1228869123630" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      An implementation of <font face="DejaVu Sans Mono">UploadThread</font>. The plugin is stored in a temporary file and loaded from there.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629498" ID="ID_804309490" MODIFIED="1229296081324" STYLE="fork" TEXT="BenchmarkManagerException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1797134950" MODIFIED="1228869526577" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      All exceptions from the whole package.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629500" ID="ID_124134402" MODIFIED="1229296081331" STYLE="fork" TEXT="BenchmarkManagerGUIInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1808303129" MODIFIED="1228869644206" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Interface used by the GUI to control the <font face="DejaVu Sans Mono">BenchmarkManager</font>. (Nothing surprising here...)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629496" ID="ID_813678240" MODIFIED="1229296081336" STYLE="fork" TEXT="BenchmarkManagerLocalInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_417034935" MODIFIED="1228869818791" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This interface provides a partial access to the <font face="DejaVu Sans Mono">BenchmarkManager</font>. A separate implementation should be created.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629491" ID="ID_502811915" MODIFIED="1229296081341" STYLE="fork" TEXT="BenchmarkManagerService">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1343911501" MODIFIED="1228869944004" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A non-standard service framework binding with empty <font face="DejaVu Sans Mono">stop()</font> and <font face="DejaVu Sans Mono">start()</font>. Well, most of the nested threads in this package start themselves, which causes confusion...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629502" ID="ID_1050343759" MODIFIED="1229296081347" STYLE="fork" TEXT="BenchmarkPluginManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1600934377" MODIFIED="1228870053290" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Extends the <font face="DejaVu Sans Mono">JPFPluginManager</font>. Just declares and returns a few <font face="DejaVu Sans Mono">String</font> constants and implements a validator.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629416" ID="ID_1923505153" MODIFIED="1229296081351" STYLE="fork" TEXT="BenchmarkRole">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_824744638" MODIFIED="1228870154120" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stores a list of hosts that perform a benchmarking role together with a RSL condition.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629448" ID="ID_1360792314" MODIFIED="1229296081361" STYLE="fork" TEXT="BinaryAdapter">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1751082180" MODIFIED="1228870379939" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      See all the notes and remarks for <font face="DejaVu Sans Mono">AnalysisAdapter</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_196610845" MODIFIED="1228870435346" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      <font face="DejaVu Sans Mono">deleteEntity()</font> not implemented. Is this what we want? The <font face="DejaVu Sans Mono">TODO</font> is confusing.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629450" ID="ID_1183368139" MODIFIED="1229296081365" STYLE="fork" TEXT="BinaryEntity">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1810419506" MODIFIED="1228870470187" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A simple metadata class. Aren't there too many of them?
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629504" ID="ID_1692274661" MODIFIED="1229296081370" STYLE="fork" TEXT="BinaryEntityInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1367522112" MODIFIED="1228870579618" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Another <font face="DejaVu Sans Mono">EntityInterface</font>... Compared to AnalysisEntityInterface, it just has a context getter instead of <font face="DejaVu Sans Mono">reschedule()</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629453" ID="ID_1831419070" MODIFIED="1229296081380" STYLE="fork" TEXT="BinaryMetadata">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_903526277" MODIFIED="1228870715459" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A container for three simple parameters that could have been stored elsewhere or available through an interface. :-(
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_1932485535" MODIFIED="1228870779387" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This implementation is obviously wrong. The <font face="DejaVu Sans Mono">id</font> field is used uninitialized. <font face="DejaVu Sans Mono">getID()</font> returns <font face="DejaVu Sans Mono">null</font> all the time.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629455" ID="ID_433853620" MODIFIED="1229296081394" STYLE="fork" TEXT="Entity">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_870601267" MODIFIED="1228870926456" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Abstract ancestor of all entity classes (analyses, experiments, runs and binaries). Again, it's nothing but a data storage class. Naming is somewhat confusing in BEEN.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228165398854" ID="ID_1045330965" MODIFIED="1228870993300" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Weird naming of a generics parameter corrected.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629457" ID="ID_779437391" MODIFIED="1229296081399" STYLE="fork" TEXT="EntityAdapter">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1450602675" MODIFIED="1228871082462" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Abstract ancestor of all so-called Adapters. See the comments under <font face="DejaVu Sans Mono">AnalysisAdapter</font> for an explanation of why I don't like that naming.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629459" ID="ID_35728156" MODIFIED="1229296081408" STYLE="fork" TEXT="EntityFactory">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1093025759" MODIFIED="1228871167799" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Creates analysis entities based on whether a regression analysis or an ordinary one is required.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_633063418" MODIFIED="1228871214458" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <ol>
      <li>
        We need custom and scriptable types of analyses, not only those two hard-coded.
      </li>
      <li>
        This naming is utterly confusing.
      </li>
    </ol>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629461" ID="ID_649321584" MODIFIED="1229296081415" STYLE="fork" TEXT="EntityInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_898979403" MODIFIED="1228871434230" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A basic interface to be implemented by all the entities. See the long Javadoc for details. That will tell you that the whole design is somewhat wrong and hardly extensible.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228165398854" ID="ID_913917796" MODIFIED="1228870993300" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Weird naming of a generics parameter corrected.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629463" ID="ID_482436082" MODIFIED="1229296081424" STYLE="fork" TEXT="EntityStatus">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_612768031" MODIFIED="1228871505125" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Status of the entity in the running system. Just an enum.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228165398854" ID="ID_726990043" MODIFIED="1228871540267" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The author must have seen Java 1.5 for the first time. :-( Use of enums corrected.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629465" ID="ID_228582941" MODIFIED="1229296081428" STYLE="fork" TEXT="ExperimentAdapter">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1035084517" MODIFIED="1228871742504" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A so-called Adapter again. See the notes under <font face="DejaVu Sans Mono">AnalysisAdapter</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629468" ID="ID_29199431" MODIFIED="1229296081436" STYLE="fork" TEXT="ExperimentEntity">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1741878644" MODIFIED="1228871977395" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A metadata container extending <font face="DejaVu Sans Mono">Entity</font>. The implementation should be merged in one of those confusing Experiment classes.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629506" ID="ID_1289502531" MODIFIED="1229296081445" STYLE="fork" TEXT="ExperimentEntityInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1685820312" MODIFIED="1228871926403" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Too similar to <font face="DejaVu Sans Mono">BinaryEntityInterface</font>. Correct me if I'm wrong, but the whole entity hierarchy is just a ballast.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_855648811" MODIFIED="1228871958903" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The entity hierarchy should use a custom generic tree with user-named levels.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629470" ID="ID_1666377475" MODIFIED="1229296081461" STYLE="fork" TEXT="ExperimentMetadata">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_879423746" MODIFIED="1228872177065" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Holds roles, packages, properties and result sets for each experiment. Stores lots of technical parameters related to the results repository, too. Some pieces of metadata can be modified.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_1366814771" MODIFIED="1228872281765" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The whole Metadata framework sucks. Each of those containers implements lots of special public methods that are not part of any generic interface. This should be redesigned. For example, there could be a generic tree of entities and only leaf and non-leaf nodes would need to be distinguished...
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629472" ID="ID_1267292002" MODIFIED="1229296081466" STYLE="fork" TEXT="GeneratorException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1966493681" MODIFIED="1228872328111" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A great undocumented exception. Probably thrown when the generator obtained from the plugin manager fails.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629508" ID="ID_695094578" MODIFIED="1229296081477" STYLE="fork" TEXT="PluginConfiguratorManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1208958755" MODIFIED="1228872820182" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Lack of documentation! Who on Earth should read this???
    </p>
    <p>
      
    </p>
    <p>
      However, this thing is quite well designed. Implements configurator (web GUI) screen traversal.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157629472" ID="ID_1513839835" MODIFIED="1229296081480" STYLE="fork" TEXT="NavigatorDirection">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1542222915" MODIFIED="1228872504426" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enum... FIRST, NEXT, PREVIOUS. (?)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629472" ID="ID_139430416" MODIFIED="1229296081489" STYLE="fork" TEXT="BlockingResultMap">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1242194628" MODIFIED="1228873650113" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Behaves like any other map when an existing key is requested. For non-existent keys, blocks untill they are available.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_623539130" MODIFIED="1228873678367" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      If multiple threads wait for the same key, only one of them will wake up. Is this what we want?
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629472" ID="ID_509473815" MODIFIED="1229296081511" STYLE="fork" TEXT="ConfiguratorEntry">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1207231729" MODIFIED="1228873342310" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stores a <font face="DejaVu Sans Mono">Configurator</font> and a corresponding timestamp.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629472" ID="ID_530087977" MODIFIED="1229296081515" STYLE="fork" TEXT="ConfiguratorWork">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1220981326" MODIFIED="1228873204236" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Just a data container. Weird access permissions, anyway. :-(
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629472" ID="ID_1203672578" MODIFIED="1229296081528" STYLE="fork" TEXT="ConfiguratorThread">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_790519437" MODIFIED="1228873078050" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Implementation of the screen traversal itself.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_1871344806" MODIFIED="1228873135329" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The <font face="DejaVu Sans Mono">switch</font> should be replaced by the functionality an enum can easily provide. Usual remarks about variables declared inside cycles bodies apply here, too.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629472" ID="ID_508860789" MODIFIED="1229296081543" STYLE="fork" TEXT="ConfiguratorSweeper">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_874596808" MODIFIED="1228872901465" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Asks for expired sessions periodically. Deletes them from a list.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_1294636948" MODIFIED="1228873015629" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Bleeee. This is the first candidate for tickless transformation. The thread should sleep untill the first sessions expires (if all the sessions have the same expiry time) or a new session is started (if expire times can vary). There's no need for periodic polling.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629513" ID="ID_1466420776" MODIFIED="1229296081548" STYLE="fork" TEXT="RegressionAnalysisEntity">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_254931685" MODIFIED="1228874662722" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is just like a normal <font face="DejaVu Sans Mono">AnalysisEntity</font>, but with time data, schedule and model.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629511" ID="ID_1870255577" MODIFIED="1229296081552" STYLE="fork" TEXT="RegressionAnalysisMetadata">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_573483703" MODIFIED="1228874789569" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      JALBDC. (Just Another Long and Boring Data Container.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629477" ID="ID_236946193" MODIFIED="1229296081558" STYLE="fork" TEXT="RunAdapter">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1637853836" MODIFIED="1228874798635" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The so-called Adapter to query entities of type <i>run</i>. See my remarks on <font face="DejaVu Sans Mono">AnalysisAdapter</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629479" ID="ID_148816444" MODIFIED="1229296081561" STYLE="fork" TEXT="RunEntity">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1430436949" MODIFIED="1228874858994" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This stores nothing but a status enum value.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629480" ID="ID_719627080" MODIFIED="1229296081564" STYLE="fork" TEXT="RunEntityInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1047973649" MODIFIED="1228874888691" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      JAEI. (Just Another Elaborate Interface.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629483" ID="ID_55040280" MODIFIED="1229296081568" STYLE="fork" TEXT="StandardRole">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_543121048" MODIFIED="1228875446331" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Represents a benchmark role. (That could be a client, a database server and the like.) Stores a RSL condition defining suitable hosts.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629485" ID="ID_866122766" MODIFIED="1229296081575" STYLE="fork" TEXT="TaskSequence">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1562328423" MODIFIED="1228876696677" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Represents a sequence of tasks for one BEEN experiment. Stores lots of metadata. As usual, lots of getters and setters are implemented. (Which sounds like horror for anyone keen on TDD. I bet most of the code would never run if I tested that with Cobertura!)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157629483" ID="ID_1268281191" MODIFIED="1229296081580" STYLE="fork" TEXT="TaskItem">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1072630531" MODIFIED="1228876877247" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      One single task record. Contains a reference to the <font face="DejaVu Sans Mono">TaskDescriptor</font> and the status of the task.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629483" ID="ID_309826445" MODIFIED="1229296081585" STYLE="fork" TEXT="SequenceItem">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_597408120" MODIFIED="1228876992882" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A tree node. Instances of this class form a Experiment / Binary / Run tree. Each of them stores a list of tasks to run on the current hierarchy level and a list of children (subentities). Another hierarchy of this type is already implemented in the framework, but does not cover task order. All this stuff will have to be considered when redesigning the hierarchy.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228157629173" ID="ID_1978521780" MODIFIED="1229296081589" STYLE="bubble" TEXT="plugins">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#0033ff" CREATED="1228157629210" ID="ID_1638006261" MODIFIED="1229296081591" STYLE="bubble" TEXT="rubis">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228157629264" ID="ID_1820765379" MODIFIED="1229296081608" STYLE="fork" TEXT="RubisConfigurator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_984791049" MODIFIED="1228761537882" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      <font face="DejaVu Sans Condensed">User interface definition for the RUBIS benchmark. Uses the </font><font face="DejaVu Sans Mono">Screen</font><font face="DejaVu Sans Condensed"> class to display a series of wizard screens. All the screens are linked with a ScreenID enum item. They are constructed using </font><font face="DejaVu Sans Mono">Section</font><font face="DejaVu Sans Condensed">s containing </font><font face="DejaVu Sans Mono">Input</font><font face="DejaVu Sans Condensed">s. Default values for the inputs are defined here. Converts the form contents into </font><font face="DejaVu Sans Mono">ExperimentMetadata</font><font face="DejaVu Sans Condensed">.</font>
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_43236949" MODIFIED="1228761360756" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Correct me if I'm wrong, but I'd guess RSL is parsed and validated twice somewhere...
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228157629264" ID="ID_1964992911" MODIFIED="1229296081617" STYLE="fork" TEXT="ScreenID">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1265935060" MODIFIED="1228761212008" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enum with all possible screen types.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_1057577075" MODIFIED="1228761302999" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This enum could do much more than just listing... It could contain a <font face="DejaVu Sans Mono">createScreen()</font> abstract method.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629266" ID="ID_995644754" MODIFIED="1229296081622" STYLE="fork" TEXT="RubisTaskGenerator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_357857578" MODIFIED="1228761931184" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is where task descriptors are created. Interesting to see how RSL <font face="DejaVu Sans Mono">Condition</font>s can be generated. This is the place where external scripting is needed. Benchmark-specific technical stuff should not be spread all over the production code like this!
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629268" ID="ID_1003793067" MODIFIED="1229296081630" STYLE="fork" TEXT="RubisVersionProvider">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1516890838" MODIFIED="1228762001537" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Creates a version name with a timestamp.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_971415398" MODIFIED="1228762093301" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is what should be changed to make version management more flexible. Two timestamps should be used: checkout time and internal upload time. That would make it possible to modify the same repository version and force BEEN to retransfer the data.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228157629271" ID="ID_1410771292" MODIFIED="1229296081633" STYLE="bubble" TEXT="simpletest">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228157629343" ID="ID_424192098" MODIFIED="1229296081642" STYLE="fork" TEXT="SimpleTestConfigurator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_365228902" MODIFIED="1228762349058" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A simple example test that illustrates the use of the <font face="DejaVu Sans Mono">Configurator</font> framework.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_1704800035" MODIFIED="1228762472471" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Maybe the data item should not be casted all the time. An <font face="DejaVu Sans Mono">enum</font> could be used in a smart way to extract the right type from them.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629347" ID="ID_1475223499" MODIFIED="1229296081647" STYLE="fork" TEXT="SimpleTestGenerator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_294724930" MODIFIED="1228762602854" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is a Hello World test generator. Useful to verify the basic functionality. Otherwise nothing special. Creates 4 tasks.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629349" ID="ID_1293934079" MODIFIED="1229296081650" STYLE="fork" TEXT="SimpleTestVersionProvider">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_657746466" MODIFIED="1228762654260" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A dummy that does nothing at all.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228157629352" ID="ID_17483104" MODIFIED="1229296081653" STYLE="bubble" TEXT="xampler">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228157629398" ID="ID_351110934" MODIFIED="1229296081665" STYLE="fork" TEXT="XamplerConfigurator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1587241091" MODIFIED="1228762995235" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A somewhat elaborate construction of the Xampler benchmark. Similar to <font face="DejaVu Sans Mono">RubisConfigurator</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_1836133572" MODIFIED="1228763122267" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Incorrect use of <font face="DejaVu Sans Mono">enum</font>s <b>again</b>. At least three useless switches. <font face="DejaVu Sans Mono">getTransition()</font> and <font face="DejaVu Sans Mono">getScreen()</font> should be rewritten and all the functionality transferred into <font face="DejaVu Sans Mono">ScreenID</font>.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629400" ID="ID_688686191" MODIFIED="1229296081679" STYLE="fork" TEXT="XamplerTaskGenerator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_134348540" MODIFIED="1228764128706" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Generates the Xampler task from subtasks. You can see how comparison and regression tests are generated. Manipulation with checkpoints is also worth reading.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_72116257" MODIFIED="1228764156287" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Lots of benchmark-specific technical code.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157629402" ID="ID_1139314669" MODIFIED="1229296081687" STYLE="fork" TEXT="XamplerVersionProvider">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_599917862" MODIFIED="1228764271363" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A very simple and weird version provider.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_1782165890" MODIFIED="1228764317352" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Version number of any form should <b>not</b> be part of the name. Store that separately!
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228084535721" ID="Freemind_Link_353646794" MODIFIED="1229296081695" STYLE="bubble" TEXT="common">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228157642330" ID="ID_1568505823" MODIFIED="1229296081703" STYLE="fork" TEXT="ConfigParserAdapter">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1032159099" MODIFIED="1228877504899" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A SAX handler that extracts metadata directly from the low-level XML stream. Performs basic validation, too.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_251269831" MODIFIED="1228877587537" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      I would use JDOM and DTD for validation. This is a typical misuse of the low-level access. It should be used to identify/enumerate elemets and/or record their line numbers, but not for data parsing.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157642324" ID="ID_1937843903" MODIFIED="1229296081712" STYLE="fork" TEXT="Configurator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1293761836" MODIFIED="1228877694642" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Abstract ancestor of all the configurators. These classes create the GUI to request user input.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_228849154" MODIFIED="1228877750894" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This should be an interface + an adapter. The <font face="DejaVu Sans Mono">isRegression</font> variable is simply a design flaw.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157642333" ID="ID_1036807354" MODIFIED="1229296081717" STYLE="fork" TEXT="ConfiguratorException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_951809156" MODIFIED="1228877801269" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Failure in the configurator. (Can this be caused by user input or not?)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157642335" ID="ID_1288278350" MODIFIED="1229296081721" STYLE="fork" TEXT="PluginEntityResolver">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_330388966" MODIFIED="1228877961363" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Interesting use of external entity resolvers. Connects a DTD input stream where applicable. (This time the resolver is used correctly.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157642326" ID="ID_1221767156" MODIFIED="1229296081730" STYLE="fork" TEXT="TaskGenerator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_895062516" MODIFIED="1228878087843" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Abstract ancestor of all task generators. Implements lots of utility methods. Again, it's a weird sort of tree node.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_416512983" MODIFIED="1228878139829" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      There are two tree structures: the structure of entities and the structure of tasks. How about merging them together?
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157642337" ID="ID_838874286" MODIFIED="1229296081733" STYLE="fork" TEXT="TaskGeneratorException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_537755394" MODIFIED="1228878169424" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Problems during task generation.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157642339" ID="ID_1696255249" MODIFIED="1229296081744" STYLE="fork" TEXT="TaskGeneratorInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_591289227" MODIFIED="1228878251743" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The basic (and probably useless) interface to the task generator. Does nothing but setting experiment metadata and starting the generator.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157642341" ID="ID_1793785456" MODIFIED="1229296081749" STYLE="fork" TEXT="TimestampBasedVersionProvider">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_740686988" MODIFIED="1228878566061" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      One of VersionProviders that should use timestamps in a way. Sounds like experiment versioning or other weird stuff. WTF???
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157642328" ID="ID_1014814087" MODIFIED="1229296081755" STYLE="fork" TEXT="VersionProvider">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1998315612" MODIFIED="1228878924101" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Long, but poor documentation! Couldn't the author have said that <b>clearly</b>?
    </p>
    <p>
      
    </p>
    <p>
      This class is supposed to provide a list of Experiments whose version has changed in some specific way in a certain period of time. Usually the time interval spans between the last getter invocation and the current time. Now it is not clear whether experiments are versioned (and only the new ones should be reported to the GUI). It might be well possible that software package versions the experiment depends on are considered here, deciding if a new run is necessary.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228084558933" ID="Freemind_Link_1193142094" MODIFIED="1229296081769" STYLE="bubble" TEXT="gui">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228157651996" ID="ID_1471825254" MODIFIED="1229296081773" STYLE="fork" TEXT="GUIInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_422161314" MODIFIED="1228879068995" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Interface for the GUI to control the rest of the application. Contains basic stuff for plugin upload and download. Can ask the application to generate the wizard screens based on user interaction.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157652030" ID="ID_1381995809" MODIFIED="1229296081777" STYLE="fork" TEXT="IllegalScreenSequenceException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_71122686" MODIFIED="1228879248554" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      When the screen requested by the web interface (or a cracker experimenting with HTTP) gets out of the allowed sequence. See the Javadoc for how the sequence is maintained.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157651999" ID="ID_1347508862" MODIFIED="1229296081782" STYLE="fork" TEXT="Input">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_703720835" MODIFIED="1228879332310" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Represents one string input from the web interface. Contains a reference to a validator.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157652000" ID="ID_125066178" MODIFIED="1229296081786" STYLE="fork" TEXT="InputValidator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_458019712" MODIFIED="1228879375583" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Abstract ancestor of all validators. Useful to validate strings obtained from the web interface.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157652003" ID="ID_1438542911" MODIFIED="1229296081791" STYLE="fork" TEXT="Item">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1026531697" MODIFIED="1228879497235" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Represents one single form element. Abstract ancestor of all GUI widgets. <font face="DejaVu Sans Mono">Input</font> is an example of a subclass of <font face="DejaVu Sans Mono">Item</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157652010" ID="ID_347432069" MODIFIED="1229296081796" STYLE="fork" TEXT="MultiSelect">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_112313671" MODIFIED="1228879581652" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Represents the widget used for multiple selection. (Names vary between toolkits.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157652012" ID="ID_1238464161" MODIFIED="1229296081799" STYLE="fork" TEXT="Option">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_377021784" MODIFIED="1228879620366" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      One option of the <i>select</i> or <i>multiselect</i> widget.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157652016" ID="ID_1053239490" MODIFIED="1229296081802" STYLE="fork" TEXT="RadiosWithSections">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1919600918" MODIFIED="1228879728660" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Probably just a group of radio buttons.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157652032" ID="ID_421260091" MODIFIED="1229296081806" STYLE="fork" TEXT="RadioWithSectionItem">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1617610062" MODIFIED="1228879740594" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      One item of a radio button group.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157652018" ID="ID_591123764" MODIFIED="1229296081819" STYLE="fork" TEXT="Role">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1381729342" MODIFIED="1228879836066" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      One more implementation of a list of hosts selected for a role.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_1624094451" MODIFIED="1228879859258" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      There's so much duplicate code everywhere...
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157652014" ID="ID_306546405" MODIFIED="1229296081824" STYLE="fork" TEXT="RSLInput">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_601972473" MODIFIED="1228879949415" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Just a normal input that takes care of RSL validation at the same time. Let's hope that the validation data is re-used... :-( NO, it is NOT reused. Grrr. Those expressions are parsed twice.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157652020" ID="ID_1859912806" MODIFIED="1229296081828" STYLE="fork" TEXT="SchedulerInfo">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1845269500" MODIFIED="1228880194782" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Data container for regression analysis... (Too specific hardcoded stuff again.) Stores timing of the experiments.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157652022" ID="ID_464923091" MODIFIED="1229296081832" STYLE="fork" TEXT="Screen">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_835949451" MODIFIED="1228880034633" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      There are so many classes of this name that one would cry! Again, represents one screen of the web interface with a list of sections.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157652024" ID="ID_1035805460" MODIFIED="1229296081837" STYLE="fork" TEXT="Section">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1413421010" MODIFIED="1228880073994" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      One section of the wizard screen. This would be called Group in a GUI toolkit like SWT...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157652026" ID="ID_837072726" MODIFIED="1229296081841" STYLE="fork" TEXT="Select">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1416596198" MODIFIED="1228880107165" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Something like a drop-down menu.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157652028" ID="ID_1686777604" MODIFIED="1229296081844" STYLE="fork" TEXT="StaticText">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1112042477" MODIFIED="1228880230282" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A non-editable text field in the wizard.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228084578930" ID="Freemind_Link_183857530" MODIFIED="1229296081848" STYLE="bubble" TEXT="scheduler">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228157663041" ID="ID_889404154" MODIFIED="1229296081852" STYLE="fork" TEXT="PastDateException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1637096513" MODIFIED="1228880298853" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Undocumented... I'd guess it has something in common with regression analysis. An experiment is expected to start before the previous run has finished.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157663051" ID="ID_1393573019" MODIFIED="1229296081857" STYLE="fork" TEXT="ScheduledJobListener">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_760598842" MODIFIED="1228880367441" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Interface to simply run scheduled jobs. Just one method.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157663046" ID="ID_1170325773" MODIFIED="1229296081864" STYLE="fork" TEXT="Scheduler">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1323222408" MODIFIED="1228880660698" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is a simple scheduler with a <font face="DejaVu Sans Mono">main()</font> method for testing. Useful as cron for regression analysis benchmarks. Please see how a good tickless approach is used in <font face="DejaVu Sans Mono">run()</font>. The rest of BEEN should work this way, too.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157663051" ID="ID_1610557491" MODIFIED="1229296081868" STYLE="fork" TEXT="EntryThread">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1537322873" MODIFIED="1228880747419" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Starts a single entry from the queue. This is a <font face="DejaVu Sans Mono">Thread</font>, presumably.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157663051" ID="ID_1916459110" MODIFIED="1229296081884" STYLE="fork" TEXT="SchedulerQueue">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1087411940" MODIFIED="1228880865142" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A synchronized queue of scheduled tasks. They can be added and removed, too. It would be nice if all these weird queues were non-blocking.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_74747822" MODIFIED="1228881116365" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A weird wait() wrapper. What's that good for? Anyway, authors often used incorrect access permissions with methods, such as public, although they used these methods only from within nested classes. It's good to know that a nested class has full access to the surrounding class, including private members.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157663048" ID="ID_47997777" MODIFIED="1229296081893" STYLE="fork" TEXT="SchedulerEntry">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_176687139" MODIFIED="1228881363443" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Alarm clock settings with repeating and other features. Most of the class is just technical stuff to manipulate and convert date and time correctly.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228165398854" ID="ID_1755153714" MODIFIED="1228881421221" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Some <font face="DejaVu Sans Mono">final</font>s added. There's probably much much more stuff like this in BEEN.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228084569396" ID="Freemind_Link_199331671" MODIFIED="1229296081899" STYLE="bubble" TEXT="testmanager">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228157679478" ID="ID_513779766" MODIFIED="1229296081908" STYLE="fork" TEXT="TestManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_529225996" MODIFIED="1228881928120" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Useful to start/stop/schedule tests. Manages all tests within the environment.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_634021085" MODIFIED="1228882000356" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <ol>
      <li>
        Should be a singleton.
      </li>
      <li>
        Too much <font face="DejaVu Sans Mono">TODO</font> stuff and auto-generated methods. Might need to be checked by someone from the former BEEN team.
      </li>
    </ol>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157679488" ID="ID_4690736" MODIFIED="1229296081911" STYLE="fork" TEXT="TestManagerException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1116632287" MODIFIED="1228882057570" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      All the <font face="DejaVu Sans Mono">testmanager</font> woes.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157679490" ID="ID_1195143754" MODIFIED="1229296081916" STYLE="fork" TEXT="TestManagerGUIInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1115759587" MODIFIED="1228882165542" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      An empty interface extending <font face="DejaVu Sans Mono">GUIInterface</font>. Those formalists...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157679492" ID="ID_1789517989" MODIFIED="1229296081920" STYLE="fork" TEXT="TestManagerLocalInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_137808212" MODIFIED="1228882213445" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      RMI lookups, task status, plugin loader, id manager...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157679482" ID="ID_87611039" MODIFIED="1229296081925" STYLE="fork" TEXT="TestManagerService">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_148630989" MODIFIED="1228882249104" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Binds the test manager to the been service framework.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157679484" ID="ID_203041900" MODIFIED="1229296081931" STYLE="fork" TEXT="TestPluginManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1886514231" MODIFIED="1228882346983" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Plugin manager for testmanager.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228165398854" ID="ID_1507701952" MODIFIED="1228882373908" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      <font face="DejaVu Sans Mono">validatePlugin()</font> always succeeds. Is that OK?
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228084771418" ID="Freemind_Link_43130729" MODIFIED="1229296081962" POSITION="right" STYLE="bubble" TEXT="common">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228152510486" ID="ID_1753155765" MODIFIED="1229296081967" STYLE="fork" TEXT="ArrayUtils">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_1454854034" MODIFIED="1228166117988" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A set of functions to manipulate arrays. Useful to create toString() output and reverse order of arrays...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228152521641" ID="ID_756242710" MODIFIED="1229296081971" STYLE="fork" TEXT="BeenException">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_72833164" MODIFIED="1228166173136" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The most generic exception of the whole project. Checked, fortunately. :-)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228152537910" ID="ID_1177850192" MODIFIED="1229296081978" STYLE="fork" TEXT="BeenUtils">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_1710079382" MODIFIED="1228166341279" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Name of this class is somewhat weird... Contains methods for two totally different purposes.
    </p>
    <ol>
      <li>
        BFS search of a directory tree.
      </li>
      <li>
        Task sorter and task ID comparator.
      </li>
    </ol>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228166388753" ID="ID_1367120941" MODIFIED="1229296081982" STYLE="fork" TEXT="TidComparator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_1758431875" MODIFIED="1228166512566" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Comparator implementation for the type <font face="DejaVu Sans Mono">TaskDescriptor</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228153031996" ID="ID_1724859741" MODIFIED="1229296081986" STYLE="fork" TEXT="ComponentInitializationException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_1880895190" MODIFIED="1228166607847" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Indicates that a component initialization was not successful.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153233314" ID="ID_487701946" MODIFIED="1229296081995" STYLE="fork" TEXT="DownloadHandle">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_242534627" MODIFIED="1228166702828" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Unique identifier of a download operation.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_408220699" MODIFIED="1228166786411" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Multiple notes here:
    </p>
    <ol>
      <li>
        The constructor of this class should be declared private.
      </li>
      <li>
        The factory method should be synchronized.
      </li>
    </ol>
    <p>
      Perhaps this class is never used from within multiple threads, but it's a good practice not to rely on this.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153239249" ID="ID_411210189" MODIFIED="1229296082004" STYLE="fork" TEXT="DownloadStatus">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_204204468" MODIFIED="1228166840820" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enum of possible download states.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1888477211" MODIFIED="1228167001008" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is a C-like enum! Will repeat this over and over: The former BEEN team did not take notice what enums can do in Java. Enums that do not implement any abstract methods are <b>weird</b>. Some functionality from other classes could be transferred to this enum. (That's just a wild guess.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153249984" ID="ID_567968992" MODIFIED="1229296082009" STYLE="fork" TEXT="GreedyURLClassLoader">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_1260657840" MODIFIED="1228167980496" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This loader searches a list of URLs (<font face="DejaVu Sans Mono">.jar</font>s or directories) for the requested class. If not found, the request is delegated to the parent class loader.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153266353" ID="ID_184895494" MODIFIED="1229296082025" STYLE="fork" TEXT="IdentifiableList">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_625722048" MODIFIED="1228168105086" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      List of identifiable objects, extends <font face="DejaVu Sans Mono">LinkedList</font>. Implements brute-force search by identifiers...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1954337181" MODIFIED="1228168308381" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The method getIDs() is somewhat weird. The list it returns is neither a set, nor a multiset. Either the implementation or the Javadoc comment are wrong.
    </p>
    <p>
      
    </p>
    <p>
      BTW, both the brute-force search and the conversion to a list of identifiers sound like brute-force to me. They should be either implemented on the calling side and used with caution.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153282866" ID="ID_767832781" MODIFIED="1229296082030" STYLE="fork" TEXT="IntegerInputValidator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_324483074" MODIFIED="1228168361258" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Just an exception wrapper. Returns either the error message or null when everything went OK.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153289977" ID="ID_1856530905" MODIFIED="1229296082041" STYLE="fork" TEXT="IntegerIntervalInputValidator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_1295676078" MODIFIED="1228168508156" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Exactly the same sort of thing as <font face="DejaVu Sans Mono">IntegerInputValidator</font> and a direct descendant of that class. Besides checking the validity of the input string, it also verifies that interval constraints are met.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1825668167" MODIFIED="1228168655709" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      BTW, the constructor just allows you to have an interval from 10 to 0 if you wish... As a TDD enthusiast, I have to ask whether this is correct. Yes, one might want to use those co-intervals, but if this is the case, the Javadoc should mention it!
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153315429" ID="ID_566718479" MODIFIED="1229296082046" STYLE="fork" TEXT="ListProperties">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_202097076" MODIFIED="1228168787344" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">main()</font> class that prints out all the JVM system properties.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153331698" ID="ID_1627252774" MODIFIED="1229296082049" STYLE="fork" TEXT="NotEmptyInputValidator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_151880919" MODIFIED="1228169168901" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Clarence Carson Parks: Something Stupid
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153339872" ID="ID_252804905" MODIFIED="1229296082055" STYLE="fork" TEXT="OutputReader">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_221098071" MODIFIED="1228169575327" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A thread that looks like the UNIX tee command, but does not copy the output anywhere... Probably used as a buffered pipe for remote output requests. One would expect a more generic mechanism in a project of this size.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153361453" ID="ID_11340629" MODIFIED="1229296082063" STYLE="fork" TEXT="OutputType">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_564598499" MODIFIED="1228169608450" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Again, a C-style enum.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_429506959" MODIFIED="1228169655974" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This enum could happily replace those conditions in <font face="DejaVu Sans Mono">OutputReader</font>'s <font face="DejaVu Sans Mono">run()</font> method...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153375035" ID="ID_1126440055" MODIFIED="1229296082067" STYLE="fork" TEXT="Pair">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_1786592500" MODIFIED="1228169826702" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      STL lovers facing generics... Amazingly, it does not implement Comparable. Whenever you create a Pair that is not immutable, the TDD people get a terrible headache. :-D
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153383625" ID="ID_1188297364" MODIFIED="1229296082072" STYLE="fork" TEXT="RegexSubstitute">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_98064665" MODIFIED="1228169905433" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is exactly like a <font face="DejaVu Sans Mono">sed</font> executed to edit a file in-place. Operates on whole text files.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153478713" ID="ID_1983723042" MODIFIED="1229296082090" STYLE="fork" TEXT="ScannerExtended">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_1399102959" MODIFIED="1228170277187" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Controlling the standard Scanner with simplified methods... Sounds like a facade.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1850658235" MODIFIED="1228170427482" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Unchecked exceptions are thrown in some of the methods! That's obviously wrong. Such an exception can easily crash BEEN if not caught properly. With unchecked exceptions, all the exception handling relies on ... a human. :-(
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153485185" ID="ID_1093591556" MODIFIED="1229296082100" STYLE="fork" TEXT="StringTokenizer">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_527552803" MODIFIED="1228171537720" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Breaks a string into a <font face="DejaVu Sans Mono">List</font> of tokens based on delimiters. Delimiters are supplied as a string.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228171549781" ID="ID_1082346376" MODIFIED="1229296082103" STYLE="fork" TEXT="Token">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_689678080" MODIFIED="1228171724607" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A generic token with position information.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228171556726" ID="ID_1218251793" MODIFIED="1229296082107" STYLE="fork" TEXT="DelimiterToken">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_1239620682" MODIFIED="1228171774988" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A (single character) delimiter. Extends <font face="DejaVu Sans Mono">AbstractToken</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228171619483" ID="ID_1071979194" MODIFIED="1229296082112" STYLE="fork" TEXT="StringToken">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_1952883469" MODIFIED="1228171800533" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The string between two delimiters. Extends <font face="DejaVu Sans Mono">AbstractToken</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228153498216" ID="ID_1620507825" MODIFIED="1229296082121" STYLE="fork" TEXT="StringUtils">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_171261049" MODIFIED="1228172073522" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Utility methods that fix semantic differences between the standard Java string library and the BEEN design.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228164700637" ID="ID_639737705" MODIFIED="1228287341555" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Fixed first two methods. Not-null condition might be evaluated first in most cases, but that's implementation-dependent. Not a good idea to rely on this.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153503781" ID="ID_598869994" MODIFIED="1229296082134" STYLE="fork" TEXT="SubstituteVariableValues">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_390696746" MODIFIED="1228173082363" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Substitutes all the variable names in a string with their values. This is a straightforward, elegant and readable implementation.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1463058771" MODIFIED="1228173232060">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <ol>
      <li>
        The DFA would deserve an enum or at least a bunch of constants for its states. Magic numbers used...
      </li>
      <li>
        The ValueProvider framework is something I would not hide inside a class... I'm convinced that a similar mechanism is used elsewhere, too.
      </li>
    </ol>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228172669262" ID="ID_1191628835" MODIFIED="1229296082141" STYLE="fork" TEXT="VariableValueProviderInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_858428840" MODIFIED="1228172782200" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Simple name -&gt; value mapping.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228172680964" ID="ID_1163048059" MODIFIED="1229296082147" STYLE="fork" TEXT="MapVariableValueProvider">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1586494002" MODIFIED="1228172836878" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Extends <font face="DejaVu Sans Mono">VariableValueProviderInterface</font>. Uses a <font face="DejaVu Sans Mono">Map</font> to provide the mapping. :-)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228172455278" ID="ID_793816563" MODIFIED="1229296082153" STYLE="fork" TEXT="RegexpVariableNameSyntaxChecker">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1731351286" MODIFIED="1228172597197" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A simple facade to the standard regexp <font face="DejaVu Sans Mono">Matcher</font>. (Despite what the Javadoc says...)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228153517473" ID="ID_1196342873" MODIFIED="1229296082165" STYLE="fork" TEXT="TaskPathBuilder">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1024051084" MODIFIED="1228176268557" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Path concatenator. :-D Makes sure correct platform-dependent delimiters are used. Once Microsoft starts using UNIX-like path names, which will happen sooner or later, all these troubles will be <b>gone</b>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153532537" ID="ID_332137861" MODIFIED="1229296082174" STYLE="fork" TEXT="UploadHandle">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_1256018118" MODIFIED="1228176354530" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Unique identifier of an upload operation.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_86238818" MODIFIED="1228176446784" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      See DownloadHandle comments for details. Objections refer to constructor visibility and thread-safety.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153539311" ID="ID_1025036457" MODIFIED="1229296082184" STYLE="fork" TEXT="UploadStatus">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_719461974" MODIFIED="1228176471718" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enum of possible upload operation states.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1459247343" MODIFIED="1228176487211" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Ohhh... Those C-like enums...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153545958" ID="ID_319765498" MODIFIED="1229296082195" STYLE="fork" TEXT="UploadThread">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_867434561" MODIFIED="1228176857814" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Abstract class useful for asynchronous uploading. The thread is bound to one IP address and port. (No wonderful thread pools used here...) The descendants can influence temporary file storage and must implement a file processing routine.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153562128" ID="ID_1087250924" MODIFIED="1229296082218" STYLE="fork" TEXT="VariableReplacer">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_648309424" MODIFIED="1228177044834" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Replaces variables in a string with their values. This is a re-implementation of <font face="DejaVu Sans Mono">SubstituteVariableValues</font>. This time Java standard libraries are used. Nice and short, but no fun with DFAs.
    </p>
    <p>
      
    </p>
    <p>
      This class can also get a <font face="DejaVu Sans Mono">ValueProvider</font> and feed those values into a <font face="DejaVu Sans Mono">Properties</font> container.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1809909812" MODIFIED="1228177099816" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <ol>
      <li>
        Two different things implemented in one class.
      </li>
      <li>
        This class should be definitely merged with <font face="DejaVu Sans Mono">SubstituteVariableValues</font>.
      </li>
    </ol>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153568346" ID="ID_258912633" MODIFIED="1229296082233" STYLE="fork" TEXT="Version">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_1422299349" MODIFIED="1228177175425" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Version number representation with comparison methods.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1699597174" MODIFIED="1228884166284" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is simply <b>insufficient</b>. Many version numbers contain other separators than just dots. Furthermore, they might contain characters, such as <font face="DejaVu Sans Mono">beta</font> and the like.
    </p>
    <ol>
      <li>
        Better version comparison algorithm should be implemented.
      </li>
      <li>
        An interface should be provided for custom per-benchmark comparators.
      </li>
    </ol>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228153572946" ID="ID_925914449" MODIFIED="1229296082237" STYLE="fork" TEXT="WorkQueue">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node CREATED="1228164700637" ID="ID_805963504" MODIFIED="1228177449595" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Producer-consumer with infinite queue.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228085423213" ID="Freemind_Link_1665005517" MODIFIED="1229296082247" STYLE="bubble" TEXT="anttasks">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228158678854" ID="ID_1199334342" MODIFIED="1229296082250" STYLE="fork" TEXT="AntTaskException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1166914206" MODIFIED="1228882595886" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Thrown by the wrapper when the ant task fails.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158678856" ID="ID_578037218" MODIFIED="1229296082261" STYLE="fork" TEXT="Chmod">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1791408395" MODIFIED="1228882923679" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Changes file permissions using Ant. (Platform dependent???) Recursive directory scan capable. (Conflicting naming.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158678858" ID="ID_1098906369" MODIFIED="1229296082266" STYLE="fork" TEXT="Copy">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1715692405" MODIFIED="1228882788286" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Copies files using Ant. (Conflicting class naming.) Extended utility methods for filesets and other stuff.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158678860" ID="ID_1592369887" MODIFIED="1229296082269" STYLE="fork" TEXT="Cvs">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1212442414" MODIFIED="1228882752565" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Performs a CVS checkout using Ant.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158678863" ID="ID_362623280" MODIFIED="1229296082273" STYLE="fork" TEXT="Delete">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_261606527" MODIFIED="1228882949615" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Deletes files using Ant. Utility methods for filesets and directories. (Conflicting naming.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158678865" ID="ID_1215963238" MODIFIED="1229296082277" STYLE="fork" TEXT="GUnzip">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_126029117" MODIFIED="1228882960385" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Unzips a file using Ant. (Conflicting class naming.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158678867" ID="ID_321186714" MODIFIED="1229296082281" STYLE="fork" TEXT="Java">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1626982260" MODIFIED="1228883018154" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      (Conflicting class naming.) Runs a Java task based on <font face="DejaVu Sans Mono">main()</font> class name, classpath and working directory.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158678870" ID="ID_329451417" MODIFIED="1229296082285" STYLE="fork" TEXT="Move">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1554111303" MODIFIED="1228883056057" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Moves files using Ant. Extensions for filesets and directories. (Conflicing class naming.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158678872" ID="ID_1811704164" MODIFIED="1229296082290" STYLE="fork" TEXT="Replace">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1764205727" MODIFIED="1228883095962" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Replaces a token in a file with a new value. (Conflicting class naming.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158678874" ID="ID_1531876826" MODIFIED="1229296082294" STYLE="fork" TEXT="Tar">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1389579338" MODIFIED="1228883137964" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Creates a tar archive using Ant. (Conflicting class naming.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158678876" ID="ID_912553392" MODIFIED="1229296082298" STYLE="fork" TEXT="Untar">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_724716473" MODIFIED="1228883186859" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Extracts a tar archive using ant, decompressing it first when requested. (Conflicting class naming.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158678879" ID="ID_714790460" MODIFIED="1229296082303" STYLE="fork" TEXT="Unzip">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_249412181" MODIFIED="1228883236171" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Unzips a file using Ant. Contains a <font face="DejaVu Sans Mono">createInstance()</font> metdhod, unusually.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158678886" ID="ID_1741254836" MODIFIED="1229296082307" STYLE="fork" TEXT="Zip">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_918563493" MODIFIED="1228883274078" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Creates a zip archive using Ant. (Conflicting class naming.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228139349568" ID="Freemind_Link_552758414" MODIFIED="1229296082326" STYLE="bubble" TEXT="id">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228158690174" ID="ID_720783657" MODIFIED="1229296082335" STYLE="fork" TEXT="AID">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1217656618" MODIFIED="1228885061891" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Analysis identifier. (In fact a <font face="DejaVu Sans Mono">long</font>.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158690176" ID="ID_1096796475" MODIFIED="1229296082339" STYLE="fork" TEXT="BID">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1697160269" MODIFIED="1228885069130" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Binary identifier. (In fact a <font face="DejaVu Sans Mono">long</font>.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158690179" ID="ID_626671396" MODIFIED="1229296082344" STYLE="fork" TEXT="BRID">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_846376933" MODIFIED="1228885075850" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Benchmark run identifier. (In fact a <font face="DejaVu Sans Mono">long</font>.) Represents the current HTTP session, if I am not mistaken.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158690182" ID="ID_1354227340" MODIFIED="1229296082347" STYLE="fork" TEXT="EID">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1947451605" MODIFIED="1228885088214" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Experiment identifier. (In fact a <font face="DejaVu Sans Mono">long</font>.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158690184" ID="ID_912868010" MODIFIED="1229296082352" STYLE="fork" TEXT="ID">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1271678075" MODIFIED="1228883869618" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Abstract ancestor of all identifiers. Does not enforce any specific identifier type. Just has to be <font face="DejaVu Sans Mono">Comparable</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158690190" ID="ID_1280216294" MODIFIED="1229296082357" STYLE="fork" TEXT="Identifiable">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_215019221" MODIFIED="1228883935087" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A generic interface. The implementor has to return an identifier of type <font face="DejaVu Sans Mono">T</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158690186" ID="ID_1148213881" MODIFIED="1229296082367" STYLE="fork" TEXT="IDManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1799456607" MODIFIED="1228884123771" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is an identifier factory. (Bad naming again.) Creates new identifiers of the requested class. All of them use <font face="DejaVu Sans Mono">long</font> as internal data type.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228164700637" ID="ID_1744080333" MODIFIED="1228884867394" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The constructor has been hidden. This class should never be instantiated.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
<node COLOR="#006566" CREATED="1228158690186" ID="ID_227555535" MODIFIED="1229296082372" STYLE="fork" TEXT="Counter">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_944134721" MODIFIED="1228884066681" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Counts instances of the given class. One <font face="DejaVu Sans Mono">Counter</font> exists for each identifier type.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228158690187" ID="ID_1442170870" MODIFIED="1229296082376" STYLE="fork" TEXT="IDManagerInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1032720996" MODIFIED="1228884447801" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Just <font face="DejaVu Sans Mono">getNext()</font>... Does that have a special meaning?
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158690192" ID="ID_354260736" MODIFIED="1229296082379" STYLE="fork" TEXT="OID">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_861621916" MODIFIED="1228884582435" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Ancestor of all the <font face="DejaVu Sans Mono">long</font>-based identifiers.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158690195" ID="ID_866816850" MODIFIED="1229296082384" STYLE="fork" TEXT="PEID">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_815109042" MODIFIED="1228884635890" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Plugin entry identifier. This one extends <font face="DejaVu Sans Mono">ID</font> directly and uses <font face="DejaVu Sans Mono">String</font> as base data type.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158690202" ID="ID_182941888" MODIFIED="1229296082405" STYLE="fork" TEXT="PersistentIDManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1196184091" MODIFIED="1228884740240" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      ID manager that stores all the IDs to the disk immediately. Contains a method that can restore all the identifiers.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1835504074" MODIFIED="1228884821806" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The copy&amp;paste reimplementation of the <font face="DejaVu Sans Mono">Counter</font> is really stupid. How about a project-wide counting and synchronization framework?
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228164700637" ID="ID_1847753979" MODIFIED="1228884867394" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The constructor has been hidden. This class should never be instantiated.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
<node COLOR="#006566" CREATED="1228158690186" ID="ID_781001488" MODIFIED="1229296082410" STYLE="fork" TEXT="Counter">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_940935336" MODIFIED="1228884066681" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Counts instances of the given class. One <font face="DejaVu Sans Mono">Counter</font> exists for each identifier type.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228158690197" ID="ID_973853303" MODIFIED="1229296082414" STYLE="fork" TEXT="RID">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1021312930" MODIFIED="1228885039823" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Run identifier with bad copy&amp;paste Javadoc. (In fact just a <font face="DejaVu Sans Mono">long</font>.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158690199" ID="ID_287739645" MODIFIED="1229296082419" STYLE="fork" TEXT="SID">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1738364952" MODIFIED="1228885159344" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">Screen</font> identifier. (BTW, which one? At least three classes are called like that.) (Just a <font face="DejaVu Sans Mono">long</font>.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158690200" ID="ID_644990257" MODIFIED="1229296082423" STYLE="fork" TEXT="TID">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1822748431" MODIFIED="1228885187551" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Task identifier. (In fact a <font face="DejaVu Sans Mono">long</font>.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228085430820" ID="Freemind_Link_1639282914" MODIFIED="1229296082449" STYLE="bubble" TEXT="rsl">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228158710436" ID="ID_374303141" MODIFIED="1229296082453" STYLE="fork" TEXT="AndCondition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1692135267" MODIFIED="1228885401748" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stores data about an AND condition (the list of its subconditions), checks the semantic correctness of the condition based on current context and evaluates the condition against the given set of metadata.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710438" ID="ID_768147928" MODIFIED="1229296082458" STYLE="fork" TEXT="ArrayProperty">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1874023358" MODIFIED="1228885615751" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Returns an array of subnodes of the current property container. The Javadoc is written in such a stupid way that it says nothing. In fact, the container property is described as if it was a propety with a special (record) data type. Hard to say if this is the case.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710445" ID="ID_775302218" MODIFIED="1229296082462" STYLE="fork" TEXT="CompareCondition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_521801161" MODIFIED="1228885783389" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Abstract superclass representing all the comparison operators.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710447" ID="ID_1893616793" MODIFIED="1229296082473" STYLE="fork" TEXT="Condition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1644630232" MODIFIED="1228885903230" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Abstract ancestor of all the RSL based conditions. Contains technical stuff property getters and means to be converted back to RSL.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710449" ID="ID_383671211" MODIFIED="1229296082479" STYLE="fork" TEXT="ContainerProperty">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1816975255" MODIFIED="1228885941056" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A general-purpose record-type property. Properties are a tree-like structure...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710456" ID="ID_561005981" MODIFIED="1229296082484" STYLE="fork" TEXT="ContainsCondition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1076477586" MODIFIED="1228885981218" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Represents the <font face="DejaVu Sans Mono">contains</font> operator. Scans through a list to find the desired value.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710502" ID="ID_1307316326" MODIFIED="1229296082489" STYLE="fork" TEXT="DoesNotMatchCondition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_873280131" MODIFIED="1228886380327" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">PatternCondition</font>. Runs a standard java regex <font face="DejaVu Sans Mono">Matcher</font> and says the contrary.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710458" ID="ID_581180191" MODIFIED="1229296082494" STYLE="fork" TEXT="EqualsCondition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1298718481" MODIFIED="1228886106413" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This does not extend <font face="DejaVu Sans Mono">CompareCondition</font>, surprisingly. It just uses <font face="DejaVu Sans Mono">equals()</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710504" ID="ID_1717909076" MODIFIED="1229296082497" STYLE="fork" TEXT="GreaterOrEqualCondition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_836176590" MODIFIED="1228886129201" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">CompareCondition</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710506" ID="ID_1734717288" MODIFIED="1229296082500" STYLE="fork" TEXT="GreaterThanCondition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1134069058" MODIFIED="1228886129201" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">CompareCondition</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710508" ID="ID_197731902" MODIFIED="1229296082504" STYLE="fork" TEXT="InvalidOperatorException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_99125811" MODIFIED="1228886211843" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Operator does not match the property class.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710510" ID="ID_1635349270" MODIFIED="1229296082508" STYLE="fork" TEXT="InvalidPropertyException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1210583663" MODIFIED="1228886239056" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Requested property does not exist within evaluation scope.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710512" ID="ID_685649855" MODIFIED="1229296082512" STYLE="fork" TEXT="InvalidValueTypeException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1687817288" MODIFIED="1228886269673" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Value does not match the property class. This depends on the operator, too.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710514" ID="ID_1293002549" MODIFIED="1229296082515" STYLE="fork" TEXT="InvalidValueUnitException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_152140086" MODIFIED="1228886294141" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Value unit does not match the property unit.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710516" ID="ID_882097944" MODIFIED="1229296082519" STYLE="fork" TEXT="LessOrEqualCondition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_234740073" MODIFIED="1228886129201" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">CompareCondition</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710460" ID="ID_264672549" MODIFIED="1229296082522" STYLE="fork" TEXT="LessThanCondition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_277564424" MODIFIED="1228886129201" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">CompareCondition</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710462" ID="ID_1746314160" MODIFIED="1229296082526" STYLE="fork" TEXT="LongWithUnit">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1198090124" MODIFIED="1228886349780" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stores a <font face="DejaVu Sans Mono">long</font> value, a unit prefix and a unit name. Implements the boring comparison stuff.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710464" ID="ID_305417533" MODIFIED="1229296082538" STYLE="fork" TEXT="MatchesCondition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_10096312" MODIFIED="1228886402100" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">PatternCondition</font>. Uses the standard Java regex <font face="DejaVu Sans Mono">Matcher</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710466" ID="ID_123586767" MODIFIED="1229296082543" STYLE="fork" TEXT="NotEqualCondition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1509898559" MODIFIED="1228886439982" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is a <font face="DejaVu Sans Mono">SimpleCondition</font>, using the standard Java <font face="DejaVu Sans Mono">equals()</font> mechanism.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710468" ID="ID_236817020" MODIFIED="1229296082547" STYLE="fork" TEXT="OrCondition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_694414662" MODIFIED="1228886498923" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This stores subconditions of an OR condition and evaluates (or validates) the whole thing.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710470" ID="ID_1213372537" MODIFIED="1229296082550" STYLE="fork" TEXT="ParseException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_491904539" MODIFIED="1228886559481" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      RSL exception. Produces carefully escaped output.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710472" ID="ID_1432082018" MODIFIED="1229296082554" STYLE="fork" TEXT="Parser">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1770363785" MODIFIED="1228886612577" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Autogenerated RSL parser.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710474" ID="ID_1905277275" MODIFIED="1229296082558" STYLE="fork" TEXT="Parser.jj">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1135267777" MODIFIED="1228886719982" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The JavaCC source. Grrr... Something like Lex and Bison together.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710476" ID="ID_432038716" MODIFIED="1229296082562" STYLE="fork" TEXT="ParserConstants">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_393458870" MODIFIED="1228886786691" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Autogenerated list of parser constants. An enum would be nicer... Is the library too old or does this simply run faster?
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710479" ID="ID_1437770826" MODIFIED="1229296082565" STYLE="fork" TEXT="ParserTokenManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1599070647" MODIFIED="1228886873868" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Looks like an autogenerated DFA.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710482" ID="ID_1686318946" MODIFIED="1229296082569" STYLE="fork" TEXT="ParserWrapper">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_748468191" MODIFIED="1228886948392" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A simple class for parser invocation.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710484" ID="ID_1399153137" MODIFIED="1229296082572" STYLE="fork" TEXT="PatternCondition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1988650490" MODIFIED="1228887018142" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Abstract condition for all regex-based conditions.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710486" ID="ID_568195630" MODIFIED="1229296082576" STYLE="fork" TEXT="Property">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1767829030" MODIFIED="1228887062400" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This interface is empty and contains a long and boring chunk of Javadoc.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710488" ID="ID_901899198" MODIFIED="1229296082582" STYLE="fork" TEXT="QualifiedCondition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1706881315" MODIFIED="1228887365561" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This encapsulates one condition and a qualifier: <font face="DejaVu Sans Mono">ContainerProperty</font> or <font face="DejaVu Sans Mono">ArrayProperty</font>. Evaluated as OR... (Don't know what that really means.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710519" ID="ID_963040136" MODIFIED="1229296082586" STYLE="fork" TEXT="RSLSemanticException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_153853457" MODIFIED="1228887403724" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Semantic error. (Those that cannot be detected on lexical or grammar levels.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710490" ID="ID_551774285" MODIFIED="1229296082591" STYLE="fork" TEXT="SimpleCharStream">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1270866258" MODIFIED="1228887700059" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A stream of characters that does not care about Unicode. What's that good for? That's a standard and mandatory part of the JavaCC parser.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710492" ID="ID_1518753253" MODIFIED="1229296082602" STYLE="fork" TEXT="SimpleCondition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_766397092" MODIFIED="1228887498692" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Ancestor of all conditions of a certain form with a common semantic checker. This is rather technical stuff. The condition hierarchy seems weird to me anyway.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710494" ID="ID_332207588" MODIFIED="1229296082607" STYLE="fork" TEXT="SimpleProperty">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_89902838" MODIFIED="1228887792420" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Interface to a property of arbitrary type. Class and value are returned separately. BTW, is that really necessary? What if those special properties could be defined using generic types or some Jyton types impoted to Java?
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710496" ID="ID_629632708" MODIFIED="1229296082611" STYLE="fork" TEXT="Token">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1498829633" MODIFIED="1228887899370" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Contains a single token. Standard part of the JavaCC parser.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228158710498" ID="ID_1421491628" MODIFIED="1229296082615" STYLE="fork" TEXT="TokenMgrError">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1893302665" MODIFIED="1228888001736" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Another standard part of the parser. Why does it use errors? That's something strange. An exception would do.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228084813803" ID="Freemind_Link_1979920901" MODIFIED="1229296082635" POSITION="right" STYLE="bubble" TEXT="hostmanager">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228154098810" ID="ID_549428672" MODIFIED="1229296082638" STYLE="fork" TEXT="HostDatabaseException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1805837518" MODIFIED="1228230327073" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A fatal database error (I/O error and the like).
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228154129157" ID="ID_1326565773" MODIFIED="1229296082642" STYLE="fork" TEXT="HostManagerApplicationData">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_633954923" MODIFIED="1228230425613" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Metadata of a service used by various parts of the <font face="DejaVu Sans Mono">HostManager</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228154138051" ID="ID_296537607" MODIFIED="1229296082648" STYLE="fork" TEXT="HostManagerEvent">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1802031675" MODIFIED="1228230542619" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A simple record of an event that occured in the <font face="DejaVu Sans Mono">HostManager</font>. Used for all types of events except those generated by the load monitor.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228230547944" ID="ID_1355100540" MODIFIED="1229296082653" STYLE="fork" TEXT="EventType">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_954587161" MODIFIED="1228230664734" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enum of all possible <font face="DejaVu Sans Mono">HostManager</font> event types. This time I don't complain about C-like enums. ;-) Enums are static structures, whereas all the event data changes from event to event.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228155463966" ID="ID_961105094" MODIFIED="1229296082661" STYLE="fork" TEXT="HostManagerEventListener">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1054762411" MODIFIED="1228230839377" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A listener accepting <font face="DejaVu Sans Mono">HostManagerEvent</font>s. A self-destruction method is available so the listener can unregister itself.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_954762624" MODIFIED="1228231147450" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This should be an interface. See the SWT design for reference.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228154163695" ID="ID_1776839385" MODIFIED="1229296082672" STYLE="fork" TEXT="HostManagerException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_839816831" MODIFIED="1228231034240" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Ancestor of all <font face="DejaVu Sans Mono">HostManager</font>-related exceptions. Should be used with caution...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228154179604" ID="ID_1122390274" MODIFIED="1229296082693" STYLE="fork" TEXT="HostManagerImplementation">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_417538922" MODIFIED="1228232647159" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Implements <b>four</b> important interfaces from the HostManager infrastructure: <font face="DejaVu Sans Mono">HostManagerInterface</font>, <font face="DejaVu Sans Mono">DatabaseManagerInterface</font>, <font face="DejaVu Sans Mono">HostRuntimeRegistrationListener</font> and <font face="DejaVu Sans Mono">HostManagerOptions.ValueChangedListener</font>.
    </p>
    <p>
      
    </p>
    <p>
      This class handles all the host manipulation, starts detectors and maintains group data. It also stores various run-time parameters and listens to changes.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_308260574" MODIFIED="1228233925637" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Just a stupid technical note: Using @see in a non-Javadoc comment serves no purpose...
    </p>
    <p>
      
    </p>
    <p>
      Synchronization... Well, more documentation or a Petri net would be fine. <font face="DejaVu Sans Mono">ConcurrentHashMap</font>s are somewhat confusing. For example, using a custom structure to hold the detector results would make it possible to avoid synchronization in <font face="DejaVu Sans Mono">uploadHostData()</font>. BTW, that method needs a thorough code cleanup.
    </p>
    <p>
      
    </p>
    <ol>
      <li>
        This class <b>must</b> be split into multiple parts! That's a not a discussion topic, that's a fact.
      </li>
      <li>
        <font face="DejaVu Sans Mono">catch ( Exception e )</font> ... Is that really necessary? This appears multiple times. All the catch blocks need revision.
      </li>
      <li>
        Method access permissions are somewhat confusing...
      </li>
      <li>
        The source file should contain nested classes, and other code sorted by access permissions... This looks like a random mixture.
      </li>
    </ol>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228154179604" ID="ID_999416683" MODIFIED="1229296082701" STYLE="fork" TEXT="NewHostEventListener">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_874815118" MODIFIED="1228234366832" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A hack to support &quot;nested&quot; operations... If I understand it well, it waits for a &quot;partial&quot; operation and proclaims the surrounding operation complete as soon as the partial one has completed.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1019142632" MODIFIED="1228234426077" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Three if/then/else branches, all containing the line <font face="DejaVu Sans Mono">removeMe()</font>... What's the point? ;-)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228154179604" ID="ID_1342719977" MODIFIED="1229296082705" STYLE="fork" TEXT="PendingHostInfo">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_543221384" MODIFIED="1228234531322" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Javadoc: Information about a host that has some add/refresh operation pending. This time it's true. :-)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228154179604" ID="ID_1961272461" MODIFIED="1229296082710" STYLE="fork" TEXT="WatchDog">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_910128351" MODIFIED="1228234751562" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">TimerTask</font> scheduled each time a detector is run on a new host. Hosts that complete their detector run after a deadline or not at all will be excluded from the list.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228155444914" ID="ID_1919383187" MODIFIED="1229296082715" STYLE="fork" TEXT="HostManagerInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1067147955" MODIFIED="1228231230976" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A nice overview of what the <font face="DejaVu Sans Mono">HostManager</font> is about. Defines methods for manipulating and querying hosts, groups and software aliases. It also provides a RMI link to the load server and means for registering and unregistering listeners.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155549224" ID="ID_888281608" MODIFIED="1229296082726" STYLE="fork" TEXT="HostManagerLogger">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1385532098" MODIFIED="1228235578007" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      According to Javadoc, this is an adapter to the generic BEEN logger. How can it be an adapter without implementing an interface or (at least) inheriting from an abstract class???
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_272753360" MODIFIED="1228235642536" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This code is repeated multiple times within BEEN! <b>The logging infrastructure must be unified.</b>
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228154179604" ID="ID_1749920703" MODIFIED="1229296082752" STYLE="fork" TEXT="LogLevels">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1244456470" MODIFIED="1228236964087" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      List of log levels. Exactly the <b>same</b> thing is in been.common and been.logging.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1352385466" MODIFIED="1228237005933" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This should NOT be hidden inside a class. It should be visible to everyone and coded just <b>once</b>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228164700637" ID="ID_1543848333" MODIFIED="1228237740865" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <ol>
      <li>
        Rewritten, especially the <font face="DejaVu Sans Mono">fromTaskLogLevel()</font> mapping method. See how simple it can be! The former code was somewhat weird.
      </li>
      <li>
        Admittedly, this required to convert been.logging.LogLevel from a terrible enum-like Java 1.4 style to a real enum.
      </li>
    </ol>
    <p>
      <b>What a Java coder needs to know about enums: </b>
    </p>
    <ul>
      <li>
        An enum is not a list of integer values. It is not a bunch of instances of one class either! <b>Today's Java enum is a bunch of singletons with a common ancestor.</b>
      </li>
      <li>
        Enums <b>can</b> implement methods.
      </li>
      <li>
        Enums <b>do</b> have constructors. These are called initializers and run only once per enum member and JVM lifetime.
      </li>
      <li>
        The notes above have an important consequence: Enum members can be assigned multiple values at compile time and can implement methods, too. When you use their stored constants and/or methods, you do <b>NOT</b> need to code a <font face="DejaVu Sans Mono">Map</font>, a <font face="DejaVu Sans Mono">switch</font> or anything of that kind. Enums just work all by themselves. Virtual method invocation does the trick for you.
      </li>
    </ul>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228155560646" ID="ID_75757853" MODIFIED="1229296082757" STYLE="fork" TEXT="HostManagerOptions">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_130915536" MODIFIED="1228237813927" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A data container for the host manager and load server. Holds some important strings and time intervals. Capable of storing and retrieving configuration to/from files.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155568101" ID="ID_652892220" MODIFIED="1229296082764" STYLE="fork" TEXT="HostManagerOptionsInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_889519748" MODIFIED="1228237906770" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Sets what a good option manager should know. :-) <font face="DejaVu Sans Mono">HostManagerOptions</font> implements this.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228154179604" ID="ID_532404971" MODIFIED="1229296082768" STYLE="fork" TEXT="Option">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1463922741" MODIFIED="1228237986233" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      An enum of basic options and their default values. This time well-defined with initializers. :-)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228155582502" ID="ID_118448827" MODIFIED="1229296082774" STYLE="fork" TEXT="HostManagerService">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1134261364" MODIFIED="1228238204624" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">Task</font> wrapper for the <font face="DejaVu Sans Mono">HostManager</font>. Implements start() and stop() which are huge, complicated and rather technical methods.
    </p>
    <p>
      
    </p>
    <p>
      Won't add a TODO comment this time, but it's still the same problem: Huge amount of logging and exception-catching stuff mixed with other code is hard to read. Classes should be divided into layers of &quot;throwers&quot; and &quot;catchers&quot; if possible.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155602983" ID="ID_706885466" MODIFIED="1229296082780" STYLE="fork" TEXT="HostManagerOperationStatus">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_419732479" MODIFIED="1228238346757" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Just a data container for some three strings and one enum value.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228154179604" ID="ID_781977644" MODIFIED="1229296082784" STYLE="fork" TEXT="Option">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_984092800" MODIFIED="1228238302845" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Possible status of the operation. This is the second time I see an enum used appropriately in BEEN. What if the code is not that bad? :-D
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228155621383" ID="ID_1812883624" MODIFIED="1229296082789" STYLE="fork" TEXT="HostQueryCallbackInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1357114301" MODIFIED="1228238491894" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      One method: <font face="DejaVu Sans Mono">match()</font>. Decides whether the supplied <font face="DejaVu Sans Mono">HostInfoInterface</font> matches the given criteria.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155635786" ID="ID_1356506532" MODIFIED="1229296082799" STYLE="fork" TEXT="IllegalOperationException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1871192130" MODIFIED="1228238544644" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A generic exception thrown when the protocol (order of operations and states) is violated.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155653863" ID="ID_1068054404" MODIFIED="1229296082808" STYLE="fork" TEXT="InputParseException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_343024616" MODIFIED="1228238570136" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Error during file parsing.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_4661769" MODIFIED="1228238666464" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      I have already seen file access and parsing many times in the code. All this stuff should be implemented using a unified file reader that would accept parser callbacks. Currently, many types of file access throwing many types of exceptions can be seen.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155661453" ID="ID_495805855" MODIFIED="1229296082815" STYLE="fork" TEXT="InvalidArgumentException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1371396286" MODIFIED="1228238770818" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is what would be called <font face="DejaVu Sans Mono">IllegalArgumentException</font> in the standard Java libraries.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155678299" ID="ID_946890818" MODIFIED="1229296082824" STYLE="fork" TEXT="OperationHandle">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_658934673" MODIFIED="1228238867270" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Link to an operation. BTW, a handle without synchronization and a static counter sounds weird, doesn't it...?
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1047725249" MODIFIED="1228238911528" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      <font face="DejaVu Sans Mono">getNext()</font>: <b>What on Earth is that???</b> Does that work?
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155704134" ID="ID_1222027102" MODIFIED="1229296082828" STYLE="fork" TEXT="OutputWriteException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1591686629" MODIFIED="1228238956318" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Thrown when a file output error occurs. Again, this would deserve a unified file output mechanism...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155724275" ID="ID_1522403515" MODIFIED="1229296082833" STYLE="fork" TEXT="ValueNotFoundException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_269373929" MODIFIED="1228239065613" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      When a property with the given name does not exist. Hides implementation details of the mapping.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155738856" ID="ID_223340840" MODIFIED="1229296082843" STYLE="fork" TEXT="ValueTypeIncorrectException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_976792909" MODIFIED="1228239138841" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Self-explanatory, isn't it? ;-) This reminds me that a lot of refactoring should be done, as the names of some classes don't match their purpose any more.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228085450897" ID="Freemind_Link_1453601676" MODIFIED="1229296082871" STYLE="bubble" TEXT="database">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228157843120" ID="ID_1256661236" MODIFIED="1229296082883" STYLE="fork" TEXT="AlternativeRestriction">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1924263265" MODIFIED="1229249804748" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Multiple <font face="DejaVu Sans Mono">ObjectRestriction</font>s with an OR perator. Implements methods from <font face="DejaVu Sans Mono">XMLSerializableInterface</font>. (It can become a XML node or be recovered therefrom.)
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_363858299" MODIFIED="1229296082889" STYLE="fork" TEXT="BeenDisk">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_433122770" MODIFIED="1229250148265" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stores information about the disk BEEN is installed on (size and free space). Has XML persistence again, this time implemented quite well. :-)
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_878843067" MODIFIED="1229296082892" STYLE="fork" TEXT="Properties">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1224251929" MODIFIED="1229250039944" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A useless class storing three string constants.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843041" ID="ID_1168039463" MODIFIED="1229296082901" STYLE="fork" TEXT="DatabaseIndex">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_555854782" MODIFIED="1229250950555" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is not a B-tree, as the name would suggest. ;-) It stores names of the database files. Stores two mappings (hosts to files and groups to files). Implements XML persistence. Everything is stored to disk immediately, using XML again. The Javadoc talks about synchronization of some sort, but I can't see anything of that kind there.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228164700637" ID="ID_1715186239" MODIFIED="1229251097785" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Useless direct use of iterators converted into colon for-cycles.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843128" ID="ID_7082123" MODIFIED="1229296082905" STYLE="fork" TEXT="DatabaseManagerInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1669258390" MODIFIED="1229251170462" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A small interface (one method) used internally by detectors to upload host data into the host manager database.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843045" ID="ID_1139542307" MODIFIED="1229296082911" STYLE="fork" TEXT="DiskDrive">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_21103766" MODIFIED="1229251463085" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stores complete info on disk drives. It's a <font face="DejaVu Sans Mono">PropertyTree</font> extension.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_47051361" MODIFIED="1229296082915" STYLE="fork" TEXT="Properties">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1420831271" MODIFIED="1229251375861" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Again, just a container for property names (<font face="DejaVu Sans Mono">String</font> constants).
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843047" ID="ID_1046584995" MODIFIED="1229296082919" STYLE="fork" TEXT="DiskPartition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_26026799" MODIFIED="1229251523395" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Information about a disk partition.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_1128099196" MODIFIED="1229296082922" STYLE="fork" TEXT="Properties">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_352656691" MODIFIED="1229251540560" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Names of the properties.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843053" ID="ID_1683128915" MODIFIED="1229296082928" STYLE="fork" TEXT="GroupIndexEntry">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_978696948" MODIFIED="1229251626538" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Information about one group with persistence stuff. Stores group name and group data file name. (XML is used everywhere.)
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843055" ID="ID_1830086378" MODIFIED="1229296082956" STYLE="fork" TEXT="HostDatabaseEngine">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1220307198" MODIFIED="1229252383471" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A huge class that stores data about all hosts and performs all the underlying file operations. BTW, the host manager has a well-designed logging infrastructure... Quite an elaborate way of restoring the XML data. Can create the database, add and remove hosts, and answer a big bunch of various queries. Implements all this once again for groups.
    </p>
    <p>
      
    </p>
    <p>
      What's quite important here: can return an array of matching hosts based on a supplied RSL condition. That's how suitable hosts are chosen.
    </p>
    <p>
      
    </p>
    <p>
      Furthermore, manages software aliases. That's quite a lot of code, too.
    </p>
    <p>
      
    </p>
    <p>
      There are some low-level methods for direct file manipulation. Generates unique file names somehow.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_617098855" MODIFIED="1229254768135" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Utility method <font face="DejaVu Sans Mono">findInArray()</font> reimplemented multiple times in this package. Not only is it an awful and terrible linear time function, but it should be somewhere in the array utils classes, not hidden here. :-(
    </p>
    <p>
      
    </p>
    <p>
      This thing implements so many unrelated operations that it simply must be split...
    </p>
    <p>
      
    </p>
    <p>
      Returns two kinds of <font face="DejaVu Sans Mono">Iterator</font>s. It would be much better to return whole <font face="DejaVu Sans Mono">Iterable</font>s instead. That makes for-cycles shorter and easily readable.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_1700804539" MODIFIED="1229296082961" STYLE="fork">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      PropertyTreeDataProvider
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1101040842" MODIFIED="1229254523150" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">DataProvider</font> that reads from a <font face="DejaVu Sans Mono">PropertyTreeReadInterface</font>. (That's an interface for read-only accers to property trees.)
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_700352059" MODIFIED="1229296082964" STYLE="fork" TEXT="ExtensionFileFilter">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1304017573" MODIFIED="1229254618040" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A trivial implementation of <font face="DejaVu Sans Mono">FilenameFilter</font>.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_611399943" MODIFIED="1229296082972" STYLE="fork" TEXT="ReadOnlyIterator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1942539209" MODIFIED="1229254910595" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Abstract ancestor of read-only iterators. Implements a final <font face="DejaVu Sans Mono">remove()</font> method that just throws and exception.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1725887173" MODIFIED="1229254819413" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This should be moved to common utilities.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_843973137" MODIFIED="1229296082975" STYLE="fork" TEXT="GroupNameGIterator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_920636295" MODIFIED="1229255002322" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Iterates through group names.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_1715178360" MODIFIED="1229296082978" STYLE="fork" TEXT="HostNameIterator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_866696149" MODIFIED="1229255013133" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Iterates through host names.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843057" ID="ID_277907973" MODIFIED="1229296082984" STYLE="fork" TEXT="HostGroup">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_316941700" MODIFIED="1229255386703" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Represents one group of hosts. Groups are based solely on user's choice. Each host is a member of at least one group. The relation between hosts and groups is N:N. XML storage here, too.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_1101113445" MODIFIED="1229296082991" STYLE="fork" TEXT="HopstGroupIterator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_656703339" MODIFIED="1229255295622" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Iterates through the list of hosts in a group.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1677743957" MODIFIED="1229255355859" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A bad design under stress in practice! This class should obviously extend <font face="DejaVu Sans Mono">ReadOnlyIterator</font>, but it does nopt, as ReadOnlyIterator is nested inside a different class. Hernajs!
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843059" ID="ID_1115981685" MODIFIED="1229296082996" STYLE="fork" TEXT="HostIndexEntry">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1176508535" MODIFIED="1229255469537" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Index entry for one host. Stores all the data about one host to XML. Can query and manipulate the host's history records, which is probably the main purpose of this class.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843062" ID="ID_221938881" MODIFIED="1229296083422" STYLE="fork" TEXT="HostInfo">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1003475817" MODIFIED="1229258024175" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stores all the data collected by detectors. Consists of collections of those classes named after hardware parts. ;-) Apart from that, one can also set and remove user properties assigned to the given host. Links the given host to a BeenDisk instance. Stores some software aliases stuff, too. Supports many queries and implements XML storage.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228164700637" ID="ID_1959269571" MODIFIED="1229258132305" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <ol>
      <li>
        Some four for-cycles converted into a more readable form.
      </li>
      <li>
        Code maintenance related to the conversion of the Detectors class to an enum. That stuff seems to be more intelligent right now. Some functionality moved to the enum.
      </li>
    </ol>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843069" ID="ID_1282373720" MODIFIED="1229296083427" STYLE="fork" TEXT="HostInfoBuilder">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1223951511" MODIFIED="1229258225537" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A small wrapper that does nothing but storing and loading instances of <font face="DejaVu Sans Mono">HostInfo</font>.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843071" ID="ID_1331563430" MODIFIED="1229296083440" STYLE="fork" TEXT="HostInfoInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_551697489" MODIFIED="1229258351692" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Lists everything a well-behaved HostInfo must know. Requires methods for hardware info manipulation, user properties and software aliases storage and retrieval and corresponding parameter names.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1643095453" MODIFIED="1229258729773" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      <b>An obvious occurence of constant iterface antipattern! </b>
    </p>
    <p>
      
    </p>
    <p>
      This project badly needs a unified parameter-&gt;value management where these weird things just do not happen.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_657096582" MODIFIED="1229296083448" STYLE="fork" TEXT="Properties">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1513705600" MODIFIED="1229258473879" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Names of properties that can be asked for. Confusing when Detectors have been converted to an enum.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_225977690" MODIFIED="1229258499122" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Ooops! This is an antipattern.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_298524193" MODIFIED="1229296083456" STYLE="fork" TEXT="Objects">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_344370741" MODIFIED="1229258689901" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      List of objects that can be requested by name. (Basically, these are various classes representing hardware parts.) Confusing after Detectors have been converted into an enum.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_859020801" MODIFIED="1229258499122" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Ooops! This is an antipattern.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_28919401" MODIFIED="1229296083464" STYLE="fork" TEXT="Detectors">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1418647584" MODIFIED="1229258549546" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enum of all the possible detectors, implements reverse-mapping of string names and some common functionality.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228164700637" ID="ID_1137632158" MODIFIED="1229258624702" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This used to be a class. Not only was it an antipattern, but its functionality was limited compared to an enum with built-in rervese mapping. So I reimplemented that stuff.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843073" ID="ID_1755747684" MODIFIED="1229296083470" STYLE="fork" TEXT="JavaInfo">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1646880877" MODIFIED="1229259076328" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Information about JVM implementation on host. With the common XML stuff, of course.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_1283882399" MODIFIED="1229296083474" STYLE="fork" TEXT="Properties">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_866142846" MODIFIED="1229259184480" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Names of properties. This is used everywhere and should be converted into an enum...
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843130" ID="ID_793195972" MODIFIED="1229296083483" STYLE="fork" TEXT="LinuxOperatingSystem">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1714729341" MODIFIED="1229259282574" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Linux system data plus XML routines.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_658808187" MODIFIED="1229296083487" STYLE="fork" TEXT="Properties">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1854638885" MODIFIED="1229259184480" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Names of properties. This is used everywhere and should be converted into an enum...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843075" ID="ID_768076642" MODIFIED="1229296083492" STYLE="fork" TEXT="Memory">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1561541480" MODIFIED="1229259370881" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Memory information + XML.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_406803947" MODIFIED="1229296083495" STYLE="fork" TEXT="Properties">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1894485062" MODIFIED="1229259184480" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Names of properties. This is used everywhere and should be converted into an enum...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843133" ID="ID_1194875099" MODIFIED="1229296083500" STYLE="fork" TEXT="ModifiableInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1040171217" MODIFIED="1229259538584" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A trivial interface for setting and resetting a flag. Not sure whether synchronization was born in mind. Especially the second should check (first of all) whether the caller has this object's monitor.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843077" ID="ID_683989166" MODIFIED="1229296083505" STYLE="fork" TEXT="NameValuePair">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_65935341" MODIFIED="1229259685295" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Something like Entry from collections. An extension to <font face="DejaVu Sans Mono">Pair&lt; String, ValueCommonInterface &gt;</font> with some XML-related stuff.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843079" ID="ID_328869155" MODIFIED="1229296083509" STYLE="fork" TEXT="NetworkAdapter">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_619839854" MODIFIED="1229259719783" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Netcard details + XML.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_1891851034" MODIFIED="1229296083513" STYLE="fork" TEXT="Properties">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1590644375" MODIFIED="1229259184480" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Names of properties. This is used everywhere and should be converted into an enum...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843082" ID="ID_1575471313" MODIFIED="1229296083520" STYLE="fork" TEXT="ObjectRestriction">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1483498870" MODIFIED="1229260602572" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This thing just stores data, it does not have much in common with RSL. (There have already been at least three classes of a similar type. (See above.))
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1832040188" MODIFIED="1229260677712" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Tha <font face="DejaVu Sans Mono">isInArray()</font> really sucks, to say the least...
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843085" ID="ID_865719409" MODIFIED="1229296083529" STYLE="fork" TEXT="OperatingSystem">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_913620401" MODIFIED="1229260861388" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Ancestor of operating systems. Just a little bit modified data class. This is where it's obvious why those constant strings are divided into two separate classes. But all that stuff should be in enums!
    </p>
    <p>
      
    </p>
    <p>
      BTW, why does <font face="DejaVu Sans Mono">WindowsOperatingSystem</font> inherit from this? Why is that class called like that? If I call something <font face="DejaVu Sans Mono">*OperatingSystem</font>, it's supposed to describe an operating system...
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_1509292862" MODIFIED="1229296083533" STYLE="fork" TEXT="Properties">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_52016777" MODIFIED="1229259184480" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Names of properties. This is used everywhere and should be converted into an enum...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_867727514" MODIFIED="1229296083537" STYLE="fork" TEXT="Objects">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1896264691" MODIFIED="1229260928837" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Names of objects one can get from this data container.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843087" ID="ID_1660410472" MODIFIED="1229296083551" STYLE="fork" TEXT="Processor">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_323864557" MODIFIED="1229261448875" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Procesor data + XML stuff.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157843039" ID="ID_138223918" MODIFIED="1229296083555" STYLE="fork" TEXT="Properties">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_492103048" MODIFIED="1229259184480" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Names of properties. This is used everywhere and should be converted into an enum...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843090" ID="ID_1295756661" MODIFIED="1229296083559" STYLE="fork" TEXT="Product">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_299626029" MODIFIED="1229261499801" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Info on software products installed on the system and its presistence companion...
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843135" ID="ID_256595025" MODIFIED="1229296083563" STYLE="fork" TEXT="PropertyDescription">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1434904111" MODIFIED="1229261581089" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is a piece of metadata. Describes one single property in the host database, its name, path, description etc.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843137" ID="ID_1808880931" MODIFIED="1229296083570" STYLE="fork" TEXT="PropertyDescriptionTable">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1066235488" MODIFIED="1229261970256" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Manages all properties, maps from strings (probably property paths) to property descriptions and stores all that stuff to XML.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228164700637" ID="ID_110963191" MODIFIED="1229261856143" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A weird for-cycle transformed.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843092" ID="ID_847476151" MODIFIED="1229296083581" STYLE="fork" TEXT="PropertyTree">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_786661156" MODIFIED="1229262144581" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Basically, two mappings: strings to properties and strings to nested property trees. Quite a lot of constructors. This thing is a <font face="DejaVu Sans Mono">Modifiable</font>. Supports modification and queries. (Well, presumably...) Implements all that twice, for objects and properties!
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_804430936" MODIFIED="1229262275704" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Those getters could easily be generic, one instance parametrized with <font face="DejaVu Sans Mono">Map&lt; String, ValueCommonInterface &gt;</font> and the second one with <font face="DejaVu Sans Mono">Map&lt; String, ArrayList&lt; PropertyTreeInterface &gt; &gt;</font>. This would avoid tons of repeated code and bring a tiny little bit of elegance, law and order.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843138" ID="ID_544390647" MODIFIED="1229296083586" STYLE="fork" TEXT="PropertyTreeFactory">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_978516186" MODIFIED="1229262453452" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This does not seem to be more than just a simple wrapper. <font face="DejaVu Sans Mono">PropertyTree</font> is a package private class, so this is the only way to get to its constructors from outside. Some of them are meant to be hidden, as they are only used to create internal subtrees.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843140" ID="ID_1752706752" MODIFIED="1229296083590" STYLE="fork" TEXT="PropertyTreeInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_852400918" MODIFIED="1229262563541" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is what PropertyTrees do, documented in detail. Those methods manipulate both data and metadata and must support lots of queries, too.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843142" ID="ID_345636613" MODIFIED="1229296083607" STYLE="fork" TEXT="PropertyTreeReadInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1989868603" MODIFIED="1229262617703" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Ancestor of PropertyInterface. Just a read-only version of the same thing.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_958808241" MODIFIED="1229262946675" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      <b>This is an obvious antipattern.</b> To provide restricted access, you need a separate interface and provide a (nested anonymous) implementation of that interface. The point is that the read-only interface should be provided by a totally different class.
    </p>
    <p>
      
    </p>
    <p>
      Like this, anyone can just cast and gain read/write access.
    </p>
    <p>
      
    </p>
    <p>
      But OK, we are not developing an enterprise-class library. I do not expect things like this to be fixed before we finish the basic project work. But think about this thoroughly when developing your new code and <b>please</b> do not make the same mistake.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843144" ID="ID_336810384" MODIFIED="1229296083613" STYLE="fork" TEXT="RestrictionInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1751081962" MODIFIED="1229263104298" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Does nothing but extending <font face="DejaVu Sans Mono">Serializable</font> and <font face="DejaVu Sans Mono">XMLSerializableInterface</font>.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843094" ID="ID_636633049" MODIFIED="1229296083621" STYLE="fork" TEXT="RSLRestriction">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_405433069" MODIFIED="1229263844163" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is a much more intelligent restriction than just a simple OR condition. With lots of technical stuff around, of course. Implements <font face="DejaVu Sans Mono">RestrictionInterface</font>. Confusing internal helper classes, generics misused.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157843144" ID="ID_158147583" MODIFIED="1229296083630" STYLE="fork" TEXT="HostManagerSimpleProperty">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1125228589" MODIFIED="1229267469873" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A weird wrapper for properties of any type that obviously breaks the well-designed property system and introduces terrible misuse of generics and lots of explicite casting.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_967240483" MODIFIED="1229267519211" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This must be redesigned, but I dunno how. When you read hundreds of thousands of lines, you can't think of each of them precisely.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228157843144" ID="ID_959295875" MODIFIED="1229296083634" STYLE="fork" TEXT="ValueGetter">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_642311400" MODIFIED="1229268195502" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A tiny generic class that can return just about anything. This one exists due to a design flaw.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843144" ID="ID_273173859" MODIFIED="1229296083639" STYLE="fork" TEXT="HostManagerArrayProperty">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1306380565" MODIFIED="1229267851693" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This implements <font face="DejaVu Sans Mono">ArrayProperty</font> and returns an array of <font face="DejaVu Sans Mono">ContainerProperty</font>, here implemented by <font face="DejaVu Sans Mono">HostManagerContainerProperty</font>.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843144" ID="ID_1947302464" MODIFIED="1229296083648" STYLE="fork" TEXT="HostManagerContainerProperty">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1529894327" MODIFIED="1229267991882" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The container (tuple) node. In fact, those inner classes follow (and reimplement, in a way) the whole property tree structure, using a form that is somewhat closer to RSL so that RSL based conditions can be easily used and evaluated.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1244348109" MODIFIED="1229268084149" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This should be unified somehow. RSL should use same data types and data structures as packages that used (or a subset thereof). This introduces complicated and inefficient facade layers.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843096" ID="ID_1993292593" MODIFIED="1229296083653" STYLE="fork" TEXT="SimpleHostInfo">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_277046444" MODIFIED="1229268361816" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The basic subset of host information needed to identify the host on the network. Javadoc says that bandwidth could be saved by not transferring the complete host data. :-D Is that a joke?
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843098" ID="ID_575931156" MODIFIED="1229296083658" STYLE="fork" TEXT="SoftwareAlias">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1869433016" MODIFIED="1229268412102" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Human-readable software alias representation. It's nothing but another <font face="DejaVu Sans Mono">PropertyTree</font>.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843147" ID="ID_1276883201" MODIFIED="1229296083669" STYLE="fork" TEXT="SoftwareAliasDefinition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1364128004" MODIFIED="1229268858780" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      One alias name can be mapped to multiple different software packages that have somehing important in common. (They provide the same functionality, implement the same interface etc.) This mapping is the main purpose of the class. An instance is initialized with some importants strings and two restrictions: one for the OS and one for the application.
    </p>
    <p>
      
    </p>
    <p>
      Long XML and data integrity stuff.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843118" ID="ID_1073570527" MODIFIED="1229296083674" STYLE="fork" TEXT="SoftwareAliasList">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1229782690" MODIFIED="1229268898341" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This class stores software alias definitions and implements file input/output.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843149" ID="ID_467448811" MODIFIED="1229296083677" STYLE="fork" TEXT="SolarisOperatingSystem">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1976536480" MODIFIED="1229268955531" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Data about the best OS on the Planet.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843151" ID="ID_279393605" MODIFIED="1229296083681" STYLE="fork" TEXT="UnknownOperatingSystem">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1544205241" MODIFIED="1229269011220" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Info about a completely unknown OS. Surprisingly, this class is longer than <font face="DejaVu Sans Mono">SolarisOperatingSystem</font>!
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843154" ID="ID_1478279606" MODIFIED="1229296083686" STYLE="fork" TEXT="WindowsOperatingSystem">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1853397584" MODIFIED="1229269068241" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Information about a Windows system. Contains XML storage/retrieva, as usual...
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157843156" ID="ID_1050609124" MODIFIED="1229296083690" STYLE="fork" TEXT="XMLSerializableInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1079781758" MODIFIED="1229269129310" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Instances of classes who implement this can be stored and loaded to/from a XML element.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228085456576" ID="Freemind_Link_1607176209" MODIFIED="1229296083692" STYLE="bubble" TEXT="detectors">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#0033ff" CREATED="1228157862810" ID="ID_691433487" MODIFIED="1229269161268" STYLE="bubble" TEXT="linux">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
</node>
<node COLOR="#0033ff" CREATED="1228157862998" ID="ID_231127466" MODIFIED="1229269161268" STYLE="bubble" TEXT="windows">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228085480748" ID="Freemind_Link_1669851985" MODIFIED="1229296083700" STYLE="bubble" TEXT="examples">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228157885656" ID="ID_1872497919" MODIFIED="1229296083705" STYLE="fork" TEXT="AddHost">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_298882364" MODIFIED="1229269564839" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A terribly bloated example of how to add a host to the host manager. It includes the technical stuff. There's an awful busy waiting of some kind, but don't ask me why. :-D Presumably, this is a <font face="DejaVu Sans Mono">main()</font> class.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157885659" ID="ID_519853031" MODIFIED="1229296083709" STYLE="fork" TEXT="Enumerate">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1474828216" MODIFIED="1229269581892" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Another <font face="DejaVu Sans Mono">main()</font> class. I'd guess this just takes host properties from the database and pretty-prints them.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157885661" ID="ID_1842957140" MODIFIED="1229296083712" STYLE="fork" TEXT="GroupsBasic">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_578033481" MODIFIED="1229269658784" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">main()</font> overview of group manipulation.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157885677" ID="ID_1769768578" MODIFIED="1229296083719" STYLE="fork" TEXT="GroupsQueryInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_342758441" MODIFIED="1229269832850" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Creates two groups of hosts: Those that have more than 200 GB of free space and those with more than 500 GB. (Recall that groups can overlap. (And almost certainly will overlap in this case.) An example use of <font face="DejaVu Sans Mono">HostQueryCallbackInterface</font>.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157885677" ID="ID_1001060149" MODIFIED="1229296083723" STYLE="fork" TEXT="HostFilter">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_575304908" MODIFIED="1229269906932" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Implements <font face="DejaVu Sans Mono">HostQueryCallbackInterface</font>. Filters out hosts with insufficient disk space.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157885663" ID="ID_1803923076" MODIFIED="1229296083734" STYLE="fork" TEXT="GroupsRestrictions">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_348164833" MODIFIED="1229270022402" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This <font face="DejaVu Sans Mono">main()</font> creates a group of hosts that have at least one compiler installed.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157885666" ID="ID_1654449097" MODIFIED="1229296083739" STYLE="fork" TEXT="History">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_232452248" MODIFIED="1229270135087" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Shows manipulation with the configuration of the current host. A main() again. Configuration history is important, as it makes it possible to evaluate the impact of the amount of RAM on overall benchmark results, for example.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157885668" ID="ID_502808864" MODIFIED="1229296083743" STYLE="fork" TEXT="ListProperties">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1854920665" MODIFIED="1229270197847" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Retrieves properties (and values) of the first host in the database. <font face="DejaVu Sans Mono">main()</font>...
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157885680" ID="ID_583474309" MODIFIED="1229296083748" STYLE="fork" TEXT="PropertyDescriptions">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_21652234" MODIFIED="1229270310600" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">main()</font> that outputs descriptions and other metadata on both properties and objects (which are those weird almost hidden non-string properties) from the database.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157885670" ID="ID_1050282435" MODIFIED="1229296083758" STYLE="fork" TEXT="RestrictionsTest">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_823915622" MODIFIED="1229270457562" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">main()</font> that simply finds all hosts that are not called <font face="DejaVu Sans Mono">www.google.com</font>. A host manager URL can be supplied on the command line.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157885673" ID="ID_1334045311" MODIFIED="1229296083764" STYLE="fork" TEXT="SoftwareAliases">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_183094794" MODIFIED="1229270744178" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Shows how software aliases work. This <font face="DejaVu Sans Mono">main()</font><font face="DejaVu Sans Condensed"> </font>examines a (somewhat impractical) alias that matches just anything from Sun Microsystems. (This will match Java on all the machines.)
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157885675" ID="ID_662768161" MODIFIED="1229296083768" STYLE="fork" TEXT="UserProperties">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1223116025" MODIFIED="1229270817056" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Retrieves and outputs all the user-defined properties of the first host in the database.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228085485075" ID="Freemind_Link_505826853" MODIFIED="1229296083788" STYLE="bubble" TEXT="load">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228157902692" ID="ID_1479141379" MODIFIED="1229296083796" STYLE="fork" TEXT="ActivityMonitor">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_853937629" MODIFIED="1229272124005" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Monitors connecting and disconnecting of hosts. Connects a listener to the load monitor, which is <b>suspicious</b>. It seems to me that listeners are used incorrectly here... A listener is always a callback. In other words, it should be connected the other way round. But I'm not sure about that.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157902692" ID="ID_1836805784" MODIFIED="1229296083811" STYLE="fork">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      ActivityMonitorThread
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1619935713" MODIFIED="1229272124005" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Monitors connecting and disconnecting of hosts. Connects a listener to the load monitor, which is <b>suspicious</b>. It seems to me that listeners are used incorrectly here... A listener is always a callback. In other words, it should be connected the other way round. But I'm not sure about that.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228164700637" ID="ID_532574825" MODIFIED="1229272503570" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Modified the cache to support Iterable directly. That made it possible to get away with the iterator.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902725" ID="ID_1705084767" MODIFIED="1229296083818" STYLE="fork" TEXT="ActivityMonitorCache">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_644374278" MODIFIED="1229272646906" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Caches (activity) data about all hosts. Supports queries on chages, status etc.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157902725" ID="ID_1819579250" MODIFIED="1229296083822" STYLE="fork">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      CacheElement
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_191537496" MODIFIED="1229272752946" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stores data about one single host in the cache. Just a few data items, but lots of utility methods.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902727" ID="ID_519712663" MODIFIED="1229296083827" STYLE="fork" TEXT="ActivityMonitorListener">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1917749986" MODIFIED="1229272846465" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is a listener in the usual sense, a callback. Listeners are probably used correctly in this project.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902729" ID="ID_791287792" MODIFIED="1229296083831" STYLE="fork" TEXT="ByteBufferSerializableInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1543765427" MODIFIED="1229272912502" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Object serialisable to byte streams. This is probably an alternative to XML. Probably useful for storing (R) binary data...?
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902694" ID="ID_1274681053" MODIFIED="1229296083838" STYLE="fork" TEXT="EventDispatcher">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1872721699" MODIFIED="1229273270795" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Just a (probably needless) wrapper of the worker thread.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157902694" ID="ID_1960891409" MODIFIED="1229296083844" STYLE="fork">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      EventProcessorThread
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_721104422" MODIFIED="1229273359938" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A strange thread that periodically polls for events and reports them to the server. What an awful approach! I'd bet there are some synchronization problems between <font face="DejaVu Sans Mono">run()</font> and <font face="DejaVu Sans Mono">stopMe()</font>.
    </p>
    <p>
      
    </p>
    <p>
      Well, thread safety is enforced by <font face="DejaVu Sans Mono">EventQueue</font>, but anyway... The word <font face="DejaVu Sans Mono">synchronized</font> is used incorrectly from time to time.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902697" ID="ID_382037174" MODIFIED="1229296083849" STYLE="fork" TEXT="EventQueue">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_171808927" MODIFIED="1229273433063" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A thread safe oversynchronized producer-consumer buffer.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902699" ID="ID_1698892667" MODIFIED="1229296083854" STYLE="fork" TEXT="EventReceiver">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1710501575" MODIFIED="1229273659962" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is probably the consumer counterpart of <font face="DejaVu Sans Mono">EventDispatcher</font>. Calls all the registered listeners when an event is received. Othrewise nothing special.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902731" ID="ID_409110180" MODIFIED="1229296083861" STYLE="fork" TEXT="EventStorageListener">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_101965290" MODIFIED="1229273791382" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Another implementation of LoadMonitorEventListener. This appends all events to a file. There's a special file for load data.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902737" ID="ID_1028961944" MODIFIED="1229296083866" STYLE="fork" TEXT="HardwareDescription">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_183684613" MODIFIED="1229274019725" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is a duplicate implementation of hardware parts listing. It is probably needed by the load monitor. (This is what happens when developers work in isolation.) Unlike many other classes, this one is serializable to a byte string.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902741" ID="ID_126858298" MODIFIED="1229296083878" STYLE="fork" TEXT="HostDataStatistician">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1838578154" MODIFIED="1229274341073" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Collects load monitor data from hosts on the network. Performs some statistical computations. Can list load samples, load events, memory statistics, drive statistics, CPU statistics and many more rather generic queries such as listing of all timestamps and the like. It can also find events or hardware configuration in time. Hardware configuration is also a variable from BEEN's point of view, just like CPU usage, for example.
    </p>
    <p>
      
    </p>
    <p>
      BTW, would that work correctly on a system with memory hotplug that would obtain new RAM in the middle of a benchmark or even a single task? How about SAS hotswap or hot-pluggable CPU boards?
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902743" ID="ID_1204037703" MODIFIED="1229296083883" STYLE="fork" TEXT="HostDataStatisticianInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1984827778" MODIFIED="1229274420272" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      All the statistician has to know. It seems to me there are some excessive exceptions in the <font face="DejaVu Sans Mono">throws</font> statements.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902701" ID="ID_188546090" MODIFIED="1229296083890" STYLE="fork" TEXT="HostStatus">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1828155999" MODIFIED="1229274509571" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This enum represents host status and its string representation.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228164700637" ID="ID_1580433681" MODIFIED="1229274464179" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enum abuse fixed.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902703" ID="ID_46813226" MODIFIED="1229296083894" STYLE="fork" TEXT="LoadFile">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_11687494" MODIFIED="1229274726768" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Handle to one load file stored on one host. Contains host name, path to the file and methods useful to parse the file. This is used mainly (or perhaps only) with detailed load monitoring.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902708" ID="ID_1545677377" MODIFIED="1229296083899" STYLE="fork" TEXT="LoadFileParser">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_78851013" MODIFIED="1229275243930" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is not really just a parser. It implements much more than the interface would require, even methods to append more stuff to the file. This class is generic, so it can be used to obtain just about any byte buffer serializable type to/from the file.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902745" ID="ID_339003277" MODIFIED="1229296083904" STYLE="fork" TEXT="LoadFileParserInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_27329681" MODIFIED="1229275082954" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A sophisticated interface to iterate over the parsed file. Can skip items and return multiple at once. (And therefore can't be and extension to Iterable.)
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902710" ID="ID_1641651430" MODIFIED="1229296083909" STYLE="fork" TEXT="LoadFileWritable">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1361965996" MODIFIED="1229275357503" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is in fact a factory that creates various <font face="DejaVu Sans Mono">LoadFileParser</font>s for different purposes. As already mentioned, the <font face="DejaVu Sans Mono">LoadFileParser</font> can write, too. (Which is a design flaw, of course.)
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902715" ID="ID_1915431762" MODIFIED="1229296083916" STYLE="fork" TEXT="LoadMapFile">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_345210309" MODIFIED="1229275537769" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A load map is in fact a database index that permits fast access to the load files. This class stores load file position on each important event and provide fast access to the load file. (The huge load data would otherwise have to be read sequentialy.)
    </p>
    <p>
      
    </p>
    <p>
      Represents the file as an indexed array of entries.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157902715" ID="ID_525270172" MODIFIED="1229296083920" STYLE="fork" TEXT="IndexEntry">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1771750233" MODIFIED="1229275670558" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stores position and timestamp of the event. Somewhat low-level, can be stored to a file, too. This means that the whole metadata (the de facto index) can be stored, too.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902717" ID="ID_1882321090" MODIFIED="1229296083925" STYLE="fork" TEXT="LoadMonitorEvent">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_730972230" MODIFIED="1229276052242" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Events emited by load monitors on hosts. Lots of constructor setters and getters.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157902717" ID="ID_1090946500" MODIFIED="1229296083937" STYLE="fork" TEXT="EventType">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_501468628" MODIFIED="1229276080476" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enum listing possible load monitor event types.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228164700637" ID="ID_729017126" MODIFIED="1229276126295" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enum abuse fixed.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902747" ID="ID_528640900" MODIFIED="1229296083942" STYLE="fork" TEXT="LoadMonitorEventFilter">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_566596738" MODIFIED="1229276488826" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This filters events based on their origin (the host that emitted them) and type (<font face="DejaVu Sans Mono">EventType</font>).
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902750" ID="ID_1815615113" MODIFIED="1229296083946" STYLE="fork" TEXT="LoadMonitorEventListener">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1876051421" MODIFIED="1229276474788" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is an abstract ancestor of all listeners that use a filter. Basic event handling is based on filter result. Introduces new purely virtual methods that should handle acceptance and rejection of events.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902752" ID="ID_1484769302" MODIFIED="1229296083950" STYLE="fork" TEXT="LoadMonitorException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_508124610" MODIFIED="1229276516109" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Just about any disaster that can happen in the load monitor.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902754" ID="ID_884981839" MODIFIED="1229296083960" STYLE="fork" TEXT="LoadMonitorImplementation">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1545828408" MODIFIED="1229276983801" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Gets a library directory and a working directory and builds a cathedral on that. Brief and detailed modes and intervals can be set. Lots of file manipulation and exception handling. :-( Can wipe the data, too. Contains methods for loadin and storing the data, generating filenames etc. Note that this is where the results database index (index of file positions) is read. (It can be used even after BEEN restart. Data can be either logged locally (detailed mode) or sent directly to the load server (brief mode).
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157902754" ID="ID_1285350652" MODIFIED="1229296083964" STYLE="fork" TEXT="LoadDatabaseEntry">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_211031343" MODIFIED="1229277023146" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      One database entry to be stored locally when running in detailed mode.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902754" ID="ID_425381830" MODIFIED="1229296083968" STYLE="fork" TEXT="MonitorThread">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_934607209" MODIFIED="1229277178231" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A tiny thread that gets data from the native library and sends it directly an event. This thread is run periodically.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902756" ID="ID_456311077" MODIFIED="1229296083974" STYLE="fork" TEXT="LoadMonitorInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_248862200" MODIFIED="1229277314240" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Everything a well-behaved load monitor should provide, fairly documented. The load monitor, as usual, implements much more than it should, which causes confusion...
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228157902754" ID="ID_21433255" MODIFIED="1229296083977" STYLE="fork" TEXT="LoadMonitorMode">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_139064504" MODIFIED="1229277257801" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Brief, detailed or off. This is a trivial enum.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902719" ID="ID_74281274" MODIFIED="1229296083982" STYLE="fork" TEXT="LoadMonitorNative">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_444062044" MODIFIED="1229277617638" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This piece of code is definitely worth reading. This is the low-level control of load monitor native code. It asks the JVM for the OS name and selects an appropriate dynamic library based on that.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902721" ID="ID_1646505888" MODIFIED="1229296083987" STYLE="fork" TEXT="LoadSample">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_235973179" MODIFIED="1229277780978" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This container holds just about everything you can measure on a running system. Byte buffer routines are fairly long here. This piece of data is obtained directly from the JNI, by the way.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902758" ID="ID_475678355" MODIFIED="1229296083998" STYLE="fork" TEXT="LoadServerImplementation">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_117665103" MODIFIED="1229277868230" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is what monitors all the hosts at once, reacts to their events and logs samples and statistics when running in brief mode.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902760" ID="ID_368959049" MODIFIED="1229296084003" STYLE="fork" TEXT="LoadServerInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1650138234" MODIFIED="1229277936401" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is something like <font face="DejaVu Sans Mono">LoadMonitorInterface</font>, light edition. There are fewer methods related to statistics or data storage, so it's much simpler.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157902723" ID="ID_1617077109" MODIFIED="1229296084007" STYLE="fork" TEXT="ValueStatistics">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1485926686" MODIFIED="1229278027427" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Statistical values read from the event file when parsing it. Personally, I would rather call that aggregate functions than statistical values...
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228085489322" ID="Freemind_Link_1550854254" MODIFIED="1229296084010" STYLE="bubble" TEXT="util">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228157920461" ID="ID_1337485721" MODIFIED="1229296084014" STYLE="fork" TEXT="MiscUtils">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1354163231" MODIFIED="1229278282637" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Path and date string processing. File handling with specific endoding. A tiny file deletion method ;-) a hostname getter and lots of methods for parameter input verification, something like string to numeric, string to RSL and that standard sort of stuff, seen so many times already.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157920470" ID="ID_1636817566" MODIFIED="1229296084019" STYLE="fork" TEXT="TimeUtils">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_119397006" MODIFIED="1229278369172" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Important time constants and time conversion methods. Lots of means to convert epoch to Java date and back, with various styles and precisions.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157920480" ID="ID_488245409" MODIFIED="1229296084023" STYLE="fork" TEXT="XMLHelper">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_513499946" MODIFIED="1229278474023" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      On one hand, it's good to see at least some XML stuff bundled together. On the other hand, these are just small ethods for element dissection rather than a framework.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228085494089" ID="Freemind_Link_1951961700" MODIFIED="1229296084032" STYLE="bubble" TEXT="value">
<edge STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228157935266" ID="ID_1177940759" MODIFIED="1229296084037" STYLE="fork" TEXT="ValueBasicInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_161182692" MODIFIED="1229278654092" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This requires the <font face="DejaVu Sans Mono">ValueCommonInterface</font>, <font face="DejaVu Sans Mono">Comparable</font> and all the <font face="DejaVu Sans Mono">Serializable</font> stuff, including XML. Adds two boolean mehods whose purpose is probably just to simplify the code and avoid numeric comparisons in <font face="DejaVu Sans Mono">if</font>s.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157935250" ID="ID_652176137" MODIFIED="1229296084041" STYLE="fork" TEXT="ValueBoolean">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1655105644" MODIFIED="1229278703073" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">boolean</font> wrapper.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157935268" ID="ID_1041342923" MODIFIED="1229296084048" STYLE="fork" TEXT="ValueCommonInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_980575320" MODIFIED="1229278868352" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is the least useful interface I have ever seen. It requires methods that are already present in <font face="DejaVu Sans Mono">Object</font>.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_534176441" MODIFIED="1229278937201" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      <b>Take that nonsense away!!!</b>
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157935273" ID="ID_994441780" MODIFIED="1229296084053" STYLE="fork" TEXT="ValueCompoundInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1581075492" MODIFIED="1229279007118" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Requires that a compound value type can say whether it contains another value or not. (<font face="DejaVu Sans Mono">equals()</font> mechanism is used.)
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157935252" ID="ID_432984397" MODIFIED="1229296084064" STYLE="fork" TEXT="ValueDouble">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_368718561" MODIFIED="1229279042057" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">double</font> wrapper. Stores the measurement unit, too.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157935254" ID="ID_1923141033" MODIFIED="1229296084069" STYLE="fork" TEXT="ValueInteger">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1275448772" MODIFIED="1229279067652" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      An <font face="DejaVu Sans Mono">int</font> wrapper. Stores the measurement unit, too.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157935256" ID="ID_1990719564" MODIFIED="1229296084072" STYLE="fork" TEXT="ValueList">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1538429270" MODIFIED="1229279110151" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A list of values of the same type.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157935258" ID="ID_783165870" MODIFIED="1229296084078" STYLE="fork" TEXT="ValueRange">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_879217908" MODIFIED="1229279191551" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Represents a range of values, performs comarison queries and the like. Can handle open, half-open and closed ranges.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157935260" ID="ID_1016726105" MODIFIED="1229296084082" STYLE="fork" TEXT="ValueRegexp">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_958276975" MODIFIED="1229279249960" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Holds a parsed <font face="DejaVu Sans Mono">Pattern</font>, which is good for efficiency. Also stores the original expression and a case sensitivity flag.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157935262" ID="ID_437714470" MODIFIED="1229296084087" STYLE="fork" TEXT="ValueString">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1448719775" MODIFIED="1229279301415" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">String</font> wrapper. Contains a flag to decide whether to use case-sensitive comparison.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228157935264" ID="ID_106017887" MODIFIED="1229296084095" STYLE="fork" TEXT="ValueVersion">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1989309259" MODIFIED="1229279447573" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is about the third implementation of a version number seen in BEEN. Compared to the previous one, it has one advantage and one disadvantage:
    </p>
    <p>
      
    </p>
    <ul>
      <li>
        It can represent a string part of the version number, which might occur with some software packages.
      </li>
      <li>
        It has a fixed number of numeric (and string) fields. This should be simply avoided.
      </li>
    </ul>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228085213608" ID="Freemind_Link_546123752" MODIFIED="1229296084103" POSITION="right" STYLE="bubble" TEXT="hostruntime">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228156234006" ID="ID_713316820" MODIFIED="1229296084108" STYLE="fork" TEXT="HostRuntimeException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_614873463" MODIFIED="1228261197808" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      All exceptions thrown in the <font face="DejaVu Sans Mono">hostruntime</font> package. (Well, there are not many of them...)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156234009" ID="ID_1138856894" MODIFIED="1229296084118" STYLE="fork" TEXT="HostRuntimeImplementation">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_910020861" MODIFIED="1228261919635" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A straightforward implementation of HostRuntimeInterface. Raher technical stuff with lots of file operations. Mind the exit hook initialized in the constructor.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1892753386" MODIFIED="1228262007438" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Why does this class have access to the <font face="DejaVu Sans Mono">TaskManagerInterface</font>? OK, we don't consider malicious host runtimes, but this sounds like <font face="DejaVu Sans Mono">TaskManagerInterface</font> needs splitting.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156234012" ID="ID_1868829002" MODIFIED="1229296084123" STYLE="fork" TEXT="HostRuntimeInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_316764799" MODIFIED="1228261498706" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Interface used to control the host runtime. Creates tasks and controls the number of contexts for which test output will be stored on the host. Returns reference to to running tasks... (But can return only all at once.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156233991" ID="ID_1428902422" MODIFIED="1229296084136" STYLE="fork" TEXT="HostRuntimeRunner">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_17728050" MODIFIED="1228262135215" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The <font face="DejaVu Sans Mono">main()</font> class of the host runtime.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_371257239" MODIFIED="1228262183479" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Command line arguments should be checked more carefully to make error messages understandable.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156234014" ID="ID_1888294115" MODIFIED="1229296084149" STYLE="fork" TEXT="PackageCacheManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1258899830" MODIFIED="1228262405955" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Operates package cache and downloads packages to the cache if necessary. Packages are divided into two grousp: boot packages and the rest. The first group of packages is available at any time. Other packages can be either cached or downloaded from the software repository. Size of the boot packages is not considered when counting statistics...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1489287258" MODIFIED="1228262660544" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Would be fine to split this into multiple classes. The current code is hardly readable. Better package versioning and forced re-synchronisation has already been requested by other team members.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228156233991" ID="ID_104446023" MODIFIED="1229296084154" STYLE="fork" TEXT="Package">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1291110930" MODIFIED="1228262512656" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Representation of a single package. File, size, Type, timestamp...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228156233991" ID="ID_1479094482" MODIFIED="1229296084157" STYLE="fork" TEXT="Type">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_736645478" MODIFIED="1228262529293" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enum. Type of the package (boot/non-boot).
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228156233991" ID="ID_1580290329" MODIFIED="1229296084167" STYLE="fork" TEXT="PackageNameQueryCallback">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1863279588" MODIFIED="1228263209925" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Implements the software repository's <font face="DejaVu Sans Mono">PackageQueryCallbackInterface</font>. Software repository can then ask whether a package matches the given <font face="DejaVu Sans Mono">PackageMetadata</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1443688703" MODIFIED="1228263263661" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Misplaced. Stores the package filename again. Should be inside the <font face="DejaVu Sans Mono">Package</font> (nested) class. (Yes, three levels of nesting again...)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228156234017" ID="ID_1680539584" MODIFIED="1229296084177" STYLE="fork" TEXT="PackageConfiguration">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_863416139" MODIFIED="1228263954847" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Representation of the <font face="DejaVu Sans Mono">config.xml</font> configuration file.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1531247544" MODIFIED="1228264268804" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      How about switching to JDOM? It's so much kinder to the humanity... Moreover, I saw the jdom.jar file among the libraries. Just hope BEEN does not use multiple libraries for XML parsing. :-(
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228156233991" ID="ID_1299411330" MODIFIED="1229296084181" STYLE="fork" TEXT="PackageConfigurationEntityResolver">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1613929817" MODIFIED="1228264144742" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stuff from <font face="DejaVu Sans Mono">org.xml.sax</font> needed to enable DTD document validation.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228156233993" ID="ID_1663591642" MODIFIED="1229296084200" STYLE="fork" TEXT="TaskImplementation">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_398934951" MODIFIED="1228265097446" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Creates a running task. Builds the command line, executes the task, grabs its output, waits for the task or kills it after a timeout. Includes (lots of) file manipulation routines...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_333265282" MODIFIED="1228266335484" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      File manipulation should be concentrated in one common package. Logging, database storage and tasks all need/use a similar functionality, re-implemented over and over.
    </p>
    <p>
      
    </p>
    <p>
      <b>This class must be split!!!</b> Imagine you had to use TDD. How would you test those three levels of nesting?
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228156233991" ID="ID_12350418" MODIFIED="1229296084209" STYLE="fork" TEXT="ProcessOutputProcessor">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1336837926" MODIFIED="1228265322132" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Grabs the program's output and sends it to the HostRuntime. It's a <font face="DejaVu Sans Mono">Thread</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_633721608" MODIFIED="1228265764884" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Design flaw: output type is switched in <font face="DejaVu Sans Mono">run()</font>. That has something in common with the obvious code duplication in <font face="DejaVu Sans Mono">TaskProcessExecutor#sendOutput()</font> and <font face="DejaVu Sans Mono">ProcessOutputProcessor#run()</font>. This stuff could (and should) be much shorter. Callbacks must be used.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156233991" ID="ID_1424865712" MODIFIED="1229296084220" STYLE="fork" TEXT="TaskProcessExecutor">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_356585317" MODIFIED="1228265514860" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Executes the process and waits for it. Communicates with <font face="DejaVu Sans Mono">LoadMonitor</font>, too.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_658884251" MODIFIED="1228265731368" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Code duplication in <font face="DejaVu Sans Mono">sendOutput()</font> Seee the previous class' TODO comment.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228156233991" ID="ID_548187811" MODIFIED="1229296084226" STYLE="fork" TEXT="TasksProcessKiller">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_804740417" MODIFIED="1228265874568" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">Thread</font>. Kills the process after a given period of time.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228156233991" ID="ID_1736514590" MODIFIED="1229296084234" STYLE="fork" TEXT="LogRecord">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_102622109" MODIFIED="1228267174575" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stores logs for context-exclusive and exclusive tasks.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_183931480" MODIFIED="1228267216264" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Repeated code again! Grrr!
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228156233996" ID="ID_973831000" MODIFIED="1229296084242" STYLE="fork" TEXT="TaskInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_651613558" MODIFIED="1228266472476" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is what a task should look like. However, the only implementation does not look that nice... :-(
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228266345520" ID="ID_430260710" MODIFIED="1228266618828" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      All those big interfaces in BEEN look pretty unnatural, as if they had been created after the implementation, just for the sake of RMI. If I'm wrong, then why do all these implementations contain three times more public methods than the interfaces require???
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="33" VALUE_WIDTH="33"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156233998" ID="ID_116870605" MODIFIED="1229296084248" STYLE="fork" TEXT="TaskLoader">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1032903273" MODIFIED="1228266587929" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">main()</font> class. Loads a BEEN task and runs or starts it, depending on whether it's a job or a service.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156234019" ID="ID_865676309" MODIFIED="1229296084259" STYLE="fork" TEXT="TasksPortImplementation">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1307121604" MODIFIED="1228267080319" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The one and only implementation of <font face="DejaVu Sans Mono">TaskPortInterface</font>. This is a good one. It does what it's expected to do and does not contain tons of ballast code. ;-)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156234001" ID="ID_103222325" MODIFIED="1229296084265" STYLE="fork" TEXT="TasksPortInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1601414408" MODIFIED="1228266924425" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Interface a task can use to communicate with the host runtime. It's pretty long and contains methods for path generation, log listening, task listing, checkpoint synchronization and service (in the BEEN sense) lookup.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228084835791" ID="Freemind_Link_144389090" MODIFIED="1229296084273" POSITION="right" STYLE="bubble" TEXT="logging">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228155823443" ID="ID_265746046" MODIFIED="1229296084277" STYLE="fork" TEXT="BeenLogger">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1603021645" MODIFIED="1228258461180" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Abstract logger. This should be an interface. A suitable adapter class would take care of the log4j initialization. (Once more, the logging code occurs at least three times in BEEN...)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155823446" ID="ID_1717603234" MODIFIED="1229296084280" STYLE="fork" TEXT="ConsoleLogger">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1746006737" MODIFIED="1228258519090" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      BeenLogger implementation for console output.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155823463" ID="ID_642856197" MODIFIED="1229296084292" STYLE="fork" TEXT="FilesystemLogStorage">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1031332218" MODIFIED="1228259428722" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      LogStorage implementation using a plain filesystem. A structure of log files and directories is used for both storing and retrieving information.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1552775962" MODIFIED="1228259627892" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <ol>
      <li>
        Counting lines by reading the whole log is a <b>severe</b> flaw.
      </li>
      <li>
        Re-generating log records by parsing the text file is a bad idea, too.
      </li>
      <li>
        How about serializing the log records into a binary log and creating the plaintext output on demand after the test? The performance impact of logging would be much lower.
      </li>
    </ol>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228155823460" ID="ID_1373130142" MODIFIED="1229296084296" STYLE="fork" TEXT="FileOutputHandle">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1558607132" MODIFIED="1228259191410" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      <font face="DejaVu Sans Mono">OutputHandle</font> implementation for file storage/access.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155823460" ID="ID_1628496321" MODIFIED="1229296084300" STYLE="fork" TEXT="LogMessagePart">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1220018891" MODIFIED="1228260284316" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Data container for an incomplete log message. (?)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228155823466" ID="ID_921804168" MODIFIED="1229296084305" STYLE="fork" TEXT="HostRuntimeAppender">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_652727717" MODIFIED="1228260180590" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Implements the log4j <font face="DejaVu Sans Mono">AppenderSkeleton</font>. Sends loging events to host runtime. BTW, I'm convinced that at least one level of the whole logging architecture could be eliminated to make things easier...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155823449" ID="ID_1428071835" MODIFIED="1229296084321" STYLE="fork" TEXT="LogLevel">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1631490009" MODIFIED="1228260333276" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enum of possible log levels with some utility methods. Repeated multiple times in the code...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228164700637" ID="ID_965775485" MODIFIED="1228260492475" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This used to be an ordinary class using the dirty trick people needed to create enums in Java 1.4. Converted to a real <font face="DejaVu Sans Mono">enum</font>. That makes things much easier and code much more readable.
    </p>
    <p>
      
    </p>
    <p>
      Surprisingly, nobody complained after the conversion... Looking forward to see those wonderful <font face="DejaVu Sans Mono">ClassCastException</font>s!
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155823452" ID="ID_380915149" MODIFIED="1229296084327" STYLE="fork" TEXT="LogRecord">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1712120414" MODIFIED="1228260678090" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Representation of a complete log message. This class could do much more, I would suggest file input/output routines with formatting callback... But that's just a shot in the dark, so no blue text this time.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228155823460" ID="ID_11490497" MODIFIED="1229296084331" STYLE="fork" TEXT="Fields">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_187424708" MODIFIED="1228260752198" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enum of all the possible <font face="DejaVu Sans Mono">LogRecord</font> fields. According to the Javadoc, web interface takes advantage of <font face="DejaVu Sans Mono">EnumMap</font>s.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228155823454" ID="ID_912077072" MODIFIED="1229296084336" STYLE="fork" TEXT="LogStorage">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1577473468" MODIFIED="1228259924912" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This interface shows how a logger implementation should work. Groups of tasks are called contexts here. Both context and task logs can be created (registered) or removed. Other methods deal with counting log messages and registering log output &quot;listeners&quot;.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155823469" ID="ID_1823147003" MODIFIED="1229296084339" STYLE="fork" TEXT="LogStorageException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1302692100" MODIFIED="1228260789283" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Failure during log storage operation.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155823457" ID="ID_1636535227" MODIFIED="1229296084348" STYLE="fork" TEXT="LogStorageTest">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1371223946" MODIFIED="1228260859181" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A very simple (and very incomplete) test of log storage. A <font face="DejaVu Sans Mono">main()</font> class.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_929626872" MODIFIED="1228260912195" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      There should be a big set of unit tests, not one weird piece of code hidden inside the package...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228155823460" ID="ID_509287203" MODIFIED="1229296084352" STYLE="fork" TEXT="OutputHandle">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_773011850" MODIFIED="1228258699699" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Interface for reading standard/error output of a process. Counts the number of lines and can skip lines.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228085222036" ID="Freemind_Link_1850905528" MODIFIED="1229296084418" POSITION="right" STYLE="bubble" TEXT="resultsrepository">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228156283201" ID="ID_595656648" MODIFIED="1229296084429" STYLE="fork" TEXT="AnalysisEntityManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1031171249" MODIFIED="1228268336666" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This singleton is the central part of all analyses. Connects the analyses with the results repository and the graph manager. Contains methods to manipulate analyses and query their state.
    </p>
    <p>
      
    </p>
    <p>
      Performs quite many filesystem operations. As already mentioned, this project needs a better file access framework.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283203" ID="ID_1615595546" MODIFIED="1229296084434" STYLE="fork" TEXT="AnalysisImplementation">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_760923256" MODIFIED="1228268814174" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is a communication link with methods that either access the subentities of an Analysis or start asynchronous data transfers of test results or graphs.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283205" ID="ID_1675819349" MODIFIED="1229296084444" STYLE="fork" TEXT="BinaryEntityManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1846099664" MODIFIED="1228268977639" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A singleton with a purpose similar to <font face="DejaVu Sans Mono">AnalysisEntityManager</font>. This time it handles communication between binaries and the rest of the world as well as data storage.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_78498812" MODIFIED="1228269054134" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Methods <font face="DejaVu Sans Mono">addBinary()</font> and <font face="DejaVu Sans Mono">eraseBinary()</font> are worse than terrible. Who's supposed to understand that???
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283207" ID="ID_1610825835" MODIFIED="1229296084449" STYLE="fork" TEXT="BinaryImplementation">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1709380233" MODIFIED="1228269140471" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Equivalent of <font face="DejaVu Sans Mono">AnalysisImplementation</font> for binaries.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283118" ID="ID_1934354080" MODIFIED="1229296084461" STYLE="fork" TEXT="Database">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1088660882" MODIFIED="1228270275597" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The center of the world, the great cornerstone. This singleton uses *EntityManager classes to store information. Moreover, it knows about all data transfers in progress and provides file upload/download functionality itself.
    </p>
    <p>
      
    </p>
    <p>
      The implementation is rather open, exposing all the filesystem paths.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_97415149" MODIFIED="1228269501116" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This class of insane length must be split. <font face="DejaVu Sans Mono">FileUploader</font> and <font face="DejaVu Sans Mono">FileDlownloader</font> shold not be nested. Multiple interfaces should not be implemented at once and combined with a bunch of other public methods.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228156283207" ID="ID_1547664686" MODIFIED="1229296084466" STYLE="fork" TEXT="FileUploader">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_236345883" MODIFIED="1228269937743" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Uploads file to a given system. It's a <font face="DejaVu Sans Mono">Thread</font>. Deals with lots of technical problems and the code is hardly readable! Not a data-independent implementation. Can do basic R script validation and perhaps other file-type-specific stuff.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283207" ID="ID_392336863" MODIFIED="1229296084471" STYLE="fork" TEXT="FileDownloader">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_176135196" MODIFIED="1228269970277" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">Thread</font> that can download files, much simpler that <font face="DejaVu Sans Mono">FileUploader</font>, but still rather complicated... :-(
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283209" ID="ID_1791365294" MODIFIED="1229296084477" STYLE="fork" TEXT="DeleteLockedException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1763955009" MODIFIED="1228270485514" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      I have just seen an <font face="DejaVu Sans Mono">UndocumentedCodeException</font> in my nervous system. Well, this is probably thrown when a locked file deletion is attempted. (Can only guess the details. <font face="DejaVu Sans Mono">fcntl</font>? BEEN-specific locsk? Please fill this in if you know.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283211" ID="ID_883244608" MODIFIED="1229296084482" STYLE="fork" TEXT="EntityImplementation">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_16449935" MODIFIED="1228270597805" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This would be a nice Adapter if it did not redeclare all the abstract methods. Like this it's an awful Adapter...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283213" ID="ID_1498902078" MODIFIED="1229296084494" STYLE="fork" TEXT="ExperimentEntityManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1644568145" MODIFIED="1228270885349" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Similar to other <font face="DejaVu Sans Mono">*EntityManager</font>s, just somewhat extended... It would require considerable amount of time to understand this. As for long and awful methods, see my objections at <font face="DejaVu Sans Mono">BinaryEntityManager</font>...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283220" ID="ID_1917224622" MODIFIED="1229296084497" STYLE="fork" TEXT="ExperimentImplementation">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_889023167" MODIFIED="1228271101649" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      See <font face="DejaVu Sans Mono">AnalysisImplementation</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283120" ID="ID_1360689593" MODIFIED="1229296084502" STYLE="fork" TEXT="ExportBEEN">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_702160536" MODIFIED="1228271505965" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      An example implementation of ExporterInterface. There's a bit of confusion, as the Exporter class is still around. Some experiments should be carried out to make this clear.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283124" ID="ID_910363416" MODIFIED="1229296084507" STYLE="fork" TEXT="Exporter">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_664701723" MODIFIED="1228271407794" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Probably the original old hard-coded export formats. Not much documentation around...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283123" ID="ID_1755319058" MODIFIED="1229296084511" STYLE="fork" TEXT="ExportInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1703117540" MODIFIED="1228271436353" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This interface declares methods to export data from BEEN, one for each of the four entity types. Useful to create new export formats.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283224" ID="ID_1737394419" MODIFIED="1229296084515" STYLE="fork" TEXT="FileUploadCallbackInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_20309507" MODIFIED="1228271610051" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A callback to be run on a file after the upload has finished. BTW, do we care about security at least a little bit? The callback can do simply whatever...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283127" ID="ID_1010063599" MODIFIED="1229296084520" STYLE="fork" TEXT="GraphManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1734443153" MODIFIED="1228272030982" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Reads and gathers graph information from the database. Stores metadata in <font face="DejaVu Sans Mono">NumberedTable</font>s and other interesting data types. I really like the variable <font face="DejaVu Sans Mono">analGraphTable</font>!
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283129" ID="ID_1008615548" MODIFIED="1229296084525" STYLE="fork" TEXT="ImportDumb">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1471267486" MODIFIED="1228272243099" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      However strange it may seem, this is a counterpart to <font face="DejaVu Sans Mono">ExportBEEN</font>. An example import filter implementation.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283137" ID="ID_337964753" MODIFIED="1229296084530" STYLE="fork" TEXT="Importer">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_680515517" MODIFIED="1228272190332" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Probably an old implementation of import routines. See <font face="DejaVu Sans Mono">Exporter</font>, too, and add more info if you know what this means.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283131" ID="ID_294695835" MODIFIED="1229296084534" STYLE="fork" TEXT="ImportInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_852445627" MODIFIED="1228272138292" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The <font face="DejaVu Sans Mono">ExportInterface</font> counterpart. Long live new great import formats! Again, designed for non-blocking operation.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283140" ID="ID_329304797" MODIFIED="1229296084539" STYLE="fork" TEXT="LoadCollector">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1571201159" MODIFIED="1228272424613" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Weird file system initialization! Grrr.
    </p>
    <p>
      
    </p>
    <p>
      Uploads load statistics from the database to the caller asynchronously.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283142" ID="ID_822335118" MODIFIED="1229296084555" STYLE="fork" TEXT="LockManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1298636492" MODIFIED="1228272592920" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Trivial filesystem-based locking. Binaries, experiments and analyses have separate lock namespaces. (Runs probably can't acquire any locks on their own...)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283145" ID="ID_694400707" MODIFIED="1229296084566" STYLE="fork" TEXT="LogCollector">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_690999633" MODIFIED="1228272786248" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A singleton that collects logs from the task manager and stores them in the <font face="DejaVu Sans Mono">Database</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283147" ID="ID_784357116" MODIFIED="1229296084574" STYLE="fork" TEXT="Logger">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1985386386" MODIFIED="1228272900149" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A logger reimplementation for standalone testing of the results repository and unit testing.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283149" ID="ID_1175967900" MODIFIED="1229296084590" STYLE="fork" TEXT="Metadata">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_189786544" MODIFIED="1228273084822" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Constructed by reading metadata from a file. Supports saving the data, too. Again, this is a class with the capability of storing and restoring itself. This is done, again, in a way that differs from other implementations within the BEEN framework...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283151" ID="ID_180155967" MODIFIED="1229296084597" STYLE="fork" TEXT="MetadataManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_457654135" MODIFIED="1228273161534" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A singleton for metadata caching and other manipulation.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283154" ID="ID_734125656" MODIFIED="1229296084608" STYLE="fork" TEXT="MetadataRef">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_214388800" MODIFIED="1228273235804" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Just another level of indirection, a pointer, a reference or whatever one could call that.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283156" ID="ID_1154183896" MODIFIED="1229296084617" STYLE="fork" TEXT="NumberedItem">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_266654053" MODIFIED="1228273293867" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      An immutable pair with a numeric key and a string value.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283158" ID="ID_94173126" MODIFIED="1229296084622" STYLE="fork" TEXT="NumberedTable">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_63071579" MODIFIED="1228273457800" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      <font face="DejaVu Sans Mono">ArrayList</font> of <font face="DejaVu Sans Mono">NumberedItem</font>s with a few wrapper methods around. File storage implemented here, again.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283160" ID="ID_530262805" MODIFIED="1229296084632" STYLE="fork" TEXT="RBackgroundThread">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1167183426" MODIFIED="1228273980440" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Executeds all the R scripts. They are (probably) executed sequentially, unless there's a hidden asynchronous call. This is where R is called.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_486590976" MODIFIED="1228274192544" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      R should be called in a different way, perhaps by composing a plaintext script in Java and providing it as R's standard input. Otherwise the SJava library would have to be fixed. I guess it wouldn't be difficult to do, but... Any volunteers?
    </p>
    <p>
      
    </p>
    <p>
      BTW, have you heard about Octave? It's open-source, well maintained and graph capable. Statistical computations from R could be used to generate graphs in Octave. Don't know whether Octave is mature enough to handle the statistics, too.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283162" ID="ID_1775443014" MODIFIED="1229296084646" STYLE="fork" TEXT="RCallbackEntity">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1383201047" MODIFIED="1228274308042" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enum with callback entities. Why the R prefix? I would expect one single BEEN-wide enum with ANALYSIS, EXPERIMENT, BINARY and RUN...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228274312708" ID="ID_1438254977" MODIFIED="1228274382710" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Converted the terrible <font face="DejaVu Sans Mono">switch ( this )</font><font face="DejaVu Sans Condensed"> construct to real Java.</font>
    </p>
  </body>
</html></richcontent>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283164" ID="ID_1347524440" MODIFIED="1229296084651" STYLE="fork" TEXT="RCallbacksManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1056995437" MODIFIED="1228274748368" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Loads, stores, registers and retrieves R callbacks. These scripts are run when after their entity has been completed.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283226" ID="ID_1278986741" MODIFIED="1229296084656" STYLE="fork" TEXT="RCallbacksManagerFactory">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_133250752" MODIFIED="1228274936369" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stores and retrieves callback managers associated with analyses and experiments.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228156283226" ID="ID_1364916606" MODIFIED="1229296084660" STYLE="fork" TEXT="IDPair">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1314988017" MODIFIED="1228274897920" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Pair of analysis ID and experiment ID. Used as a map key in the factory.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283237" ID="ID_1852177934" MODIFIED="1229296084664" STYLE="fork" TEXT="ResultsRepositoryBusyException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1664372122" MODIFIED="1228274988083" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Lack of documentation... Thrown after a timeout in communication?
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283241" ID="ID_1868469276" MODIFIED="1229296084668" STYLE="fork" TEXT="ResultsRepositoryDebugInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1278170790" MODIFIED="1228275030257" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A technical interface for the sake of unit testing. Somewhat short... :-(
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283231" ID="ID_1690759218" MODIFIED="1229296084671" STYLE="fork" TEXT="ResultsRepositoryException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1060041680" MODIFIED="1228275051416" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      All exceptions within the results repository.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283239" ID="ID_837415722" MODIFIED="1229296084675" STYLE="fork" TEXT="ResultsRepositoryImplementation">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_865102215" MODIFIED="1228275467929" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The results repository cornerstone. Uses the <font face="DejaVu Sans Mono">Database</font>. Implements three interfaces, one for communication with other parts of BEEN, one for the GUI and one for unit testing purposes.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283233" ID="ID_1771404454" MODIFIED="1229296084680" STYLE="fork" TEXT="ResultsRepositoryInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_694100580" MODIFIED="1228275199399" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      RMI interface for adding test entities, starting asynchronous downloads of result data and setting R callback scripts.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283234" ID="ID_1255550519" MODIFIED="1229296084690" STYLE="fork" TEXT="ResultsRepositoryService">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1387148834" MODIFIED="1228275577301" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Initializes the three remote interfaces and starts the <font face="DejaVu Sans Mono">resultsrepository</font> BEEN service.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228275591323" ID="ID_66870419" MODIFIED="1228275680491" STYLE="bubble" TEXT="BTW, is it necessary to expose the debugging and testing interface all the time? This is where nested classes should be used! (They can be easily excluded from the jars.)">
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283167" ID="ID_1285059483" MODIFIED="1229296084694" STYLE="fork" TEXT="RHelpManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1587290339" MODIFIED="1228275902904" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Retrieves help for R functions from a text file. Homebrew file manipulation again...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283169" ID="ID_328304113" MODIFIED="1229296084705" STYLE="fork" TEXT="RManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1453180105" MODIFIED="1228276509979" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This class implements two important pieces of functionality at once. Firstly, it handles storage, retrieval, activation and deactivation of R packages. Secondly, it calls the low-level routines from the omegahat library used to control R. Supplies scripts to be parsed by R and runs them.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283180" ID="ID_1475677480" MODIFIED="1229296084712" STYLE="fork" TEXT="RoleItem">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1834823837" MODIFIED="1228276564530" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      host &lt;-&gt; role mapping.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228275591323" ID="ID_1968259539" MODIFIED="1228276618201" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      There are too many classes of this kind. All of them should be &quot;consolidated&quot; using generics, enums and inheritance.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283182" ID="ID_1193319212" MODIFIED="1229296084716" STYLE="fork" TEXT="RoleList">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1743879162" MODIFIED="1228276755762" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Implements the role to host mapping. As usual, implements database-like access and file storage/retrieval. A good candidate for code consolidation.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283184" ID="ID_74945788" MODIFIED="1229296084722" STYLE="fork" TEXT="RolesManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1856027963" MODIFIED="1228277162655" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Retrieves a <font face="DejaVu Sans Mono">RoleList</font> for each experiment based on its location obtained from the <font face="DejaVu Sans Mono">Database</font>. Supports some queries against the <font face="DejaVu Sans Mono">RoleList</font>, too.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283228" ID="ID_1353771235" MODIFIED="1229296084726" STYLE="fork" TEXT="RPackageUploadException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1029203643" MODIFIED="1228277279938" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Self-explanatory.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283171" ID="ID_738170291" MODIFIED="1229296084732" STYLE="fork" TEXT="RREvent">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_756899675" MODIFIED="1228277355782" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      One event of the (sequence of) events executed by the ResultsRepository background thread.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283173" ID="ID_644817355" MODIFIED="1229296084737" STYLE="fork" TEXT="RRIDItem">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1935355790" MODIFIED="1228277436776" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      One item of the validity maps table. (Stores validity of every single run.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283174" ID="ID_255044453" MODIFIED="1229296084741" STYLE="fork" TEXT="RRIDTable">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_971463124" MODIFIED="1228277499254" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Loading and storing run validity maps from/to a file. Implements queries against the validity map table.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283177" ID="ID_987707584" MODIFIED="1229296084745" STYLE="fork" TEXT="RScriptsRunner">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1187888465" MODIFIED="1228277722481" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A singleton to manipulate the background thread's workqueue. Starts and stops the thread and answers queries against the queue of R tasks.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283186" ID="ID_824570842" MODIFIED="1229296084753" STYLE="fork" TEXT="RunEntityManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1463964864" MODIFIED="1228277802243" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      See other <font face="DejaVu Sans Mono">*EntityManager</font> comments.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228275591323" ID="ID_687617952" MODIFIED="1228277850420" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This time the <font face="DejaVu Sans Mono">eraseRun()</font> method is such that one would vomit!
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283188" ID="ID_806987110" MODIFIED="1229296084763" STYLE="fork" TEXT="RunImplementation">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_709212597" MODIFIED="1228277913889" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      See other <font face="DejaVu Sans Mono">*Implementation</font> classes for test entities.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283190" ID="ID_268599034" MODIFIED="1229296084773" STYLE="fork" TEXT="RunUploadCallback">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1392646488" MODIFIED="1228278868832" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Implements <font face="DejaVu Sans Mono">FileUploadCallbackInterface</font>. <b>Contains the most confusing documentation I have ever seen.</b> Gathers metadata and scripts and enqueues them. This means registering them with <font face="DejaVu Sans Mono">RScriptsRunner</font> so that they can be run later by the background thread.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228275591323" ID="ID_1381314354" MODIFIED="1228278916698" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      <font face="DejaVu Sans Mono">callbackFunction()</font>: There is <b>no</b> excuse for a 200-lines-long method. This mus be fixed.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283243" ID="ID_911321654" MODIFIED="1229296084782" STYLE="fork" TEXT="RunValidityMapsManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_982288085" MODIFIED="1228279109969" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Maps run identifiers to results repository internal identifiers. Implements many queries and path getters.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228275591323" ID="ID_1890744261" MODIFIED="1228279213936" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is somewhat annoying: A method with more than 150 lines contains a mere 2 comments! Who's supposed to read that?
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283193" ID="ID_1711200869" MODIFIED="1229296084786" STYLE="fork" TEXT="StatisticsManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_603654316" MODIFIED="1228279381965" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Database table storage and retrieval, using a simple text representation. This time long methods are quite well documented. :-)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283195" ID="ID_1723588872" MODIFIED="1229296084790" STYLE="fork" TEXT="TaskListManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_351483572" MODIFIED="1228279553096" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Loads and stores task lists on virtually all entity levels. Implemented through getters and communication routines. Designed for communidation with the GUI.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283197" ID="ID_1564696982" MODIFIED="1229296084793" STYLE="fork" TEXT="Zipper">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_887873154" MODIFIED="1228279597048" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      ZIP utility class. Can zip directories recursively.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228156282854" ID="ID_1936796442" MODIFIED="1229296084795" STYLE="bubble" TEXT="api">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228156282917" ID="ID_445188111" MODIFIED="1229296084802" STYLE="fork" TEXT="AnalysisType">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1327016652" MODIFIED="1228888612979" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enum of analysis types. Currently only those two are avaliable.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228164700637" ID="ID_1469639035" MODIFIED="1228888719313" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This used to be a weird class with constats. Such a solution would be wrong even in pre-1.5 Java. Converted to a simple enum.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228156282920" ID="ID_770198358" MODIFIED="1229296084810" STYLE="bubble" TEXT="gui">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228156282994" ID="ID_1980860815" MODIFIED="1229296084813" STYLE="fork" TEXT="CallbackRScripts">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1937152880" MODIFIED="1228918697284" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Data container for callback scripts. Called after corresponding parts of the experiment are completed.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156282996" ID="ID_603201063" MODIFIED="1229296084824" STYLE="fork" TEXT="DataFormat">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_604107669" MODIFIED="1228918862852" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Information on import/export data formats. Useful to communicate with the web interface.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156282998" ID="ID_285459923" MODIFIED="1229296084828" STYLE="fork" TEXT="Entity">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_748583984" MODIFIED="1228918951110" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Another representation of an entity interface/reference. This one is used to communicate with the web interface.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283008" ID="ID_1589065076" MODIFIED="1229296084832" STYLE="fork" TEXT="EntityInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1282930469" MODIFIED="1228919076808" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      All possible operations with an entity (from the web interface's point of view). Handles asynchonous data transfers, metadata manipulation and other useful tricks.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283016" ID="ID_726847299" MODIFIED="1229296084836" STYLE="fork" TEXT="GraphInfo">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1642688416" MODIFIED="1228919116097" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Information about a graph, which is mainly geometry and the like. Communicates with the web interface, too.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283018" ID="ID_700326913" MODIFIED="1229296084840" STYLE="fork" TEXT="GraphType">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1061785528" MODIFIED="1228919211257" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Results or load graphs. Modified from terrible enum misuse to real Java.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283021" ID="ID_1438664228" MODIFIED="1229296084847" STYLE="fork" TEXT="MetadataItem">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1866767461" MODIFIED="1229247575921" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A weird and simple key-&gt;value mapping.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228275591323" ID="ID_1836790681" MODIFIED="1229247659184" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Something like the generic <font face="DejaVu Sans Mono">Collection.Entry</font> should be used for these purposes. Still re-inventing the wheel...
    </p>
  </body>
</html>
</richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283029" ID="ID_1677573359" MODIFIED="1229296084853" STYLE="fork" TEXT="ResultsRepositoryGUIInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_187230659" MODIFIED="1229247810967" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Communication channel used to control the results repository from the GUI. Most methods deal with R scripting.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283023" ID="ID_1392098001" MODIFIED="1229296084858" STYLE="fork" TEXT="RFunctionHelp">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1732200299" MODIFIED="1229247880768" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Another key-&gt;value mapping. Keys are R funciton signatures. Values are help texts for those functions.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283025" ID="ID_1171895886" MODIFIED="1229296084862" STYLE="fork" TEXT="RScriptPackage">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1341419958" MODIFIED="1229247965156" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Key-&gt;value mapping. Key: R package name. Value: R package description.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283027" ID="ID_1884559841" MODIFIED="1229296084870" STYLE="fork" TEXT="Statistics">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1505699645" MODIFIED="1229248236687" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      One table of entity output data. Basically just an array of strings (column names) and a 2-dim array of double (the table). This conveys data to the GUI.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228275591323" ID="ID_1104912092" MODIFIED="1229248220075" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      WATCH OUT!!!
    </p>
    <p>
      Why do we use Double instead of double? There could be a measurable performance penalty causing a latency if the GUI has to display and transfer say tens of MBs of data.
    </p>
  </body>
</html>
</richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228156283051" ID="ID_233119280" MODIFIED="1229296084874" STYLE="bubble" TEXT="test">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#006566" CREATED="1228156283109" ID="ID_1142428930" MODIFIED="1229296084883" STYLE="fork" TEXT="ResourceRScripts">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_633004090" MODIFIED="1229248968765" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A thread-like testing <font face="DejaVu Sans Mono">main()</font> class, which is not a thread... ;-)
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283111" ID="ID_1034195642" MODIFIED="1229296084889" STYLE="fork" TEXT="TestCreateEntities">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_557662832" MODIFIED="1229248952068" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Another <font face="DejaVu Sans Mono">main()</font> class wit a <font face="DejaVu Sans Mono">run()</font> method. But again, not a thread. Tries to extract a bunch of entities from the results repository and fiddles with that a little bit... Should be converted into a JUnit test case if possible.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283113" ID="ID_897695120" MODIFIED="1229296084893" STYLE="fork" TEXT="TestRunScripts">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1139271373" MODIFIED="1229249063751" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enqueues some scripts from the <font face="DejaVu Sans Mono">beendemo</font> (?) R package. Still the same type of class in this package.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228156283115" ID="ID_1445213759" MODIFIED="1229296084898" STYLE="fork" TEXT="TestUpload">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1655641220" MODIFIED="1229249209352" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The same thig again, but pretty long this time. Tests binary upload and (probably also) results collection.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228085260472" ID="Freemind_Link_578915459" MODIFIED="1229296084911" POSITION="right" STYLE="bubble" TEXT="softwarerepository">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228159010310" ID="ID_1504567435" MODIFIED="1229296084915" STYLE="fork" TEXT="ArrayListAttributeHelper">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1052020265" MODIFIED="1228285437476" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Manipulates ArrayList values stored as XML elements.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010298" ID="ID_1597152330" MODIFIED="1229296084918" STYLE="fork" TEXT="AttributeHelper">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_312195452" MODIFIED="1228285591457" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Abstract ancestor for classes that use XML elements for value storage/extraction.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010300" ID="ID_1596189370" MODIFIED="1229296084922" STYLE="fork" TEXT="AttributeInfo">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1452377170" MODIFIED="1228285698127" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Provides information about attribute storage using reflection. Returns direct references to getter methods. (This is weird.)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010313" ID="ID_1789085727" MODIFIED="1229296084927" STYLE="fork" TEXT="DateAttributeHelper">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1591444590" MODIFIED="1228285759853" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stores and retrieves dates to/from XML. Locale fixed to United Totalitarian States.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010315" ID="ID_1680466668" MODIFIED="1229296084931" STYLE="fork" TEXT="ElementAttributeHelper">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_882694112" MODIFIED="1228285847476" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A trivial helper that does not perform any conversion at all and works with the <font face="DejaVu Sans Mono">Element</font> itself.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010302" ID="ID_1701386084" MODIFIED="1229296084935" STYLE="fork" TEXT="MatchException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1462338219" MODIFIED="1228285937144" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Thrown by implementations of <font face="DejaVu Sans Mono">PackageQueryCallbackInterface</font> on mismatch. A similar mechanism (redundant, but necessary for general correctness and unit testing) is used in GRUX.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010304" ID="ID_1633635090" MODIFIED="1229296084946" STYLE="fork" TEXT="PackageMetadata">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1871459534" MODIFIED="1228286229822" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A data container with file storage capability. Contains a static mapping of parameter names to sets of <font face="DejaVu Sans Mono">PackageType</font>s where the parameters make sense.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010322" ID="ID_1077924213" MODIFIED="1229296084952" STYLE="fork" TEXT="PackageMetadataPropertyRoot">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_644425734" MODIFIED="1228286456501" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Property root for the software repository RSL interface. Just a simple key &lt;-&gt; value mapping with a few getters. This occurs at least five times in BEEN.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228159010322" ID="ID_483514766" MODIFIED="1229296084959" STYLE="fork" TEXT="PackageMetadataProperty">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_139726577" MODIFIED="1228286596267" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Implementation of <font face="DejaVu Sans Mono">SimplerProperty</font>. Uses <font face="DejaVu Sans Mono">AttributeInfo</font> and reflection to return values.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228286603391" ID="ID_876739917" MODIFIED="1228286671947" STYLE="bubble" TEXT="The design of key/value pair storage should be consolidated and modified to avoid the need of reflection.">
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010324" ID="ID_659480362" MODIFIED="1229296084964" STYLE="fork" TEXT="PackageQueryCallbackInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1235638368" MODIFIED="1228286752960" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      One single method, <font face="DejaVu Sans Mono">match()</font>. Decides whether the given package corresponds to the supplied metadata container.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010306" ID="ID_1190837435" MODIFIED="1229296084970" STYLE="fork" TEXT="PackageType">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_230077762" MODIFIED="1228291292829" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enum of possible package types with string constants.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228291297801" ID="ID_364227578" MODIFIED="1228291366527" STYLE="bubble" TEXT="Translated the terrible enum abuse to real Java... This one was really bad, to say the least.">
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010327" ID="ID_87681004" MODIFIED="1229296084973" STYLE="fork" TEXT="PackageTypeAttributeHelper">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1245909876" MODIFIED="1228291475668" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stores and retrieves <font face="DejaVu Sans Mono">PackageType</font>s to/from XML.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010329" ID="ID_1317184461" MODIFIED="1229296084977" STYLE="fork" TEXT="PackageUploadException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1818232724" MODIFIED="1228291502350" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      General failure of a package upload.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010331" ID="ID_1100582240" MODIFIED="1229296084981" STYLE="fork" TEXT="RSLPackageQueryCallback">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1500107217" MODIFIED="1228291641065" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Implementation of <font face="DejaVu Sans Mono">PackageQueryCallbackInterface</font>. Useful to match packages against (non-trivial) RSL conditions.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010337" ID="ID_627839118" MODIFIED="1229296084985" STYLE="fork" TEXT="SoftwareRepositoryException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1453576867" MODIFIED="1228291686497" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The most generic and least useful exceptions...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010333" ID="ID_759845482" MODIFIED="1229296085003" STYLE="fork" TEXT="SoftwareRepositoryImplementation">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_739073279" MODIFIED="1228292245916" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Implements much more than the interface suggests. :-(
    </p>
    <p>
      
    </p>
    <p>
      Methods for package manipulaction, attribute storage and validation. Reimplementation of logging methods for standalone experiments.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228286603391" ID="ID_101552052" MODIFIED="1228292368584" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Upload and download thread reimplementation. Again, complicated and barely readable.
    </p>
    <p>
      
    </p>
    <p>
      Conditions are re-evaluated on each log message. This is wrong, as it involves lots of repeated code (especially the if conditions). It would be much better to simply swap two implementations of an interface, instances of two anonymous inner classes.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
<node COLOR="#006566" CREATED="1228159010333" ID="ID_1829804029" MODIFIED="1229296085007" STYLE="fork" TEXT="PackageUploadThread">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1839518562" MODIFIED="1228292053923" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Another needless reimplementation of a download thread...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010333" ID="ID_742827411" MODIFIED="1229296085011" STYLE="fork" TEXT="PackageDownloadThread">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_397468293" MODIFIED="1228292107323" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Reimplementation of an upload thread. Does every class that deals with network need its own download and upload handlers???
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010339" ID="ID_422940014" MODIFIED="1229296085015" STYLE="fork" TEXT="SoftwareRepositoryInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1775110967" MODIFIED="1228291754229" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This interface describes what a well-behaved software repository can do. Downloads, uploads, removals queries.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010335" ID="ID_1470434678" MODIFIED="1229296085023" STYLE="fork" TEXT="SoftwareRepositoryService">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1334184681" MODIFIED="1228292476805" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Integration with BEEN services framework. Nothing special.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228286603391" ID="ID_417239884" MODIFIED="1228292499537" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Filesystem queries and manipulation should be moved out of these classes.
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010341" ID="ID_1218846376" MODIFIED="1229296085031" STYLE="fork" TEXT="StringAttributeHelper">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_351838620" MODIFIED="1228292525014" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stores and retrieves <font face="DejaVu Sans Mono">String</font> type attributes.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228286603391" ID="ID_1221427659" MODIFIED="1228292651751" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      See the comment in <font face="DejaVu Sans Mono">validateInXML()</font>. Yes, <font face="DejaVu Sans Mono">String</font>s are final. So we need to implement a suitable wrapper. Volunteers?
    </p>
  </body>
</html></richcontent>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159010343" ID="ID_460481789" MODIFIED="1229296085036" STYLE="fork" TEXT="VersionAttributeHelper">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_98057182" MODIFIED="1228292699402" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stores and retrieves the BEEN-specific <font face="DejaVu Sans Mono">Version</font> type. (The type itself should be changed, as already suggested above...)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228085267495" ID="Freemind_Link_1368543677" MODIFIED="1229296085046" POSITION="right" STYLE="bubble" TEXT="taskmanager">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228159044857" ID="ID_425031934" MODIFIED="1229296085050" STYLE="fork" TEXT="CheckPoint">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1438731671" MODIFIED="1228295980465" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Checkpoint representation, in fact just a data container.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044859" ID="ID_756099293" MODIFIED="1229296085055" STYLE="fork" TEXT="Dependency">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1906828412" MODIFIED="1228296141411" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Represents a dependency, which is a piece of data that must match a checkpoint. There are two possibilities: Either both checkpoint type and value must match, or type is sufficient.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044879" ID="ID_235412832" MODIFIED="1229296085065" STYLE="fork" TEXT="HostRuntimeRegistrationListener">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1990385581" MODIFIED="1228296215438" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A listener that conveys host runtime registration and unregistration events from the host manager.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044882" ID="ID_282203385" MODIFIED="1229296085073" STYLE="fork" TEXT="HostRuntimesPortInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_621245325" MODIFIED="1228296599082" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      The way host runtimes see the task manager. (Currently, they have a much more powerful interface (<font face="DejaVu Sans Mono">TaskManagerInterface</font>) available due to bad design. That should change in the future... Only privileged tasks/runtimes need direct access to the task manager.) This interface defines checkpoint queries, service registration, logging, service retrieval and other utility methods.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044862" ID="ID_207985604" MODIFIED="1229296085077" STYLE="fork" TEXT="ServiceEntry">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1662072229" MODIFIED="1228296668066" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A data container representing one service for service lookup.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044864" ID="ID_752947198" MODIFIED="1229296085082" STYLE="fork" TEXT="SystemOutput">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1089534909" MODIFIED="1228296780442" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Just a simple utility class that does nothing but setting the system output verbosity level.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044866" ID="ID_1750440960" MODIFIED="1229296085087" STYLE="fork" TEXT="TaskDescriptor">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1242544620" MODIFIED="1228297071494" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A container for all the task data and metadata. Implements getters and setters for attributes. Supports creating tasks and manipulating their metadata. Represents the XML task description.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228159044866" ID="ID_731180804" MODIFIED="1229296085092" STYLE="fork" TEXT="TaskPropertyObject">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_292093180" MODIFIED="1228297187998" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A special property implementation with a <font face="DejaVu Sans Mono">String</font> key and <font face="DejaVu Sans Mono">Serializable</font> value. (Which could be virtually any <font face="DejaVu Sans Mono">Object</font>...)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044902" ID="ID_1722091252" MODIFIED="1229296085097" STYLE="fork" TEXT="TaskDescriptorException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_429040754" MODIFIED="1228297283255" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      All exceptions that can happen inside <font face="DejaVu Sans Mono">TaskDescriptor</font>. (Are there any descendants?)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044904" ID="ID_771578280" MODIFIED="1229296085101" STYLE="fork" TEXT="TaskDescriptorXmlParser">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_551666105" MODIFIED="1228297988370" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Parses the task descriptor XML structure and creates a corresponding data container object.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044906" ID="ID_883307135" MODIFIED="1229296085105" STYLE="fork" TEXT="TaskManagerException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_764368602" MODIFIED="1228298026440" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      All exceptions within the <font face="DejaVu Sans Mono">taskmanager</font> package.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044913" ID="ID_1978339253" MODIFIED="1229296085115" STYLE="fork" TEXT="TaskManagerImplementation">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_239389748" MODIFIED="1228299297453" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stores and restores configuration to/from a file. (This is not required by the interface.) Listens to host runtime registrations. Selects host runtimes for tasks and enforces exclusiveness constraints when applicable. Some methods just wrap similar host runtime methods. Implements checkpoint handling and service registration as required by the interface. Checks task dependencies and other constraints. Provides <font face="DejaVu Sans Mono">HostRuntimePortInterfaces</font> as a restricted communication channel for host runtimes.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228299305804" ID="ID_1083224148" MODIFIED="1228299383734" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This class should be split into smaller modules. The whole <font face="DejaVu Sans Mono">TaskManagerInterface</font> should not be exposed to all host runtimes. More than 2600 lines in a single file... :-(
    </p>
  </body>
</html></richcontent>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044916" ID="ID_589574210" MODIFIED="1229296085125" STYLE="fork" TEXT="TaskManagerInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_549101918" MODIFIED="1228298537635" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A huge interface. Methods for running tasks, manipulating contexts and tasks, service retrieval, checkpoint queries, log output collection and event listening.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044868" ID="ID_160989591" MODIFIED="1229296085128" STYLE="fork" TEXT="TaskManagerRunner">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1180312612" MODIFIED="1228299441325" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A <font face="DejaVu Sans Mono">main()</font> class that starts the task manager.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044870" ID="ID_660601692" MODIFIED="1229296085133" STYLE="fork" TEXT="TaskRunner">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1389276179" MODIFIED="1228299537126" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A command line guest interface to the task manager. Tasks can be added to a running task manager. It's a <font face="DejaVu Sans Mono">main()</font> class, of course.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159044715" ID="ID_34761076" MODIFIED="1229296085146" STYLE="bubble" TEXT="data">
<edge COLOR="#808080" STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228159044822" ID="ID_851374386" MODIFIED="1229296085233" STYLE="fork" TEXT="CheckPointEntry">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_774639574" MODIFIED="1229281026397" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Represents one reached checkpoint in the taskmanager. Time, host, context, &quot;magic object&quot;... Huh?
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044825" ID="ID_1261357699" MODIFIED="1229296085245" STYLE="fork" TEXT="CheckPointNode">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1078082742" MODIFIED="1229281383044" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This represents the checkpopint in some sort of local data structures. (<font face="DejaVu Sans Mono">DataStructures</font>) (<font face="DejaVu Sans Mono">CheckpointEntry</font> is used by the <font face="DejaVu Sans Mono">TaskManagerImplementation</font>.)
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044827" ID="ID_1651392544" MODIFIED="1229296085253" STYLE="fork" TEXT="CloneSerializable">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_943001760" MODIFIED="1229281273668" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This takes a serializable object and clones it by serializing and deserializing. (Sorry for the question, but: How about atomicity and synchronization?)
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044829" ID="ID_1197410345" MODIFIED="1229296085261" STYLE="fork" TEXT="ContextEntry">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_635449084" MODIFIED="1229281320141" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This represents a context in the <font face="DejaVu Sans Mono">TaskManagerImplementation</font>.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044831" ID="ID_375841676" MODIFIED="1229296085269" STYLE="fork" TEXT="ContextNode">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_956081" MODIFIED="1229281414061" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Again, duplicate representation, this one is used by <font face="DejaVu Sans Mono">DataStructures</font>.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044834" ID="ID_707145123" MODIFIED="1229296085291" STYLE="fork" TEXT="Data">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1462676393" MODIFIED="1229281570633" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This class is a sort of a Facade to DataStructures. From what I can see, synchronization is the only major purpose of this layer.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1142923082" MODIFIED="1229281690358" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      <b>Insane misuse of the <font face="DejaVu Sans Mono">synchronized</font> keyword.</b> This certainly causes a performance penalty.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044853" ID="ID_1857653205" MODIFIED="1229296085308" STYLE="fork" TEXT="DataRuntimeException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1170817434" MODIFIED="1229281726593" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Just about any exception from the <font face="DejaVu Sans Mono">data</font> package.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044836" ID="ID_868295891" MODIFIED="1229296085318" STYLE="fork" TEXT="DataStructures">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_965289061" MODIFIED="1229282442937" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Something big. It manges tasks, contexts, host runtimes and checkpoints. Answers queries about all of them. Can add and remove (nearly) all of them. Links tasks with host runtimes. This is a huge class, but as usal, most lines of code are just exception handling and auxiliary stuff encpsulating (rare) operations on collections.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044838" ID="ID_473192057" MODIFIED="1229296085326" STYLE="fork" TEXT="HostRuntimeEntry">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_423536804" MODIFIED="1229282523549" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This entry is used by the <font face="DejaVu Sans Mono">TaskManagerImplementation</font>. Among the usual stuff, it carries information on reservation of the host runtime for host exclusive tasks.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044840" ID="ID_1596668759" MODIFIED="1229296085332" STYLE="fork" TEXT="HostRuntimeNode">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_409047297" MODIFIED="1229282643648" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Representation of the host runtime in the local <font face="DejaVu Sans Mono">DataStructures</font>. Not a single word about reservation here. Lists checkpoints and task nodes.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044842" ID="ID_632113070" MODIFIED="1229296085337" STYLE="fork" TEXT="Rescue">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_395442977" MODIFIED="1229282821722" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Huge and elaborate backup mechanism that should probably rescue some of the data after a task manager crash. Tries to rescue all the items of the <font face="DejaVu Sans Mono">DataStructures</font> container somehow.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044844" ID="ID_512678188" MODIFIED="1229296085342" STYLE="fork" TEXT="TaskData">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_197253510" MODIFIED="1229282966957" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Stores a <font face="DejaVu Sans Mono">TaskDescriptor</font> and a <font face="DejaVu Sans Mono">TaskInterface</font>. Clones the whole thing, but does not do a deep clone.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044846" ID="ID_323337920" MODIFIED="1229296085346" STYLE="fork" TEXT="TaskEntry">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_213873724" MODIFIED="1229283055323" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      <font face="DejaVu Sans Mono">TaskManagerImplementation</font>'s representation of a task. Lots of strings, paths and timestamps.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044848" ID="ID_449109142" MODIFIED="1229296085353" STYLE="fork" TEXT="TaskExclusivity">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_40569691" MODIFIED="1229283423512" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Exclusive, context-exclusive, non-exclusive. This is an enum.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228164700637" ID="ID_1871499055" MODIFIED="1229283491130" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Corrected enum misuse.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044850" ID="ID_43316053" MODIFIED="1229296085358" STYLE="fork" TEXT="TaskNode">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_291765396" MODIFIED="1229283622913" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is how <font face="DejaVu Sans Mono">DataStructures</font> see the task. The task's checkpoints are listed here and not on the <font face="DejaVu Sans Mono">TaskManagerImplementation</font>'s side.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159044852" ID="ID_137741076" MODIFIED="1229296085365" STYLE="fork" TEXT="TaskState">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_263123668" MODIFIED="1229283693008" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This enum lists nine possible phases of a task's lifetime.
    </p>
  </body>
</html>
</richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228164700637" ID="ID_1533156280" MODIFIED="1229283491130" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Corrected enum misuse.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228084821719" ID="Freemind_Link_1024679247" MODIFIED="1229296085392" POSITION="left" STYLE="bubble" TEXT="task">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159470489" ID="ID_1501445823" MODIFIED="1228180515976" STYLE="bubble" TEXT="anttasks">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
</node>
<node COLOR="#0033ff" CREATED="1228159470558" ID="ID_1788483034" MODIFIED="1229296085393" STYLE="bubble" TEXT="build">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159470597" ID="ID_63041712" MODIFIED="1229296085394" STYLE="bubble" TEXT="ant">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159470638" ID="ID_1487692550" MODIFIED="1229296085397" STYLE="fork" TEXT="AntBuild">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_563163357" MODIFIED="1228180443242" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159470648" ID="ID_133459361" MODIFIED="1229296085399" STYLE="bubble" TEXT="detector">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159470713" ID="ID_912695791" MODIFIED="1229296085403" STYLE="fork" TEXT="DetectorTask">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_949067689" MODIFIED="1228180443242" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159470715" ID="ID_169258639" MODIFIED="1229296085407" STYLE="fork" TEXT="NativeDetector">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_467266237" MODIFIED="1228180443241" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159470719" ID="ID_1076962177" MODIFIED="1229296085411" STYLE="bubble" TEXT="download">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159470797" ID="ID_1856776501" MODIFIED="1229296085413" STYLE="fork" TEXT="CVSDownload">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1703012649" MODIFIED="1228180443240" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159470799" ID="ID_1589092707" MODIFIED="1229296085416" STYLE="fork" TEXT="DownloadThread">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_403379946" MODIFIED="1228180443239" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159470801" ID="ID_1247071616" MODIFIED="1229296085419" STYLE="fork" TEXT="LocalDownload">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1271666594" MODIFIED="1228180443238" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159470802" ID="ID_1145810413" MODIFIED="1229296085422" STYLE="fork" TEXT="LoggingCVSListener">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1038067871" MODIFIED="1228180443237" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159470804" ID="ID_848294051" MODIFIED="1229296085425" STYLE="fork" TEXT="NativeCVSDownload">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_991619141" MODIFIED="1228180443236" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159470809" ID="ID_689007811" MODIFIED="1229296085426" STYLE="bubble" TEXT="edit">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159470852" ID="ID_357992442" MODIFIED="1229296085428" STYLE="bubble" TEXT="regexsubstitute">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159470893" ID="ID_611745225" MODIFIED="1229296085431" STYLE="fork" TEXT="RegexSubstitute">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1404719180" MODIFIED="1228180443235" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159470898" ID="ID_1303658226" MODIFIED="1229296085433" STYLE="bubble" TEXT="example">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159470929" ID="ID_702455301" MODIFIED="1229296085437" STYLE="bubble" TEXT="service1">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159470995" ID="ID_1689102885" MODIFIED="1229296085441" STYLE="fork" TEXT="ExampleService1">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1218192518" MODIFIED="1228180443234" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159471009" ID="ID_1725207691" MODIFIED="1229296085444" STYLE="fork" TEXT="ExampleRemoteInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_751392462" MODIFIED="1228180443233" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159471010" ID="ID_1737084439" MODIFIED="1229296085447" STYLE="fork" TEXT="ExampleRemoteInterfaceImplementation">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1897667987" MODIFIED="1228180443232" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159471013" ID="ID_1223223213" MODIFIED="1229296085453" STYLE="bubble" TEXT="task1">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159471059" ID="ID_338524714" MODIFIED="1229296085461" STYLE="fork" TEXT="Example1Exception">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1722378074" MODIFIED="1228180443231" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159471061" ID="ID_1829374270" MODIFIED="1229296085465" STYLE="fork" TEXT="Example1Task">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_656655036" MODIFIED="1228180443230" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159471067" ID="ID_603434848" MODIFIED="1229296085466" STYLE="bubble" TEXT="execute">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159471136" ID="ID_837282778" MODIFIED="1229296085469" STYLE="fork" TEXT="XamplerExecute">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_429640548" MODIFIED="1228180443229" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159471143" ID="ID_1865853875" MODIFIED="1228180515974" STYLE="bubble" TEXT="fileoperations">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
</node>
<node COLOR="#0033ff" CREATED="1228159471182" ID="ID_1314546145" MODIFIED="1229296085476" STYLE="bubble" TEXT="jboss">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159471212" ID="ID_1424296519" MODIFIED="1229296085477" STYLE="bubble" TEXT="build">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159471253" ID="ID_1442162625" MODIFIED="1229296085479" STYLE="fork" TEXT="JBossBuild">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_916842484" MODIFIED="1228180443228" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159471259" ID="ID_579428969" MODIFIED="1229296085481" STYLE="bubble" TEXT="configure">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159471296" ID="ID_1411232382" MODIFIED="1229296085483" STYLE="fork" TEXT="JBossConfigure">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1868165329" MODIFIED="1228180443227" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159471303" ID="ID_80397701" MODIFIED="1229296085485" STYLE="bubble" TEXT="run">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159471350" ID="ID_897395405" MODIFIED="1229296085488" STYLE="fork" TEXT="JBossRun">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_70799409" MODIFIED="1228180443226" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159471352" ID="ID_724197234" MODIFIED="1229296085492" STYLE="fork" TEXT="JBossStartupTest">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1250114778" MODIFIED="1228180443218" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159471356" ID="ID_650310938" MODIFIED="1229296085494" STYLE="bubble" TEXT="shutdown">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159471393" ID="ID_1922946080" MODIFIED="1229296085496" STYLE="fork" TEXT="JBossShutdown">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_867614488" MODIFIED="1228180443217" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159471398" ID="ID_609294846" MODIFIED="1229296085500" STYLE="bubble" TEXT="jonas">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159471434" ID="ID_258825327" MODIFIED="1229296085501" STYLE="bubble" TEXT="build">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159471484" ID="ID_1145066403" MODIFIED="1229296085504" STYLE="fork" TEXT="JonasBuild">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_352564556" MODIFIED="1228180443216" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159471489" ID="ID_598219230" MODIFIED="1229296085505" STYLE="bubble" TEXT="configure">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159471539" ID="ID_277913910" MODIFIED="1229296085508" STYLE="fork" TEXT="JonasConfigure">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1486593501" MODIFIED="1228180443215" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159471545" ID="ID_543234314" MODIFIED="1229296085509" STYLE="bubble" TEXT="run">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159471581" ID="ID_695162158" MODIFIED="1229296085512" STYLE="fork" TEXT="JonasRun">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_991540679" MODIFIED="1228180443214" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159471587" ID="ID_1223350487" MODIFIED="1229296085514" STYLE="bubble" TEXT="shutdown">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159471640" ID="ID_612347630" MODIFIED="1229296085516" STYLE="fork" TEXT="JonasShutdown">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_100845856" MODIFIED="1228180443213" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159471645" ID="ID_981172121" MODIFIED="1229296085518" STYLE="bubble" TEXT="jythonscript">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159471720" ID="ID_1154921602" MODIFIED="1229296085521" STYLE="fork" TEXT="JythonScript">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1438629842" MODIFIED="1228180443212" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159471722" ID="ID_558532309" MODIFIED="1229296085524" STYLE="fork" TEXT="PythonTaskContext">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1480650708" MODIFIED="1229108832361" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159471727" ID="ID_1732064885" MODIFIED="1229296085527" STYLE="bubble" TEXT="mysql">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159471779" ID="ID_995732882" MODIFIED="1229296085528" STYLE="bubble" TEXT="initialize">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159471820" ID="ID_201524419" MODIFIED="1229296085537" STYLE="fork" TEXT="MySQLInitialize">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_283411338" MODIFIED="1228180443211" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159471825" ID="ID_1115313792" MODIFIED="1229296085539" STYLE="bubble" TEXT="run">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159471877" ID="ID_766247945" MODIFIED="1229296085542" STYLE="fork" TEXT="MySQLRun">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1866192531" MODIFIED="1228180443210" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159471882" ID="ID_1709606605" MODIFIED="1229296085544" STYLE="bubble" TEXT="runcommand">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159471933" ID="ID_207524680" MODIFIED="1229296085546" STYLE="fork" TEXT="MySQLRunCommand">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1238031476" MODIFIED="1228180443209" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159471938" ID="ID_230337184" MODIFIED="1229296085548" STYLE="bubble" TEXT="shutdown">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159471974" ID="ID_308978180" MODIFIED="1229296085550" STYLE="fork" TEXT="MySQLShutdown">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_622972797" MODIFIED="1228180443208" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159471979" ID="ID_675688666" MODIFIED="1229296085553" STYLE="bubble" TEXT="omniorb">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159472015" ID="ID_1262983121" MODIFIED="1229296085554" STYLE="bubble" TEXT="compile">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159472068" ID="ID_489795536" MODIFIED="1229296085555" STYLE="bubble" TEXT="linux">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159472134" ID="ID_990261590" MODIFIED="1229296085557" STYLE="fork" TEXT="OmniOrbLinuxCompile">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_565799561" MODIFIED="1228180443207" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159472137" ID="ID_885058345" MODIFIED="1229296085558" STYLE="bubble" TEXT="source">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159472175" ID="ID_665591993" MODIFIED="1229296085561" STYLE="fork" TEXT="OmniorbSourcePackage">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1554180454" MODIFIED="1228180443206" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159472179" ID="ID_120347890" MODIFIED="1229296085567" STYLE="bubble" TEXT="results">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159472249" ID="ID_1841158643" MODIFIED="1229296085568" STYLE="bubble" TEXT="collect">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159472299" ID="ID_446271978" MODIFIED="1229296085571" STYLE="fork" TEXT="ResultsCollectionTask">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1428893592" MODIFIED="1228180443205" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159472302" ID="ID_274674352" MODIFIED="1228180515967" STYLE="bubble" TEXT="fakerubisrun">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
</node>
<node COLOR="#0033ff" CREATED="1228159472337" ID="ID_420429616" MODIFIED="1229296085572" STYLE="bubble" TEXT="log">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159472405" ID="ID_529401387" MODIFIED="1229296085574" STYLE="fork" TEXT="LogUploadTask">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_802191713" MODIFIED="1228180443204" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159472409" ID="ID_1556086197" MODIFIED="1229296085576" STYLE="bubble" TEXT="rubis">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159472448" ID="ID_501215624" MODIFIED="1229296085578" STYLE="fork" TEXT="CheckAndConvertRubisTask">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1903902668" MODIFIED="1228180443203" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159472450" ID="ID_1409731376" MODIFIED="1229296085580" STYLE="bubble" TEXT="xampler">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159472505" ID="ID_1537488620" MODIFIED="1229296085582" STYLE="fork" TEXT="CheckAndConvertXamplerTask">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_942804854" MODIFIED="1228180443202" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228159472507" ID="ID_1879190863" MODIFIED="1229296085585" STYLE="fork" TEXT="RunResultsPosition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_460490078" MODIFIED="1228180443201" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159472510" ID="ID_1465321085" MODIFIED="1229296085588" STYLE="fork" TEXT="CheckAndConvertTask">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1872929753" MODIFIED="1228180443200" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159472512" ID="ID_584736734" MODIFIED="1229296085591" STYLE="fork" TEXT="ResultsCollectionException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_9438785" MODIFIED="1228180443199" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159472515" ID="ID_6281171" MODIFIED="1229296085596" STYLE="bubble" TEXT="rubis">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159472553" ID="ID_331071687" MODIFIED="1228180515966" STYLE="bubble" TEXT="build">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
</node>
<node COLOR="#0033ff" CREATED="1228159472595" ID="ID_495395989" MODIFIED="1229296085596" STYLE="bubble" TEXT="buildprepare">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159472659" ID="ID_569919453" MODIFIED="1229296085599" STYLE="fork" TEXT="RubisBuildPrepare">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_594697712" MODIFIED="1228180443198" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159472663" ID="ID_49400339" MODIFIED="1229296085602" STYLE="bubble" TEXT="deployer">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159472725" ID="ID_1685353041" MODIFIED="1229296085611" STYLE="fork" TEXT="RubisDeployer">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_252980668" MODIFIED="1228180443197" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159472730" ID="ID_1065948823" MODIFIED="1229296085615" STYLE="bubble" TEXT="mysql">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159472762" ID="ID_714877670" MODIFIED="1229296085616" STYLE="bubble" TEXT="backup">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159472807" ID="ID_704777526" MODIFIED="1229296085619" STYLE="fork" TEXT="RubisMySQLBackup">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1931313680" MODIFIED="1228180443196" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159472819" ID="ID_427388898" MODIFIED="1229296085620" STYLE="bubble" TEXT="initialize">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159472867" ID="ID_1048534791" MODIFIED="1229296085622" STYLE="fork" TEXT="RubisMySQLInitialize">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_350220031" MODIFIED="1228180443195" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159472870" ID="ID_976068935" MODIFIED="1229296085624" STYLE="bubble" TEXT="restore">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159472916" ID="ID_1226945307" MODIFIED="1229296085626" STYLE="fork" TEXT="RubisMySQLRestore">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1738749802" MODIFIED="1228180443194" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159472926" ID="ID_1950182335" MODIFIED="1229296085628" STYLE="bubble" TEXT="run">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159472975" ID="ID_948672500" MODIFIED="1229296085632" STYLE="fork" TEXT="RubisRun">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_199355894" MODIFIED="1228180443193" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159472981" ID="ID_322782547" MODIFIED="1229296085635" STYLE="bubble" TEXT="sofa">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159473031" ID="ID_1257036301" MODIFIED="1229296085639" STYLE="bubble" TEXT="run">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159473116" ID="ID_1799548974" MODIFIED="1229296085654" STYLE="fork" TEXT="SofaDockRun">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1229856090" MODIFIED="1228180443192" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159473118" ID="ID_34163106" MODIFIED="1229296085657" STYLE="fork" TEXT="SofaRunApplication">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1605517463" MODIFIED="1228180443191" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159473122" ID="ID_468337722" MODIFIED="1229296085662" STYLE="fork" TEXT="SofaConnectionManagerService">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1748268493" MODIFIED="1228180443184" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159473124" ID="ID_111059411" MODIFIED="1229296085665" STYLE="fork" TEXT="SofaDockRegistryService">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1718621500" MODIFIED="1228180443183" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159473126" ID="ID_6476541" MODIFIED="1229296085668" STYLE="fork" TEXT="SofaRepositoryService">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1531453826" MODIFIED="1228180443182" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159473129" ID="ID_495308508" MODIFIED="1229296085670" STYLE="bubble" TEXT="status">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159473172" ID="ID_1836649339" MODIFIED="1229296085674" STYLE="fork" TEXT="SofaPS">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_212404444" MODIFIED="1228180443181" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228159473175" ID="ID_572501013" MODIFIED="1229296085677" STYLE="fork" TEXT="SSM">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1866135921" MODIFIED="1228180443180" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159473177" ID="ID_1700586371" MODIFIED="1229296085680" STYLE="fork" TEXT="SofaCommon">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1342698178" MODIFIED="1228180443179" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159473180" ID="ID_1310656719" MODIFIED="1229296085681" STYLE="bubble" TEXT="svn">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159473210" ID="ID_1363349816" MODIFIED="1229296085682" STYLE="bubble" TEXT="checkout">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159473257" ID="ID_34635384" MODIFIED="1229296085685" STYLE="fork" TEXT="SVNCheckout">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1021073337" MODIFIED="1228180443178" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159473263" ID="ID_1339916236" MODIFIED="1229296085687" STYLE="bubble" TEXT="test">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159473294" ID="ID_1248844655" MODIFIED="1229296085688" STYLE="bubble" TEXT="logtester">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159473335" ID="ID_759600782" MODIFIED="1229296085690" STYLE="fork" TEXT="LogTester">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1194373989" MODIFIED="1228180443177" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159473345" ID="ID_378029237" MODIFIED="1229296085691" STYLE="bubble" TEXT="testworker">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159473382" ID="ID_240107121" MODIFIED="1229296085700" STYLE="fork" TEXT="TestWorker">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_818429446" MODIFIED="1228180443176" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159473388" ID="ID_175681785" MODIFIED="1229296085703" STYLE="bubble" TEXT="upload">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159473424" ID="ID_1689753373" MODIFIED="1229296085705" STYLE="bubble" TEXT="localupload">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159473471" ID="ID_547499944" MODIFIED="1229296085707" STYLE="fork" TEXT="LocalUpload">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_969813860" MODIFIED="1228180443175" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159473473" ID="ID_712970319" MODIFIED="1229296085710" STYLE="fork" TEXT="UploadThread">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_457715970" MODIFIED="1228180443173" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159473478" ID="ID_1762072912" MODIFIED="1229296085711" STYLE="bubble" TEXT="metadata">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159473546" ID="ID_881094826" MODIFIED="1229296085716" STYLE="fork" TEXT="CreatePackageMetadata">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_637691653" MODIFIED="1228180443172" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159473552" ID="ID_1247279093" MODIFIED="1229296085718" STYLE="bubble" TEXT="xampler">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159473588" ID="ID_352347717" MODIFIED="1229296085718" STYLE="bubble" TEXT="compile">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#0033ff" CREATED="1228159473631" ID="ID_338052736" MODIFIED="1229296085722" STYLE="bubble" TEXT="linux">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#006566" CREATED="1228159473675" ID="ID_281615487" MODIFIED="1229296085726" STYLE="fork" TEXT="XamplerLinuxCompile">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1196250028" MODIFIED="1228180443171" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159473679" ID="ID_486589799" MODIFIED="1228180515950" STYLE="bubble" TEXT="xamplerresultscollection">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<icon BUILTIN="desktop_new"/>
</node>
<node COLOR="#006566" CREATED="1228159473721" ID="ID_1923850324" MODIFIED="1229296085729" STYLE="fork" TEXT="Job">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1873730218" MODIFIED="1228180443170" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159473723" ID="ID_101200345" MODIFIED="1229296085735" STYLE="fork" TEXT="Service">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1629073036" MODIFIED="1228180443169" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159473724" ID="ID_945924234" MODIFIED="1229296085738" STYLE="fork" TEXT="Task">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_70474282" MODIFIED="1228180443168" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159473727" ID="ID_1473697059" MODIFIED="1229296085741" STYLE="fork" TEXT="TaskException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_840942411" MODIFIED="1228180443167" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159473731" ID="ID_1247606147" MODIFIED="1229296085745" STYLE="fork" TEXT="InvalidServiceStateException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1506524327" MODIFIED="1228180443166" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159473733" ID="ID_1050816812" MODIFIED="1229296085750" STYLE="fork" TEXT="ServiceControlInterface">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_63708958" MODIFIED="1228180443165" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159473736" ID="ID_978355776" MODIFIED="1229296085753" STYLE="fork" TEXT="TaskInitializationException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<icon BUILTIN="desktop_new"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_443191339" MODIFIED="1228180443164" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      NOT SET
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228085292947" ID="Freemind_Link_616281575" MODIFIED="1229296085773" POSITION="left" STYLE="bubble" TEXT="webinterface">
<edge STYLE="sharp_bezier" WIDTH="8"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228159059689" ID="ID_195729619" MODIFIED="1229296085784" STYLE="fork" TEXT="Config">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_278222702" MODIFIED="1228299847913" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Uses the Preferences class to store configuration settings into an implementation dependent backing store. This is how all parts of BEEN should store their config data. There should be one common module to export/import preferences to/from files, but that file storage should be optional. It should not be repeated over and over in every second class.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059713" ID="ID_68654528" MODIFIED="1229296085788" STYLE="fork" TEXT="IllegalWizardScreenSequenceException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1826208162" MODIFIED="1228299985385" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      When the GUI protocol is violated (possibly by the user).
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059715" ID="ID_1016220620" MODIFIED="1229296085793" STYLE="fork" TEXT="InvalidParamValueException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1489525235" MODIFIED="1228300014388" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Wrong value in a JSP required parameter.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059717" ID="ID_411809966" MODIFIED="1229296085798" STYLE="fork" TEXT="InvalidRequestMethodException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1647298535" MODIFIED="1228300043016" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      JSP script called via invalid request method.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059691" ID="ID_44942240" MODIFIED="1229296085813" STYLE="fork" TEXT="LogEntityColumn">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_171623742" MODIFIED="1228301586663" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A mapping useful for generating graphic log outputs. The word Column in the name causes just confusion. It's basically a mapping of context ids to mappings of task ids to task hierarchy entities. (Wow, that was a nice curried sentence.) The type of entity is a generic class parameter.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228159059691" ID="ID_1635841827" MODIFIED="1229296085816" STYLE="fork" TEXT="Value">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_694465037" MODIFIED="1228301830269" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A simple generic utility class with value and id.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059694" ID="ID_1003767191" MODIFIED="1229296085820" STYLE="fork" TEXT="LogUtils">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_240554981" MODIFIED="1228301741278" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Log records concatenation (multiple arrays to one) and log filtering by tasks.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059696" ID="ID_1575277406" MODIFIED="1229296085825" STYLE="fork" TEXT="Message">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1276852508" MODIFIED="1228301920175" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Representation of a message displayed to the user on the web. Immutable, of course.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228159059696" ID="ID_944776285" MODIFIED="1229296085828" STYLE="fork" TEXT="Format">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1352619345" MODIFIED="1228301964858" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Distinguishing between HTML and plain text. Nested enum.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059698" ID="ID_1769829619" MODIFIED="1229296085833" STYLE="fork" TEXT="Messages">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1122442505" MODIFIED="1228302115052" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Just encapsulates a list of messages. Implements some manipulation routines.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228159059698" ID="ID_731120416" MODIFIED="1229296085837" STYLE="fork" TEXT="MessagesIterator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1100911215" MODIFIED="1228302201563" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Makes the <font face="DejaVu Sans Mono">Iterable</font> interface work. Disables the <font face="DejaVu Sans Mono">remove()</font> method.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059719" ID="ID_1708407827" MODIFIED="1229296085842" STYLE="fork" TEXT="MissingParamException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_98481353" MODIFIED="1228302536228" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      When required parameters of the JSP script are missing.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059700" ID="ID_1774798690" MODIFIED="1229296085854" STYLE="fork" TEXT="Page">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_766240942" MODIFIED="1228302701637" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Supports the page display. Contains many technical methods and variables. Probably used as a data container for web page rendering from within JSP. Contains yet another re-implementation of the famous logging methods.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228159059698" ID="ID_1672432579" MODIFIED="1229296085858" STYLE="fork" TEXT="LayoutType">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1177746705" MODIFIED="1228302756827" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Distinguishes between normal and simple layout. It's an enum.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059703" ID="ID_1571146312" MODIFIED="1229296085861" STYLE="fork" TEXT="Params">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1586917623" MODIFIED="1228302822860" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Lots of technical methods for validating and checking the form input values.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059707" ID="ID_1972761883" MODIFIED="1229296085865" STYLE="fork" TEXT="Routines">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_797644948" MODIFIED="1228302923480" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Text formatting, string escaping, date manipulation and a few more functions. Inspired by PHP.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059705" ID="ID_645043827" MODIFIED="1229296085870" STYLE="fork" TEXT="RSLValidator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1805438970" MODIFIED="1228303374455" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Uses the JavaCC generated classes to validate a RSL expression. The whole class is about formatting error messages rather than RSL validation. ;-)
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059709" ID="ID_75090679" MODIFIED="1229296085873" STYLE="fork" TEXT="TaskUtils">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1678460079" MODIFIED="1228303482491" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Gets all checkpoints for a list of tasks in the form of a map. Task entries are mapped to arrays of checkpoints.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059711" ID="ID_1106965183" MODIFIED="1229296085877" STYLE="fork" TEXT="UploadResult">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1776841091" MODIFIED="1228303577272" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Undocumented class with some sort of result data and an optional error message.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059721" ID="ID_569923137" MODIFIED="1229296085881" STYLE="fork" TEXT="WebInterfaceException">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1181158345" MODIFIED="1228303596215" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      All the bad things that might happen to the web interface.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059723" ID="ID_203891887" MODIFIED="1229296085885" STYLE="fork" TEXT="WebInterfaceServlet">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_564791866" MODIFIED="1228305858114" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Gathers HTTP GET and POST messages and passes requests to various modules based on the URL.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159058935" ID="ID_535314516" MODIFIED="1229296085886" STYLE="bubble" TEXT="benchmarkexecution">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#0033ff" CREATED="1228159058974" ID="ID_207664224" MODIFIED="1229271011483" STYLE="bubble" TEXT="configuration">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159059015" ID="ID_1541147970" MODIFIED="1229296085890" STYLE="bubble" TEXT="benchmarks">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228159059138" ID="ID_1503290260" MODIFIED="1229296085895" STYLE="fork" TEXT="BenchmarkRunState">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_373284397" MODIFIED="1229284152188" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Metadata for the benchmark run wizard where form input is stored.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228159059138" ID="ID_293223514" MODIFIED="1229296085897" STYLE="fork" TEXT="WizardScreen">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_141825132" MODIFIED="1229284195124" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This enum lists the screens of the wizard.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059140" ID="ID_1822235335" MODIFIED="1229296085907" STYLE="fork" TEXT="Binary">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_607807586" MODIFIED="1229284455969" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This class wraps the binary entity. Javadoc says it's used in the benchmark manager.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059142" ID="ID_524409298" MODIFIED="1229296085911" STYLE="fork" TEXT="Experiment">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1164506679" MODIFIED="1229284386373" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Benchmark manager's wrapper for the experiment entity. This shows how inconsistent the whole design is. All these representations will have to be unified when the structure is redesigned.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059144" ID="ID_1873800344" MODIFIED="1229296085915" STYLE="fork" TEXT="Run">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1282111781" MODIFIED="1229284444806" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Again, the same thing for runs. This wraps the entity for the benchmark manager.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059152" ID="ID_1349032358" MODIFIED="1229296085922" STYLE="fork" TEXT="ScreenHandler">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1186984458" MODIFIED="1229285011062" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Verifies data from HTTP and sets the appropriate values. It sets <font face="DejaVu Sans Mono">ItemHandler</font>s hat receive and store the data. Verifies that all required parameters are present and correct. Reads from a <font face="DejaVu Sans Mono">HttpServletRequest</font> directly.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228159059152" ID="ID_835446524" MODIFIED="1229296085926" STYLE="fork" TEXT="ItemHandler">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1541241003" MODIFIED="1229285139472" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is used and implemented anonymously inside <font face="DejaVu Sans Mono">ScreenHandler</font> many times. Handling widget-specific events is simply delegated to a corresponding <font face="DejaVu Sans Mono">ItemHandler</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159059079" ID="ID_602034147" MODIFIED="1229271011482" STYLE="bubble" TEXT="rsl">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159059156" ID="ID_866213169" MODIFIED="1229296085929" STYLE="bubble" TEXT="event">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228159059193" ID="ID_1833298037" MODIFIED="1229296085932" STYLE="fork" TEXT="Event">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_297607883" MODIFIED="1229285269510" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Task manager status change or service status change. This enum lists the two basic events that can come from the web interface.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059196" ID="ID_1598576480" MODIFIED="1229296085935" STYLE="fork" TEXT="EventListener">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_701513968" MODIFIED="1229285324673" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Sipmly <font face="DejaVu Sans Mono">receiveEvent()</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059197" ID="ID_919724262" MODIFIED="1229296085939" STYLE="fork" TEXT="EventManager">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_290703735" MODIFIED="1229285807656" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Receives an event and sends it to all registered listeners. Implements event sending and listener registration and unregistration. That's all.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159059203" ID="ID_1742497559" MODIFIED="1229271011482" STYLE="bubble" TEXT="forms">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
</node>
<node COLOR="#0033ff" CREATED="1228159059250" ID="ID_1561728286" MODIFIED="1229296085941" STYLE="bubble" TEXT="hosts">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228159059294" ID="ID_1523524623" MODIFIED="1229296085947" STYLE="fork" TEXT="UserPropertiesHandler">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1009222475" MODIFIED="1229286069005" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Users can set user-defined properties to hosts. This is what takes care of those properties. The handler follows exactly the same pattern as the <font face="DejaVu Sans Mono">ScreenHandler</font> from <font face="DejaVu Sans Mono">webinterface.benchmarks</font>. The main purpose of the class is to receive data from HTTP, verify it and store it.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228159059152" ID="ID_123781209" MODIFIED="1229296085951" STYLE="fork" TEXT="ValueHandler">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1486030216" MODIFIED="1229286191601" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Just like <font face="DejaVu Sans Mono">ItemHandler</font> nested in <font face="DejaVu Sans Mono">ScreenHandler</font>. Implemented anonymously many times to support various value types.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159059296" ID="ID_829029942" MODIFIED="1229271011482" STYLE="bubble" TEXT="layout">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
</node>
<node COLOR="#0033ff" CREATED="1228159059332" ID="ID_1813760763" MODIFIED="1229296085958" STYLE="bubble" TEXT="modules">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228159059403" ID="ID_657785446" MODIFIED="1229296085968" STYLE="fork" TEXT="BenchmarksModule">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1498678458" MODIFIED="1229286755051" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This module takes care of benchmark manager GUI representation. This class is huge and poorly documented. Each class in this package has a list of actions it can perform. These actions are triggered by user interaction. Each of them is represented by a public method that receives (among others) the servlet request and servlet response. Then it's all about getting information from the request and generating the response.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059420" ID="ID_345987448" MODIFIED="1229296085973" STYLE="fork" TEXT="ConfigurationModule">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1130993086" MODIFIED="1229286985893" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is a shorter and quite well readable example of the whole GUI event handling principle. See <font face="DejaVu Sans Mono">invokeMethodForAction()</font> and <font face="DejaVu Sans Mono">configuration()</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059405" ID="ID_1648749079" MODIFIED="1229296085980" STYLE="fork" TEXT="HostsModule">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1925068083" MODIFIED="1229287188899" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This handles communication between the task manager and the web interface. Exactly the same pattern as above.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228159059405" ID="ID_778182413" MODIFIED="1229296085982" STYLE="fork" TEXT="HopstListComarator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1792205178" MODIFIED="1229287219692" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Auxiliary comparator.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059405" ID="ID_1758292636" MODIFIED="1229296085985" STYLE="fork" TEXT="HostListInGroupComparator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_991720978" MODIFIED="1229287219692" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Auxiliary comparator.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059405" ID="ID_765304539" MODIFIED="1229296085988" STYLE="fork" TEXT="GroupListComparator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1393497883" MODIFIED="1229287219692" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Auxiliary comparator.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059405" ID="ID_1976788750" MODIFIED="1229296085991" STYLE="fork" TEXT="AliasListComparator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1442196500" MODIFIED="1229287219692" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Auxiliary comparator.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059408" ID="ID_122869904" MODIFIED="1229296085994" STYLE="fork" TEXT="MenuItem">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_821079456" MODIFIED="1229287587135" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is an item id -&gt; item name pair.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059410" ID="ID_671777694" MODIFIED="1229296085999" STYLE="fork" TEXT="Module">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1283835469" MODIFIED="1229287488665" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Abstract ancestor of all the module classes. Defines basic functionality. Amazingly, there is no mapping from action names to methods. Instead, action names are first converted to method names and invocations are carried out using reflection.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059412" ID="ID_1298261037" MODIFIED="1229296086004" STYLE="fork" TEXT="PackagesModule">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_41483682" MODIFIED="1229287664899" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Communicates with the software repository, just another module.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228159059405" ID="ID_1256721700" MODIFIED="1229296086007" STYLE="fork" TEXT="PackageListComparator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_502048394" MODIFIED="1229287219692" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Auxiliary comparator.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059405" ID="ID_1631052601" MODIFIED="1229296086011" STYLE="fork" TEXT="NoPackagesReason">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_528675356" MODIFIED="1229287785295" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enum of reasons why zero packages are displayed in the pakage list. Intial state, no matching packages or errors during package list evaluation.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059414" ID="ID_1527030668" MODIFIED="1229296086015" STYLE="fork" TEXT="ResultsModule">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1938081697" MODIFIED="1229287947782" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Communicates with the results repository. This class performs lots of unchecked operations. (But that's not the worst problem.) The hardcoded entity hierarchy is used thoroughly here, so this module will have to be rewritten if the hierarchy is changed.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059416" ID="ID_682151863" MODIFIED="1229296086030" STYLE="fork" TEXT="ServicesModule">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_155628019" MODIFIED="1229288150094" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This module controls service management. Probably performs some kind of services lookup, too. Core BEEN services are stored here as constnts. Contains lots of auixliary methods, including active waiting.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#0033ff" CREATED="1228164700637" ID="ID_1793226441" MODIFIED="1229288391618" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Busy waiting is <b>evil</b>. This would be justifiable on a SMP OS that has idle CPUs available, but should never appear in userspace. The whole problem is caused by the fact that there's no unified event and messaging infrastructure in BEEN.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="TODO"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059418" ID="ID_241573636" MODIFIED="1229296086034" STYLE="fork" TEXT="TasksModule">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1149636558" MODIFIED="1229288496688" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This module communicates with the task manager and provides the task execution environment.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159059422" ID="ID_1817594753" MODIFIED="1229296086037" STYLE="bubble" TEXT="packages">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228159059480" ID="ID_1910131235" MODIFIED="1229296086041" STYLE="fork" TEXT="Condition">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1302251182" MODIFIED="1229289390058" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A simple condition wrapper that hides a complicated approach. Sofware repository receives just an empty wrapper with parameter names. The real values are then taken from the callback that is still bound to the correct source. In fact it is much more difficult.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059483" ID="ID_1914863740" MODIFIED="1229296086049" STYLE="fork" TEXT="Operator">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1178053130" MODIFIED="1229294433783" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enums of operator types and corresponding evaluation methods.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
<node COLOR="#006566" CREATED="1228164700637" ID="ID_1742547062" MODIFIED="1229294744534" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This class was coded so poorly that I had to reorganize it completely. I transformed the weird class containing instances of itself to what it should have always been: two nested enums. Operators can now be requested directly by class and name, thus avoiding any possible external array traversals. Mind the use of variable arguments to create arays painlessly from an initializer.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="CHANGELOG"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059484" ID="ID_1630934439" MODIFIED="1229296086053" STYLE="fork" TEXT="PackageDetailsQueryCallback">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1213422332" MODIFIED="1229295047341" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      A callback interface. Asks whether a package matches the given metadata.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059486" ID="ID_851014020" MODIFIED="1229296086058" STYLE="fork" TEXT="PackageListQueryCallback">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1484415769" MODIFIED="1229295264048" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is a AND condition consisting of multiple &quot;atomic&quot; conditions. All of the mare not transferred at once with the query interface. Instead, they are fetched upon request usign a reverse calback. This matter is in fact much more complicated...
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159059489" ID="ID_815515104" MODIFIED="1229296086061" STYLE="bubble" TEXT="ref">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228159059540" ID="ID_407393314" MODIFIED="1229296086065" STYLE="fork" TEXT="LoadServerReference">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1408227129" MODIFIED="1229295619749" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Extension to <font face="DejaVu Sans Mono">RMIReference</font>, holds a reference to <font face="DejaVu Sans Mono">LoadServerInterface</font>.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059535" ID="ID_478293099" MODIFIED="1229296086071" STYLE="fork" TEXT="RMIReference">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_587128653" MODIFIED="1229295575071" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This is a good piece of design. It's a self-renewing reference to RMI remote objects. Implemented using generics. <font face="DejaVu Sans Mono">get()</font>, <font face="DejaVu Sans Mono">drop()</font>, <font face="DejaVu Sans Mono">acquire()</font> - that's a nice interface. This is an abstract ancestor of all the reference classes.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059537" ID="ID_988823291" MODIFIED="1229296086081" STYLE="fork" TEXT="ServiceReference">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_68184363" MODIFIED="1229295663095" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This encapsulates a reference to a generic BEEN service.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
<node COLOR="#006566" CREATED="1228159059542" ID="ID_1914890842" MODIFIED="1229296086084" STYLE="fork" TEXT="TaskManagerReference">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1177255370" MODIFIED="1229295694951" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Reference to the TaskManager.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159059546" ID="ID_1673127353" MODIFIED="1229296086086" STYLE="bubble" TEXT="services">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228159059604" ID="ID_1819959855" MODIFIED="1229296086089" STYLE="fork" TEXT="ServiceInfo">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_323093051" MODIFIED="1229295988613" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Just another piece of metadata that stores information about a BEEN service. Brobably a helper class used within the web interface.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159059606" ID="ID_951619266" MODIFIED="1229296086091" STYLE="bubble" TEXT="tasks">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
<node COLOR="#006566" CREATED="1228159059640" ID="ID_1181238394" MODIFIED="1229296086094" STYLE="fork" TEXT="TaskListMode">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="16"/>
<node COLOR="#000000" CREATED="1228164700637" ID="ID_1899388615" MODIFIED="1229295751873" STYLE="bubble">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      Enum that says when to display the tasklist. Normal, before execution, after execution.
    </p>
  </body>
</html></richcontent>
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<attribute_layout NAME_WIDTH="97" VALUE_WIDTH="97"/>
<attribute NAME="beenNodeType" VALUE="NOTE"/>
</node>
</node>
</node>
<node COLOR="#0033ff" CREATED="1228159059643" ID="ID_927012388" MODIFIED="1229271011482" STYLE="bubble" TEXT="widgets">
<edge COLOR="#808080" STYLE="bezier" WIDTH="thin"/>
<font NAME="DejaVu Sans Condensed" SIZE="18"/>
</node>
</node>
</node>
</map>
