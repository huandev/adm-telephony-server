<#macro page title>
  <html>
  <head>
    <title>ADM TELEPHONY SERVER - ${title?html}</title>
    <meta http-equiv="Content-type" content="text/html">
    
    <style>
 * {
	margin: 0;
	padding: 0;
}

body {
	font-family: "Trebuchet MS", Helvetica, Sans-Serif;
	font-size: 14px;
}

a {
	text-decoration: none;
	color: #838383;
}

a:hover {
	color: black;
}

#cssmenu ul,
#cssmenu li,
#cssmenu span,
#cssmenu a {
  margin: 0;
  padding: 0;
  position: relative;
}
#cssmenu {
  height: 49px;
  border-radius: 5px 5px 0 0;
  -moz-border-radius: 5px 5px 0 0;
  -webkit-border-radius: 5px 5px 0 0;
  background: #141414;
  background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAAxCAIAAACUDVRzAAAAA3NCSVQICAjb4U/gAAAALElEQVQImWMwMrJi+v//PxMDw3+m//8ZoPR/qBgDEhuXGLoeYswhXg8R5gAAdVpfoJ3dB5oAAAAASUVORK5CYII=) 100% 100%; 
  background: -moz-linear-gradient(top, #32323a 0%, #141414 100%);
  background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #32323a), color-stop(100%, #141414));
  background: -webkit-linear-gradient(top, #32323a 0%, #141414 100%);
  background: -o-linear-gradient(top, #32323a 0%, #141414 100%);
  background: -ms-linear-gradient(top, #32323a 0%, #141414 100%);
  background: linear-gradient(to bottom, #32323a 0%, #141414 100%);
  border-bottom: 2px solid #e00f16;
}
#cssmenu:after,
#cssmenu ul:after {
  content: '';
  display: block;
  clear: both;
}
#cssmenu a {
  background: #141414;
  background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAAxCAIAAACUDVRzAAAAA3NCSVQICAjb4U/gAAAALElEQVQImWMwMrJi+v//PxMDw3+m//8ZoPR/qBgDEhuXGLoeYswhXg8R5gAAdVpfoJ3dB5oAAAAASUVORK5CYII=) 100% 100%; 
  background: -moz-linear-gradient(top, #32323a 0%, #141414 100%);
  background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #32323a), color-stop(100%, #141414));
  background: -webkit-linear-gradient(top, #32323a 0%, #141414 100%);
  background: -o-linear-gradient(top, #32323a 0%, #141414 100%);
  background: -ms-linear-gradient(top, #32323a 0%, #141414 100%);
  background: linear-gradient(to bottom, #32323a 0%, #141414 100%);
  color: #ffffff;
  display: inline-block;
  font-family: Helvetica, Arial, Verdana, sans-serif;
  font-size: 12px;
  line-height: 49px;
  padding: 0 20px;
  text-decoration: none;
}
#cssmenu ul {
  list-style: none;
}
#cssmenu > ul {
  float: left;
}
#cssmenu > ul > li {
  float: left;
}
#cssmenu > ul > li:hover:after {
  content: '';
  display: block;
  width: 0;
  height: 0;
  position: absolute;
  left: 50%;
  bottom: 0;
  border-left: 10px solid transparent;
  border-right: 10px solid transparent;
  border-bottom: 10px solid #e00f16;
  margin-left: -10px;
}
#cssmenu > ul > li:first-child > a {
  border-radius: 5px 0 0 0;
  -moz-border-radius: 5px 0 0 0;
  -webkit-border-radius: 5px 0 0 0;
}
#cssmenu > ul > li:last-child > a {
  border-radius: 0 5px 0 0;
  -moz-border-radius: 0 5px 0 0;
  -webkit-border-radius: 0 5px 0 0;
}
#cssmenu > ul > li.active > a {
  box-shadow: inset 0 0 3px #000000;
  -moz-box-shadow: inset 0 0 3px #000000;
  -webkit-box-shadow: inset 0 0 3px #000000;
  background: #070707;
  background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAAxCAIAAACUDVRzAAAAA3NCSVQICAjb4U/gAAAALklEQVQImWNQU9Nh+v//PxMDw3+m//8ZkNj/mRgYIHxy5f//Z0BSi18e2TwS5QG4MGB54HL+mAAAAABJRU5ErkJggg==) 100% 100%; 
  background: -moz-linear-gradient(top, #26262c 0%, #070707 100%);
  background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #26262c), color-stop(100%, #070707));
  background: -webkit-linear-gradient(top, #26262c 0%, #070707 100%);
  background: -o-linear-gradient(top, #26262c 0%, #070707 100%);
  background: -ms-linear-gradient(top, #26262c 0%, #070707 100%);
  background: linear-gradient(to bottom, #26262c 0%, #070707 100%);
}
#cssmenu > ul > li:hover > a {
  background: #070707;
  background:url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAAxCAIAAACUDVRzAAAAA3NCSVQICAjb4U/gAAAALklEQVQImWNQU9Nh+v//PxMDw3+m//8ZkNj/mRgYIHxy5f//Z0BSi18e2TwS5QG4MGB54HL+mAAAAABJRU5ErkJggg==) 100% 100%; 
  background: -moz-linear-gradient(top, #26262c 0%, #070707 100%);
  background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #26262c), color-stop(100%, #070707));
  background: -webkit-linear-gradient(top, #26262c 0%, #070707 100%);
  background: -o-linear-gradient(top, #26262c 0%, #070707 100%);
  background: -ms-linear-gradient(top, #26262c 0%, #070707 100%);
  background: linear-gradient(to bottom, #26262c 0%, #070707 100%);
  box-shadow: inset 0 0 3px #000000;
  -moz-box-shadow: inset 0 0 3px #000000;
  -webkit-box-shadow: inset 0 0 3px #000000;
}
#cssmenu .has-sub {
  z-index: 1;
}
#cssmenu .has-sub:hover > ul {
  display: block;
}
#cssmenu .has-sub ul {
  display: none;
  position: absolute;
  width: 200px;
  top: 100%;
  left: 0;
}
#cssmenu .has-sub ul li {
  *margin-bottom: -1px;
}
#cssmenu .has-sub ul li a {
  background: #e00f16;
  border-bottom: 1px dotted #ec6f73;
  filter: none;
  font-size: 11px;
  display: block;
  line-height: 120%;
  padding: 10px;
}
#cssmenu .has-sub ul li:hover a {
  background: #b00c11;
}
#cssmenu .has-sub .has-sub:hover > ul {
  display: block;
}
#cssmenu .has-sub .has-sub ul {
  display: none;
  position: absolute;
  left: 100%;
  top: 0;
}
#cssmenu .has-sub .has-sub ul li a {
  background: #b00c11;
  border-bottom: 1px dotted #d06d70;
}
#cssmenu .has-sub .has-sub ul li a:hover {
  background: #80090d;
}

