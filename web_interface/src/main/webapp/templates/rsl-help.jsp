<%--

  BEEN: Benchmarking Environment
  ==============================
   
  File author: David Majda

  GNU Lesser General Public License Version 2.1
  ---------------------------------------------
  Copyright (C) 2004-2006 Distributed Systems Research Group,
  Faculty of Mathematics and Physics, Charles University in Prague

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License version 2.1, as published by the Free Software Foundation.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General Public
  License along with this library; if not, write to the Free Software
  Foundation, Inc., 59 Temple Place, Suite 330, Boston,
  MA  02111-1307  USA

--%><%@
	include file="includes.jsp"
%><%
	String type = (String)application.getAttribute("type");
%>

<h1>
	RSL help
	<% if (type.equals("host")) { %>(host specific)<% } %>
	<% if (type.equals("os")) { %>(operating system specific)<% } %>
	<% if (type.equals("app")) { %>(application specific)<% } %>
</h1>

<p>Using RSL (Restriction Specification Language), you can specify restrictions
on
<% if (type.equals("host")) { %>hosts<% } %>
<% if (type.equals("os")) { %>operating system<% } %>
<% if (type.equals("app")) { %>applications<% } %>
using their properties.</p>

<h2>Simple conditions</h2>

<h3>With integer properties:</h3>

<pre>processor.speed == 1024
processor.speed != 1024
processor.speed &lt;  1024
processor.speed &lt;= 1024
processor.speed &gt;= 1024
processor.speed &gt;  1024</pre>

<h3>With version properties:</h3>

<pre>java.version == 1.5
java.version != 1.5
java.version &lt;  1.5
java.version &lt;= 1.5
java.version &gt;= 1.5
java.version &gt;  1.5</pre>

<h3>With date+time properties:</h3>

<pre>date &gt;= 2006-08
date &gt;= 2006-08-09
date &gt;= 2006-08-09T14:54+01:00
date &gt;= 2006-08-09T14:54:07+01:00
date &gt;= 2006-08-09T14:54:07.48+01:00</pre>

<pre>date == 2006-08-09T14:54:07.48+01:00
date != 2006-08-09T14:54:07.48+01:00
date &lt;  2006-08-09T14:54:07.48+01:00
date &lt;= 2006-08-09T14:54:07.48+01:00
date &gt;= 2006-08-09T14:54:07.48+01:00
date &gt;  2006-08-09T14:54:07.48+01:00</pre>

<h3>With string properties:</h3>

<pre>name = &quot;aiya.ms.mff.cuni.cz&quot;
name != &quot;aiya.ms.mff.cuni.cz&quot;
name =~ /mff\.cuni\.cz/
name !~ /MFF\.CUNI\.CZ/i
</pre>

<h3>With list properties:</h3>

<pre>memberof contains &quot;my-group&quot;</pre>

<h2>Combining expressions</h2>

<p>Conditions can be combined using <code>&amp;&amp;</code> (logical and) and
<code>||</code> (logical or) operators, which work exactly like similar 
operators used in boolean expressions in Java.</p>

<pre>procesor.speed &gt; 1024 &amp;&amp; java.version &gt; 1.5
procesor.speed &gt; 1024 || java.version &gt; 1.5</pre>

<p>Parenthesis can be used to denote precedence:</p>

<pre>procesor.speed &gt; 2048 || (processor.speed &gt; 1024 &amp;&amp; processor.cache &gt; 512)</pre>

<h2>Qualifying expressions</h2>

<p>Following expression will evaluate as true on machine with two processors,
one of which will be faster then 1GHz and other will be from AMD. This is
because the subconditions are both evaluated in context of the whole property
tree:</p>

<pre>processor.speed &gt; 1024 &amp;&amp; processor.vendor == &quot;AMD&quot;</pre>

<p>To signify that expression should be evaluated in context
of concrete object, use qualification:</p>

<pre>processor { speed &gt; 1024 &amp;&amp; vendor == &quot;AMD&quot; }</pre>

<% if (type.equals("host")) { %><h2>Host properties</h2><% } %>
<% if (type.equals("os")) { %><h2>Operating system properties</h2><% } %>
<% if (type.equals("app")) { %><h2>Application properties</h2><% } %>

<% if (type.equals("host")) { %>
<table class="real">
  <tr>
    <th>Full property/object path</th>
    <th>Value type</th>
    <th>Description</th>
    <th>OS type</th>
  </tr>
  <tr>
    <td>adapters</td>
    <td>integer</td>
    <td>Number of network adapters installed on the host. May be zero if none 
    have been detected.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>aliases</td>
    <td>integer</td>
    <td>Number of software aliases defined for the host. May be zero if no 
    aliases matched.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>applications</td>
    <td>integer</td>
    <td>Number of applications or software packages installed on the host. May 
    be zero if none have been detected.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>detector</td>
    <td>string</td>
    <td>Identification string of the detector used to collect data.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>drives</td>
    <td>integer</td>
    <td>Number of physical drives installed on the host. May be zero if none 
    have been detected.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>checkdate</td>
    <td>string</td>
    <td>Date when the data about host have been collected. Format is YYYY/MM/DD.
    </td>
    <td>all</td>
  </tr>
  <tr>
    <td>checktime</td>
    <td>string</td>
    <td>Time when data about host have been collected. Format is hh:mm.ss.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>memberof</td>
    <td>list of strings</td>
    <td>Names of all groups host is member of.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>name</td>
    <td>string</td>
    <td>Network name of the host.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>processors</td>
    <td>integer</td>
    <td>Number of processors installed on the host. May be zero if none have 
    been detected.</td>
    <td>all</td>
  </tr>
  <tr class="object-header">
    <td>adapter(<i>i</i>)</td>
    <td>object</td>
    <td>Object which represents network adapter.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>adapter(<i>i</i>).mac</td>
    <td>string</td>
    <td>MAC address of the newtork adapter. May be empty if none has been found.
    </td>
    <td>all</td>
  </tr>
  <tr>
    <td>adapter(<i>i</i>).name</td>
    <td>string</td>
    <td>Name of the network adapter.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>adapter(<i>i</i>).type</td>
    <td>string</td>
    <td>Type of the network adapter (e.g. Ethernet 802.3).</td>
    <td>all</td>
  </tr>
  <tr>
    <td>adapter(<i>i</i>).vendor</td>
    <td>string</td>
    <td>Vendor name of the adapter. May be empty on *NIX.</td>
    <td>all</td>
  </tr>
  <tr class="object-header">
    <td>alias(<i>i</i>)</td>
    <td>object</td>
    <td>Object which stores details about software alias.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>alias(<i>i</i>).alias</td>
    <td>string</td>
    <td>Name of the alias as defined by the user.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>alias(<i>i</i>).name</td>
    <td>string</td>
    <td>Name of the application this alias represents.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>alias(<i>i</i>).vendor</td>
    <td>string</td>
    <td>Vendor of the application this alias represents.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>alias(<i>i</i>).version</td>
    <td>version</td>
    <td>Version of the application this alias represents.</td>
    <td>all</td>
  </tr>
  <tr class="object-header">
    <td>application(<i>i</i>)</td>
    <td>object</td>
    <td>Object which represents software package or application installed on the 
    system.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>application(<i>i</i>).name</td>
    <td>string</td>
    <td>Name of the application as reported by the installer or packaging system. 
    This cannot be empty.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>application(<i>i</i>).vendor</td>
    <td>string</td>
    <td>Vendor of the application as reported by the installer of packager. May 
    be empty.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>application(<i>i</i>).version</td>
    <td>version</td>
    <td>Version of the application or package.</td>
    <td>all</td>
  </tr>
  <tr class="object-header">
    <td>beendisk</td>
    <td>object</td>
    <td>Object which represents drive on which BEEN is installed.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>beendisk.beenhome</td>
    <td>string</td>
    <td>Path to the root directory of the BEEN installation.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>beendisk.freespace</td>
    <td>integer</td>
    <td>Free space (in bytes) available on the drive on which BEEN is installed.
    </td>
    <td>all</td>
  </tr>
  <tr>
    <td>beendisk.size</td>
    <td>integer</td>
    <td>Size of the drive on which BEEN is installed in bytes.</td>
    <td>all</td>
  </tr>
  <tr class="object-header">
    <td>drive(<i>i</i>)</td>
    <td>object</td>
    <td>Object which represents physical drive installed on the host. This 
    includes CD/DVD drives, HDDs, tape drives.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>drive(<i>i</i>).device</td>
    <td>string</td>
    <td>Name of the device assigned to the drive by the operating system. This 
    will differ for various os families.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>drive(<i>i</i>).media</td>
    <td>string</td>
    <td>Type of media in the drive. E.g. HDD or CD-ROM.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>drive(<i>i</i>).model</td>
    <td>string</td>
    <td>Model name of the drive as stored in the drive's internal memory.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>drive(<i>i</i>).partitions</td>
    <td>integer</td>
    <td>Number of partitions detected on the drive.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>drive(<i>i</i>).size</td>
    <td>integer</td>
    <td>Total size of the drive in bytes. This may be larger than what is 
    accessible to the user due to hidden data.</td>
    <td>all</td>
  </tr>
  <tr class="object-header">
    <td>drive(<i>i</i>).partition(<i>j</i>)</td>
    <td>object</td>
    <td>Object which represents partition on the drive.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>drive(<i>i</i>).partition(<i>j</i>).device</td>
    <td>string</td>
    <td>Name of the device assigned to the partition by the OS. Name formats 
    differ on various Oses.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>drive(<i>i</i>).partition(<i>j</i>).filesystem</td>
    <td>string</td>
    <td>Name of the filesystem on the partition.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>drive.partition.freespace</td>
    <td>integer</td>
    <td>Free space available on the partition in bytes.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>drive(<i>i</i>).partition(<i>j</i>).name</td>
    <td>string</td>
    <td>Name of the partition.On Windows this is name of the disk (e.g. C:), on 
    *NIX this is mountpoint.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>drive(<i>i</i>).partition(<i>j</i>).size</td>
    <td>integer</td>
    <td>Size of the partition in bytes. This may be larger that what is 
    accessible to the user due to hidden data.</td>
    <td>all</td>
  </tr>
  <tr class="object-header">
    <td>java</td>
    <td>object</td>
    <td>Object which stores data about Java installed on the host.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>java.runtime</td>
    <td>string</td>
    <td>Name of the Java runtime installed on the host.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>java.runtimever</td>
    <td>version</td>
    <td>Version of the runtime installed on the host.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>java.specification</td>
    <td>version</td>
    <td>Version of the Java specification Java on the host complies to.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>java.vendor</td>
    <td>string</td>
    <td>Name of the vendor of the Java.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>java.version</td>
    <td>version</td>
    <td>Version of the Java installed on the host.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>java.vmvendor</td>
    <td>string</td>
    <td>Name of the vendor of the virtual machine.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>java.vmversion</td>
    <td>version</td>
    <td>Version of the virtual machine installed on the host.</td>
    <td>all</td>
  </tr>
  <tr class="object-header">
    <td>memory</td>
    <td>object</td>
    <td>Object which represents memory sub-system of the host.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>memory.pagefile</td>
    <td>integer</td>
    <td>Total size of all paging files in use by the operating system. It is in 
    bytes.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>memory.physical</td>
    <td>integer</td>
    <td>Amount of the RAM installed on the host in bytes.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>memory.swap</td>
    <td>integer</td>
    <td>Total size of swap files used by the system in bytes.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>memory.virtual</td>
    <td>integer</td>
    <td>Total amount of the virtual memory accessible to the processes on the 
    host in bytes.</td>
    <td>all</td>
  </tr>
  <tr class="object-header">
    <td>os</td>
    <td>object</td>
    <td>Object which represents operating system on the host.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>os.arch</td>
    <td>string</td>
    <td>Computer architecture of the host (e.g. x86_64).</td>
    <td>all</td>
  </tr>
  <tr>
    <td>os.family</td>
    <td>string</td>
    <td>Operating system family. This can be one of Windows, Solaris, Linux, 
    Other.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>os.name</td>
    <td>string</td>
    <td>Name of the operating system.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>os.vendor</td>
    <td>string</td>
    <td>Vendor of the operating system.</td>
    <td>all</td>
  </tr>
  <tr class="object-header">
    <td>os.linux</td>
    <td>object</td>
    <td>Object which stores details about Linux operating system.</td>
    <td>Linux</td>
  </tr>
  <tr>
    <td>os.linux.distribution</td>
    <td>string</td>
    <td>Name of the Linux distribution.</td>
    <td>Linux</td>
  </tr>
  <tr>
    <td>os.linux.distversion</td>
    <td>string</td>
    <td>Version of the Linux distribution.</td>
    <td>Linux</td>
  </tr>
  <tr>
    <td>os.linux.kernelversion</td>
    <td>version</td>
    <td>Version of the kernel.</td>
    <td>Linux</td>
  </tr>
  <tr>
    <td>os.linux.osrelease</td>
    <td>string</td>
    <td>Operating system release string (usually contains kernel version and some additional data).</td>
    <td>Linux</td>
  </tr>
  <tr>
    <td>os.linux.osversion</td>
    <td>string</td>
    <td>Operating system version string (usually kernel type and compilation date).</td>
    <td>Linux</td>
  </tr>
  <tr class="object-header">
    <td>os.other</td>
    <td>object</td>
    <td>Object which stores data about OS not supported by native detector.</td>
    <td>Other</td>
  </tr>
  <tr>
    <td>os.other.version</td>
    <td>version</td>
    <td>Version of the operating system as reported by Java.</td>
    <td>Other</td>
  </tr>
  <tr>
    <td>os.solaris</td>
    <td>object</td>
    <td>Object which stores properties of the Solaris operating system.</td>
    <td>Solaris</td>
  </tr>
  <tr class="object-header">
    <td>os.windows</td>
    <td>object</td>
    <td>Object which stores details about Windows operating system.</td>
    <td>Windows</td>
  </tr>
  <tr>
    <td>os.windows.build</td>
    <td>string</td>
    <td>Build identification string (usually Uniprocessor free).</td>
    <td>Windows</td>
  </tr>
  <tr>
    <td>os.windows.encryption</td>
    <td>integer</td>
    <td>Strength of the encryption provided by the system. It is the size of the 
    encryption key in bits.</td>
    <td>Windows</td>
  </tr>
  <tr>
    <td>os.windows.sp</td>
    <td>version</td>
    <td>Version of the service pack.</td>
    <td>Windows</td>
  </tr>
  <tr>
    <td>os.windows.sysdir</td>
    <td>string</td>
    <td>Path to the system directory of the Windows.</td>
    <td>Windows</td>
  </tr>
  <tr>
    <td>os.windows.version</td>
    <td>string</td>
    <td>Version of the Windows.</td>
    <td>Windows</td>
  </tr>
  <tr>
    <td>os.windows.windir</td>
    <td>string</td>
    <td>Path to the Windows installation directory.</td>
    <td>Windows</td>
  </tr>
  <tr class="object-header">
    <td>processor(<i>i</i>)</td>
    <td>object</td>
    <td>Object which stores details about processor.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>processor(<i>i</i>).cache</td>
    <td>integer</td>
    <td>Size of the built-in L2 cache in kilobytes.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>processor(<i>i</i>).model</td>
    <td>string</td>
    <td>Model name of the processor.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>processor(<i>i</i>).speed</td>
    <td>integer</td>
    <td>Speed of the processor in MHz. This may change between readings (especially 
    on modern CPUs).</td>
    <td>all</td>
  </tr>
  <tr>
    <td>processor(<i>i</i>).vendor</td>
    <td>string</td>
    <td>Name of the processor's vendor.</td>
    <td>all</td>
  </tr>
  <tr class="object-header">
    <td>user</td>
    <td>object</td>
    <td>User-defined properties. This object can contain anything you like.</td>
    <td>all</td>
  </tr>
</table>
<% } %>