#header {margin: 0 0 25px;padding: 0 0 8px}

	#header #site-name {font: 265% arial;letter-spacing: -.05em;margin:0 0 0 40px;padding:3px 0;color:#ccc;border:none}
	
#wrap {min-width:770px;max-width:1200px;margin: 0 auto;position:relative}
#content-wrap {position:relative;width:100%}
	#utility {position:absolute;top:0;left:25px;width:165px;border-top: 5px solid #999;padding-bottom: 40px}
	#sidebar {position:absolute;top:0;right:25px;width:20%;border-top: 5px solid #999;padding-top: 1px;padding-bottom: 40px}

#content {margin: 0 50px}
#footer {clear:both;border-top: 1px solid #E3E8EE;padding: 10px 0 30px;font-size:86%;color:#999}
	#footer p {margin:0}
	#footer a:link {color:#999}

.field{ margin-top:10px;margin-bottom:10px;display:block}
}
    </style>
    
  </head>
  <body> 
  	<div id="wrap">
  		<h1>ADM Telephony Server</h1>
  		<br/>
	<div id="header">
	    <div id="cssmenu">
	    	<ul>
	    		<li><a href="?action=index">Home</a></li>
	    		<li class="has-sub"><a href="">Show</a>
	    			<ul>
		    			<li><a href="?action=channels">Channels</a></li>
		    			<li><a href="?action=scripts">Scripts</a></li>
		  				<li><a href="?action=queues">Queues</a></li>
		  				<li><a href="?action=queue_calls">Queue Calls</a></li>	  					  			
		  				<li><a href="?action=users">Users</a></li>	    			
	    			</ul>
	    		</li>
	  			<li class="has-sub"><a href="">Actions</a>
	  				<ul>
                        <li><a href="?action=originate">Originate</a></li>
	  					<li><a href="?action=reload">Reload</a></li>
	  				</ul>
	  			</li>
	  			
	  			<li><a href="?action=logout">Logout</a></li>
	    	</ul>
	    </div>
    </div>
    
    <div id="content-wrap">
    <div id="content">
   		<#nested>
   	</div>
   	</div>
   	</div>
  </body>
  </html>
</#macro>