<% if (type.equals("os")) { %>
<table class="real">
  <tr>
    <th>Full property/object path</th>
    <th>Value type</th>
    <th>Description</th>
    <th>OS type</th>
  </tr>
  <tr>
    <td>arch</td>
    <td>string</td>
    <td>Computer architecture of the host (e.g. x86_64).</td>
    <td>all</td>
  </tr>
  <tr>
    <td>family</td>
    <td>string</td>
    <td>Operating system family. This can be one of Windows, Solaris, Linux, 
    Other.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>name</td>
    <td>string</td>
    <td>Name of the operating system.</td>
    <td>all</td>
  </tr>
  <tr>
    <td>vendor</td>
    <td>string</td>
    <td>Vendor of the operating system.</td>
    <td>all</td>
  </tr>
  <tr class="object-header">
    <td>windows</td>
    <td>object</td>
    <td>Object which stores details about Windows operating system.</td>
    <td>Windows</td>
  </tr>
  <tr>
    <td>windows.build</td>
    <td>string</td>
    <td>Build identification string (usually Uniprocessor free).</td>
    <td>Windows</td>
  </tr>
  <tr>
    <td>windows.encryption</td>
    <td>integer</td>
    <td>Strength of the encryption provided by the system. It is the size of the 
    encryption key in bits.</td>
    <td>Windows</td>
  </tr>
  <tr>
    <td>windows.sp</td>
    <td>version</td>
    <td>Version of the service pack.</td>
    <td>Windows</td>
  </tr>
  <tr>
    <td>windows.sysdir</td>
    <td>string</td>
    <td>Path to the system directory of the Windows.</td>
    <td>Windows</td>
  </tr>
  <tr>
    <td>windows.version</td>
    <td>string</td>
    <td>Version of the Windows.</td>
    <td>Windows</td>
  </tr>
  <tr>
    <td>windows.windir</td>
    <td>string</td>
    <td>Path to the Windows installation directory.</td>
    <td>Windows</td>
  </tr>
  <tr class="object-header">
    <td>linux</td>
    <td>object</td>
    <td>Object which stores details about Linux operating system.</td>
    <td>Linux</td>
  </tr>
  <tr>
    <td>linux.distribution</td>
    <td>string</td>
    <td>Name of the Linux distribution.</td>
    <td>Linux</td>
  </tr>
  <tr>
    <td>linux.distversion</td>
    <td>string</td>
    <td>Version of the Linux distribution.</td>
    <td>Linux</td>
  </tr>
  <tr>
    <td>linux.kernelversion</td>
    <td>version</td>
    <td>Version of the kernel.</td>
    <td>Linux</td>
  </tr>
  <tr>
    <td>linux.osversion</td>
    <td>string</td>
    <td>Operating system version string (usually kernel type and compilation date).</td>
    <td>Linux</td>
  </tr>
  <tr>
    <td>linux.osrelease</td>
    <td>string</td>
    <td>Operating system release string (usually contains kernel version and some additional data).</td>
    <td>Linux</td>
  </tr>
  <tr class="object-header">
    <td>other</td>
    <td>object</td>
    <td>Object which stores data about OS not supported by native detector.</td>
    <td>Other</td>
  </tr>
  <tr>
    <td>other.version</td>
    <td>version</td>
    <td>Version of the operating system as reported by Java.</td>
    <td>Other</td>
  </tr>
  <tr>
    <td>solaris</td>
    <td>object</td>
    <td>Object which stores properties of the Solaris operating system.</td>
    <td>Solaris</td>
  </tr>
</table>
<% } %>

<% if (type.equals("app")) { %>
<table class="real">
  <tr>
    <th>Full property/object path</th>
    <th>Value type</th>
    <th>Description</th>
  </tr>
  <tr>
    <td>name</td>
    <td>string</td>
    <td>Name of the application as reported by the installer or packaging system. 
    This cannot be empty.</td>
  </tr>
  <tr>
    <td>vendor</td>
    <td>string</td>
    <td>Vendor of the application as reported by the installer of packager. May 
    be empty.</td>
  </tr>
  <tr>
    <td>version</td>
    <td>version</td>
    <td>Version of the application or package.</td>
  </tr>
</table>
<% } %>